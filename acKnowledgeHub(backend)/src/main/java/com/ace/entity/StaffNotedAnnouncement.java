package com.ace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_noted_announcement")
public class StaffNotedAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private int id;
    @Column(name = "noted_at")
    private Timestamp notedAt ;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "noted_staff_id",nullable = false)
    private StaffHasAnnouncement notedStaff;
//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "staff_id",nullable = false)
//    private Staff staff;
//    @ManyToOne(cascade =  CascadeType.MERGE)
//    @JoinColumn(name = "announcement_id",nullable = false)
//    private Announcement announcement;


    @PrePersist
    protected void onCreate() {
        if (this.notedAt == null) {
            this.notedAt = Timestamp.valueOf(LocalDateTime.now());
        }
    }
}
