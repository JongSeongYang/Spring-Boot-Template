package com.example.template.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class Notifi {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationRequest {
        private String title;
        private String body;
        private String image;
        private String topic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationResponse {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscribeRequest {
        private List<String> tokens;
        private String topic;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationToken {
        private Integer id;
        private String token;
    }
}
