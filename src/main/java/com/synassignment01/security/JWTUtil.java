package com.synassignment01.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating and managing JWT tokens.
 */
@Slf4j
@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    String secret;

    private SecretKey key;

    /**
     * Initializes the secret key after the component is constructed.
     * Converts the plain-text secret into an HMAC SHA key.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given username.
     * Token is valid for 10 hours and contains the "ROLE_USER" authority.
     *
     * @param username the username to include in the token
     * @return the generated JWT token as a String
     */
    public String generateJWTToken(String username){
        log.info("Generating JWT Token for user {}", username);
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", List.of("ROLE_USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10)) //10 hrs
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
