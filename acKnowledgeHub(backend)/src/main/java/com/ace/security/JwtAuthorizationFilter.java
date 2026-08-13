package com.ace.security;

import com.ace.entity.Staff;
import com.ace.service.StaffService;
import com.ace.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
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
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final StaffService staffService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthorizationFilter(@Lazy StaffService staffService, TokenBlacklistService tokenBlacklistService, JwtUtil jwtUtil) {
        this.staffService = staffService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (jwt != null) {
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                Staff staffData = jwtUtil.extractUserDataFromToken(request);
                if (staffData.getCompanyStaffId() != null) {
                    var userDetails = staffService.loadUserByUsername(staffData.getCompanyStaffId());

                    if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create a list of authorities based on both role and position
                        var authorities = new ArrayList<SimpleGrantedAuthority>();
                        if (staffData.getRole() != null) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + staffData.getRole().toString()));
                        }
                        if (staffData.getPosition() != null) {
                            authorities.add(new SimpleGrantedAuthority(staffData.getPosition().getName()));
                        }
                        // Create authentication token
                        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication in context
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}