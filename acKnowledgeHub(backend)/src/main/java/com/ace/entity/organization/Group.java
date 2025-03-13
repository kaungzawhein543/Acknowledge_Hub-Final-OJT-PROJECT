package com.ace.entity.organization;

import com.ace.utility.entity.BaseEntity;
import com.ace.entity.Announcement.Announcement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "`group`")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name",nullable = false)
    private String name;
    @Column (name = "status")
    private String status = "active";
    @ManyToMany(mappedBy = "group")
    @JsonIgnore
    private List<Announcement> announcement;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(
            name = "staff_has_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @JsonIgnore
    private List<Staff> staff = new ArrayList<>();

    @Override
    public String toString() {
        return "Group{id=" + id + ", name='" + name + "', status='" + status + "', createdAt=" + super.createdAt + "}";}

}
