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
@Table(name = "questions")
public class Question {
    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questions_generator")
    @SequenceGenerator(name = "questions_generator", sequenceName = "questions_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "question", columnDefinition = "text")
    private String questionText;
    private Integer time;
    private Integer score;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "question_title_id")
    private QuestionTitle questionTitle;
    @OneToMany(mappedBy = "question")
    private List<Choice> choices;
    @OneToOne(mappedBy = "question")
    private Media media;
    @OneToMany(mappedBy = "question")
    private List<Test> tests;
}
