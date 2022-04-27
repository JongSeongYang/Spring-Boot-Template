package com.example.template.controller;

import com.example.template.domain.MemberEntity;
import com.example.template.domain.WalletEntity;
import com.example.template.dto.Auth;
import com.example.template.exception.ExceptionCode;
import com.example.template.service.AuthService;
import com.example.template.service.MemberService;
import com.example.template.service.WalletService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final WalletService walletService;

    @ApiOperation(value = "회원가입", notes = "회원가입")
    @PostMapping(value = "/signUp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.AuthResponse> signUp(@RequestBody Auth.SignUpRequest signUpRequest) {
        MemberEntity memberEntity = memberService.createMember(signUpRequest);
//        WalletEntity walletEntity = walletService.createWallet(memberEntity);
        Auth.AuthResponse response = authService.signUp(memberEntity);
//        Auth.AuthResponse response = authService.signUp(memberEntity, walletEntity);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Token 유효 확인", notes = "Token 유효 확인")
    @GetMapping(value = "/token/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.AuthResponse> tokenCheck(HttpServletRequest request) {
        Boolean validation = authService.verifyToken(request);
        MemberEntity memberEntity = memberService.findOneByEmail(request, ExceptionCode.SIGN_IN_FAIL);
        Auth.AuthResponse response = authService.getAuthResponse(validation, memberEntity, "VERIFY_TOKEN");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "로그인", notes = "로그인")
    @PostMapping(value = "/signIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.AuthResponse> signIn(@RequestBody Auth.SignInRequest signInRequest) {
        MemberEntity memberEntity = memberService.findOneByEmail(signInRequest.getEmail(), ExceptionCode.SIGN_IN_FAIL);
        Auth.AuthResponse response = authService.signIn(memberEntity, signInRequest.getPassword(), signInRequest.getDeviceToken());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "휴대전화 중복 체크", notes = "휴대전화 중복 체크")
    @PostMapping(value = "/sms/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.SmsResponse> checkPhone(@RequestBody Auth.SmsRequest smsRequest) {
        Auth.SmsResponse response = null;
        if(memberService.checkDupPhone(smsRequest.getPhone())){
            // 존재하면
            response = Auth.SmsResponse.builder().result(true).message("EXIST").build();
        } else {
            // 존재하지 않으면
            response = Auth.SmsResponse.builder().result(false).message("NOT_EXIST").build();
        }
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Sms 문자 발송", notes = "Sms 문자 발송")
    @PostMapping(value = "/sms/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.SmsResponse> sendSms(@RequestBody Auth.SmsRequest smsRequest) {
        Auth.SmsResponse response = authService.sendSms(smsRequest.getPhone());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Sms verify", notes = "Sms verify")
    @PostMapping(value = "/sms/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.SmsResponse> verifySms(@RequestBody Auth.SmsVerifyRequest smsVerifyRequest) {
        Auth.SmsResponse response = authService.verifySms(smsVerifyRequest.getPhone(),smsVerifyRequest.getCode());
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "이메일 중복 체크", notes = "이메일 중복 체크")
    @PostMapping(value = "/email/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.EmailResponse> checkPhone(@RequestBody Auth.EmailRequest emailRequest) {
        Auth.EmailResponse response = null;
        if(memberService.checkDupEmail(emailRequest.getEmail())){
            // 존재하면
            response = Auth.EmailResponse.builder().result(true).message("EXIST").build();
        } else {
            // 존재하지 않으면
            response = Auth.EmailResponse.builder().result(false).message("NOT_EXIST").build();
        }
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Email 발송", notes = "Email 발송")
    @PostMapping(value = "/email/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.EmailResponse> sendEmail(@RequestBody Auth.EmailRequest emailRequest) {
        Auth.EmailResponse response = null;
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Email verify", notes = "Email verify")
    @PostMapping(value = "/email/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Auth.EmailResponse> verifyEmail(@RequestBody Auth.EmailVerifyRequest emailVerifyRequest) {
        Auth.EmailResponse response = null;
        return ResponseEntity.ok(response);
    }
}
