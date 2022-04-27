package com.example.template.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Wallet")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WalletEntity extends BaseTimeEntity{

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long memberId;
  private String symbol;
  private String address;
  private BigDecimal balance;

}

