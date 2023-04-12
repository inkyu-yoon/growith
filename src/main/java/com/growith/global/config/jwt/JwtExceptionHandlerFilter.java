package com.growith.global.config.jwt;

import com.growith.global.util.ErrorHandlingUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.growith.global.exception.ErrorCode.*;


@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            ErrorHandlingUtil.sendErrorMessage(response, TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            ErrorHandlingUtil.sendErrorMessage(response, TOKEN_NOT_FOUND);
        } catch (NullPointerException e) {
            filterChain.doFilter(request, response);
        }
    }
}
