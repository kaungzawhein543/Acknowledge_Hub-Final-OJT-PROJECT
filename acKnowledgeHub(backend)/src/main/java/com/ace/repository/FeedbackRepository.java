package com.ace.repository;

import com.ace.dto.FeedbackListResponseDTO;
import com.ace.dto.FeedbackResponseListDTO;
import com.ace.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {

//    @Modifying
//    @Transactional
//    @Query("update Feedback f set f.status = 'inactive' where f.id = :id")
//    void deactivateFeedback(@Param("id") Integer id);

//    @Query("SELECT NEW com.ace.dto.FeedbackListResponseDTO(f.id as feedbackId, f.content, f.staff.name as staffName,COALESCE( rp.content, 'reply'), COALESCE(rp.staff.name, 'no user')) " +
//            "FROM Feedback f " +
//            "LEFT JOIN FeedbackReply rp ON rp.feedback.id = f.id " +
//            "WHERE f.announcement.id = ?1")
//    List<FeedbackListResponseDTO> getFeedbackListByAnnouncement(Integer id);

    @Query(value = "select f.id , f.content , s.name, rp.content,(select s.name from staff s where s.id=rp.staff_id ), f.created_at, rp.created_at " +
            "from feedback f " +
            "join staff s on s.id = f.staff_id " +
            "join announcement a on a.id = f.announcement_id " +
            "left join feedback_reply rp on rp.feedback_id = f.id where announcement_id = ?1 ORDER BY f.created_at desc",
            nativeQuery = true)
    List<Object[]> getFeedbackAndReplyByAnnouncement(Integer id);

    @Query("select new com.ace.dto.FeedbackResponseListDTO(f.id, f.content, f.created_at, s.name, f.announcement.title, s.company.name, s.department.name, s.position.name) " +
            "from Feedback f " +
            "Join f.staff s " +
            "where f.announcement.id = ?1")
    List<FeedbackResponseListDTO> getAllFeedbackList(Integer id);

}
