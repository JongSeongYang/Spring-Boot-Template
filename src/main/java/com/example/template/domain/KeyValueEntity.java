package com.example.template.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "KeyValue")
public class KeyValueEntity {

    //key:value 형식의 데이터를 저장하는 KeyValue 엔티티
    @Id
    private String key;
    private String value;

    public String value() {
        return value;
    }
}
