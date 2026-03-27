package com.malgn.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "공통 에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "에러 발생 일자", example = "2026-03-27 17:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "HTTP 상태 코드", example = "404")
    private final int status;

    @Schema(description = "에러 코드", example = "CONTENT_NOT_FOUND")
    private final String code;

    @Schema(description = "에러 메시지", example = "해당 게시글을 찾을 수 없습니다.")
    private final String message;

    // ErrorCode를 인자로 받는 정적 생성 메서드
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }
}