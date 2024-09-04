package com.ace.controller;

import com.ace.dto.FeedbackReplyRequestDTO;
import com.ace.entity.Feedback;
import com.ace.entity.FeedbackReply;
import com.ace.service.AnnouncementService;
import com.ace.service.FeedbackReplyService;
import com.ace.service.FeedbackService;
import com.ace.service.StaffService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/feedback-reply")
public class FeedbackReplyController {


    private final FeedbackReplyService feedbackReplyService;
    private final FeedbackService feedbackService;
    private final StaffService staffService;

    public FeedbackReplyController(FeedbackReplyService feedbackReplyService, FeedbackService feedbackService, StaffService staffService) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.feedbackReplyService = feedbackReplyService;
    }

    @PostMapping
    public ResponseEntity<FeedbackReply> saveFeedbackReply(@RequestBody FeedbackReplyRequestDTO feedbackReplyRequestDTO){
        System.out.println("reply text"+feedbackReplyRequestDTO.getReplyText());
        System.out.println("reply by "+ feedbackReplyRequestDTO.getReplyBy());
        System.out.println("feedback id "+ feedbackReplyRequestDTO.getFeedbackId());
        FeedbackReply feedbackReply = new FeedbackReply();
        feedbackReply.setContent(feedbackReplyRequestDTO.getReplyText());
        feedbackReply.setFeedback(feedbackService.findById(feedbackReplyRequestDTO.getFeedbackId()).get());
        feedbackReply.setStaff(staffService.findById(feedbackReplyRequestDTO.getReplyBy()));
        FeedbackReply savedFeedbackReply = feedbackReplyService.saveFeedbackReply(feedbackReply);
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
