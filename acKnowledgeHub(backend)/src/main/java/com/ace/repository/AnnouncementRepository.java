package com.ace.repository;

import com.ace.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {
    List<Announcement> findByStatus(String status);

    @Modifying
    @Transactional
    @Query("update Announcement a set a.status = 'inactive' where a.id = ?1")
    void softDeleteAnnouncement(Integer id);

    @Query("SELECT a FROM Announcement a JOIN a.staff s WHERE s.id = :staffId")
    List<Announcement> findAnnouncementsByStaffId(@Param("staffId") int staffId);
}
