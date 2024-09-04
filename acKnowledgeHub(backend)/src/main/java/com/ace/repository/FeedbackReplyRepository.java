package com.ace.repository;

import com.ace.entity.FeedbackReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackReplyRepository extends JpaRepository<FeedbackReply , Integer> {

}
