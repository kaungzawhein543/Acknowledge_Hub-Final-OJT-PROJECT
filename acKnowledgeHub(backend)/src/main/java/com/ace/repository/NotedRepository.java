package com.ace.repository;

import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.entity.StaffNotedAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotedRepository extends JpaRepository<StaffNotedAnnouncement,Integer> {

    Optional<StaffNotedAnnouncement> findByStaffAndAnnouncement(Staff staff, Announcement announcement);
}
