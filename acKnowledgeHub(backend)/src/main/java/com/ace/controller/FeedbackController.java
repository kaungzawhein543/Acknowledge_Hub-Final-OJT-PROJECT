package com.ace.controller;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackRequestDTO;
import com.ace.dto.FeedbackResponseDTO;
import com.ace.dto.FeedbackResponseListDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Feedback;
import com.ace.entity.Notification;
import com.ace.entity.Staff;
import com.ace.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private  final StaffService staffService;
    private final AnnouncementService announcementService;
    private final BlogService blogService;
    private final NotificationService notificationService;

    public FeedbackController(FeedbackService feedbackService, StaffService staffService, AnnouncementService announcementService, BlogService blogService, NotificationService notificationService) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.announcementService = announcementService;
        this.blogService = blogService;
        this.notificationService = notificationService;
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

        String description;
        if(feedbacks.size() > 0){
            description = feedback.getStaff().getName() + " and "+feedbacks.size()+" others are ask a question!Check it out!";
        }else{
            description = feedback.getStaff().getName() + "  ask a question!Check it out!";
        }
        Notification notification = blogService.createNotification(feedback.getAnnouncement(), feedback.getAnnouncement().getCreateStaff(), description);
        notificationService.sendNotification(blogService.convertToDTO(notification));
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
}
