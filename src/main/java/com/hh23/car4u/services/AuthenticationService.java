package com.hh23.car4u.services;

import com.hh23.car4u.dtos.request.*;
import com.hh23.car4u.dtos.response.AuthenticationResponse;
import com.hh23.car4u.dtos.response.IntrospectResponse;
import com.hh23.car4u.dtos.response.UserResponse;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
    AuthenticationResponse register(UserCreationRequest request);
    void logout(LogoutRequest request) throws Exception;
    AuthenticationResponse refreshToken(RefreshTokenRequest request) throws Exception;
    UserResponse getMyProfile();
}
