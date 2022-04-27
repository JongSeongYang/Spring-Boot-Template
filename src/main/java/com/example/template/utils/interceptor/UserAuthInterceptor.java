package com.example.template.utils.interceptor;

import com.example.template.exception.CustomResponseStatusException;
import com.example.template.exception.ExceptionCode;
import com.example.template.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println(">>> UserAuthInterceptor.preHandle 호출");
        String token = extract(request, "Bearer");
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        // 토큰이 없을 경우
        if (StringUtils.isEmpty(token)) {
            throw new CustomResponseStatusException(ExceptionCode.UNAUTHORIZED, "");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomResponseStatusException(ExceptionCode.INVALID_TOKEN, "");
        }

        Map<String, String> map = jwtTokenProvider.getClaims(token);
        request.setAttribute("memberId", Integer.parseInt(map.get("id")));
        request.setAttribute("name", map.get("name"));
        request.setAttribute("email", map.get("email"));

        return true;
    }

    private String extract(HttpServletRequest request, String type) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(type.toLowerCase())) {
                return value.substring(type.length()).trim();
            }
        }
        return "";
    }


}
