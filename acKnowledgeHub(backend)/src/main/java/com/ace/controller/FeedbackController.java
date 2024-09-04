package com.ace.controller;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackRequestDTO;
import com.ace.dto.FeedbackResponseDTO;
import com.ace.dto.FeedbackResponseListDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Feedback;
import com.ace.entity.Staff;
import com.ace.service.AnnouncementService;
import com.ace.service.FeedbackService;
import com.ace.service.StaffService;
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

    public FeedbackController(FeedbackService feedbackService, StaffService staffService, AnnouncementService announcementService) {
        this.feedbackService = feedbackService;
        this.staffService = staffService;
        this.announcementService = announcementService;

    }

    @PostMapping
    public ResponseEntity<Feedback> addFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO){
        Feedback feedback = new Feedback();
        Staff staff = staffService.findById(feedbackRequestDTO.getStaffId());
        Optional<Announcement> announcement = announcementService.getAnnouncementById(feedbackRequestDTO.getAnnouncementId());
        feedback.setAnnouncement(announcement.get());
        feedback.setStaff(staff);
        feedback.setContent(feedbackRequestDTO.getContent());
        Feedback feedback2 = feedbackService.addFeedback(feedback);
        return ResponseEntity.ok(feedback2);
    }

//    @PutMapping
//    public ResponseEntity<Feedback> updateFeedback(@RequestBody Feedback feedback){
//        Feedback feedback2 = feedbackService.updateFeedback(feedback);
//        return ResponseEntity.ok(feedback2);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteFeedback(@PathVariable Integer id){
//        try {
//            feedbackService.deleteFeedback(id);
//            return ResponseEntity.ok("Feedback deleted successfully");
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid feedback ID");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete feedback");
//        }
//    }

    @GetMapping("/all-by-announcement/{id}")
    public List<FeedbackListResponseDTO> getFeedBackListByAnnouncementId(@PathVariable Integer id){
        return feedbackService.getFeedbackByAnnouncement(id);
    }

    @GetMapping("list/{id}")
    public List<FeedbackResponseListDTO> getList(@PathVariable Integer id){
        return feedbackService.getFeedbackList(id);
    }
}
