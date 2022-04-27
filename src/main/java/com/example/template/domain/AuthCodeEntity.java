package com.example.template.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "AuthCode")
@Getter
@Setter
public class AuthCodeEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String auth;
    private String code;
    private Integer status; // 0: 인증 전, 1: 인증 후
    private LocalDateTime expiredTime;
}
