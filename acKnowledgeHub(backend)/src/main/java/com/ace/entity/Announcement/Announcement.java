package com.ace.entity.Announcement;


import com.ace.utility.entity.BaseEntity;
import com.ace.entity.common.Category;
import com.ace.entity.organization.Group;
import com.ace.entity.organization.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "announcement")
public class Announcement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
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
    @Column(name ="permission")
    private String permission;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "create_staff_id")
    private Staff createStaff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_has_announcement",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @BatchSize(size = 10)
    private List<Group> group;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "staff_has_announcement",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @BatchSize(size = 10)
    private List<Staff> staff;


    @PrePersist
    protected void onCreate() {
        if(this.scheduleAt == null){
            this.scheduleAt = LocalDateTime.now();
        }
    }

}
