package com.ace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announcement")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date created_at;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;
    @Column(name = "file")
    private String file;
    @Column(name = "description",nullable = false,columnDefinition = "TEXT")
    private String description;
    @Column(name="isPublished")
    private boolean isPublished=false;
    @Column(name="status")
    private String status="active";
    @Column(name ="group_status")
    private byte groupStatus;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "create_staff_id")
    private Staff createStaff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToMany
    @JoinTable(
            name ="group_has_announcement",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @BatchSize(size = 10)
    private List<Group> group;
    @ManyToMany
    @JoinTable(
            name ="staff_has_announcement",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @BatchSize(size = 10)
    private List<Staff> staff;



    @PrePersist
    protected void onCreate() {
        if (this.created_at == null) {
            this.created_at = new Date();
        }
        if(this.scheduleAt == null){
            this.scheduleAt = LocalDateTime.now();
        }
    }

}
