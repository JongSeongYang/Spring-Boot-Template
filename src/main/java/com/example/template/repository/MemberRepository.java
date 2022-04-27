package com.example.template.repository;

import com.example.template.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmailAndDeletedTimeIsNull(String email);
    Optional<MemberEntity> findByIdAndDeletedTimeIsNull(Long id);
    Optional<MemberEntity> findByPhoneAndDeletedTimeIsNull(String phone);
    MemberEntity findByRewardCode(String rewardCode);
}
