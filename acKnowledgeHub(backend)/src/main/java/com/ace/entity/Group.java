package com.ace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`group`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name",nullable = false)
    private String name;
    @Column (name = "status")
    private String status = "active";
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date createdAt;
    @ManyToMany(mappedBy = "group")
    @JsonIgnore
    private List<Announcement> announcement = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "staff_has_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @JsonIgnore
    private List<Staff> staff = new ArrayList<>();
   

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }

    @Override
    public String toString() {
        return "Group{id=" + id + ", name='" + name + "', status='" + status + "', createdAt=" + createdAt + "}";
    }
}
