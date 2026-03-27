package com.malgn.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface AuthApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "회원가입",
            description = "username과 password를 받아 회원가입을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입을 성공했습니다.", content = @Content),
            @ApiResponse(responseCode = "400", description = "[U001]이미 존재하는 아이디입니다.", content = @Content)
    })
    @interface SignUpDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그인",
            description = "username과 password를 받아 로그인을 처리하고 accessTokenCookie 와 refreshTokenCookie를 헤더에 추가합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인을 성공했습니다. (쿠키 발급)", content = @Content),
            @ApiResponse(responseCode = "403", description = "[A004]로그인 정보가 일치하지 않습니다.", content = @Content)
    })
    @interface LoginDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그아웃",
            description = "accessTokenCookie 와 refreshTokenCookie를 삭제하여 로그아웃을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃을 성공했습니다. (쿠키 삭제)", content = @Content)
    })
    @interface LogoutDoc {
    }
}