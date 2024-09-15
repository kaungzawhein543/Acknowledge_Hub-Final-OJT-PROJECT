package com.ace.controller;

import com.ace.dto.*;
import com.ace.entity.*;
import com.ace.repository.StaffRepository;
import com.ace.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable int id) {
        Announcement announcement = announcement_service.getAnnouncementById(id).orElseThrow();
        return ResponseEntity.ok(announcement);
    }

    @GetMapping("/getPublishedAnnouncements")
    public ResponseEntity<List<AnnouncementListDTO>> getPublishedAnnouncements() {
        List<AnnouncementListDTO> publishedAnnouncements = announcement_service.getPublishedAnnouncements();
        return ResponseEntity.ok(publishedAnnouncements);
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


            List<Group> groupsForAnnounce = new ArrayList<>();
            List<Staff> staffForAnnounce = new ArrayList<>();

            //Announce People
            if (request.getGroupStatus() == 1) {
                groupsForAnnounce = groupService.findGroupsByIds(groupIds);
                // Initialize staff list before async operation
                for (Group group : groupsForAnnounce) {
                    group.getStaff().size(); // Force initialization
                }
                staffForAnnounce = null;
            } else {
                staffForAnnounce = staffService.findStaffsByIds(userIds);
                groupsForAnnounce = null;
            }

            // Map DTO to Entity
            Announcement announcement = mapper.map(request, Announcement.class);
            announcement.setFile("N/A");
            announcement.setCreateStaff(user);
            announcement.setCategory(request.getCategory());


            // If ScheduledAt is null assign default
            if (announcement.getScheduleAt() == null) {
                LocalDateTime publishDateTime = LocalDateTime.now();
                announcement.setScheduleAt(publishDateTime);
            }

            //initialize the project Announce people
            if(groupsForAnnounce != null) {
                System.out.println("Group size is "+groupsForAnnounce.size());
                announcement.setGroup(groupsForAnnounce);
            }
            if(staffForAnnounce != null){
                System.out.println("Staff size is "+staffForAnnounce.size());
                announcement.setStaff(staffForAnnounce);
            }



            // Save the announcement
            Announcement savedAnnouncement = announcement_service.createAnnouncement(announcement);


//        ============================== TO FIX ====================================

            //Publish the post
            if(request.getForRequest() == 1){
                if(request.getScheduleAt() != null){
                    LocalDateTime requestAnnounceScheduleTime = request.getScheduleAt();
                    savedAnnouncement.setScheduleAt(requestAnnounceScheduleTime);
                    blogService.createPost(savedAnnouncement);
                }else{
                    savedAnnouncement.setPublished(true);
                    announcement_service.updateAnnouncement( savedAnnouncement.getId(),savedAnnouncement);
                }
            }else{
                if(request.getScheduleAt() != null){
                    LocalDateTime requestAnnounceScheduleTime = request.getScheduleAt();
                    savedAnnouncement.setScheduleAt(requestAnnounceScheduleTime);
                    blogService.createPost(savedAnnouncement);
                }else{
                    savedAnnouncement.setPublished(true);
                    announcement_service.updateAnnouncement(savedAnnouncement.getId(),savedAnnouncement);
                }
            }
//            ==========================================================================

            if (files != null && !files.isEmpty()) {
                MultipartFile file = files.get(0);
                CompletableFuture<Map<String, Object>> uploadFuture;
                if(request.getId() != 0){
                    uploadFuture = cloudinaryService.uploadFile(file, "Announce" + request.getId());
                }else{
                    uploadFuture = cloudinaryService.uploadFile(file, "Announce" + savedAnnouncement.getId());
                }


                List<Staff> finalStaffForAnnounce = staffForAnnounce;
                List<Group> finalGroupForAnnounce = groupsForAnnounce;


                uploadFuture.thenAccept(uploadResult -> {
                    try {
                        String fileName = uploadResult.get("public_id").toString();

                        // Send Announcement to Telegram & email
                        if (request.getGroupStatus() != 1) {
                            for (Staff AnnounceStaff : finalStaffForAnnounce) {
                                if (AnnounceStaff != null) {
                                    botService.sendFile(AnnounceStaff.getChatId(), file, savedAnnouncement.getId());
                                }
                                if (AnnounceStaff.getEmail() != null && !AnnounceStaff.getEmail().isEmpty()) {
                                    emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have a new Announcement", file, fileName);
                                }
                            }
                        } else {
                            for (Group group : finalGroupForAnnounce) {
                                if (group != null) {
                                    List<Staff> staffFromGroup = group.getStaff(); // Accessing initialized collection
                                    for (Staff AnnounceStaff : staffFromGroup) {
                                        if (AnnounceStaff.getChatId() != null) {
                                            botService.sendFile(AnnounceStaff.getChatId(), file, savedAnnouncement.getId());
                                        }
                                        if (AnnounceStaff.getEmail() != null && !AnnounceStaff.getEmail().isEmpty()) {
                                            emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have a new Announcement", file, fileName);
                                        }
                                    }
                                }
                            }
                        }

                        // Update announcement with the file name
                        savedAnnouncement.setFile(fileName);
                        Announcement updateFileUrlAnnounce = announcement_service.updateFileUrl(savedAnnouncement);

                        // Check if the announcement is for a request
//                    if (forRequest != null) {
//                        AnnouncementForReq announcementForReq = new AnnouncementForReq();
//                        announcementForReq.setAnnouncement(updateFileUrlAnnounce);

//                        ReqAnnouncement reqAnnouncement = reqAnnouncementService.getById(requestId);
//                        announcementForReq.setRequestAnnouncement(reqAnnouncement);

//                        announcementForReqService.createAnnouncementForReq(announcementForReq);
//                    }
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



    @PutMapping("/{id}")
    public ResponseEntity<String> updateAnnouncement(@RequestBody Announcement announcement, @PathVariable int id) {
        try {
            // Update announcement
            Announcement updatedAnnouncement = announcement_service.updateAnnouncement(id, announcement);
            if (updatedAnnouncement != null) {
                LocalDateTime newPublishDateTime = announcement.getScheduleAt();
                blogService.updateScheduledPost(id, newPublishDateTime);

                // Generate PDF asynchronously
                reportService.generateAnnouncementFile(updatedAnnouncement.getId(), "Announce" + updatedAnnouncement.getId(), new AsyncCallback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] pdfBytes) {
                        try {
                            MultipartFile pdfFile = new MockMultipartFile("announcement.pdf", "announcement.pdf", "application/pdf", pdfBytes);
                            CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadFile(pdfFile, "Announce" + updatedAnnouncement.getId());

                            uploadFuture.thenAccept(uploadResult -> {
                                try {
                                    String fileName = uploadResult.get("public_id").toString();

                                    Announcement announce = new Announcement();
                                    announce.setId(updatedAnnouncement.getId());
                                    announce.setFile(fileName);
                                    announcement_service.updateFileUrl(announce);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).exceptionally(uploadEx -> {
                                uploadEx.printStackTrace();
                                return null;
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        // Handle error
                        throwable.printStackTrace();
                    }
                });

                return ResponseEntity.ok("Announcement Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Announcement Fail To Update");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating announcement");
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable int id) {
        System.out.println("hay");
        try {
            cloudinaryService.deleteFile("Announce" + id);//is only accept multipartfile
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        announcement_service.deleteAnnouncement(id);
        return ResponseEntity.ok("Announcement Deleted Successfully");
    }

    @GetMapping("/getAnnounceFile/{publicId}")
    public Map getFile(@PathVariable String publicId) {
        return cloudinaryService.getFile(publicId);
    }

    @GetMapping("/download/{publicId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String publicId) {
        try {
            byte[] pdfBytes = cloudinaryService.downloadPdf(publicId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", publicId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdfFromEmail(@RequestParam("publicId") String publicId,@RequestParam("userEmail") String userEmail, HttpServletResponse response) {
        try {
            // Use the service method to download the PDF file
            byte[] pdfContent = cloudinaryService.downloadPdf(publicId);

            //Noted User
            Staff NotedUser = staffService.findByEmail(userEmail);

            //Find Id From publicId
            Pattern pattern = Pattern.compile("(\\d+)$");
            Matcher matcher = pattern.matcher(publicId);
            if (matcher.find()) {
                Integer AnnouncementId = Integer.valueOf(matcher.group(1));
                Announcement announcement =  announcement_service.getAnnouncementById(AnnouncementId).orElseThrow();

                //Check Already Noted or not
                Optional<StaffNotedAnnouncement> NotedConditionAnnouncement = userNotedAnnouncementService.checkNotedOrNot(NotedUser,announcement);
                if(!NotedConditionAnnouncement.isPresent()){
                    StaffNotedAnnouncement staffNotedAnnouncement = new StaffNotedAnnouncement();
                    staffNotedAnnouncement.setStaff(NotedUser);
                    staffNotedAnnouncement.setAnnouncement(announcement);
                    staffNotedAnnouncement.setNotedAt(Timestamp.valueOf(LocalDateTime.now()));
                    //Save Noted User and Announcement
                    userNotedAnnouncementService.save(staffNotedAnnouncement);
                }
            }

            // Prepare the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + publicId + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            // Return the PDF file content
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } catch (IOException | InterruptedException e) {
            // Handle errors appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/staff-noted/{staffId}")
    public List<StaffNotedResponseDTO> getStaffNotedList(@PathVariable Integer staffId){
        List<StaffNotedResponseDTO> announcementList = announcement_service.getStaffNoted(staffId);
        return announcementList;
    }

    @GetMapping("/staff-unnoted/{staffId}")
    public List<AnnouncementResponseDTO> getStaffUnNotedList(@PathVariable Integer staffId){
        List<AnnouncementResponseDTO> announcementList = announcement_service.getStaffUnNoted(staffId);
        return announcementList;
    }

    @GetMapping("/staff/{staffId}")
    public List<AnnouncementResponseDTO> getStaffAnnouncement(@PathVariable Integer staffId){
        List<AnnouncementResponseDTO> announcementList = announcement_service.getStaffAnnouncement(staffId);
        return announcementList;
    }

    @GetMapping("/pending-list")
    public List<AnnouncementResponseDTO> getPendingAnnouncement(){
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


}
