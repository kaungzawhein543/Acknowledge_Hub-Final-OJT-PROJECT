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

}
