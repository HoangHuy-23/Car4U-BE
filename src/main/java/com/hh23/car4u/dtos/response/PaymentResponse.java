package com.hh23.car4u.dtos.response;

import lombok.Builder;

public abstract class PaymentResponse {
@Builder
public static class VNPayResponse{
    public String code;
    public String message;
    public String paymentUrl;

    public VNPayResponse(String code, String message, String paymentUrl) {
        this.code = code;
        this.message = message;
        this.paymentUrl = paymentUrl;
    }
}
}
