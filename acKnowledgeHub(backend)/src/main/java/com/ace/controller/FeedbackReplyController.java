package com.ace.controller;

import com.ace.dto.FeedBackReplyDTO;
import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackReplyRequestDTO;
import com.ace.dto.TypingStatusMessage;
import com.ace.entity.Feedback;
import com.ace.entity.FeedbackReply;
import com.ace.entity.Notification;
import com.ace.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/feedback-reply")
public class FeedbackReplyController {


    private final FeedbackReplyService feedbackReplyService;
    private final FeedbackService feedbackService;
    private final StaffService staffService;
    private final BlogService blogService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ModelMapper mapper;


    public FeedbackReplyController(FeedbackReplyService feedbackReplyService, FeedbackService feedbackService, StaffService staffService, BlogService blogService, NotificationService notificationService, SimpMessagingTemplate simpMessagingTemplate, ModelMapper mapper) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.feedbackReplyService = feedbackReplyService;
        this.blogService = blogService;
        this.notificationService = notificationService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mapper = mapper;
    }

    @PostMapping("/all/saveFeedbackReply")
    public ResponseEntity<FeedbackReply> saveFeedbackReply(@RequestBody FeedbackReplyRequestDTO feedbackReplyRequestDTO){
        FeedbackReply feedbackReply = new FeedbackReply();
        feedbackReply.setContent(feedbackReplyRequestDTO.getReplyText());
        feedbackReply.setFeedback(feedbackService.findById(feedbackReplyRequestDTO.getFeedbackId()).get());
        feedbackReply.setStaff(staffService.findById(feedbackReplyRequestDTO.getReplyBy()));
        FeedbackReply savedFeedbackReply = feedbackReplyService.saveFeedbackReply(feedbackReply);


        String description  = savedFeedbackReply.getFeedback().getAnnouncement().getCreateStaff().getName()+ " reply you in announcement!Check it out!";
        String url =  "/acknowledgeHub/announcement/detail/"+ Base64.getEncoder().encodeToString(savedFeedbackReply.getFeedback().getAnnouncement().getId().toString().getBytes());
        Notification notification = blogService.createNotification(savedFeedbackReply.getFeedback().getAnnouncement(), savedFeedbackReply.getFeedback().getStaff(), description,url);
        notificationService.sendNotification(blogService.convertToDTO(notification));

        Optional<Feedback> feedback =  feedbackService.findById(savedFeedbackReply.getFeedback().getId());
        FeedBackReplyDTO feedBackReplyDTO = mapFeedbackToReplyDTO(feedback.get(),savedFeedbackReply);
        feedBackReplyDTO.setPhotoPath(savedFeedbackReply.getFeedback().getStaff().getPhotoPath());
        feedBackReplyDTO.setReplyPhotoPath(savedFeedbackReply.getStaff().getPhotoPath());
        simpMessagingTemplate.convertAndSend("/topic/feedback/", feedBackReplyDTO);
        return ResponseEntity.ok(savedFeedbackReply);
    }
    public FeedBackReplyDTO mapFeedbackToReplyDTO(Feedback feedback,FeedbackReply reply) {
        FeedBackReplyDTO feedBackReplyDTO = new FeedBackReplyDTO();

        // Mapping the common fields
        feedBackReplyDTO.setId(feedback.getId());
        feedBackReplyDTO.setContent(feedback.getContent());
        feedBackReplyDTO.setCreatedAt(feedback.getCreated_at());  // Convert LocalDateTime to String
        feedBackReplyDTO.setReplyBy(reply.getStaff().getName());
        feedBackReplyDTO.setReply(reply.getContent());
        feedBackReplyDTO.setReplyAt(reply.getCreated_at());
        feedBackReplyDTO.setPhotoPath(feedback.getStaff().getPhotoPath());
        // Mapping nested staff fields
        if (feedback.getStaff() != null) {
            feedBackReplyDTO.setStaffName(feedback.getStaff().getName());
        }

        // Mapping nested announcement fields
        if (feedback.getAnnouncement() != null) {
            feedBackReplyDTO.setAnnouncementId(feedback.getAnnouncement().getId());
        }

        return feedBackReplyDTO;
    }

    @MessageMapping("/typing")
    public void handleTypingStatus(@Payload TypingStatusMessage typingStatus) {
        // Broadcast typing status to all clients
        System.out.println(typingStatus.getStaffId()+"He is typing");
        System.out.println(typingStatus.isTyping());
        simpMessagingTemplate.convertAndSend("/topic/typing", typingStatus);
    }

}
