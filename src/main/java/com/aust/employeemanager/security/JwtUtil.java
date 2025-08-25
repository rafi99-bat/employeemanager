package com.aust.employeemanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET = "thisIsAReallyLongSecretKeyWithAtLeast32Chars!";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    private final Key key;

    public JwtUtil() {
        try {
            key = Keys.hmacShaKeyFor(SECRET.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid JWT secret key", e);
        }
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String extractUsername(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public String extractRole(String token) {
        return validateToken(token).getBody().get("role", String.class);
    }
}
