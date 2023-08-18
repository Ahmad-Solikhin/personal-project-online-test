package com.gayuh.personalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private Boolean activated;
    private Boolean suspend;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserVerify userVerify;
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private ForgetPassword forgetPassword;
    @OneToMany(mappedBy = "user")
    private List<QuestionTitle> questionTitles;
    @OneToMany(mappedBy = "user")
    private List<TestHistory> testHistories;
}
