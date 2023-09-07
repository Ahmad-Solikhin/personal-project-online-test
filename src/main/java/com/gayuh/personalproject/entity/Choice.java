package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "choices")
public class Choice {
    @Id
    @Column(name = "choice_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "choices_generator")
    @SequenceGenerator(name = "choices_generator", sequenceName = "choices_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "choice")
    private String choiceText;
    private Boolean correct;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    @OneToMany(mappedBy = "choice")
    private List<Test> tests;
}
