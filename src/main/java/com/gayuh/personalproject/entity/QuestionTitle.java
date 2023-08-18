package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "question_titles")
public class QuestionTitle {
    @Id
    @Column(name = "question_title_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String token;
    private Boolean started;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp")

    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
    @ManyToOne
    @JoinColumn(name = "difficulty_id")
    private Difficulty difficulty;
    @ManyToOne
    @JoinColumn(name = "access_id")
    private Access access;
    @OneToMany(mappedBy = "questionTitle")
    private List<Question> questions;
    @OneToMany(mappedBy = "questionTitle")
    private List<TestHistory> testHistories;
}
