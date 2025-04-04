package com.hh23.car4u.controllers;

import com.hh23.car4u.dtos.ApiResponse;
import com.hh23.car4u.dtos.request.*;
import com.hh23.car4u.dtos.response.AuthenticationResponse;
import com.hh23.car4u.dtos.response.IntrospectResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    ApiResponse<AuthenticationResponse> register(@RequestBody @Valid UserCreationRequest request) {
        var response = authenticationService.register(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("User registered successfully")
                .data(response)
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        var response = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("User logged in successfully")
                .data(response)
                .build();
    }

    @GetMapping("/social-login")
    ApiResponse<String> socialLogin(@RequestParam String provider) {
        log.info("Social login request received");
        provider = provider.trim().toLowerCase();
        String url = authenticationService.generateSocialAuthenticationURL(provider);
        return ApiResponse.<String>builder()
                .message("User request login with "+ provider)
                .data(url)
                .build();
    }

    @GetMapping("/social-login/callback")
    ApiResponse<AuthenticationResponse> socialCallback(@RequestParam String provider, @RequestParam String code) throws Exception {
        log.info("Social callback request received", provider, code);
        provider = provider.trim().toLowerCase();
        Map<String, Object> userInfo = authenticationService.authenticationAndFetchProfile(provider, code);
        if (userInfo == null) {
            return ApiResponse.<AuthenticationResponse>builder()
                    .message("User not found")
                    .data(null)
                    .build();
        }
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String avatar = (String) userInfo.get("picture");
        String googleAccountId = (String) userInfo.get("sub");

        LoginSocialRequest request = LoginSocialRequest.builder()
                .email(email)
                .name(name)
                .avatar(avatar)
                .googleAccountId(googleAccountId)
                .gender("")
                .phone("")
                .dob(null)
                .build();

        AuthenticationResponse response = authenticationService.loginSocial(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .message("User logged in successfully with "+ provider)
                .data(response)
                .build();
    }

    @PostMapping("/refresh-token")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws Exception {
        var response = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Token refreshed successfully")
                .data(response)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<String> logout(@RequestBody @Valid LogoutRequest request) throws Exception {
        authenticationService.logout(request);
        return ApiResponse.<String>builder()
                .message("User logged out successfully")
                .data("Logout successful")
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) throws Exception {
        var response = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .message("Token introspected successfully")
                .data(response)
                .build();
    }

    @GetMapping("/me")
    ApiResponse<UserResponse> getMyProfile() {
        var response = authenticationService.getMyProfile();
        return ApiResponse.<UserResponse>builder()
                .message("User profile fetched successfully")
                .data(response)
                .build();
    }
}
