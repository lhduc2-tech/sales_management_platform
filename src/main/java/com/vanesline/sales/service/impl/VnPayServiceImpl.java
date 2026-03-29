package com.vanesline.sales.service.impl;

import com.vanesline.sales.config.VnPayConfig;
import com.vanesline.sales.dto.payment.VnPayReturnResponse;
import com.vanesline.sales.entity.Order;
import com.vanesline.sales.repository.OrderRepository;
import com.vanesline.sales.service.contract.VnPayService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
public class VnPayServiceImpl implements VnPayService {

    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURR_CODE = "VND";
    private static final String VNP_LOCALE = "vn";
    private static final String VNP_ORDER_TYPE = "other";

    private static final String PAYMENT_STATE_PENDING = "PENDING";
    private static final String PAYMENT_STATE_PAID = "PAID";
    private static final String PAYMENT_STATE_FAILED = "FAILED";

    private static final String VNP_RESPONSE_CODE_SUCCESS = "00";

    private final VnPayConfig vnPayConfig;
    private final OrderRepository orderRepository;

    public VnPayServiceImpl(VnPayConfig vnPayConfig, OrderRepository orderRepository) {
        this.vnPayConfig = vnPayConfig;
        this.orderRepository = orderRepository;
    }

    @Override
    public String createPaymentUrl(UUID orderId, String ipAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Kiểm tra đơn hàng đã thanh toán chưa
        if (PAYMENT_STATE_PAID.equalsIgnoreCase(order.getPaymentState())) {
            throw new IllegalStateException("Order has already been paid: " + orderId);
        }

        // Kiểm tra giá trị đơn hàng
        if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Order price must be greater than 0: " + orderId);
        }

        // VNPay yêu cầu amount * 100
        long vnpAmount = order.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

        // Tạo vnp_TxnRef unique = orderId-timestamp
        String vnpTxnRef = orderId.toString();

        // Thời gian tạo giao dịch (GMT+7)
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String vnpCreateDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // Thời gian hết hạn thanh toán (15 phút)
        String vnpExpireDate = now.plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Build params map
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", VNP_VERSION);
        params.put("vnp_Command", VNP_COMMAND);
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_CurrCode", VNP_CURR_CODE);
        params.put("vnp_TxnRef", vnpTxnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderName());
        params.put("vnp_OrderType", VNP_ORDER_TYPE);
        params.put("vnp_Locale", VNP_LOCALE);
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", vnpCreateDate);
        params.put("vnp_ExpireDate", vnpExpireDate);

        // Build query string và hash data (đã sort bởi TreeMap)
        // VNPay yêu cầu hash data và query string đều dùng URL-encoded values
        StringBuilder queryBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null && !value.isEmpty()) {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append("&");
                }
                queryBuilder.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        // Hash data = query string (URL-encoded, sorted, không chứa vnp_SecureHash)
        String hashData = queryBuilder.toString();

        // Generate HMAC-SHA512 secure hash
        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData);
        queryBuilder.append("&vnp_SecureHash=").append(secureHash);

        // Cập nhật trạng thái đơn hàng thành PENDING
        order.setPaymentState(PAYMENT_STATE_PENDING);
        order.setPaymentMethod("VNPAY");
        orderRepository.save(order);

        String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + queryBuilder.toString();
        log.info("Created VNPay payment URL for order {}: {}", orderId, paymentUrl);

        return paymentUrl;
    }

    @Override
    public VnPayReturnResponse processReturn(Map<String, String> params) {
        log.info("Processing VNPay return with params: {}", params);

        // Lấy secure hash từ params
        String vnpSecureHash = params.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
            log.warn("Missing vnp_SecureHash in VNPay return params");
            return VnPayReturnResponse.builder()
                    .success(false)
                    .message("Missing secure hash")
                    .build();
        }

        // Loại bỏ vnp_SecureHash và vnp_SecureHashType khỏi params để verify
        Map<String, String> verifyParams = new TreeMap<>(params);
        verifyParams.remove("vnp_SecureHash");
        verifyParams.remove("vnp_SecureHashType");

        // Build hash data string
        StringBuilder hashDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (hashDataBuilder.length() > 0) {
                    hashDataBuilder.append("&");
                }
                hashDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
            }
        }

        // Verify secure hash
        String computedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashDataBuilder.toString());
        if (!computedHash.equalsIgnoreCase(vnpSecureHash)) {
            log.warn("VNPay secure hash verification failed. Expected: {}, Got: {}", computedHash, vnpSecureHash);
            return VnPayReturnResponse.builder()
                    .success(false)
                    .message("Invalid secure hash")
                    .build();
        }

        // Lấy thông tin từ params
        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String vnpAmountStr = params.get("vnp_Amount");

        UUID orderId;
        try {
            orderId = UUID.fromString(vnpTxnRef);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid vnp_TxnRef format: {}", vnpTxnRef);
            return VnPayReturnResponse.builder()
                    .success(false)
                    .message("Invalid transaction reference")
                    .build();
        }

        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("Order not found for vnp_TxnRef: {}", vnpTxnRef);
            return VnPayReturnResponse.builder()
                    .success(false)
                    .message("Order not found")
                    .orderId(orderId)
                    .vnpResponseCode(vnpResponseCode)
                    .build();
        }

        // Kiểm tra nếu đơn hàng đã được xử lý (tránh xử lý trùng lặp)
        if (PAYMENT_STATE_PAID.equalsIgnoreCase(order.getPaymentState())) {
            log.info("Order {} already paid, skipping update", orderId);
            return VnPayReturnResponse.builder()
                    .success(true)
                    .message("Order already paid")
                    .orderId(orderId)
                    .vnpResponseCode(vnpResponseCode)
                    .vnpTransactionNo(vnpTransactionNo)
                    .build();
        }

        // Cập nhật trạng thái thanh toán
        boolean isSuccess = VNP_RESPONSE_CODE_SUCCESS.equals(vnpResponseCode);
        if (isSuccess) {
            order.setPaymentState(PAYMENT_STATE_PAID);
            log.info("Order {} payment successful. TransactionNo: {}", orderId, vnpTransactionNo);
        } else {
            order.setPaymentState(PAYMENT_STATE_FAILED);
            log.info("Order {} payment failed. ResponseCode: {}", orderId, vnpResponseCode);
        }
        orderRepository.save(order);

        // Parse amount (VNPay amount / 100)
        BigDecimal vnpAmount = null;
        if (vnpAmountStr != null && !vnpAmountStr.isEmpty()) {
            vnpAmount = new BigDecimal(vnpAmountStr).divide(BigDecimal.valueOf(100));
        }

        return VnPayReturnResponse.builder()
                .success(isSuccess)
                .message(isSuccess ? "Payment successful" : "Payment failed")
                .orderId(orderId)
                .vnpResponseCode(vnpResponseCode)
                .vnpTransactionNo(vnpTransactionNo)
                .vnpAmount(vnpAmount)
                .build();
    }

    /**
     * Tạo HMAC-SHA512 hash.
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA512", e);
        }
    }
}
