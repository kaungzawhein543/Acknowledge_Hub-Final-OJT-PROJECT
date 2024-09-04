package com.ace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback_reply")
public class FeedbackReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @OneToOne( fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @PrePersist
    protected void onCreate() {
        if (this.created_at == null) {
            this.created_at = LocalDateTime.now();
        }
    }
}
