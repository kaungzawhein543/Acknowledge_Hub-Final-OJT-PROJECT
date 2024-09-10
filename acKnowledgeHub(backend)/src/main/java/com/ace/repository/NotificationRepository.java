package com.ace.repository;

import com.ace.entity.Notification;
import com.ace.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    @Query("SELECT n FROM Notification n WHERE n.staff.id = :staffId")
    List<Notification> findByStaffId(@Param("staffId") int staffId);



}
