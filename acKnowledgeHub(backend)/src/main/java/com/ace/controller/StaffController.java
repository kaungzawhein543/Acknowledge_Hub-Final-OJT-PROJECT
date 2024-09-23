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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private final TokenBlacklistService tokenBlacklistService;
    private final PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler;
    private final UserNotedAnnouncementService userNotedAnnouncementService;
    private final AnnouncementService announcementService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${default.photo.path}")
    private String DEFAULT_PHOTO_PATH;

    public StaffController(StaffService staffService, ModelMapper mapper, CompanyService companyService, DepartmentService departmentService, PositionService positionService, PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler, UserNotedAnnouncementService userNotedAnnouncementService, AnnouncementService announcementService,TokenBlacklistService tokenBlacklistService) {
        this.staffService = staffService;
        this.mapper = mapper;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.userNotedAnnouncementService = userNotedAnnouncementService;
        this.announcementService = announcementService;
    }

    @GetMapping("/sys/list")
    public List<StaffResponseDTO> getStaffList() {
        return staffService.getStaffList();
    }

//    @GetMapping("/active-list")
//    public List<ActiveStaffResponseDTO> getActiveStaffList() {
//        return staffService.getActiveStaffList();
//    }

    @PostMapping("/sys/add")
    public ResponseEntity<String> addStaff(@RequestBody StaffRequestDTO staffRequestDTO) {

        try {
            Staff staff = new Staff();
            Company company = companyService.getCompanyById(staffRequestDTO.getCompanyId());
            staff.setCompany(company);
            Department department = departmentService.getDepartmentById(staffRequestDTO.getDepartmentId());
            staff.setDepartment(department);
            Optional<Position> position = positionService.findById(staffRequestDTO.getPositionId());
            staff.setPosition(position.get());
            staff.setRole(staffRequestDTO.getRole());
            staff.setCompanyStaffId(staffRequestDTO.getCompanyStaffId());
            staff.setName(staffRequestDTO.getName());
            staff.setEmail(staffRequestDTO.getEmail());
            staff.setPhotoPath(DEFAULT_PHOTO_PATH);
            staffService.addStaff(staff);
            return ResponseEntity.ok("Adding is successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding staff: " + e.getMessage());
        }
    }

    @GetMapping("/sys/group-staff")
    public List<StaffGroupDTO> getStaffListByDepartmentId() {
        List<StaffGroupDTO> staffList = staffService.getStaffListForGroup();
        return staffList;
    }

    @GetMapping("/all/noted-list/{id}")
    public List<NotedResponseDTO> getNotedStaff(@PathVariable("id") Integer announcementId) {
        List<NotedResponseDTO> staffList = staffService.getNotedStaffList(announcementId);
        return staffList;
    }

    @GetMapping("/HRM/not-noted-list/{id}/{groupStatus}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId, @PathVariable("groupStatus") byte groupStatus) {
        List<UnNotedResponseDTO> staffList = new ArrayList<UnNotedResponseDTO>();
        if (groupStatus == 1) {
            staffList = staffService.getUnNotedStaffListWithGroup(announcementId);
        } else if (groupStatus == 0) {
            staffList = staffService.getUnNotedStaffList(announcementId);
        }
        return staffList;
    }

    @GetMapping(value = "/allHR/getStaff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResponse<StaffDTO>> getStaffs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchTerm) {
        Page<StaffDTO> staffPage;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            staffPage = staffService.searchStaffs(searchTerm, page, size);
        } else {
            staffPage = staffService.getStaffs(page, size);
        }

        PaginatedResponse<StaffDTO> response = new PaginatedResponse<>(staffPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sys/hr-list")
    public List<StaffResponseDTO> getHRList() {
        return staffService.getHRStaffList();
    }

    @GetMapping("/sys/put-HR/{id}")
    public List<StaffResponseDTO> putStaffHRMain(@PathVariable("id") Integer staffId) {
        Staff staffMain = staffService.getHRMainStaff("Human Resource(Main)");
        if (staffMain != null) {
            Position position1 = positionService.findByName("Human Resource");
            staffMain.setPosition(position1);
            staffService.save(staffMain);
        }
        Staff staff = staffService.findById(staffId);
        Position position2 = positionService.findByName("Human Resource(Main)");
        staff.setPosition(position2);
        staffService.save(staff);
        return staffService.getHRStaffList();

    }


    @GetMapping("/sys/staff-count-by-announcement")
    public List<Map<String, Object>> getStaffCountByAnnouncement() {
        return staffService.getStaffCountByAnnouncement();
    }

    //get announcements list by staff id
    @GetMapping("/STF/announcements/count")
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
                Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

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
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }


    // method to get announcement noted by staff for chart
    @GetMapping("/STF/notesCountByMonth")
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
                Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

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
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    //Mapping to get staff summary count
    @GetMapping("/sys/summary")
    public StaffSummaryDTO getStaffSummary() {
        return staffService.getStaffSummary();
    }

    //Mapping to get announcement by staff id desc
    @GetMapping("/all/staff-announcements")
    public ResponseEntity<?> getMyAnnouncements(HttpServletRequest request) {
        String token = null;
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
                Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

                String staffId = claims.getSubject(); // Extract staff ID from JWT

                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is blacklisted. Please log in again.");
                }

                Staff staff = staffService.findByStaffId(staffId);
                if (staff != null) {
                    List<AnnouncementListbyStaff> announcements = staffService.getAnnouncementsForStaff(staff.getId());
                    return ResponseEntity.ok(announcements);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
                }
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid token found.");
    }

    //update profile photo
    @PostMapping("/all/profile/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = null;
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
                Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

                String staffId = claims.getSubject();
                Staff staff = staffService.findByStaffId(staffId);
                if (staff != null) {
                    // Save the file to a folder
                    String fileName = file.getOriginalFilename();
                    String uploadDir = "src/main/resources/images/";
                    String filePath = uploadDir + staffId + "_" + fileName;
                    String DatabasePath = "/images/" + staffId + "_" + fileName;

                    // Create directories if not exist
                    File uploadFolder = new File(uploadDir);
                    if (!uploadFolder.exists()) {
                        uploadFolder.mkdirs();
                    }

                    // Save the file
                    try {
                        Path path = Paths.get(filePath);
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                        // Update the photoPath in the Staff entity
                        staff.setPhotoPath(DatabasePath);
                        staffService.updateStaff(staff);  // Assume updateStaff method exists in your service

                        return ResponseEntity.ok(DatabasePath);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
                }
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid token found.");
    }

    @GetMapping("/noted")
    public ResponseEntity<String> makeNotedAnnouncement(@RequestParam Integer userId, @RequestParam Integer announcementId) {
        // Noted User
        Staff notedUser = staffService.findById(userId);
        Announcement announcement = announcementService.getAnnouncementById(announcementId).orElseThrow();
        Optional<StaffNotedAnnouncement> notedConditionAnnouncement = userNotedAnnouncementService.checkNotedOrNot(notedUser, announcement);
        if (!notedConditionAnnouncement.isPresent()) {
            StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
            staffNotedAnnouncement.setStaff(notedUser);
            staffNotedAnnouncement.setAnnouncement(announcement);
            staffNotedAnnouncement.setNotedAt(Timestamp.valueOf(LocalDateTime.now()));
            // Save Noted User and Announcement
            userNotedAnnouncementService.save(staffNotedAnnouncement);
            return ResponseEntity.ok("Noted Successfully");
        } else {
            return ResponseEntity.ok("You are ALready Noted");
        }
    }

    @GetMapping("/check-noted")
    public ResponseEntity<Boolean> checkNotedOrNot(@RequestParam Integer userId, @RequestParam Integer announcementId) {
        Staff notedUser = staffService.findById(userId);
        Announcement announcement = announcementService.getAnnouncementById(announcementId).orElseThrow();
        Optional<StaffNotedAnnouncement> notedConditionAnnouncement = userNotedAnnouncementService.checkNotedOrNot(notedUser, announcement);
        if (notedConditionAnnouncement.isPresent()) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    //Change Password from profile(changeOldPassword)
    @PostMapping("/all/change_Old_Password")
    public ResponseEntity<String> changeOldPassword(@RequestBody ChangePasswordRequest request) {
        String result = staffService.changeOldPassword(request);

        if (result.equals("Password changed successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result); // 401 Unauthorized for incorrect password
        }
    }



}

