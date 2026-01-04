package org.tricol.supplierchain.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - JWT Service")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long jwtExpiration = 86400000;
    private final long refreshExpiration = 604800000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    @DisplayName("Génération de token JWT valide")
    void shouldGenerateValidJwtToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Validation de token JWT valide")
    void shouldValidateValidToken() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Extraction du username depuis le token")
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Génération de refresh token")
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(jwtService.extractUsername(refreshToken)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Token expiré doit être invalide")
    void shouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 1000000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        try {
            boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
            assertThat(isValid).isFalse();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Token avec mauvais username doit être invalide")
    void shouldRejectTokenWithWrongUsername() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        boolean isValid = jwtService.isTokenValid(token, wrongUser);

        assertThat(isValid).isFalse();
    }
}