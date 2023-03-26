package com.growith.global.jwt;

import com.growith.global.util.CookieUtil;
import com.growith.global.util.JwtUtil;
import com.growith.global.util.constant.CookieConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.growith.global.util.constant.CookieConstants.*;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //쿠키가 없는 경우 그냥 진행
        String token = CookieUtil.getCookie(request, JWT_COOKIE_NAME);

        if (token == null || token.equals("deleted") || jwtUtil.validateToken(token,secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtil.getUserEmail(token,secretKey));

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()));

        filterChain.doFilter(request, response);

    }
}
