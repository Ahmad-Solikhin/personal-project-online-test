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
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_generator")
    @SequenceGenerator(name = "roles_generator", sequenceName = "roles_sequence", allocationSize = 1)
    @Column(name = "role_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "role")
    private List<User> users;
}
