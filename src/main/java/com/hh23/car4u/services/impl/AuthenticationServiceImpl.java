package com.hh23.car4u.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh23.car4u.dtos.request.*;
import com.hh23.car4u.dtos.response.AuthenticationResponse;
import com.hh23.car4u.dtos.response.IntrospectResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.RefreshToken;
import com.hh23.car4u.entities.User;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.mappers.UserMapper;
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
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    UserMapper userMapper;

    @NonFinal
    @Value("${app.jwt.signer-key}")
    protected String signerKey;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String clientId;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String clientSecret;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    protected String googleRedirectUri;


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
        var accessToken = generateAccessToken(user);
        var newRefreshToken = generateRefreshToken(user);

        var refreshToken = refreshTokenRepository.findByUserId(user.getId());
        if (refreshToken.isEmpty()) {
            refreshTokenRepository.save(RefreshToken.builder()
                        .userId(user.getId())
                        .token(newRefreshToken)
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());
        } else {
            refreshTokenRepository.save(RefreshToken.builder()
                    .id(refreshToken.get().getId())
                    .userId(user.getId())
                    .token(newRefreshToken)
                    .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                    .build());
        }

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
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(List.of("USER"));
        user.setNumOfTrips(0);
        user.setRating(0.0);
        user.setAvatar(null);
        user.setActive(true);

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
        return userMapper.toResponse(user);
    }

    @Override
    public String generateSocialAuthenticationURL(String provider) {
        provider = provider.trim().toLowerCase();
        if (provider.equals("google")) {
            return "https://accounts.google.com/o/oauth2/auth?client_id=" + clientId +
                    "&redirect_uri=" + googleRedirectUri +
                    "&response_type=code&scope=openid%20email%20profile";
        }
        return "";
    }

    @Override
    public Map<String, Object> authenticationAndFetchProfile(String provider, String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String accessToken;
        if (provider.toLowerCase().equals("google")) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> tokenParams = getTokenParams(code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, headers);
            ResponseEntity<String> tokenResponse = restTemplate.postForEntity("https://oauth2.googleapis.com/token", tokenRequest, String.class);
            Map<String, Object> tokenMap = objectMapper.readValue(tokenResponse.getBody(), Map.class);
            validateTokenResponse(tokenMap);
            accessToken = (String) tokenMap.get("access_token");
            ResponseEntity<String> tokenInfoResponse = restTemplate.getForEntity(
                    "https://oauth2.googleapis.com/tokeninfo?access_token=" + accessToken,
                    String.class
            );

            if (accessToken == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);
            ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    new HttpEntity<>(userInfoHeaders),
                    String.class
            );
            Map<String, Object> userInfoMap = objectMapper.readValue(userInfoResponse.getBody(), Map.class);
            if (userInfoMap.containsKey("error")) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return userInfoMap;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }


    private MultiValueMap<String, String> getTokenParams(String code) {
        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("client_id", clientId);
        tokenParams.add("client_secret", clientSecret);
        tokenParams.add("redirect_uri", googleRedirectUri);
        tokenParams.add("scope", "openid email profile");
        tokenParams.add("code", code);
        tokenParams.add("grant_type", "authorization_code");
        return tokenParams;
    }

    private void validateTokenResponse(Map<String, Object> tokenMap) {
        if (tokenMap == null || !tokenMap.containsKey("access_token")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @Override
    public AuthenticationResponse loginSocial(LoginSocialRequest request) throws Exception {
        var userAccountId = userRepository.findByGoogleAccountId(request.googleAccountId());

        if (userAccountId.isPresent()) {
            var user = userAccountId.get();
            return getAuthenticationResponse(user);
        }
        var optionalUser = userRepository.findByEmail(request.email());
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            user.setGoogleAccountId(request.googleAccountId());
            user.setAvatar(request.avatar());
            user.setName(request.name());
            userRepository.save(user);
            return getAuthenticationResponse(user);
        }
        var user = userMapper.toEntity(request);
        user.setPassword(null);
        user.setRoles(List.of("USER"));
        user.setNumOfTrips(0);
        user.setRating(0.0);
        user.setActive(true);
        user = userRepository.save(user);
        return getAuthenticationResponse(user);
    }

    private AuthenticationResponse getAuthenticationResponse(User user) {
        var accessToken = generateAccessToken(user);
        var refreshToken = generateRefreshToken(user);
        refreshTokenRepository.save(RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
                .claim("type", "access_token")
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
                .claim("type", "refresh_token")
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
