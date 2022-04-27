package com.example.template.service;

import com.example.template.config.AmazonSnsConfig;
import com.example.template.domain.AuthCodeEntity;
import com.example.template.domain.MemberEntity;
import com.example.template.dto.Auth;
import com.example.template.enums.AuthType;
import com.example.template.exception.CustomResponseStatusException;
import com.example.template.exception.ExceptionCode;
import com.example.template.repository.AuthCodeRepository;
import com.example.template.utils.AesUtils;
import com.example.template.utils.HashUtils;
import com.example.template.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthCodeRepository authCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashUtils hashUtils;
    private final AesUtils aesUtils;
    private final AmazonSnsConfig amazonSnsConfig;

    private static SnsClient snsClient;

    @PostConstruct
    public void initSms() {
        snsClient = SnsClient.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(getAwsCredentials(amazonSnsConfig.getAwsAccessKey(), amazonSnsConfig.getAwsSecretKey()))
                .build();
    }

    public Auth.AuthResponse signUp(MemberEntity memberEntity) {
        if (memberEntity.getId() == wa ) {

        }
    }

    @Transactional(noRollbackFor = CustomResponseStatusException.class)
    public Auth.AuthResponse signIn(MemberEntity memberEntity, String password, String deviceToken) {

        boolean equals = hashUtils.toPasswordHash(password).equals(memberEntity.getPassword());
        // 비밀번호가 틀릴 경우
        if (!equals) {
            memberEntity.setPasswordFailCnt(memberEntity.getPasswordFailCnt() + 1);
            checkPwdFailCnt(memberEntity);
            throw new CustomResponseStatusException(ExceptionCode.SIGN_IN_FAIL, "");
        }
        // 비밀번호가 같을 경우
        else {
            memberEntity.setPasswordFailCnt(0);
        }
        // deviceToken 갱신
        if (null != deviceToken)
            memberEntity.setDeviceToken(deviceToken);

        return getAuthResponse(true, memberEntity, "LOGIN_SUCCESS");
    }

    public Boolean verifyToken(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenByHeader(request);
        Boolean validation = false;
        if (!token.equals("")) {
            validation = jwtTokenProvider.validateToken(token);
        }
        // token이 유효하지 않을 경우
        if (!validation) {
            throw new CustomResponseStatusException(ExceptionCode.INVALID_TOKEN, "");
        }

        return true;
    }

    @Transactional
    public Auth.SmsResponse verifySms(String phone, String authCode) {
        AuthCodeEntity authCodeEntity = authCodeRepository.findByAuthAndCodeAndStatus(aesUtils.encrypt(phone), hashUtils.toPasswordHash(authCode), 0)
                .orElseThrow(() -> new CustomResponseStatusException(ExceptionCode.SMS_WRONG_CODE, ""));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        // 인증시간이 만료될 경우
        if(!now.isBefore(authCodeEntity.getExpiredTime())){
            throw new CustomResponseStatusException(ExceptionCode.SMS_EXPIRED_CODE, "");
        }
        authCodeEntity.setStatus(1);
        return Auth.SmsResponse.builder().result(true).message("VERIFY_SUCCESS").build();
    }

    @Transactional
    public Auth.SmsResponse sendSms(String phone) {
        // 하루 개수 초과했는지 체크
        if (!checkLimitSmsPerDay(phone, 5)) {
            throw new CustomResponseStatusException(ExceptionCode.LIMIT_SMS, "");
        }

        LocalDateTime expired = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5);  // 만료 시간
        String authCode = getAuthCode();
        try {
            PublishRequest request = PublishRequest.builder()
                    .message("[HighTop] Verification code : [" + authCode + "]")
                    .phoneNumber(phone)
                    .build();
            PublishResponse publishResponse = snsClient.publish(request);
            AuthCodeEntity authCodeEntity = createAuthCodeEntity(phone, expired, authCode);
            authCodeRepository.save(authCodeEntity);
        } catch (SnsException e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw new CustomResponseStatusException(ExceptionCode.SMS_SEND_FAIL, "");
        }

        return Auth.SmsResponse.builder()
                .result(true)
                .message("SEND_SMS_SUCCESS")
                .build();
    }

    // 휴대전화 한 개당 하루 전송 가능한 문자 개수 제한
    private Boolean checkLimitSmsPerDay(String phone, Integer limit) {
        List<AuthCodeEntity> list = authCodeRepository.findAllByAuthAndStatusOrderByCreatedTimeDesc(aesUtils.encrypt(phone), 0);
        List<AuthCodeEntity> filterList = list.stream()
                .filter(l -> l.getCreatedTime().isAfter(LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS)))
                .collect(Collectors.toList());
        return filterList.size() < limit;
    }

    private AuthCodeEntity createAuthCodeEntity(String phone, LocalDateTime expired, String authCode) {
        return AuthCodeEntity.builder()
                .code(hashUtils.toPasswordHash(authCode))
                .status(0)
                .expiredTime(expired)
                .type(AuthType.SMS.name())
                .auth(aesUtils.encrypt(phone))
                .build();
    }

    public String getAuthCode() {
        String result = "";
        for (int i=0; i<6; i++) {
            Random random = new Random();
            random.nextInt(10);
            result += String.valueOf(random.nextInt(10));
        }
        return result;
    }

    private AwsCredentialsProvider getAwsCredentials(String accessKeyID, String secretAccessKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyID, secretAccessKey);
        return () -> awsBasicCredentials;
    }

    public Auth.AuthResponse getAuthResponse(Boolean result, MemberEntity memberEntity, String message) {
        if (result)
            return Auth.AuthResponse.builder()
                    .result(result)
                    .token(jwtTokenProvider.createToken(memberEntity.getId(), memberEntity.getType(), memberEntity.getEmail(), 7))
                    .message(message)
                    .build();
        else
            return Auth.AuthResponse.builder()
                    .result(result)
                    .message(message)
                    .build();
    }

    private Integer checkPwdFailCnt(MemberEntity memberEntity) {
        Integer signInFailCount = memberEntity.getPasswordFailCnt();
        if (signInFailCount >= 5)
            throw new CustomResponseStatusException(ExceptionCode.WRONG_PWD_FIVE, "");
        return signInFailCount;
    }
}
