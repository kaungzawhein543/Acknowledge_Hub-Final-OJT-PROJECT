package com.ace.controller;

import com.ace.dto.NotedResponseDTO;
import com.ace.dto.StaffGroupDTO;
import com.ace.dto.UnNotedResponseDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.service.StaffService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/staff")
public class StaffController {

    private  final StaffService staffService;
    private final ModelMapper mapper;
    @Value("${jwt.secret}")
    private String jwtSecret;
    public StaffController(StaffService staffService, ModelMapper mapper) {
        this.staffService = staffService;
        this.mapper = mapper;
    }

    @GetMapping("/group-staff")
    public List<StaffGroupDTO> getStaffListByDepartmentId(){
        List<StaffGroupDTO> staffList = staffService.getStaffListForGroup();
        return staffList;
    }

    @GetMapping("/noted-list/{id}")
    public List<NotedResponseDTO> getNotedStaff(@PathVariable("id") Integer announcementId){
        List<NotedResponseDTO> staffList =staffService.getNotedStaffList(announcementId);
        return staffList;
    }

    @GetMapping("/not-noted-list/{id}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId){
        List<UnNotedResponseDTO> staffList = staffService.getUnNotedStaffList(announcementId);
        return staffList;
    }

    @GetMapping("/staff-count-by-announcement")
    public List<Map<String, Object>> getStaffCountByAnnouncement() {
        return staffService.getStaffCountByAnnouncement();
    }

    //get announcements list by staff id
    @GetMapping("/announcements/count")
    public ResponseEntity<Map<String, Object>> getAnnouncements(HttpServletRequest request) {
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

                // Retrieve staff ID
                String staffId = claims.getSubject();
                Staff user = staffService.findByStaffId(staffId);
                if (user != null) {
                    Map<String, Long> monthlyCount = staffService.getMonthlyAnnouncementCount(user.getId());

                    response.put("monthlyCount", monthlyCount);
                    return ResponseEntity.ok(response);
                }
            } catch (JwtException e) {
                // Token is invalid, expired, or tampered with
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        // No valid token found
        response.put("error", "No valid token found");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);    }


    // method to get announcement noted by staff for chart
    @GetMapping("/notesCountByMonth")
    public ResponseEntity<Map<String, Object>> getNotesCountByMonth(HttpServletRequest request) {
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

                // Retrieve user by staff ID
                String staffId = claims.getSubject();
                Map<String, Long> notesCountByMonth = staffService.getNotesCountByMonthForStaff(staffId);

                // Wrap the result
                Map<String, Object> result = new HashMap<>();
                result.put("monthlyCount", notesCountByMonth);
                System.out.println("Notes Count by Month: " + notesCountByMonth);  // Debugging line

                return ResponseEntity.ok(result);
            } catch (JwtException e) {
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } catch (IllegalArgumentException e) {
                response.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }

        response.put("error", "Unauthorized");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);     }
}
