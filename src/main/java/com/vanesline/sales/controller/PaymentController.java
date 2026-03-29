package com.vanesline.sales.controller;

import com.vanesline.sales.config.VnPayConfig;
import com.vanesline.sales.dto.payment.VnPayCreateResponse;
import com.vanesline.sales.dto.payment.VnPayReturnResponse;
import com.vanesline.sales.service.contract.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class PaymentController {

    private final VnPayService vnPayService;
    private final VnPayConfig vnPayConfig;

    public PaymentController(VnPayService vnPayService, VnPayConfig vnPayConfig) {
        this.vnPayService = vnPayService;
        this.vnPayConfig = vnPayConfig;
    }

    /**
     * Tạo URL thanh toán VNPay cho một đơn hàng.
     * Frontend sẽ redirect user đến URL này.
     */
    @PostMapping("${application.path.base.private}/payment/vnpay/create")
    @PreAuthorize("hasAuthority('order:write')")
    public ResponseEntity<VnPayCreateResponse> createPayment(
            @RequestParam UUID orderId,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String paymentUrl = vnPayService.createPaymentUrl(orderId, ipAddress);

        VnPayCreateResponse response = VnPayCreateResponse.builder()
                .paymentUrl(paymentUrl)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * VNPay redirect user về endpoint này sau khi thanh toán.
     * Xử lý kết quả và redirect về frontend.
     */
    @GetMapping("${application.path.base.public}/payment/vnpay/return")
    public ResponseEntity<Void> vnpayReturn(HttpServletRequest request) {
        Map<String, String> params = extractVnPayParams(request);

        VnPayReturnResponse result = vnPayService.processReturn(params);
        log.info("VNPay return processed: success={}, orderId={}, responseCode={}",
                result.isSuccess(), result.getOrderId(), result.getVnpResponseCode());

        // Redirect về frontend
        String redirectUrl = vnPayConfig.getFrontendReturnUrl();

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * VNPay IPN (Instant Payment Notification) - server-to-server callback.
     * VNPay gọi endpoint này để xác nhận giao dịch.
     */
    @GetMapping("${application.path.base.public}/payment/vnpay/ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(HttpServletRequest request) {
        Map<String, String> params = extractVnPayParams(request);

        VnPayReturnResponse result = vnPayService.processReturn(params);
        log.info("VNPay IPN processed: success={}, orderId={}, responseCode={}",
                result.isSuccess(), result.getOrderId(), result.getVnpResponseCode());

        Map<String, String> response = new HashMap<>();
        if (result.isSuccess()) {
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
        } else {
            response.put("RspCode", "99");
            response.put("Message", result.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Extract tất cả VNPay params từ query string.
     */
    private Map<String, String> extractVnPayParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        }

        return params;
    }

    /**
     * Lấy IP address thực của client (hỗ trợ proxy/load balancer).
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_CLIENT_IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For có thể chứa nhiều IP, lấy IP đầu tiên
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
