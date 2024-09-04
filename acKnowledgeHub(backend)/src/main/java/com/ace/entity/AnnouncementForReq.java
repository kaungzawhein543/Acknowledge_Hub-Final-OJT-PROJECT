//package com.ace.entity;
//
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "announcement_for_requests")
//public class AnnouncementForReq {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name ="id")
//    private int id;
//    @OneToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "announcement_id",nullable = false)
//    private Announcement announcement;
//    @OneToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "request_announcement_id",nullable = false)
//    private ReqAnnouncement requestAnnouncement;
//
//}
