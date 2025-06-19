package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.response.PaymentResponse;
import com.hh23.car4u.services.impl.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    @GetMapping("/vn-pay")
    public ResponseEntity<PaymentResponse.VNPayResponse> getVnPayUrl(HttpServletRequest request) {
        return new ResponseEntity<>(paymentService.createVNPayPayment(request), HttpStatus.OK);
    }
    @GetMapping("/vn-pay/callback")
    public ResponseEntity<PaymentResponse.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return new ResponseEntity<>(new PaymentResponse.VNPayResponse("00", "Success", ""), HttpStatus.OK);
        }
        else if (status.equals("24")) {
            return new ResponseEntity<>(new PaymentResponse.VNPayResponse("24", "Cancel", ""), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
