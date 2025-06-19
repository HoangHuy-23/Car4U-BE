package com.hh23.car4u.services.impl;

import com.hh23.car4u.configs.VNPAYConfig;
import com.hh23.car4u.dtos.response.PaymentResponse;
import com.hh23.car4u.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentServiceImpl {
    VNPAYConfig vnpConfig;
    public PaymentResponse.VNPayResponse createVNPayPayment(HttpServletRequest request) {
        long amount = Long.parseLong(request.getParameter("amount")); // Convert to VND
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParams = vnpConfig.getVNPayConfig();
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParams.put("vnp_BankCode", bankCode);
        }
        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        String queryUrl = VNPayUtil.getPaymentURL(vnpParams, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParams, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnpConfig.getVnp_SecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String vnpUrl = vnpConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentResponse.VNPayResponse.builder()
                .code("ok")
                .message("Create VNPay payment successfully")
                .paymentUrl(vnpUrl)
                .build();
    }
}
