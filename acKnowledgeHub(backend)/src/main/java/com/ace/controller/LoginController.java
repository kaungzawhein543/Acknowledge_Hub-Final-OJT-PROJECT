package com.ace.controller;

import com.ace.dto.ChangePasswordRequest;
import com.ace.dto.LoginRequest;
import com.ace.dto.LoginUserInfo;
import com.ace.dto.ProfileDTO;
import com.ace.entity.Company;
import com.ace.entity.Staff;
import com.ace.security.JwtUtil;
import com.ace.service.AuthService;
import com.ace.service.CompanyService;
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
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public LoginController(StaffService staffService, AuthService authService, JwtUtil jwtUtil) {
        this.staffService = staffService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest,response);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletResponse response) {
        boolean result = staffService.changePassword(changePasswordRequest.getStaffId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        if (result) {
            return ResponseEntity.ok("Password changed successfully. Please log in with your new password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed. Please check your old password and try again.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserFromToken(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Staff staffData = jwtUtil.extractUserDataFromToken(request);
        response.put("position",staffData.getPosition().getName());
        response.put("company",staffData.getCompany().getName());
        response.put("companyId",staffData.getCompany().getId());

        // Retrieve user by staff ID
        Staff user = staffService.findByStaffId(staffData.getCompanyStaffId());
        if (user == null) {
            response.put("isLoggedIn", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("isLoggedIn", true);
        response.put("user", new LoginUserInfo(user.getId(), user.getCompanyStaffId(), user.getName(), user.getRole(), user.getPosition().getName()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);

        if (token != null) {
            try {
                Staff staffData = jwtUtil.extractUserDataFromToken(request);

                // Retrieve the staff member by their ID
                Staff staff = staffService.findByStaffId(staffData.getCompanyStaffId());
                if (staff != null) {
                    Map<String, Long> monthlyCount = staffService.getMonthlyAnnouncementCount(staff.getId());

                    // Map Staff entity to StaffProfileDTO
                    ProfileDTO profileDTO = new ProfileDTO(staff.getId(), staff.getName(), staff.getCompanyStaffId(),
                            staff.getEmail(), staff.getPassword(), staff.getStatus(), staff.getRole(), staff.getPhotoPath(),
                            staff.getPosition().getName(), staff.getDepartment().getName(), staff.getCompany().getName(),
                            staff.getCreatedAt(), staff.getChatId(), monthlyCount);

                    return ResponseEntity.ok(profileDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
                }
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid token found.");
    }
}