package com.ace.repository;

import com.ace.dto.*;
import com.ace.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {
    //List<Announcement> findByStatus(String status);

    @Query("select new com.ace.dto.AnnouncementListDTO(a.id, a.title, a.description, a.createStaff.name, a.category.name, a.status, a.created_at, a.scheduleAt, a.groupStatus ,a.file) " +
            "from Announcement a where a.permission = 'approved' order by a.scheduleAt DESC")
    List<AnnouncementListDTO> getAnnouncementList();


    @Modifying
    @Transactional
    @Query("update Announcement a set a.status = 'inactive' where a.id = ?1")
    void softDeleteAnnouncement(Integer id);

    @Query(value = "SELECT * FROM Announcement a WHERE a.file LIKE %:fileName%", nativeQuery = true)
    List<Announcement> findAllByFileName(@Param("fileName") String fileName);


    @Query("SELECT a.file FROM Announcement a WHERE a.file LIKE CONCAT('%/', :baseFileName, '%')")
    List<String> getAllVersionsOfAnnouncement(@Param("baseFileName") String baseFileName);

    @Query("SELECT a FROM Announcement a WHERE a.file LIKE CONCAT('%/', :baseFileName, '%') ORDER BY a.created_at DESC")
    List<Announcement> getLatestVersionsOfAnnouncement(@Param("baseFileName") String baseFileName);


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

    @Query("select new com.ace.dto.AnnouncementListDTO(a.id, a.title, a.description, a.createStaff.name, a.category.name, a.status, a.created_at, a.scheduleAt, a.groupStatus ,a.file) " +
            "from Announcement a WHERE a.isPublished = false and a.permission = 'approved'")
    List<AnnouncementListDTO> getPendingAnnouncement();

    //Query for staffNotedAnnouncement
    @Query("SELECT new com.ace.dto.AnnouncementStaffCountDTO(a.id, a.title, a.created_at, COUNT(s.id)) " +
            "FROM Announcement a " +
            "LEFT JOIN StaffNotedAnnouncement s ON a.id = s.announcement.id " +
            "GROUP BY a.id, a.title, a.created_at")
    List<AnnouncementStaffCountDTO> findAnnouncementStaffCounts();

    //Query for announcement stats card
    @Query("SELECT new com.ace.dto.AnnouncementStatsDTO( " +
            "COUNT(a), " +
            "SUM(CASE WHEN a.isPublished = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.isPublished = FALSE THEN 1 ELSE 0 END)) " +
            "FROM Announcement a")
    AnnouncementStatsDTO getAnnouncementCounts();

//    //Query for announcement desc card
//    @Query("select new com.ace.dto.AnnouncementListDTO(a.id, a.title, a.description, a.createStaff.name, a.category.name, a.status, a.created_at, a.scheduleAt, a.groupStatus) " +
//            "from Announcement a where a.status = 'active' order by a.scheduleAt DESC")
//    List<AnnouncementListDTO> getAnnouncementList();

    //Query for all announcement count by month
    @Query("select new com.ace.dto.MonthlyCountDTO(YEAR(a.scheduleAt), MONTH(a.scheduleAt), count(a)) " +
            "from Announcement a where a.status = 'active' " +
            "group by YEAR(a.scheduleAt), MONTH(a.scheduleAt)")
    List<MonthlyCountDTO> countActiveAnnouncementsByMonth();


    @Query("SELECT a FROM Announcement a JOIN a.staff s WHERE s.id = :staffId ORDER BY a.created_at DESC")
    List<Announcement> findAnnouncementsByStaffId(@Param("staffId") int staffId);

    @Query("SELECT new com.ace.dto.AnnouncementVersionDTO(a.id, a.file) " +
            "FROM Announcement a WHERE a.file LIKE :baseFileName ")
    List<AnnouncementVersionDTO> getAllVersions(@Param("baseFileName") String baseFileName);
//
//    @Query("SELECT a FROM Announcement a WHERE a.file LIKE CONCAT('%/', :baseFileName, '%') ORDER BY a.created_at DESC")
//    List<Announcement> getLatestVersionsOfAnnouncement(@Param("baseFileName") String baseFileName);

    @Query("select new com.ace.dto.RequestAnnouncementResponseDTO" +
            "(a.id, a.title , a.description,a.created_at, a.scheduleAt,  a.category.name, a.createStaff.name,cs.company.name) " +
            "from Announcement a join a.createStaff cs where a.permission = 'pending'")
    List<RequestAnnouncementResponseDTO> getRequestAnnouncement();

    @Modifying
    @Transactional
    @Query("update Announcement a set a.permission = 'approved' where a.id = ?1")
    void approvedRequestAnnouncement(Integer id);

    @Modifying
    @Transactional
    @Query("update Announcement a set a.permission = 'reject' where a.id = ?1")
    void rejectRequestAnnouncement(Integer id);

    @Query("select new com.ace.dto.AnnouncementListDTO(a.id, a.title, a.description, a.createStaff.name, a.category.name, a.permission, a.created_at, a.scheduleAt, a.groupStatus ,a.file) " +
            "from Announcement a where a.isPublished = false and a.createStaff.id = ?1 order by a.scheduleAt DESC")
    List<AnnouncementListDTO> getAnnouncementListByStaffRequest(Integer staffId);

    @Modifying
    @Transactional
    @Query("update Announcement a set a.permission = 'reject' where a.id = ?1")
    void cancelPendingAnnouncement(Integer id);
}
