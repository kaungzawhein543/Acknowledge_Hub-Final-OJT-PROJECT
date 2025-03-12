package com.ace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "status")
    private String status = "active";
    @Column(name = "url")
    private String url;
    @Column(name = "checked")
    private boolean checked;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id",nullable = false)
    private Announcement announcement;


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt =  LocalDateTime.now();
        }
    }
}
