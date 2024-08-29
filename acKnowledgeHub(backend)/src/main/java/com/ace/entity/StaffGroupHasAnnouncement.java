package com.ace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_group_has_announcement")
public class StaffGroupHasAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_has_group_id")
    private StaffHasGroup staffGroup;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;
    @Column(name = "noted_at")
    private LocalDateTime notedAt;

}
