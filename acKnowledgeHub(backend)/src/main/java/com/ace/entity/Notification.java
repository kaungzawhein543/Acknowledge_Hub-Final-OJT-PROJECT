package com.ace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "description",nullable = false)
    private String description;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "status")
    private String status = "active";
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id",nullable = false)
    private Announcement announcement;


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new java.util.Date();
        }
    }
}
