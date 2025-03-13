package com.ace.entity.feedbackAndReply;

import com.ace.utility.entity.BaseEntity;
import com.ace.entity.organization.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "feedback_reply")
public class FeedbackReply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "content")
    private String content;
    @OneToOne( fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id")
    private Staff staff;
}
