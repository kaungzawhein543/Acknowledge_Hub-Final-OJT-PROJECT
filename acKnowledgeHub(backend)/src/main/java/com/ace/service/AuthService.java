package com.ace.service;

import com.ace.dto.LoginRequest;
import com.ace.entity.Staff;
import com.ace.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final StaffService staffService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(StaffService staffService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.staffService = staffService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<String> login(LoginRequest request, HttpServletResponse response) {
        Staff user = staffService.findByStaffId(request.getStaffId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid staff ID");
        } else if (!user.getStatus().equals("active")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your account is inactive. Please contact the administrator.");
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if (passwordEncoder.matches("acknowledgeHub", user.getPassword()) || passwordEncoder.matches("adminPassword", user.getPassword())) {
                return ResponseEntity.ok(user.getCompanyStaffId() + ":Please change your password");
            } else {
                String token = jwtUtil.generateToken(user);

                Cookie cookie = jwtUtil.createCookie(request.isRememberMe(), token);
                response.addCookie(cookie);
                return ResponseEntity.ok("Login successful\n" + token);
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }

    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }
}
