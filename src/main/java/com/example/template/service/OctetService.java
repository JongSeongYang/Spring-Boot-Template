package com.example.template.service;

import com.example.template.config.OctetConfig;
import com.example.template.domain.KeyValueEntity;
import com.example.template.dto.octet.Address;
import com.example.template.dto.octet.Coin;
import com.example.template.dto.octet.Scheduler;
import com.example.template.dto.octet.Transfer;
import com.example.template.enums.SymbolType;
import com.example.template.repository.KeyValueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class OctetService {

    private final KeyValueRepository keyValueRepository;
    private final Environment environment;
    private final OkHttpClient client;
    private final OctetConfig octetConfig;

    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CREATE_CHILD_ADDRESS_URL = "/v1/%s/address";
    private static final String TRANSFER_TOKEN_URL = "/v1/%s/transfer";
    private static final String RETRANSFER_TOKEN_URL = "/v1/%s/retransmit";
    private static final String FEE_URL = "/v1/%s/fee";
    private static final String NEXT_KEY_URL = "/v1/%s/scheduler/next-keyindex";
    private static String accessToken;


    @PostConstruct
    public void octetService() {
        String[] profiles = environment.getActiveProfiles();
        if(Arrays.stream(profiles).anyMatch(env -> (env.equalsIgnoreCase("production")))) {
            KeyValueEntity octet_real = keyValueRepository.findKeyValueEntityByKey("OCTET_REAL");
            if(null == octet_real)
                accessToken = octetConfig.accessToken;
            else
                accessToken = octet_real.value();
        }
        else {
            KeyValueEntity octet_test = keyValueRepository.findKeyValueEntityByKey("OCTET_TEST");
            if(null == octet_test)
                accessToken = octetConfig.accessToken;
            else
                accessToken = octet_test.value();
        }
        System.out.println(">>> token : "+ accessToken);
    }

    /**
     * 자식주소 생성
     *
     * @param model
     * @return
     */
    private Address.ChildAddressResponse createChildAddress(String symbol, Address.ChildAddressRequest model) {

        Address.ChildAddressResponse result;
        log.info("=======================================================================================");
        log.info("octetURL:" + octetConfig.baseUrl + String.format(CREATE_CHILD_ADDRESS_URL, symbol));
        log.info("=======================================================================================");

        try {
            String json = objectMapper.writeValueAsString(model);
            RequestBody requestBody = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(octetConfig.baseUrl + String.format(CREATE_CHILD_ADDRESS_URL, symbol))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", accessToken)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("createChildAddress > " + response.code() + "/" + response.body().string());
                throw new Exception();
            }

            String responseBody = response.body().string();
            log.info("자식주소 생성 RESPONSE:" + responseBody);
            result = objectMapper.readValue(responseBody, Address.ChildAddressResponse.class);

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    /**
     * BTR 자식주소 생성
     * @return
     */
    public Address.ChildAddressResponse createBtrChildAddress() {

        Address.ChildAddressResponse response;

        try {
            var nextKeyResponse = getNextKey(SymbolType.HTC);
            if (nextKeyResponse == null) throw new Exception();

            Address.ChildAddressRequest request = Address.ChildAddressRequest.builder()
                    .account(SymbolType.BTR + "-" + StringUtils.leftPad(nextKeyResponse.getKeyIndex().toString(), 5, "0"))
                    .addressType("EOA")
                    .type("HD")
                    .pos(nextKeyResponse.getKeyIndex())
                    .offset(1)
                    .showPrivateKey(false)
                    .registerScheduler(true)
                    .build();

            response = createChildAddress(SymbolType.HTC, request);

        } catch (Exception e) {
            log.error(e.getMessage());
            response = null;
        }

        return response;
    }

    /**
     * 토큰 전송
     *
     * @param symbol
     * @param model
     * @return
     */
    public Transfer.Response transferToken(String symbol, Transfer.TransferRequest model) {

        Transfer.Response result;
        try {
            String json = objectMapper.writeValueAsString(model);
            log.info("request:" + json);
            log.info("url:" + octetConfig.baseUrl + String.format(TRANSFER_TOKEN_URL, symbol));

            RequestBody requestBody = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(octetConfig.baseUrl + String.format(TRANSFER_TOKEN_URL, symbol))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", accessToken)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("transferToken > " + response.code() + "/" + response.body().string());
                throw new Exception(response.message());
            }

            String responseBody = response.body().string();
            result = objectMapper.readValue(responseBody, Transfer.Response.class);

        } catch (Exception e) {
            log.error(e.getMessage());
            result = null;
        }
        return result;
    }

    /**
     * 수수료 정보 조회
     * @param walletSymbol
     * @return
     */
    public Coin.FeeResponse getGas(String walletSymbol) {

        Coin.FeeResponse result;
        try {
            Request request = new Request.Builder()
                    .url(octetConfig.baseUrl + String.format(FEE_URL, walletSymbol))
//                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", accessToken)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            log.info("response:" + response.body().toString());
            if(!response.isSuccessful()) {
                log.error("getFee > " + response.code() + "/" + response.body().string());
                throw new Exception();
            }

            String responseBody = response.body().string();
            result = objectMapper.readValue(responseBody, Coin.FeeResponse.class);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            result = null;
        }
        return result;

    }

    /**
     * 다음키인덱스
     * @param walletSymbol
     * @return
     */
    public Scheduler.NextKeyResponse getNextKey(String walletSymbol) {

        Scheduler.NextKeyResponse result;
        try {
            Request request = new Request.Builder()
                    .url(octetConfig.baseUrl + String.format(NEXT_KEY_URL, walletSymbol))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", accessToken)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if(!response.isSuccessful()) {
                log.error("getNextKey > " + response.code() + "/" + response.body().string());
                throw new Exception();
            }

            String responseBody = response.body().string();
            result = objectMapper.readValue(responseBody, Scheduler.NextKeyResponse.class);
        }
        catch (Exception e) {
            result = null;
        }
        return result;

    }

}
