package com.hh23.car4u.services.impl;

import com.hh23.car4u.dtos.request.*;
import com.hh23.car4u.dtos.response.AuthenticationResponse;
import com.hh23.car4u.dtos.response.IntrospectResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.RefreshToken;
import com.hh23.car4u.entities.User;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.repositories.RefreshTokenRepository;
import com.hh23.car4u.repositories.UserRepository;
import com.hh23.car4u.services.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${app.jwt.signer-key}")
    protected String signerKey;

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.accessToken();
        boolean isValid = true;
        try {
             verifyToken(token);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByEmail(request.email()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean isValid = passwordEncoder.matches(request.password(), user.getPassword());
        if (!isValid) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        var accessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                        .id(refreshToken.getId())
                        .userId(user.getId())
                        .token(newRefreshToken)
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse register(UserCreationRequest request) {
        var userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .dob(request.dob())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .roles(List.of("USER"))
                .numOfTrips(0)
                .rating(0.0)
                .avatar(null)
                .isActive(true)
                .build();
        user = userRepository.save(user);
        var refreshToken = generateRefreshToken(user);
        refreshTokenRepository.save(RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());
        var accessToken = generateAccessToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) throws Exception {
        var signedJWT = verifyToken(request.refreshToken());
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws Exception {
        var signedJWT = verifyToken(request.refreshToken());
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        if (!refreshToken.getToken().equals(request.refreshToken())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var newAccessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                        .id(refreshToken.getId())
                        .userId(user.getId())
                        .token(newRefreshToken)
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

    @Override
    public UserResponse getMyProfile() {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .dob(user.getDob())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(user.getRoles())
                .numOfTrips(user.getNumOfTrips())
                .rating(user.getRating())
                .isActive(user.isActive())
                .build();
    }

    private String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        Date issuedAt = new Date();
        Date expiresAt = new Date(Instant.ofEpochMilli(issuedAt.getTime()).plus(1, ChronoUnit.DAYS).toEpochMilli());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("car4u")
                .issueTime(issuedAt)
                .expirationTime(expiresAt)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("email", user.getEmail())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create access token",e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        Date issuedAt = new Date();
        Date expiresAt = new Date(Instant.ofEpochMilli(issuedAt.getTime()).plus(7, ChronoUnit.DAYS).toEpochMilli());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("car4u")
                .issueTime(issuedAt)
                .expirationTime(expiresAt)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create refresh token",e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private SignedJWT verifyToken(String token) throws Exception {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expirationDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                joiner.add("ROLE_" + role);
            });
        }
        return joiner.toString();
    }

}
