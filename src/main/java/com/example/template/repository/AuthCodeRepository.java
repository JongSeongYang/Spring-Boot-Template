package com.example.template.repository;

import com.example.template.domain.AuthCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCodeEntity, Long> {

    List<AuthCodeEntity> findAllByAuthAndStatusOrderByCreatedTimeDesc(String auth, Integer Status);

    Optional<AuthCodeEntity> findByAuthAndCodeAndStatus(String auth, String code, Integer status);
}
