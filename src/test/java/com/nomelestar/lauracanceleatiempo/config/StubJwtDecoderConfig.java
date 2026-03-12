package com.nomelestar.lauracanceleatiempo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

/**
 * Stub JwtDecoder for tests.
 * Replaces the real Keycloak JwtDecoder so tests don't need a running Keycloak.
 * Use @Import(StubJwtDecoderConfig.class) on any @SpringBootTest test.
 */
@TestConfiguration
public class StubJwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
