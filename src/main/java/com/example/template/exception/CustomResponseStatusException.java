package com.example.template.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomResponseStatusException extends RuntimeException {
    private final ExceptionCode exceptionCode;
    private final String message;
}
