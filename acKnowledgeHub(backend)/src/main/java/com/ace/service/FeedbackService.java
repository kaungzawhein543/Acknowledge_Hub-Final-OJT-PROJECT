package com.ace.service;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackResponseListDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Feedback;
import com.ace.entity.Notification;
import com.ace.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final BlogService blogService;
    private final NotificationService notificationService;

    public FeedbackService(FeedbackRepository feedbackRepository, BlogService blogService, NotificationService notificationService) {
        this.feedbackRepository = feedbackRepository;
        this.blogService = blogService;
        this.notificationService = notificationService;
    }

    public Feedback addFeedback(Feedback feedback){
        return  feedbackRepository.save(feedback);
    }

//    public Feedback updateFeedback(Feedback feedback){
//        Optional<Feedback> existFeedback = feedbackRepository.findById(feedback.getId());
//        return feedbackRepository.save(existFeedback.get());
//    }

//    public void deleteFeedback(Integer id){
//       feedbackRepository.deactivateFeedback(id);
//    }

    public List<FeedbackListResponseDTO> getFeedbackByAnnouncement(Integer id){
        List<Object[]> results = feedbackRepository.getFeedbackAndReplyByAnnouncement(id);

        return results.stream()
                .map(result -> new FeedbackListResponseDTO(
                        (Integer) result[0],       // feedbackId
                        (String) result[1],     // feedbackContent
                        (String) result[2],     // staffName
                        (String) result[3],     // replyContent
                        (String) result[4],        // replyStaffId
                        (Date) result[5],
                        (Date) result[6],
                        (String) result[7],
                        (String) result[8]
                ))
                .collect(Collectors.toList());
    }

    public Optional<Feedback> findById(Integer id ){
        return feedbackRepository.findById(id);
    }

    public List<FeedbackResponseListDTO> getFeedbackList(Integer id){
        System.out.println(feedbackRepository.getAllFeedbackList(id));
        return feedbackRepository.getAllFeedbackList(id);
    }
}
