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
  private String transferPassword;
  private String phone;
  private String email;
  private String rewardCode;
  private Integer status; // 0 : 정상, 1 : 정지, 2: 잠김(비밀번호 잘못 입력 5회 이상)
  private Integer passwordFailCnt;
  private Integer transferPasswordFailCnt;
  private LocalDateTime deletedTime;
}

