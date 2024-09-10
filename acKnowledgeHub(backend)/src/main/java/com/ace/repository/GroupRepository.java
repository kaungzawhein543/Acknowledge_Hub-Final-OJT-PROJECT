package com.ace.repository;

import com.ace.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT g FROM Group g JOIN g.announcement a WHERE a.id IN :announcementIds")
    List<Group> findGroupsByAnnouncementIds(@Param("announcementIds") List<Integer> announcementIds);
}
