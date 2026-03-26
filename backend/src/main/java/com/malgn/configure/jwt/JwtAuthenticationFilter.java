package com.malgn.configure.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = resolveToken(request, "accessToken");
        String refreshToken = resolveToken(request, "refreshToken");

        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        }else if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {

            Claims claims = jwtTokenProvider.parseClaims(refreshToken);
            String username = claims.getSubject();
            String role = (String) claims.get("role");

            String newAccessToken = jwtTokenProvider.createAccessToken(username, role);

            ResponseCookie cookie = ResponseCookie.from("accessToken", newAccessToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(30 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            setAuthentication(newAccessToken);
        }

        filterChain.doFilter(request, response);
    }

    //쿠키에서 Token 값 가져오기
    private String resolveToken(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
