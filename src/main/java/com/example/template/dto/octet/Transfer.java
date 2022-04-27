package com.example.template.dto.octet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Transfer {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {

        private String to;
        private String amount;
        private String reqId;
        private Integer memo;
        private String passphrase;
        private String privateKey;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("transaction_id")
        private String transactionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetransferRequest {

        private String txid;
        private String reqId;
        private String gasPrice;
        private Integer passphrase;
        private String privateKey;
    }

}
