//package com.ace.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//import java.sql.Timestamp;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "request_announcement")
//
//public class ReqAnnouncement {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name ="id")
//    private int id;
//    @Column(name = "description",nullable = false)
//    private String description;
//    @Temporal(TemporalType.DATE)
//    @Column(name = "requested_at")
//    private Date requestedAt;
//    @Column(name = "status")
//    private String status;
//    @OneToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "staff_id",nullable = false)
//    private Staff staff;
//
//    @PrePersist
//    protected void onCreate() {
//        if (this.requestedAt == null) {
//            this.requestedAt = new Date();
//        }
//        this.status = "active";
//    }
//}
