package com.example.template.repository;

import com.example.template.domain.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyValueRepository extends JpaRepository<KeyValueEntity, String> {
    KeyValueEntity findKeyValueEntityByKey(String key);
}
