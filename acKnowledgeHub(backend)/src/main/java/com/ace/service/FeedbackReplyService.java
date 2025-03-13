package com.ace.service;

import com.ace.entity.feedbackAndReply.FeedbackReply;
import com.ace.repository.feedbackAndReply.FeedbackReplyRepository;
import org.springframework.stereotype.Service;

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
