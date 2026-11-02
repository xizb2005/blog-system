package com.example.demo3.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static String SECRET_KEY;
    private static SecretKey key;
    private static long EXPIRE;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.expiration:86400000}")
    public void setExpiration(long expiration) {
        EXPIRE = expiration;
    }

    public static String generateToken(Long userId, Integer role){
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(key)
                .compact();
    }

    public static Long getUserIdByToken(String token){
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }

    public static Integer getRoleByToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", Integer.class);
    }
}