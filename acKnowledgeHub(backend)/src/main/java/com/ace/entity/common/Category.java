package com.ace.entity.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private int id;
    @Column(name = "name",nullable = true)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT",nullable = true)
    private String description;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private LocalDate created_at;
    @Column(name = "status")
    private String status = "active";

}
