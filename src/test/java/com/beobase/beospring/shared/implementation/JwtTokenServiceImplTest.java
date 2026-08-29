package com.beobase.beospring.shared.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtTokenServiceImplTest {

    private static final String SECRET =
            "this-is-a-test-secret-key-that-is-at-least-32-bytes-long";

    private JwtTokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new JwtTokenServiceImpl(SECRET);
    }

    @Test
    void generateShouldCreateValidToken() {
        String token = tokenService.generate("user-123", "user@example.com", "ROLE_USER");
        assertNotNull(token);
        assertTrue(tokenService.isValid(token));
    }

    @Test
    void generateShouldIncludeUserIdClaim() {
        String token = tokenService.generate("user-123", "user@example.com", "ROLE_USER");
        assertEquals("user-123", tokenService.extractUserId(token));
    }

    @Test
    void generateShouldIncludeEmailAsSubjectAndRoleClaim() {
        String token = tokenService.generate("user-123", "user@example.com", "ROLE_USER");
        assertEquals("user@example.com", tokenService.extractEmail(token));
        assertEquals("ROLE_USER", tokenService.extractRole(token));
    }

    @Test
    void extractUserIdShouldReturnCorrectUserId() {
        String token = tokenService.generate("abc-123", "john@example.com", "ROLE_ADMIN");
        String userId = tokenService.extractUserId(token);
        assertEquals("abc-123", userId);
    }

    @Test
    void isValidShouldReturnFalseForMalformedToken() {
        assertFalse(tokenService.isValid("not-a-valid-jwt"));
    }

    @Test
    void isValidShouldReturnFalseForTokenSignedWithDifferentSecret() {
        SecretKey differentKey = Keys.hmacShaKeyFor(
                "another-test-secret-key-that-is-at-least-32-bytes-long"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user-123")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(differentKey)
                .compact();

        assertFalse(tokenService.isValid(token));
    }

    @Test
    void isValidShouldReturnFalseForExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user-123")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();

        assertFalse(tokenService.isValid(token));
    }

    @Test
    void isValidShouldReturnFalseForTamperedToken() {
        String token = tokenService.generate("user-123", "user@example.com", "ROLE_USER");

        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");

        assertFalse(tokenService.isValid(tamperedToken));
    }

    @Test
    void constructorShouldThrowExceptionForSecretThatIsTooShort() {
        String shortSecret = "short-secret";

        assertThrows(
                WeakKeyException.class,
                () -> new JwtTokenServiceImpl(shortSecret)
        );
    }

    @Test
    void validateTokenAndGetUserIdShouldReturnUserIdWhenUserExists() {
        String token = tokenService.generate(
                "user-123",
                "user@example.com",
                "ROLE_USER"
        );

        assertEquals(
                "user-123",
                tokenService.validateTokenAndGetUserId(token, _ -> true)
        );
    }

    @Test
    void validateTokenAndGetUserIdShouldReturnNullWhenUserDoesNotExist() {
        String token = tokenService.generate(
                "user-123",
                "user@example.com",
                "ROLE_USER"
        );

        assertNull(
                tokenService.validateTokenAndGetUserId(token, _ -> false)
        );
    }

    @Test
    void validateTokenAndGetUserIdShouldReturnNullForInvalidToken() {
        assertNull(
                tokenService.validateTokenAndGetUserId("not-a-valid-jwt", _ -> true)
        );
    }
}
