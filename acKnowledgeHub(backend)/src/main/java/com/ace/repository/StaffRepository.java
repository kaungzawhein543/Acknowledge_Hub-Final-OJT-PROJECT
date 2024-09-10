package com.ace.repository;

import com.ace.dto.NotedResponseDTO;
import com.ace.dto.StaffDTO;
import com.ace.dto.StaffGroupDTO;
import com.ace.dto.UnNotedResponseDTO;
import com.ace.entity.Staff;
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

    List<Staff> findByPositionId(Integer positionId);

    @Query("SELECT NEW com.ace.dto.StaffGroupDTO(s.id , s.name , p.name, s.department ) FROM Staff s JOIN Position p on p.id = s.position.id")
    List<StaffGroupDTO> getStaffListForGroup();

    // Method to find staff by a list of companyStaffIds
    List<Staff> findByCompanyStaffIdIn(List<String> companyStaffIds);

    @Query("SELECT s FROM Staff s JOIN s.groups g WHERE g.id = :groupId")
    List<Staff> findStaffByGroupId(@Param("groupId") int groupId);


    @Query("SELECT s FROM Staff s WHERE s.id IN :ids")
    List<Staff> findStaffsByIds(@Param("ids") List<Integer> ids);

    @Query("SELECT s FROM Staff s WHERE s.id IN :ids")
    List<Staff> findByIds(List<Integer> ids);

}
