package com.example.template.service;

import com.example.template.domain.MemberEntity;
import com.example.template.dto.Auth;
import com.example.template.dto.Member;
import com.example.template.enums.MemberType;
import com.example.template.exception.CustomResponseStatusException;
import com.example.template.exception.ExceptionCode;
import com.example.template.repository.MemberRepository;
import com.example.template.utils.AesUtils;
import com.example.template.utils.HashUtils;
import com.example.template.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashUtils hashUtils;
    private final AesUtils aesUtils;
    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long save(MemberEntity memberEntity) {
        MemberEntity save = memberRepository.save(memberEntity);
        return save.getId();
    }

    @javax.transaction.Transactional(dontRollbackOn = CustomResponseStatusException.class)
    public MemberEntity createMember(Auth.SignUpRequest signUpRequest) {
        MemberEntity memberEntity = MemberEntity.builder()
                .type(MemberType.USER.name())
                .deviceToken(signUpRequest.getDeviceToken())
                .email(signUpRequest.getEmail())
                .passwordFailCnt(0)
                .transferPasswordFailCnt(0)
                .phone(aesUtils.encrypt(signUpRequest.getPhone()))
                .password(hashUtils.toPasswordHash(signUpRequest.getPassword()))
                .transferPassword(hashUtils.toPasswordHash(signUpRequest.getTransferPassword()))
                .rewardCode(createRewardCode())
                .build();
        return memberRepository.save(memberEntity);
    }

    @Transactional
    public Long update(HttpServletRequest request, Member.MemberRequest memberRequest) {
        MemberEntity exist = findOneById(request);
        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();
        String phone = memberRequest.getPhone();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        if (null != password) {
            String hashed = hashUtils.toPasswordHash(password);
            if (!hashed.equals(exist.getPassword())) {
                exist.setPassword(hashed);
            }
        }
        if (null != phone) {
            if (!phone.equals(exist.getPhone())) {
                exist.setPhone(phone);
            }
        }
        if (email != null) {
            if (!email.equals(exist.getEmail())) {
                exist.setEmail(email);
            }
        }
        memberRepository.save(exist);
        return exist.getId();
    }

    public MemberEntity findOneByEmail(HttpServletRequest request, ExceptionCode e) {
        String email = jwtTokenProvider.getEmailByClaims(request);
        MemberEntity memberEntity = memberRepository.findByEmailAndDeletedTimeIsNull(email)
                .orElseThrow(() -> new CustomResponseStatusException(e, ""));
        checkMemberStatus(memberEntity);
        return memberEntity;
    }

    public MemberEntity findOneByEmail(String email, ExceptionCode e) {
        MemberEntity memberEntity = memberRepository.findByEmailAndDeletedTimeIsNull(email)
                .orElseThrow(() -> new CustomResponseStatusException(e, ""));
        checkMemberStatus(memberEntity);
        return memberEntity;
    }

    public MemberEntity findOneByPhone(String phone, ExceptionCode e) {
        MemberEntity memberEntity = memberRepository.findByPhoneAndDeletedTimeIsNull(phone)
                .orElseThrow(() -> new CustomResponseStatusException(e, ""));
        checkMemberStatus(memberEntity);
        return memberEntity;
    }

    public Boolean checkDupPhone(String phone) {
        return memberRepository.findByPhoneAndDeletedTimeIsNull(aesUtils.encrypt(phone)).isPresent();
    }

    public Boolean checkDupEmail(String email) {
        return memberRepository.findByEmailAndDeletedTimeIsNull(email).isPresent();
    }

    public MemberEntity findOneById(HttpServletRequest request) {
        long memberId = jwtTokenProvider.getMemberIdByClaims(request);
        MemberEntity memberEntity =  memberRepository.findByIdAndDeletedTimeIsNull(memberId)
                .orElseThrow(()->new CustomResponseStatusException(ExceptionCode.ACCOUNT_NOT_FOUND,""));
        checkMemberStatus(memberEntity);
        return memberEntity;
    }

    public void checkMemberStatus(MemberEntity memberEntity) {
        if (memberEntity.getStatus() == 1)
            throw new CustomResponseStatusException(ExceptionCode.ACCOUNT_SUSPENSION, "");
        if (memberEntity.getStatus() == 2)
            throw new CustomResponseStatusException(ExceptionCode.ACCOUNT_LOCK, "");
    }

    private String createRewardCode() {
        Random rnd = new Random();
        String rewardCode = "";
        // 중복된 rewardCode 생성 시 다시 생성
        while(true){
            StringBuffer buf = new StringBuffer();

            for (int i = 0; i < 10; i++) {
                if (rnd.nextBoolean()) {
                    buf.append((char) ((int) (rnd.nextInt(26)) + 65));
                } else {
                    buf.append((rnd.nextInt(10)));
                }
            }
            MemberEntity memberEntity = memberRepository.findByRewardCode(buf.toString());
            if(null == memberEntity) {
                rewardCode = buf.toString();
                break;
            }
        }
        return rewardCode;
    }
}
