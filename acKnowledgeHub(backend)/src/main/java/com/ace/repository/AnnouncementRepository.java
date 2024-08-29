package com.ace.repository;

import com.ace.dto.AnnouncementStaffCountDTO;
import com.ace.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT new com.ace.dto.AnnouncementStaffCountDTO(a.id, a.title, a.created_at, COUNT(s.id)) " +
            "FROM Announcement a " +
            "LEFT JOIN StaffNotedAnnouncement s ON a.id = s.announcement.id " +
            "GROUP BY a.id, a.title, a.created_at")
    List<AnnouncementStaffCountDTO> findAnnouncementStaffCounts();



}
