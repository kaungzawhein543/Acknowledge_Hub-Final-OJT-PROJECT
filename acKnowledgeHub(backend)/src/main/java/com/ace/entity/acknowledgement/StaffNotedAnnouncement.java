package com.ace.entity.acknowledgement;

import com.ace.entity.Announcement.Announcement;
import com.ace.entity.organization.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
    @CreationTimestamp
    private Timestamp notedAt ;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;
    @ManyToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name = "announcement_id",nullable = false)
    private Announcement announcement;

}
