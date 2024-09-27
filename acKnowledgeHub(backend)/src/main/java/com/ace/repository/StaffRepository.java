package com.ace.repository;

import com.ace.dto.*;
import com.ace.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    @Query("SELECT s FROM Staff s where s.email = :email")
    public Staff findByEmail(@Param("email") String email);

    @Query("SELECT s FROM Staff s WHERE s.id IN :ids")
    List<Staff> findStaffsByIds(List<Integer> ids);

    @Query("SELECT s.chatId FROM Staff s WHERE s.id IN :ids")
    List<String> findStaffsChatIdByIds(List<Integer> ids);

    @Query("select NEW com.ace.dto.StaffResponseDTO(s.id, s.companyStaffId, s.name, s.email, s.role, s.position.name, s.department.name, s.company.name, s.status ) " +
            "from Staff s where s.position.name = 'Human Resource' or s.position.name = 'Human Resource(Main)' order by s.companyStaffId")
    List<StaffResponseDTO> getHRStaffList();

    @Query("select s from Staff s where s.position.name = ?1")
    Staff findByPosition(String position);

    public Optional<Staff> findByChatId(String id);

    @Query("select s.chatId from Staff s")
    public List<String> findAllChatIds();

    @Query("SELECT NEW com.ace.dto.NotedResponseDTO( s.companyStaffId, s.name, s.department.name, s.company.name, s.position.name,a.scheduleAt , sn.notedAt, s.email) " +
            "FROM Staff s " +
            "JOIN StaffNotedAnnouncement sn ON s.id = sn.staff.id " +
            "Join sn.announcement a " +
            "WHERE a.id = :announcementId")
    List<NotedResponseDTO> getNotedStaffByAnnouncement(@Param("announcementId") Integer announcementId);

    @Query("SELECT NEW com.ace.dto.UnNotedResponseDTO(s.companyStaffId, s.name, s.department.name, s.company.name, s.position.name, s.email) " +
            "FROM Staff s " +
            "JOIN s.announcement a " +
            "LEFT JOIN StaffNotedAnnouncement sn ON s.id = sn.staff.id AND sn.announcement.id = :announcementId "+
            "WHERE a.id = :announcementId AND sn.id IS NULL")
    public List<UnNotedResponseDTO> getUnNotedStaffByAnnouncementWithEach(@Param("announcementId") Integer announcementId);


    @Query("SELECT NEW com.ace.dto.UnNotedResponseDTO(s.companyStaffId, s.name, s.department.name, s.company.name, s.position.name, s.email) " +
            "FROM Staff s " +
            "JOIN s.groups g " +
            "JOIN g.announcement a " +
            "LEFT JOIN StaffNotedAnnouncement sn ON s.id = sn.staff.id AND sn.announcement.id = :announcementId " +
            "WHERE a.id = :announcementId AND sn.id IS NULL")
    public List<UnNotedResponseDTO> getUnNotedStaffByAnnouncementWithGroup(@Param("announcementId") Integer announcementId);

    @Query("SELECT s FROM Staff s where companyStaffId = ?1")
    Staff findByCompanyStaffId(String staffId);

    Optional<Staff> findByName(String name);

    Page<Staff> findAll(Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.position.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.department.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.company.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Staff> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);



    List<Staff> findByPositionId(Integer positionId);

    @Query("SELECT NEW com.ace.dto.StaffGroupDTO(s.id , s.name , s.position, s.department,s.photoPath,s.company) FROM Staff s ")
    List<StaffGroupDTO> getStaffListForGroup();

    @Query("select NEW com.ace.dto.StaffResponseDTO(s.id, s.companyStaffId, s.name, s.email, s.role, s.position.name, s.department.name, s.company.name, s.status ) " +
            "from Staff s order by s.companyStaffId  DESC")
    List<StaffResponseDTO> getStaffList();

    @Query("select NEW com.ace.dto.ActiveStaffResponseDTO(s.id, s.companyStaffId, s.name, s.email, s.role, s.position.name, s.department.name, s.company.name) " +
            "from Staff s where s.status = 'active' ")
    List<ActiveStaffResponseDTO> getActiveStaffList();

    @Query(value = "SELECT sa.announcement_id AS announcementId, COUNT(sa.staff_id) AS staffCount " +
            "FROM staff_has_announcement sa " +
            "GROUP BY sa.announcement_id", nativeQuery = true)
    List<Map<String, Object>> countStaffByAnnouncement();

    //@Query to get staff summary count
    @Query("SELECT new com.ace.dto.StaffSummaryDTO(" +
            "COUNT(s), " +
            "SUM(CASE WHEN s.status = 'active' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN s.status = 'inactive' THEN 1 ELSE 0 END)) " +
            "FROM Staff s")
    StaffSummaryDTO getStaffSummary();

    @Query("SELECT s FROM Staff s JOIN s.announcement a WHERE a.id = :announcementId")
    List<Staff> findStaffByAnnouncementId(@Param("announcementId") Integer announcementId);


    @Query("select s.company.name from Staff s where s.id = ?1")
    String getCompanyNameById(Integer id);

    @Query("select NEW com.ace.dto.StaffResponseDTO(s.id, s.companyStaffId, s.name, s.email, s.role, s.position.name, s.department.name, s.company.name, s.status ) " +
            "from Staff s " +
            "Join s.announcement a where a.id = ?1  order by s.company.name")
    List<StaffResponseDTO> getStaffListByAnnouncementId(Integer id);
}

