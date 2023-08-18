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
@Table(name = "test_histories")
public class TestHistory {
    @Id
    @Column(name = "test_history_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(columnDefinition = "float")
    private Double score;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime startedAt;
    @Column(columnDefinition = "timestamp")

    private LocalDateTime finishedAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "question_title_id")
    private QuestionTitle questionTitle;
    @OneToMany(mappedBy = "testHistory")
    private List<Test> tests;
}
