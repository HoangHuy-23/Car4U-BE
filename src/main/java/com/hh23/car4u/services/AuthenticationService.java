package com.hh23.car4u.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hh23.car4u.dtos.request.*;
import com.hh23.car4u.dtos.response.AuthenticationResponse;
import com.hh23.car4u.dtos.response.IntrospectResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.User;

import java.util.Map;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
    AuthenticationResponse register(UserCreationRequest request);
    void logout(LogoutRequest request) throws Exception;
    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws Exception;
    UserResponse getMyProfile();
    String generateSocialAuthenticationURL(String provider);
    Map<String, Object> authenticationAndFetchProfile(String provider, String code) throws JsonProcessingException;
    AuthenticationResponse loginSocial(LoginSocialRequest request) throws Exception;
}
