package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medias")
public class Media {
    @Id
    @Column(name = "media_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;
    private Integer size;
    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
