package com.example.template.repository;

import com.example.template.domain.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository  extends JpaRepository<WalletEntity, Long> {
}
