package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accesses")
public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accesses_generator")
    @SequenceGenerator(name = "accesses_generator", sequenceName = "accesses_sequence", allocationSize = 1)
    @Column(name = "access_id")
    private Long id;
    private String name;
}
