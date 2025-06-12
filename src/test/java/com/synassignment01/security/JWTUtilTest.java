package com.synassignment01.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    private JWTUtil jwtUtil;
    private final String secret = "super-secret-key-that-is-long-enough-to-be-safe-and-valid";

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        jwtUtil.init(); // manually call init to set the key
    }

    @Test
    void testGenerateJWTToken_validStructure() {
        String username = "testuser";
        String token = jwtUtil.generateJWTToken(username);

        assertNotNull(token);

        // Parse token to verify claims
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
        assertEquals("ROLE_USER", ((java.util.List<?>) claims.get("authorities")).get(0));
    }

    @Test
    void testTokenExpirationIsSetCorrectly() {
        String token = jwtUtil.generateJWTToken("testuser");

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        long issuedAt = claims.getIssuedAt().getTime();
        long expiration = claims.getExpiration().getTime();

        // Should be 10 hours later
        assertEquals(1000 * 60 * 60 * 10, expiration - issuedAt);
    }
}
