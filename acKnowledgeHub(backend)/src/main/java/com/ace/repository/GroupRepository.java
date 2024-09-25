package com.ace.repository;

import com.ace.dto.GroupDTO;
import com.ace.dto.GroupResponseDTO;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Query("SELECT g FROM Group g JOIN g.announcement a WHERE a.id IN :announcementIds")
    List<Group> findGroupsByAnnouncementIds(@Param("announcementIds") List<Integer> announcementIds);

    @Query("SELECT g FROM Group g")
    List<Group> findAllGroups();

    @Query("SELECT g FROM Group g WHERE g.id IN :ids")
    List<Group> findGroupsByIds(List<Integer> ids);

    @Query("select g from Group g where g.name = ?1")
    Group findByName(String name);

    @Query("SELECT COUNT(g) FROM Group g WHERE :staff MEMBER OF g.staff AND g = :group")
    Integer hasStaffInGroup(@Param("staff") Staff staff, @Param("group") Group group);

    @Query("SELECT g FROM Group g JOIN g.announcement a WHERE a.id = :announcementId")
    List<Group> findGroupByAnnouncementId(@Param("announcementId") Integer announcementId);

    @Query("select new com.ace.dto.GroupResponseDTO(g.id , g.name , g.status) from Group g where g.name LIKE CONCAT('%', :companyName, '%')")
    List<GroupResponseDTO> getGroupsByHR(@Param("companyName") String companyName);

    @Query("select new com.ace.dto.GroupResponseDTO(g.id , g.name , g.status) from Group g Join g.announcement a where a.id = ?1")
    List<GroupResponseDTO> getGroupsByAnnouncementId(Integer id);
}
