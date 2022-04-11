package com.example.template.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Member")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberEntity extends BaseTimeEntity{

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String type;
  private String deviceToken;
  private String password;
  private String phone;
  private String name;
  private String email;
  private Integer status;
  private Integer passwordFailCnt;
  private LocalDateTime passwordUpdatedTime;
  private LocalDateTime lastChallengeTime;
  private LocalDateTime deletedTime;
}

