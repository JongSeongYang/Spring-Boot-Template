package com.example.template.dto.octet;

import lombok.*;

public class Webhook {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Withdraw {

        private Integer idx;
        private String requestId;
        private String type;
        private String status;
        private String coinSymbol;
        private String txid;
        private String fromAddress;
        private String toAddress;
        private String amount;
        private String actualFee;
        private String data;
        private Integer expireBlockHeight;
        private Integer nonce;
        private Integer webhookStatus;      // -1:만료, 0:요청대기, 1:승인
        private String createDate;
        private String modifiedDate;
        private String finishedDate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Deposit {

        private Integer id;
        private String coinSymbol;
        private String fromAddress;
        private String toAddress;
        private String amount;
        private String txid;
        private Integer outputIndex;
        private String data;
        private Integer blockHeight;
        private String dwDate;
        private String dwModifiedDate;
    }


}
