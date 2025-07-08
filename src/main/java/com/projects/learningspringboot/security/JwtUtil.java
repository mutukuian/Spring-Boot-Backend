package com.projects.learningspringboot.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

//    @Value("${jwt.secret}")
//    private String secret;


    private Key key;
    private final long expirationMillis = 1000 * 60 * 60; // 1 hour

    public JwtUtil() {
        Dotenv dotenv = Dotenv.load(); // Loads .env from root
        String secret = dotenv.get("JWT_SECRET");

        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env file!");
        }

        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

//    @PostConstruct
//    public void init() {
//        byte[] decodedKey = Base64.getDecoder().decode(secret);
//        this.key = Keys.hmacShaKeyFor(decodedKey);
//    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
