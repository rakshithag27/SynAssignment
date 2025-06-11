package com.synassignment01.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {
    String secret = "secret";

    public String generateJWTToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", List.of("ROLE_USER"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10)) //10 hrs
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
