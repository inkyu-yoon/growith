package com.growith.global.config;

import com.growith.global.jwt.JwtAuthenticationFilter;
import com.growith.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("jwt")

                .and()
                .authorizeHttpRequests()
                .requestMatchers("/assets/**","/","/oauth2/redirect","/githubLogin/success","/logout").permitAll()
                .requestMatchers("/api/v1/users/mypage").authenticated()
                .requestMatchers("/api/v1/users/**").permitAll()

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil,userDetailsService,secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
