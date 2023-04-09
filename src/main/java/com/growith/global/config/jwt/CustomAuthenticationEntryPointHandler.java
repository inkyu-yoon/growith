package com.growith.global.config.jwt;

import com.growith.global.util.CookieUtil;
import com.growith.global.util.ErrorHandlingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.growith.global.exception.ErrorCode.TOKEN_NOT_FOUND;
import static com.growith.global.util.constant.CookieConstants.JWT_COOKIE_NAME;

@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String jwt = CookieUtil.getCookie(request, JWT_COOKIE_NAME);
        if (jwt == null) {
            ErrorHandlingUtil.sendErrorMessage(response, TOKEN_NOT_FOUND);
            ErrorHandlingUtil.sendAlert(response);
        }
    }
}