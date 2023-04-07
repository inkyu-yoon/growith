package com.growith.global.config.jwt;

import com.growith.global.util.CookieUtil;
import com.growith.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.growith.global.util.constant.CookieConstants.*;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final UserDetailsService userDetailsService;

    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //쿠키가 없는 경우 그냥 진행
        String token = CookieUtil.getCookie(request, JWT_COOKIE_NAME);
        if (token == null || token.equals(JWT_COOKIE_NAME_DELETED) || JwtUtil.isExpired(token,secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }
        String userName = JwtUtil.getUserName(token, secretKey);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        HttpSession session = request.getSession(true);
        session.setAttribute("userName", userName);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext()
                .setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);

    }
}
