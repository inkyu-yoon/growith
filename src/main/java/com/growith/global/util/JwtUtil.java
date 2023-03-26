package com.growith.global.util;

import com.growith.global.util.constant.UserConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.growith.global.util.constant.UserConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {


    public static String createToken(String email, String role, String secretKey, long tokenValidMillis) {

        Claims claims = Jwts.claims()
                .setSubject(email);

        claims.put(USER_ROLE_IN_CLAIM, role);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }


    public String getUserEmail(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String secretKey) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);

        return !claims.getBody()
                .getExpiration()
                .before(new Date());
    }
}
