package com.ace.repository;


import com.ace.entity.ReqAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReqAnnouncementRepository extends JpaRepository<ReqAnnouncement,Integer> {

    @Modifying
    @Transactional
    @Query("update ReqAnnouncement ra set ra.status = 'inactive' where ra.id = ?1")
    void softDeleteAnnouncement(Integer id);
}
