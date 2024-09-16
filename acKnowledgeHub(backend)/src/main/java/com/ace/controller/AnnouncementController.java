package com.ace.controller;

import com.ace.dto.*;
import com.ace.entity.*;
import com.ace.repository.StaffRepository;
import com.ace.service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.util.stream.Collectors;


@EnableAsync
@Slf4j
@RestController
@RequestMapping(value = "api/v1/announcement")
public class AnnouncementController {

    private final AnnouncementService announcement_service;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper mapper;
    private final ReportService reportService;
    private final BlogService blogService;
    private final PostSchedulerService postSchedulerService;
    private final BotService botService;
    private final StaffService staffService;
    private final EmailService emailService;
    private final GroupService groupService;
    private final UserNotedAnnouncementService userNotedAnnouncementService;

    public AnnouncementController(AnnouncementService announcement_service, CloudinaryService cloudinaryService, ModelMapper mapper, ReportService reportService, BlogService blogService, PostSchedulerService postSchedulerService, BotService botService, StaffRepository staffRepository, StaffService staffService, EmailService emailService, GroupService groupService, UserNotedAnnouncementService userNotedAnnouncementService) {
        this.announcement_service = announcement_service;
        this.cloudinaryService = cloudinaryService;
        this.mapper = mapper;
        this.reportService = reportService;
        this.blogService = blogService;
        this.postSchedulerService = postSchedulerService;
        this.botService = botService;
        this.staffService = staffService;
        this.emailService = emailService;
        this.groupService = groupService;
        this.userNotedAnnouncementService = userNotedAnnouncementService;
    }

