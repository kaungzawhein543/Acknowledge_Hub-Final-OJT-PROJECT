package com.ace.repository;

import com.ace.dto.AnnouncementResponseDTO;
import com.ace.dto.StaffNotedResponseDTO;
import com.ace.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {
    List<Announcement> findByStatus(String status);

    @Modifying
    @Transactional
    @Query("update Announcement a set a.status = 'inactive' where a.id = ?1")
    void softDeleteAnnouncement(Integer id);


    @Query("select NEW com.ace.dto.StaffNotedResponseDTO(a.title , a.description, a.scheduleAt, sn.notedAt, a.file) from Announcement a " +
            "JOIN StaffNotedAnnouncement sn ON a.id = sn.announcement.id " +
            "WHERE sn.staff.id = :staffId")
    List<StaffNotedResponseDTO> getStaffNoted(@Param("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name, a.file, a.category.name) " +
            "from Announcement a " +
            "join a.staff s "+
            "Left JOIN StaffNotedAnnouncement sn ON sn.staff.id = :staffId and sn.announcement.id = a.id " +
            "WHERE s.id = :staffId AND sn.id IS NULL ")
    List<AnnouncementResponseDTO> getNotStaffNoted(@RequestParam("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name, a.file,  a.category.name) " +
            "from Announcement a " +
            "join a.group g "+
            "JOIN g.staff s  " +
            "left JOIN StaffNotedAnnouncement sn ON sn.staff.id = :staffId and sn.announcement.id = a.id " +
            "WHERE s.id = :staffId AND sn.id IS NULL ")
    List<AnnouncementResponseDTO> getNotStaffNotedGroup(@RequestParam("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name , a.file , a.category.name) " +
            "from Announcement a " +
            "JOIN a.staff s " +
            "WHERE s.id = :staffId")
    List<AnnouncementResponseDTO> getStaffAnnouncement(@Param("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name , a.file , a.category.name) " +
            "from Announcement a " +
            "JOIN a.group g " +
            "join g.staff s "+
            "WHERE s.id = :staffId")
    List<AnnouncementResponseDTO> getStaffAnnouncementGroup(@Param("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name , a.file,  a.category.name) " +
            "from Announcement a WHERE a.isPublished = true ")
    List<AnnouncementResponseDTO> getPendingAnnouncement();

}
