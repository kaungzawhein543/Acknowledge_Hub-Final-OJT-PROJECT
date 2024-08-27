package com.ace.repository;


import com.ace.entity.AnnouncementForReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementForReqRepository extends JpaRepository<AnnouncementForReq,Integer> {

}
