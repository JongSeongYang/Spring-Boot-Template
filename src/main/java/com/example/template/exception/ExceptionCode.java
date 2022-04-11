package com.example.template.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /* 10000  */
    DELETED_POST(1001, ""),
    DUPLICATED(1002, ""),
    CREATE_FAIL(1003, ""),
    LOCAL_SAVE_FAIL(1004, ""),
    NOT_FOUND(1005, ""),
    UNAUTHORIZED(1006, ""),
    NOT_CHANGE(1007, ""),

    /* 20000 */
    USER_NOT_FOUND(200001, ""),

    /* 500 internal server error */
    INTERNAL_ERROR(500, "Internal server error")
    ;


    private final int code;
    private final String message;
    private final HttpStatus status = OK;
}
