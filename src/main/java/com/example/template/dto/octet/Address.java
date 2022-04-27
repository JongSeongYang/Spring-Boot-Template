package com.example.template.dto.octet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class Address {

    @Data
    @Builder
    public static class ChildAddressRequest {

        private String account;
        private String addressType;
        private String type;
        private Integer pos;
        private Integer offset;
        private Boolean showPrivateKey;
        private Boolean registerScheduler;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildAddressResponse {

        @JsonProperty("addresses")
        private List<ChildAddress> addresses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChildAddress {
        private String address;
        private String KeyIndex;
        private String type;
        private String privateKey;
        private String addressType;
    }
}
