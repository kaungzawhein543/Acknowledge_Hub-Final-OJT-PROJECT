package com.ace.repository.feedbackAndReply;

import com.ace.dto.FeedbackResponseListDTO;
import com.ace.entity.feedbackAndReply.Feedback;
import com.ace.utility.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends BaseRepository<Feedback,Integer> {

    @Query(value = "select f.id, f.content, s.name, rp.content, " +
            "(select s2.name from staff s2 where s2.id = rp.staff_id), " +
            "f.created_at, rp.created_at, s.photo_path, s2.photo_path " +
            "from feedback f " +
            "join staff s on s.id = f.staff_id " +
            "join announcement a on a.id = f.announcement_id " +
            "left join feedback_reply rp on rp.feedback_id = f.id " +
            "left join staff s2 on s2.id = rp.staff_id " + // Join again to get the rp staff
            "where f.announcement_id = ?1 " +
            "ORDER BY f.created_at desc",
            nativeQuery = true)
    List<Object[]> getFeedbackAndReplyByAnnouncement(Integer id);

    @Query("select new com.ace.dto.FeedbackResponseListDTO(f.id, f.content, f.createdAt, s.name, f.announcement.title, f.announcement.id ,s.company.name, s.department.name, s.position.name, s.photoPath) " +
            "from Feedback f " +
            "Join f.staff s " +
            "where f.announcement.id = ?1")
    List<FeedbackResponseListDTO> getAllFeedbackList(Integer id);

}
