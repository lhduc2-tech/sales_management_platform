package com.vanesline.sales.service.contract;

import com.vanesline.sales.dto.payment.VnPayReturnResponse;

import java.util.Map;
import java.util.UUID;

public interface VnPayService {

    /**
     * Tạo URL thanh toán VNPay cho một đơn hàng.
     *
     * @param orderId   ID đơn hàng cần thanh toán
     * @param ipAddress IP address của client
     * @return URL thanh toán VNPay đầy đủ
     */
    String createPaymentUrl(UUID orderId, String ipAddress);

    /**
     * Xử lý callback return/IPN từ VNPay.
     * Verify secure hash, cập nhật trạng thái thanh toán đơn hàng.
     *
     * @param params query parameters từ VNPay
     * @return kết quả xử lý
     */
    VnPayReturnResponse processReturn(Map<String, String> params);
}
