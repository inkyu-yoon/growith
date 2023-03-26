package com.growith.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        //쿠키가 없는 경우 그냥 진행
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue, int cookieAge) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .path("/")
                .httpOnly(true)
                .maxAge(cookieAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
