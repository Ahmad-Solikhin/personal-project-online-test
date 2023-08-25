package com.gayuh.personalproject.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "VW_question_title")
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTitleView {
    @Id
    @Column(name = "question_title_id")
    private String id;
    private String title;
    private String token;
    private Boolean started;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    private String userName;
    private Long topicId;
    private String topicName;
    private Long difficultyId;
    private String difficultyName;
    private Long accessId;
    private String accessName;
    private Long tested;
}
