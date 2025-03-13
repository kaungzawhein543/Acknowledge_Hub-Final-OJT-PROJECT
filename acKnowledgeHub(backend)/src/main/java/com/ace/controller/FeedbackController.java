package com.ace.controller;

import com.ace.announcements.feedbackAndReply.dtos.FeedbackListResponseDTO;
import com.ace.announcements.feedbackAndReply.dtos.FeedbackRequestDTO;
import com.ace.announcements.feedbackAndReply.dtos.FeedbackResponseListDTO;
import com.ace.entity.Announcement.Announcement;
import com.ace.entity.feedbackAndReply.Feedback;
import com.ace.entity.common.Notification;
import com.ace.entity.organization.Staff;
import com.ace.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private  final StaffService staffService;
    private final AnnouncementService announcementService;
    private final BlogService blogService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMapper mapper;
    private final ReportService reportService;

    public FeedbackController(FeedbackService feedbackService, StaffService staffService, AnnouncementService announcementService, BlogService blogService, NotificationService notificationService, SimpMessagingTemplate simpMessagingTemplate, ModelMapper mapper, ReportService reportService) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.announcementService = announcementService;
        this.blogService = blogService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mapper = mapper;
        this.reportService = reportService;
    }

    @PostMapping("/all/sendFeedback")
    public ResponseEntity<Feedback> addFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO){
        Feedback feedback = new Feedback();
        Staff staff = staffService.findById(feedbackRequestDTO.getStaffId());
        Announcement announcement = announcementService.getAnnouncementById(feedbackRequestDTO.getAnnouncementId()).orElseThrow();
        feedback.setAnnouncement(announcement);
        feedback.setStaff(staff);
        feedback.setContent(feedbackRequestDTO.getContent());
        Feedback feedback2 = feedbackService.addFeedback(feedback);

        List<FeedbackListResponseDTO> feedbacks = feedbackService.getFeedbackByAnnouncement(feedback2.getAnnouncement().getId());

        String description ="";
        if(feedbacks.size() == 1) {
            description = feedback.getStaff().getName() + "  ask a question!Check it out!";
        }else if (feedbacks.size() > 1){
            description = feedback.getStaff().getName() + " and "+feedbacks.size()+" others are ask a question!Check it out!";
        }
        String url =  "/acknowledgeHub/announcement/detail/"+ Base64.getEncoder().encodeToString(feedback.getAnnouncement().getId().toString().getBytes());
        Notification notification = blogService.createNotification(feedback.getAnnouncement(), feedback.getAnnouncement().getCreateStaff(), description,url);
        notificationService.sendNotification(blogService.convertToDTO(notification));

        FeedbackResponseListDTO sendNewFeedbackDTO = mapper.map(feedback2,FeedbackResponseListDTO.class);
        sendNewFeedbackDTO.setPhotoPath(feedback2.getStaff().getPhotoPath());
        simpMessagingTemplate.convertAndSend("/topic/feedback/", sendNewFeedbackDTO);

        return ResponseEntity.ok(feedback2);
    }


    @GetMapping("/all/all-by-announcement/{id}")
    public List<FeedbackListResponseDTO> getFeedBackListByAnnouncementId(@PathVariable Integer id){
        return feedbackService.getFeedbackByAnnouncement(id);
    }

    @GetMapping("/HRM/list/{id}")
    public List<FeedbackResponseListDTO> getList(@PathVariable Integer id){
        return feedbackService.getFeedbackList(id);
    }

    @GetMapping("/all/report")
    public ResponseEntity<byte[]> generateFeedbackReport(@RequestParam Integer announcementId, @RequestParam String format) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        reportService.generateFeedbackReport(announcementId, format, new AsyncCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                future.complete(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        try {
            byte[] reportData = future.join();

            String contentType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF_VALUE : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            String fileExtension = "pdf".equalsIgnoreCase(format) ? ".pdf" : ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=feedback_report" + fileExtension)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(reportData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
