package com.gayuh.personalproject.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "VW_test_history")
@AllArgsConstructor
@NoArgsConstructor
public class TestHistoryView {
    @Id
    @Column(name = "test_history_id")
    private String id;
    private String questionTitleId;
    private Double score;
    private String title;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    @Column(columnDefinition = "numeric")
    private Integer time;
    private String name;
    private String email;
}
