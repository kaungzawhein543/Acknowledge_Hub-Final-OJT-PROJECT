package com.ace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_has_announcement")
public class StaffHasAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;


}
