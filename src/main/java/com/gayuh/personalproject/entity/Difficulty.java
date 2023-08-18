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
@Table(name = "difficulties")
public class Difficulty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "difficulties_generator")
    @SequenceGenerator(name = "difficulties_generator", sequenceName = "difficulties_sequence", allocationSize = 1)
    @Column(name = "difficulty_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "difficulty")
    private List<QuestionTitle> questionTitles;
}
