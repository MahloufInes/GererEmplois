package com.edt.security;

import com.edt.entity.Utilisateur;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generate JWT
    public String generateToken(Utilisateur utilisateur) {

        return Jwts.builder()
                .subject(utilisateur.getEmail())
                .claim("id", utilisateur.getId())
                .claim("role", utilisateur.getRole().name())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    // Extract email from JWT
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validate JWT
    public boolean isTokenValid(String token, String email) {
        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject().equals(email)
                    && claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    // Extract all token information
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}