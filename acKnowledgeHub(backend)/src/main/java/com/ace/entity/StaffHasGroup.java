package com.ace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "staff_has_group")
public class StaffHasGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id")
    private Group group;


}
