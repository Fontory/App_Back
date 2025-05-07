package com.fontservice.fontory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    public String createToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = resolveToken(request);
        return getUserIdFromToken(token);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 토큰만 추출
        }
        return null;
    }

    public String getUserIdFromToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("JWT 토큰이 없습니다.");
        }

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes()) // HMAC-SHA로 서명 검증
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // 또는 claims.get("userId", String.class);
    }
}