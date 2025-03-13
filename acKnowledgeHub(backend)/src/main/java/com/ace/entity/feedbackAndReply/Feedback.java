package com.ace.entity.feedbackAndReply;

import com.ace.utility.entity.BaseEntity;
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
@Table(name = "feedback")
public class Feedback extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "content")
    private String content;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

}
