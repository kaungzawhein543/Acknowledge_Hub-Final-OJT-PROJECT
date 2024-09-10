package com.ace.repository;

import com.ace.dto.AnnouncementListDTO;
import com.ace.dto.AnnouncementResponseListDTO;
import com.ace.dto.AnnouncementVersionDTO;
import com.ace.dto.StaffNotedResponseDTO;
import com.ace.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {
    //List<Announcement> findByStatus(String status);

    @Query("select new com.ace.dto.AnnouncementListDTO(a.id, a.title, a.description, a.createStaff.name, a.category.name, a.status, a.created_at, a.scheduleAt, a.groupStatus) " +
            "from Announcement a where a.status = 'active' order by a.scheduleAt DESC")
    List<AnnouncementListDTO> getAnnouncementList();


    @Modifying
    @Transactional
    @Query("update Announcement a set a.status = 'inactive' where a.id = ?1")
    void softDeleteAnnouncement(Integer id);

    @Query("SELECT a FROM Announcement a JOIN a.staff s WHERE s.id = :staffId")
    List<Announcement> findAnnouncementsByStaffId(@Param("staffId") int staffId);

    @Query("select NEW com.ace.dto.StaffNotedResponseDTO(a.id , a.title , a.description, a.scheduleAt, sn.notedAt, a.createStaff.name) from Announcement a " +
            "JOIN StaffNotedAnnouncement sn ON a.id = sn.announcement.id " +
            "WHERE sn.staff.id = :staffId")
    List<StaffNotedResponseDTO> getStaffNoted(@Param("staffId") Integer staffId);

    @Query("SELECT NEW com.ace.dto.AnnouncementResponseListDTO(a.id, a.title, a.description, a.scheduleAt, a.createStaff.name, a.category.name) " +
            "FROM Announcement a " +
            "JOIN a.staff s " +
            "LEFT JOIN StaffNotedAnnouncement sn ON sn.staff.id = :staffId AND sn.announcement.id = a.id " +
            "WHERE s.id = :staffId AND sn.id IS NULL")
    List<AnnouncementResponseListDTO> getNotStaffNoted(@RequestParam("staffId") Integer staffId);


    @Query("select NEW com.ace.dto.AnnouncementResponseListDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name,  a.category.name) " +
            "from Announcement a " +
            "join a.group g "+
            "JOIN g.staff s  " +
            "left JOIN StaffNotedAnnouncement sn ON sn.staff.id = :staffId and sn.announcement.id = a.id " +
            "WHERE s.id = :staffId AND sn.id IS NULL ")
    List<AnnouncementResponseListDTO> getNotStaffNotedGroup(@RequestParam("staffId") Integer staffId);

    @Query("SELECT NEW com.ace.dto.AnnouncementResponseListDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name  , a.category.name) " +
            "from Announcement a " +
            "JOIN a.staff s " +
            "WHERE s.id = :staffId")
    List<AnnouncementResponseListDTO> getStaffAnnouncement(@Param("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseListDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name  , a.category.name) " +
            "from Announcement a " +
            "JOIN a.group g " +
            "join g.staff s "+
            "WHERE s.id = :staffId")
    List<AnnouncementResponseListDTO> getStaffAnnouncementGroup(@Param("staffId") Integer staffId);

    @Query("select NEW com.ace.dto.AnnouncementResponseListDTO(a.id , a.title , a.description, a.scheduleAt, a.createStaff.name,  a.category.name) " +
            "from Announcement a WHERE a.isPublished = true ")
    List<AnnouncementResponseListDTO> getPendingAnnouncement();


    @Query("SELECT new com.ace.dto.AnnouncementVersionDTO(a.id, a.file)  FROM Announcement a WHERE a.file like %?1%")
    List<AnnouncementVersionDTO> getAllVersions(String baseFileName);

    @Query("SELECT a FROM Announcement a WHERE a.file LIKE CONCAT('%/', :baseFileName, '%')")
    List<Announcement> getAllVersionsOfAnnouncement(@Param("baseFileName") String baseFileName);

    @Query("SELECT a FROM Announcement a WHERE a.file LIKE CONCAT('%/', :baseFileName, '%') ORDER BY a.created_at DESC")
    List<Announcement> getLatestVersionsOfAnnouncement(@Param("baseFileName") String baseFileName);
}
