package com.example.template.service;

import com.example.template.domain.MemberEntity;
import com.example.template.dto.Member;
import com.example.template.exception.CustomResponseStatusException;
import com.example.template.exception.ExceptionCode;
import com.example.template.repository.MemberRepository;
import com.example.template.utils.EncUtils;
import com.example.template.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer";
    private final ModelMapper modelMapper;

    @Transactional
    public Long save(MemberEntity memberEntity) {
        MemberEntity save = memberRepository.save(memberEntity);
        return save.getId();
    }

    @Transactional
    public Long update(HttpServletRequest request, Member.MemberRequest memberRequest) {
        MemberEntity exist = findOneById(request);
        String email = exist.getEmail();
        String name = exist.getName();
        String password = exist.getPassword();
        String phone = exist.getPhone();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        if (password != null) {
            String hashed = EncUtils.toPasswordHash(password);
            if (!hashed.equals(exist.getPassword())) {
                exist.setPassword(hashed);
                exist.setPasswordUpdatedTime(now);
            }
        }
        if (phone != null) {
            if (!phone.equals(exist.getPhone())) {
                exist.setPhone(phone);
            }
        }
        if (name != null) {
            if (!name.equals(exist.getName())) {
                exist.setName(name);
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

    public MemberEntity findOneByEmail(String email) {
        return memberRepository.findByEmailAndDeletedTimeIsNull(email)
                .orElseThrow(()->new CustomResponseStatusException(ExceptionCode.USER_NOT_FOUND,""));
    }

    public MemberEntity findOneById(HttpServletRequest request) {
        long memberId = getMemberIdByClaims(request);
        return memberRepository.findByIdAndDeletedTimeIsNull(memberId)
                .orElseThrow(()->new CustomResponseStatusException(ExceptionCode.USER_NOT_FOUND,""));
    }

    public String getTokenByClaims(HttpServletRequest request) {
        String authorizationToken = request.getHeader(AUTHORIZATION);
        if (null != authorizationToken && authorizationToken.toLowerCase().startsWith(AUTHORIZATION_TYPE.toLowerCase())) {
            return authorizationToken.replace(AUTHORIZATION_TYPE, "").trim();
        }
        return "";
    }

    public Long getMemberIdByClaims(HttpServletRequest request) {
        String authorizationToken = request.getHeader(AUTHORIZATION);
        if (null != authorizationToken && authorizationToken.toLowerCase().startsWith(AUTHORIZATION_TYPE.toLowerCase())) {
            String token = authorizationToken.replace(AUTHORIZATION_TYPE, "").trim();
            Map<String, String> map = jwtTokenProvider.getClaims(token);
            return Long.parseLong(map.get("id"));
        }
        return -1L;
    }
}
