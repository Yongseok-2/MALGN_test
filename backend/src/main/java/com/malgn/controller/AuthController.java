package com.malgn.controller;

import com.malgn.document.AuthApiDocumentation;
import com.malgn.dto.LoginRequest;
import com.malgn.dto.SignUpRequest;
import com.malgn.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth API", description = "회원가입, 로그인, 로그아웃 등 인증에 관련된 작업을 수행합니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @AuthApiDocumentation.SignUpDoc
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    @AuthApiDocumentation.LoginDoc
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request,
            @Parameter(hidden = true) HttpServletResponse response) {
        Map<String, String> tokens = authService.login(request);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokens.get("accessToken"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(30 * 60) // 30분
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.get("refreshToken"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일 유효
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok("로그인 성공");
    }

    @AuthApiDocumentation.LogoutDoc
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Parameter(hidden = true) HttpServletResponse response) {
        //만료시간이 0인 쿠키 생성
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
