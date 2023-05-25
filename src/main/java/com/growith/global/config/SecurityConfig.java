package com.growith.global.config;

import com.growith.global.config.jwt.CustomAuthenticationEntryPointHandler;
import com.growith.global.config.jwt.JwtAuthenticationFilter;
import com.growith.global.config.jwt.JwtExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .requestMatchers("/assets/**","/","/oauth2/redirect","/githubLogin/success","/logout","/users/**","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
                .requestMatchers("/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/v1/posts/**").permitAll()
                .requestMatchers("/api/v1/users/mypage").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/products/**").authenticated()
                .requestMatchers(HttpMethod.PUT,"/api/v1/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/api/v1/posts/**").authenticated()
                .requestMatchers("/api/v1/users/**").permitAll()
                .requestMatchers("/**").permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPointHandler())

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(userDetailsService,secretKey), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                .build();
    }

}
