package com.ace.controller;

import com.ace.dto.ChangePasswordRequest;
import com.ace.dto.LoginRequest;
import com.ace.dto.LoginUserInfo;
import com.ace.entity.Staff;
import com.ace.service.StaffService;
import com.ace.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth")
@Slf4j
public class LoginController {

    private final StaffService staffService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;


    @Value("${jwt.secret}")
    private String jwtSecret;

    public LoginController(StaffService staffService, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
        this.staffService = staffService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Staff user = staffService.authenticate(loginRequest.getStaffId(), loginRequest.getPassword());
        if (user != null) {
            if (passwordEncoder.matches("acknowledgeHub", user.getPassword()) || passwordEncoder.matches("adminPassword", user.getPassword())) {
                return ResponseEntity.ok(user.getCompanyStaffId() + ":Please change your password");
            } else {
                String token = Jwts.builder()
                        .setSubject(user.getCompanyStaffId())
                        .claim("name", user.getName())
                        .claim("role", user.getRole())
                        .claim("position",user.getPosition().getName())
                        .claim("company",user.getCompany().getName())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact();

                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(86400);
                cookie.setSecure(true);
                response.addCookie(cookie);
                return ResponseEntity.ok("Login successful\n" + token);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid staff ID or password");
        }
    }


    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletResponse response) {
        boolean result = staffService.changePassword(changePasswordRequest.getStaffId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        if (result) {
            String token = Jwts.builder()
                    .setSubject(changePasswordRequest.getStaffId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();


            return ResponseEntity.ok("Password changed successfully. Please log in with your new password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed. Please check your old password and try again.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserFromToken(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String token = null;

        // Extract JWT from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                // Parse and validate JWT
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                //Check Position
                String staffId = claims.getSubject();
                String position = claims.get("position",String.class);
                String company = claims.get("company",String.class);
                response.put("position",position);
                response.put("company",company);

                // Check if the token is blacklisted
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    response.put("isLoggedIn", false);
                    return ResponseEntity.ok(response);
                }

                // Retrieve user by staff ID
                Staff user = staffService.findByStaffId(staffId);
                if (user != null) {
                    response.put("isLoggedIn", true);
                    response.put("user", new LoginUserInfo(user.getId(), user.getName(), user.getCompanyStaffId(), user.getRole(), user.getPosition().getName()));
                    return ResponseEntity.ok(response);
                }
            } catch (JwtException e) {
                // Token is invalid, expired, or tampered with
                response.put("isLoggedIn", false);
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        // No valid token found
        response.put("isLoggedIn", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    tokenBlacklistService.blacklistToken(token); // Blacklist token on logout
                }
            }
        }

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

}