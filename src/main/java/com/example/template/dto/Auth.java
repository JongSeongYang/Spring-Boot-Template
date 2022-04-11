package com.example.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Auth {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthTokenRequest {
        private String token;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthTokenResponse {
        private boolean result;
        private String token;
    }
}
