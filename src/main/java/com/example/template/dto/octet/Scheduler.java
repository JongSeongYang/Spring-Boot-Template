package com.example.template.dto.octet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Scheduler {

    // 자식 주소 등록 Request
    @Data
    @Builder
    public static class RegisterRequest {

        private String type;
        private Integer keyIndex;
        private String account;
    }

    // 등록된 자식주소 별칭수정 Request
    @Data
    @Builder
    public static class ModifyRequest {

        private String subAddress;
        private Integer keyIndex;
        private String account;
    }

    // 자식주소 등록여부 Request
    @Data
    @Builder
    public static class ExistsRequest {

        private String address;
        private String subAddress;
        private Integer keyIndex;
    }

    // 공통 Response
    @Data
    @Builder
    public static class Response {

        private String symbol;
        private String address;
        private String account;
        private Integer keyIndex;
    }

    // 갯수 Response
    @Data
    @Builder
    public static class CountResponse {

        private Integer count;
    }

    // 삭제 Response
    @Data
    @Builder
    public static class DeleteResponse {

        private Integer count;
    }

    // 등록여부 Response
    @Data
    @Builder
    public static class ExistsResponse {

        private boolean exists;
        private String symbol;
        private String address;
        private String account;
        private Integer keyIndex;
    }

    // 다음키인덱스 Response
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextKeyResponse {

        private Integer keyIndex;
    }
}
