package com.growith.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import static com.growith.global.util.constant.JwtConstants.USER_ROLE_IN_CLAIM;

public class JwtUtil {


    public static String createToken(String userName, String role, String secretKey, long tokenValidMillis) {

        Claims claims = Jwts.claims()
                .setSubject(userName);

        claims.put(USER_ROLE_IN_CLAIM, role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }


    public static String getUserName(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean isExpired(String token, String secretKey) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);


        return claims.getBody()
                .getExpiration()
                .before(new Date());
    }
}
