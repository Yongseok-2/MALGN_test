package com.malgn.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 에러가 발생했습니다."),

    // User
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 아이디입니다."),

    // Authentication
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "로그인이 필요한 서비스입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A004", "로그인 정보가 일치하지 않습니다."),

    // Authorization
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Z001", "접근 권한이 없습니다."),

    // Content
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "해당 콘텐츠를 찾을 수 없습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "해당 댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}