package com.ace.repository.feedbackAndReply;

import com.ace.entity.feedbackAndReply.FeedbackReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackReplyRepository extends JpaRepository<FeedbackReply , Integer> {

}
