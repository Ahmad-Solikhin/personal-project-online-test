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
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topics_generator")
    @SequenceGenerator(name = "topics_generator", sequenceName = "topics_sequence", allocationSize = 1)
    @Column(name = "topic_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "topic")
    private List<QuestionTitle> questionTitles;
}
