package com.hh23.car4u.configs;

import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${app.jwt.signer-key}")
    private String signerKey;

    private final AuthenticationService authenticationService;

    public CustomJwtDecoder(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
            if (!signedJWT.verify(jwsVerifier)) {
                throw new JwtException("Token verification failed");
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            return new Jwt(token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());
        } catch (ParseException e) {
            throw new JwtException("Invalid token");
        } catch (JOSEException e) {
            throw new JwtException("Token verification failed");
        }
    }
}
