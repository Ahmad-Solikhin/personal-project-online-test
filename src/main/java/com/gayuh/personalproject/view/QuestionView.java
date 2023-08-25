package com.gayuh.personalproject.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "VW_question")
@AllArgsConstructor
@NoArgsConstructor
public class QuestionView {
    @Id
    @Column(name = "question_id")
    private Long id;
    private String questionTitleId;
    @Column(name = "question")
    private String questionText;
    private Integer time;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String mediaId;
}
