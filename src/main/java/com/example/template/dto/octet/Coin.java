package com.example.template.dto.octet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Coin {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeeResponse {
        private String fastest;         // 가장 빠른 속도의 수수료 가격
        private String fast;            // 빠른 속도의 수수료 가격 (기본값)
        private String average;         // 일반 수수료
        private String slow;            // 최저가 수수료
        private String baseFee;         // 현재 어떤기준인지
    }
}
