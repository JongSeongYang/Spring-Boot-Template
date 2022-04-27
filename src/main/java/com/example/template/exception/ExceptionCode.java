package com.example.template.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /* 10000  */
    EMAIL_NOT_FOUND(10001,"이메일이 존재하지 않습니다."),
    SIGN_IN_FAIL(10002,"아이디와 비밀번호가 일치하지 않습니다."),
    WRONG_PWD_FIVE(10003,"비밀번호 입력이 5회 이상 실패했습니다."),
    UNAUTHORIZED(10004, "권한이 없습니다."),
    INVALID_TOKEN(10005, "유효하지 않은 토큰입니다."),
    ACCOUNT_NOT_FOUND(10006, "사용자를 찾을 수 없습니다."),
    ACCOUNT_SUSPENSION(10007, "계정 정지된 사용자입니다."),
    ACCOUNT_LOCK(10008, "계정이 잠긴 사용자입니다."),
    LIMIT_SMS(10009, "하루 전송 가능한 개수를 초과했습니다."),
    SMS_SEND_FAIL(10010, "문자메세지 전송이 실패하였습니다."),
    SMS_WRONG_CODE(10011, "인증코드를 잘못 입력하였습니다."),
    SMS_EXPIRED_CODE(10012, "인증코드가 만료되었습니다."),

    /* 500 internal server error */
    INTERNAL_ERROR(500, "Internal server error")
    ;


    private final int code;
    private final String message;
    private final HttpStatus status = OK;
}
