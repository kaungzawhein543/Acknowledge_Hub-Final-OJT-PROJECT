package com.ace.repository;

import com.ace.dto.NotedResponseDTO;
import com.ace.dto.StaffGroupDTO;
import com.ace.dto.UnNotedResponseDTO;
import com.ace.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    @Query("SELECT s FROM Staff s where s.email = :email")
    public Staff findByEmail(@Param("email") String email);

    @Query("SELECT s FROM Staff s WHERE s.id IN :ids")
    List<Staff> findStaffsByIds(List<Integer> ids);

    @Query("SELECT s.chatId FROM Staff s WHERE s.id IN :ids")
    List<String> findStaffsChatIdByIds(List<Integer> ids);


    public Optional<Staff> findByChatId(String id);

    @Query("select s.chatId from Staff s")
    public List<String> findAllChatIds();

    @Query("SELECT NEW com.ace.dto.NotedResponseDTO( s.companyStaffId, s.name, s.department.name, s.company.name, s.position.name, sn.notedAt, s.email) " +
            "FROM Staff s " +
            "JOIN StaffNotedAnnouncement sn ON s.id = sn.staff.id " +
            "WHERE sn.announcement.id = :announcementId")
    List<NotedResponseDTO> getNotedStaffByAnnouncement(@Param("announcementId") Integer announcementId);

    @Query("SELECT NEW com.ace.dto.UnNotedResponseDTO(s.companyStaffId, s.name, s.department.name, s.company.name, s.position.name, s.email)" +
            "FROM Staff s " +
            "LEFT JOIN StaffNotedAnnouncement sn ON s.id = sn.staff.id AND sn.announcement.id = :announcementId " +
            "WHERE sn.id IS NULL")
    public List<UnNotedResponseDTO> getUnNotedStaffByAnnouncement(@Param("announcementId") Integer announcementId);

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

    @Query("SELECT NEW com.ace.dto.StaffGroupDTO(s.id , s.name , p.name, s.department ) FROM Staff s JOIN Position p on p.id = s.position.id")
    List<StaffGroupDTO> getStaffListForGroup();
}
