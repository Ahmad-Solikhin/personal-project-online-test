package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_verifies")
public class UserVerify {
    @Id
    @Column(name = "user_verify_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_verifies_generator")
    @SequenceGenerator(name = "user_verifies_generator", sequenceName = "user_verifies_sequence", allocationSize = 1)
    private Long id;
    private String token;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime expiredAt;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
