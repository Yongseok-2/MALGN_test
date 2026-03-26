package com.malgn.configure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyPlain;
    private SecretKey secretKey;

    private final long accessTokenValidTime = 30 * 60 * 1000L; // 30분
    private final long refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L; // 7일

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createAccessToken(String username, String role) {
        return createToken(username, role, accessTokenValidTime);
    }

    // Refresh Token 생성
    public String createRefreshToken(String username, String role) {
        return createToken(username, role, refreshTokenValidTime);
    }

    // 토큰 생성
    private String createToken(String username, String role, long validTime) {
        Claims claims = Jwts.claims().subject(username).add("role", role).build();
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validTime))
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 ID 추출
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // 토큰이 유효한지 체크
    public boolean validateToken(String token) {
        try {
            return !Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
