package com.cafe.stockmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Generate a SecretKey from our secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Called after login — creates the token
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername());
    }

    // Overload — generate token directly from email string
    public String generateToken(String email) {
        return buildToken(email);
    }

    private String buildToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(email)             // Who this token belongs to
                .issuedAt(now)              // When it was created
                .expiration(expiry)         // When it expires
                .signWith(getSigningKey())  // Sign it so nobody can tamper
                .compact();
    }

    // Extract email from token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Check if token is valid and not expired
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT error: " + e.getMessage());
        }
        return false;
    }
}