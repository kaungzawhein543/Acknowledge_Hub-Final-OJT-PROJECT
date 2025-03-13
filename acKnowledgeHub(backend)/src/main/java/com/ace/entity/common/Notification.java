package com.ace.entity.common;


import com.ace.utility.core.coreEntity.BaseEntity;
import com.ace.entity.Announcement.Announcement;
import com.ace.entity.organization.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "description",nullable = false)
    private String description;
    @Column(name = "url")
    private String url;
    @Column(name="status")
    private boolean status;
    @Column(name = "checked")
    private boolean checked;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id",nullable = false)
    private Announcement announcement;

}
