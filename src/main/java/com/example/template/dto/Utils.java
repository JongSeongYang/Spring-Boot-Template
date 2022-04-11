package com.example.template.dto;

import lombok.*;

public class Utils {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignedUrlResponse {
        private String uploadUrl;
        private String downloadUrl;
    }
}
