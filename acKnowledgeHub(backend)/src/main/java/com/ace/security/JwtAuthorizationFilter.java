package com.ace.security;

import com.ace.service.StaffService;
import com.ace.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final StaffService staffService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthorizationFilter(@Lazy StaffService staffService, TokenBlacklistService tokenBlacklistService) {
        this.staffService = staffService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String username = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        if (jwt != null) {
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                username = claims.getSubject();
                String role = claims.get("role", String.class); // Extract role
                if (role != null) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var userDetails = staffService.loadUserByUsername(username);

                    if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}