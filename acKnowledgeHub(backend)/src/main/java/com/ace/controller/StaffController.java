package com.ace.controller;

import com.ace.dto.*;
import com.ace.entity.*;
import com.ace.service.*;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/staff")
public class StaffController {

    private final StaffService staffService;
    private final ModelMapper mapper;
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler;
    private final UserNotedAnnouncementService userNotedAnnouncementService;
    private final AnnouncementService announcementService;

    @Value("${jwt.secret}")
    private String jwtSecret;
    public StaffController(StaffService staffService, ModelMapper mapper, CompanyService companyService, DepartmentService departmentService, PositionService positionService, PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler, UserNotedAnnouncementService userNotedAnnouncementService, AnnouncementService announcementService) {
        this.staffService = staffService;
        this.mapper = mapper;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.userNotedAnnouncementService = userNotedAnnouncementService;
        this.announcementService = announcementService;
    }

    @GetMapping("/list")
    public List<StaffResponseDTO> getStaffList() {
        return staffService.getStaffList();
    }

    @GetMapping("/active-list")
    public List<ActiveStaffResponseDTO> getActiveStaffList() {
        return staffService.getActiveStaffList();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStaff(@RequestBody StaffRequestDTO staffRequestDTO) {

        try {
            Staff staff = new Staff();
            Optional<Company> company = companyService.findById(staffRequestDTO.getCompanyId());
            staff.setCompany(company.get());
            Optional<Department> department = departmentService.findById(staffRequestDTO.getDepartmentId());
            staff.setDepartment(department.get());
            Optional<Position> position = positionService.findById(staffRequestDTO.getPositionId());
            staff.setPosition(position.get());
            staff.setRole(staffRequestDTO.getRole());
            staff.setCompanyStaffId(staffRequestDTO.getCompanyStaffId());
            staff.setName(staffRequestDTO.getName());
            staff.setEmail(staffRequestDTO.getEmail());
            staffService.addStaff(staff);
            return ResponseEntity.ok("Adding is successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding staff: " + e.getMessage());
        }
    }

    @GetMapping("/group-staff")
    public List<StaffGroupDTO> getStaffListByDepartmentId() {
        List<StaffGroupDTO> staffList = staffService.getStaffListForGroup();
        return staffList;
    }

    @GetMapping("/noted-list/{id}")
    public List<NotedResponseDTO> getNotedStaff(@PathVariable("id") Integer announcementId) {
        List<NotedResponseDTO> staffList = staffService.getNotedStaffList(announcementId);
        return staffList;
    }


    @GetMapping("/not-noted-list/{id}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId, @RequestParam("groupStatus") byte groupStatus) {
        List<UnNotedResponseDTO> staffList = new ArrayList<UnNotedResponseDTO>();
        if (groupStatus == 1) {
            staffList = staffService.getUnNotedStaffListWithGroup(announcementId);
        } else if (groupStatus == 0) {
            staffList = staffService.getUnNotedStaffList(announcementId);
        }
        return staffList;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResponse<StaffDTO>> getStaffs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String searchTerm) {
        Page<StaffDTO> staffPage;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            staffPage = staffService.searchStaffs(searchTerm, page, size);
        } else {
            staffPage = staffService.getStaffs(page, size);
        }

        PaginatedResponse<StaffDTO> response = new PaginatedResponse<>(staffPage);
        return ResponseEntity.ok(response);
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
            .body(response);
    }

    @GetMapping("/noted")
    public ResponseEntity<String> makeNotedAnnouncement(@RequestParam Integer userId ,@RequestParam Integer announcementId){
        // Noted User
        Staff notedUser = staffService.findById(userId);
        Announcement announcement = announcementService.getAnnouncementById(announcementId)
                .orElseThrow();
        Optional<StaffNotedAnnouncement> notedConditionAnnouncement = userNotedAnnouncementService
                .checkNotedOrNot(notedUser, announcement);
        if (!notedConditionAnnouncement.isPresent()) {
            StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
            staffNotedAnnouncement.setStaff(notedUser);
            staffNotedAnnouncement.setAnnouncement(announcement);
            staffNotedAnnouncement.setNotedAt(Timestamp.valueOf(LocalDateTime.now()));
            // Save Noted User and Announcement
            userNotedAnnouncementService.save(staffNotedAnnouncement);
            return ResponseEntity.ok("Noted Successfully");
        }else{
            return ResponseEntity.ok("You are ALready Noted");
        }
    }

    @GetMapping("/check-noted")
    public ResponseEntity<Boolean> checkNotedOrNot(@RequestParam Integer userId,@RequestParam Integer announcementId){
        Staff notedUser = staffService.findById(userId);
        Announcement announcement = announcementService.getAnnouncementById(announcementId)
                .orElseThrow();
        Optional<StaffNotedAnnouncement> notedConditionAnnouncement = userNotedAnnouncementService
                .checkNotedOrNot(notedUser, announcement);
        if (notedConditionAnnouncement.isPresent()){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }
}

