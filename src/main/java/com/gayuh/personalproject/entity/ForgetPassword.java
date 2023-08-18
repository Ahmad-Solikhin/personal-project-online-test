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
@Table(name = "forget_passwords")
public class ForgetPassword {
    @Id
    @Column(name = "forget_password_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forget_passwords_generator")
    @SequenceGenerator(name = "forget_passwords_generator", sequenceName = "forget_passwords_sequence", allocationSize = 1)
    private Long id;
    private String token;
    private LocalDateTime expiredAt;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
