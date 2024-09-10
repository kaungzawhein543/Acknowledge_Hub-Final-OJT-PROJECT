package com.ace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(String username, String staffId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("staffId", staffId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
//    // Extract staffId from token
//    public String extractStaffId(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(jwtSecret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("staffId", String.class);
//    }

//    // Retrieves JWT token from cookies
//    public String getTokenFromCookies(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("jwt".equals(cookie.getName())) {
//                    String token = cookie.getValue();
//                    System.out.println("Token found: " + token);
//                    return token;
//                }
//            }
//        }
//        System.out.println("Token not found in cookies.");
//        return null;
//    }
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(jwtSecret)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (JwtException e) {
//            System.out.println("Invalid token: " + e.getMessage());
//            return false;
//        }
//    }
}