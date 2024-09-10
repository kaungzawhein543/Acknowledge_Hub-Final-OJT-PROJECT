package com.ace.service;

import com.ace.entity.Feedback;
import com.ace.entity.FeedbackReply;
import com.ace.repository.FeedbackReplyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedbackReplyService {
    private final FeedbackReplyRepository feedbackReplyRepository;

    public FeedbackReplyService(FeedbackReplyRepository feedbackReplyRepository) {
        this.feedbackReplyRepository = feedbackReplyRepository;
    }

    public FeedbackReply saveFeedbackReply(FeedbackReply feedbackReply){
        return feedbackReplyRepository.save(feedbackReply);
    }

//    public FeedbackReply updateFeedbackReply(FeedbackReply feedbackReply){
//        Optional<FeedbackReply> feedbackReply1 = feedbackReplyRepository.findById(feedbackReply.getId());
//        feedbackReply1.get().setContent(feedbackReply.getContent());
//        feedbackReply1.get().setStatus("active");
//       return feedbackReplyRepository.save(feedbackReply1.get());
//    }
//
//    public FeedbackReply deleteFeedbackReply(Integer id){
//        return feedbackReplyRepository.deleteFeedbackReply(id);
//    }

}