    @GetMapping("/latest-version-by-id/{id}")
    public ResponseEntity<AnnouncementUpdateDTO> getLatestAnnouncementById(@PathVariable int id) {
        Optional<Announcement> getFirstVersionOfAnnouncement = announcement_service.getAnnouncementById(id);
        String[] pathParts = getFirstVersionOfAnnouncement.get().getFile().split("/");
        return announcement_service.findLastByFileName(pathParts[2])
                .map(announcement -> {
                    // Create DTO manually
                    AnnouncementUpdateDTO dto = new AnnouncementUpdateDTO();
                    dto.setId(announcement.getId());
                    dto.setTitle(announcement.getTitle());
                    dto.setDescription(announcement.getDescription());
                    dto.setCategory(announcement.getCategory());
                    dto.setCreatedStaffId(announcement.getCreateStaff().getId());
                    dto.setGroupStatus(announcement.getGroupStatus());
                    dto.setFile(announcement.getFile());
                    // Manually map groups and staff to prevent recursion
                    List<Integer> groupIds = announcement.getGroup().stream()
                            .map(Group::getId)
                            .collect(Collectors.toList());
                    dto.setGroup(groupIds);

                    // Initialize a Set to store unique staff IDs
                    Set<Integer> allStaff = new HashSet<>();
                    // Fetch staff for each group ID
                    for (Integer groupId : groupIds) {
                        List<Staff> staffInGroup = groupService.getStaffsByGroupId(groupId);
                        for (Staff staff : staffInGroup) {
                            allStaff.add(staff.getId());
                        }
                    }
                    dto.setStaffInGroups(allStaff);

                    dto.setStaff(announcement.getStaff());

                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    //    Create and update method (because update is also insert the row in database)
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Announcement> createAnnouncement(
            @RequestPart AnnouncementDTO request,
            @RequestPart(name = "userIds", required = false) List<Integer> userIds,
            @RequestPart(name = "groupIds", required = false) List<Integer> groupIds,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            @RequestParam(name = "createUserId") Integer createUserId) {
        try {
            // Find Create Staff From Announcement DTO
            Staff user = staffService.findById(createUserId);
            Announcement announcement = mapper.map(request, Announcement.class);

            List<Group> groupsForAnnounce = new ArrayList<>();
            List<Staff> staffForAnnounce = new ArrayList<>();

            //Announce People
            if (request.getGroupStatus() == 1) {
                groupsForAnnounce = groupService.findGroupsByIds(groupIds);
                // Initialize staff list before async operation
                for (Group group : groupsForAnnounce) {
                    group.getStaff().size(); // Force initialization
                }
                announcement.setGroup(groupsForAnnounce);
                staffForAnnounce = null;
            } else {
                staffForAnnounce = staffService.findStaffsByIds(userIds);
                announcement.setStaff(staffForAnnounce);
                groupsForAnnounce = null;
            }

            // Map DTO to Entity
            announcement.setFile("N/A");
            announcement.setCreateStaff(user);
            announcement.setCategory(request.getCategory());


            // If ScheduledAt is null assign default
            if (announcement.getScheduleAt() == null) {
                LocalDateTime publishDateTime = LocalDateTime.now();
                announcement.setScheduleAt(publishDateTime);
            }
            if (request.getForRequest() == 1) {
                announcement.setPermission("pending");
            } else {
                announcement.setPermission("approved");
            }
            //Set id to null because even that is update need to add new row
            announcement.setId(null);
            // Save the announcement
            Announcement savedAnnouncement = announcement_service.createAnnouncement(announcement);


            // Send Announcement to Telegram & email
            if (request.getScheduleAt() != null) {
                if (request.getForRequest() != 1) {
                    LocalDateTime requestAnnounceScheduleTime = request.getScheduleAt();
                    savedAnnouncement.setScheduleAt(requestAnnounceScheduleTime);
                    blogService.createPost(savedAnnouncement);
                }
            } else {
                if (request.getForRequest() != 1) {
                    blogService.sendTelegramAndEmail(staffForAnnounce, groupsForAnnounce, files.get(0), savedAnnouncement.getId(), request.getGroupStatus());
                    savedAnnouncement.setPublished(true);
                    announcement_service.updateAnnouncement(savedAnnouncement.getId(), savedAnnouncement);
                }
            }

            if (files != null && !files.isEmpty()) {
                MultipartFile file = files.get(0);
                CompletableFuture<Map<String, Object>> uploadFuture;
                if (request.getId() != 0) {
                    uploadFuture = cloudinaryService.uploadFile(file, "Announce" + request.getId());
                } else {
                    uploadFuture = cloudinaryService.uploadFile(file, "Announce" + savedAnnouncement.getId());
                }

                uploadFuture.thenAccept(uploadResult -> {
                    try {
                        String fileName = uploadResult.get("public_id").toString();

                        // Update announcement with the file name
                        savedAnnouncement.setFile(fileName);
                        Announcement updateFileUrlAnnounce = announcement_service.updateFileUrl(savedAnnouncement);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).exceptionally(uploadEx -> {
                    uploadEx.printStackTrace();
                    return null;
                });
            }

            return ResponseEntity.ok(savedAnnouncement);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncementById(@PathVariable Integer id) {

        return announcement_service.getAnnouncementById(id)
                .map(announcement -> {
                    // Create DTO manually
                    AnnouncementDTO dto = new AnnouncementDTO();
                    dto.setId(announcement.getId());
                    dto.setTitle(announcement.getTitle());
                    dto.setDescription(announcement.getDescription());
                    dto.setCategory(announcement.getCategory());
                    dto.setCreatedStaffId(announcement.getCreateStaff().getId());
                    dto.setGroupStatus(announcement.getGroupStatus());
                    dto.setFile(announcement.getFile());
                    // Manually map groups and staff to prevent recursion
                    List<Integer> groupIds = announcement.getGroup().stream()
                            .map(Group::getId)
                            .collect(Collectors.toList());
                    dto.setGroup(groupIds);

                    // Initialize a Set to store unique staff IDs
                    Set<Integer> allStaff = new HashSet<>();
                    // Fetch staff for each group ID
                    for (Integer groupId : groupIds) {
                        List<Staff> staffInGroup = groupService.getStaffsByGroupId(groupId);
                        for (Staff staff : staffInGroup) {
                            allStaff.add(staff.getId());
                        }
                    }
                    dto.setStaffInGroups(allStaff);
                    System.out.println(allStaff);

                    List<Integer> staffIds = announcement.getStaff().stream()
                            .map(Staff::getId)
                            .collect(Collectors.toList());
                    dto.setStaff(staffIds);

                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    @GetMapping("/getPublishedAnnouncements")
    public ResponseEntity<List<AnnouncementListDTO>> getPublishedAnnouncements() {
        List<AnnouncementListDTO> publishedAnnouncements = announcement_service.getPublishedAnnouncements();
        return ResponseEntity.ok(publishedAnnouncements);
    }


    @GetMapping("/announcement-versions/{baseFileName}")
    public List<String> getAnnouncementVersions(@PathVariable("baseFileName") String baseFileName) {
        return announcement_service.getAllVersionsByFilePattern(baseFileName);
    }

    @GetMapping("/announcement-get-url")
    public ResponseEntity<String> getAnnouncementDownloadLink(@RequestParam("fileName") String fileName){

        String Url = cloudinaryService.getUrlsOfAnnouncements(fileName);

        return ResponseEntity.ok().body(Url);
    }

    @GetMapping("/note")
    public ResponseEntity<Void> noteAnnouncement(@RequestParam Integer announcementId, @RequestParam String userEmail, HttpServletResponse response) {
        StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
        Optional<Announcement> announcement = announcement_service.getAnnouncementById(announcementId);
        staffNotedAnnouncement.setAnnouncement(announcement.get());
        Staff staff = staffService.findByEmail(userEmail);
        staffNotedAnnouncement.setStaff(staff);
        Optional<StaffNotedAnnouncement> notedAnnouncement =  userNotedAnnouncementService.checkNotedOrNot(staff,announcement.get());
        if(!notedAnnouncement.isPresent()){
            userNotedAnnouncementService.save(staffNotedAnnouncement);
        }
            String frontendUrl = "http://localhost:4200/noted?announcementId=" + announcementId;
            response.setHeader("Location", frontendUrl);
            return new ResponseEntity<>(HttpStatus.FOUND);
    }

    @GetMapping("/downloadfile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String file) {
        try {
            Map<String, Object> fileData = cloudinaryService.downloadFile(file);
            byte[] fileBytes = (byte[]) fileData.get("fileBytes");
            String contentType = (String) fileData.get("contentType");
            String finalFileName = getFileNameWithVersion(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", finalFileName);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getFileNameWithVersion(String fileName) {
        // Regex to find version patterns like "V1", "V2", etc.
        Pattern pattern = Pattern.compile("V(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);

        String versionSuffix = "";
        if (matcher.find()) {
            versionSuffix = "version" + matcher.group(1);  // Extract version number
        }

        // Remove any version indicators from original name and add the new version suffix
        String baseName = fileName.replaceAll("_V\\d+", ""); // Removes version like "_V1" or "_V2"

        return versionSuffix.isEmpty() ? baseName : versionSuffix + ".xlsx"; // Return the version as file name
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdfFromEmail(@RequestParam("publicId") String publicId, @RequestParam("userEmail") String userEmail, HttpServletResponse response) {
        try {
            // Use the service method to download the file
            Map<String, Object> fileData = cloudinaryService.downloadFile(publicId);
            byte[] fileContent = (byte[]) fileData.get("fileBytes");
            String contentType = (String) fileData.get("contentType");
            String fileName = (String) fileData.get("fileName");

            // Noted User
            Staff notedUser = staffService.findByEmail(userEmail);

            // Find Id From publicId
            Pattern pattern = Pattern.compile("(\\d+)$");
            Matcher matcher = pattern.matcher(publicId);
            if (matcher.find()) {
                Integer announcementId = Integer.valueOf(matcher.group(1));
                Announcement announcement = announcement_service.getAnnouncementById(announcementId)
                        .orElseThrow();

                // Check Already Noted or not
                Optional<StaffNotedAnnouncement> notedConditionAnnouncement = userNotedAnnouncementService
                        .checkNotedOrNot(notedUser, announcement);
                if (!notedConditionAnnouncement.isPresent()) {
                    StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
                    staffNotedAnnouncement.setStaff(notedUser);
                    staffNotedAnnouncement.setAnnouncement(announcement);
                    staffNotedAnnouncement.setNotedAt(Timestamp.valueOf(LocalDateTime.now()));
                    // Save Noted User and Announcement
                    userNotedAnnouncementService.save(staffNotedAnnouncement);
                }
            }

            // Prepare the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            // Return the file content
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException | InterruptedException e) {
            // Handle errors appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/staff-noted/{staffId}")
    public List<StaffNotedResponseDTO> getStaffNotedList(@PathVariable Integer staffId) {
        List<StaffNotedResponseDTO> announcementList = announcement_service.getStaffNoted(staffId);
        return announcementList;
    }

    @GetMapping("/staff-unnoted/{staffId}")
    public List<AnnouncementResponseListDTO> getStaffUnNotedList(@PathVariable Integer staffId) {
        List<AnnouncementResponseListDTO> announcementList = announcement_service.getStaffUnNoted(staffId);
        return announcementList;
    }

    @GetMapping("/staff/{staffId}")
    public List<AnnouncementResponseListDTO> getStaffAnnouncement(@PathVariable Integer staffId) {
        List<AnnouncementResponseListDTO> announcementList = announcement_service.getStaffAnnouncement(staffId);
        return announcementList;
    }

    @GetMapping("/pending-list")
    public List<AnnouncementResponseListDTO> getPendingAnnouncement() {
        return announcement_service.getPendingAnnouncement();
    }
    //Mapping for staffNotedAnnouncement
    @GetMapping("/staff-counts")
    public List<AnnouncementStaffCountDTO> getAnnouncementStaffCounts() {
        return announcement_service.getAnnouncementStaffCounts();
    }


    @GetMapping("/stats")
    public AnnouncementStatsDTO getAnnouncementStats() {
        // Fetch the announcement statistics using the service
        return announcement_service.getAnnouncementStats();
    }

    //Mapping to get all announcement monthly count
    @GetMapping("/monthly-counts")
    public List<MonthlyCountDTO> getMonthlyAnnouncementCounts() {
        return announcement_service.getMonthlyAnnouncementCounts();
    }


    @GetMapping("/versions/{id}")
    public List<AnnouncementVersionDTO> getAnnouncementVersion(@PathVariable Integer id) {
        List<AnnouncementVersionDTO> list = announcement_service.getAnnouncementVersion(id);
        log.info("here is versions" + list);
        return list;
    }

    @GetMapping("request-list")
    public List<RequestAnnouncementResponseDTO> getRequestAnnouncements() {
        return announcement_service.getRequestAnnouncements();
    }

    @GetMapping("approved/{id}")
    public ResponseEntity<Boolean> approveRequestAnnouncement(@PathVariable("id") Integer id) {
        try {
            announcement_service.approvedRequestAnnouncement(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("reject/{id}")
    public ResponseEntity<Boolean> rejectRequestAnnouncement(@PathVariable("id") Integer id) {
        try {
            announcement_service.rejectRequestAnnouncement(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
