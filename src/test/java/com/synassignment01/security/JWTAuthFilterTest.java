package com.synassignment01.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.Assertions.assertThat;

class JWTAuthFilterTest {

    private JWTAuthFilter jwtAuthFilter;
    private final String secret = "ThisIsASecretKeyForJwtTokenThatIsAtLeast32CharsLong";

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JWTAuthFilter();
        ReflectionTestUtils.setField(jwtAuthFilter, "secret", secret);
    }

    private String generateToken(String username, List<String> roles) {
        SecretKey key = hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testValidJwtTokenAuthentication() throws Exception {
        String token = generateToken("user", List.of("ROLE_USER"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isNotNull();
    }

    @Test
    void testMissingAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void testInvalidTokenReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
    }
}