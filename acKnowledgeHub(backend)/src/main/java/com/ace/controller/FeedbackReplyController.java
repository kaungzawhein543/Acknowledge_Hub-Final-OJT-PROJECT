package com.ace.controller;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackReplyRequestDTO;
import com.ace.entity.Feedback;
import com.ace.entity.FeedbackReply;
import com.ace.entity.Notification;
import com.ace.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/feedback-reply")
public class FeedbackReplyController {


    private final FeedbackReplyService feedbackReplyService;
    private final FeedbackService feedbackService;
    private final StaffService staffService;
    private final BlogService blogService;
    private final NotificationService notificationService;

    public FeedbackReplyController(FeedbackReplyService feedbackReplyService, FeedbackService feedbackService, StaffService staffService, BlogService blogService, NotificationService notificationService) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.feedbackReplyService = feedbackReplyService;
        this.blogService = blogService;
        this.notificationService = notificationService;
    }

    @PostMapping("/all/saveFeedbackReply")
    public ResponseEntity<FeedbackReply> saveFeedbackReply(@RequestBody FeedbackReplyRequestDTO feedbackReplyRequestDTO){
//        System.out.println("reply text"+feedbackReplyRequestDTO.getReplyText());
//        System.out.println("reply by "+ feedbackReplyRequestDTO.getReplyBy());
//        System.out.println("feedback id "+ feedbackReplyRequestDTO.getFeedbackId());
        FeedbackReply feedbackReply = new FeedbackReply();
        feedbackReply.setContent(feedbackReplyRequestDTO.getReplyText());
        feedbackReply.setFeedback(feedbackService.findById(feedbackReplyRequestDTO.getFeedbackId()).get());
        feedbackReply.setStaff(staffService.findById(feedbackReplyRequestDTO.getReplyBy()));
        FeedbackReply savedFeedbackReply = feedbackReplyService.saveFeedbackReply(feedbackReply);


        String description  = savedFeedbackReply.getFeedback().getAnnouncement().getCreateStaff().getName()+ " reply you in announcement!Check it out!";

        Notification notification = blogService.createNotification(savedFeedbackReply.getFeedback().getAnnouncement(), savedFeedbackReply.getFeedback().getStaff(), description);
        notificationService.sendNotification(blogService.convertToDTO(notification));
        return ResponseEntity.ok(savedFeedbackReply);
    }

//    @PutMapping
//    public ResponseEntity<FeedbackReply> updateFeedbackReply(@RequestBody FeedbackReply feedbackReply){
//        FeedbackReply updateFeedbackReply = feedbackReplyService.updateFeedbackReply(feedbackReply);
//        return  ResponseEntity.ok(updateFeedbackReply);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<FeedbackReply> deleteFeedbackReply(@PathVariable Integer id ){
//        FeedbackReply feedbackReply = feedbackReplyService.deleteFeedbackReply(id);
//        return ResponseEntity.ok(feedbackReply);
//    }
}
