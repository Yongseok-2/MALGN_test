package com.malgn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 정보")
public class SignUpRequest {

    @Schema(description = "아이디", example = "user1")
    private String username;

    @Schema(description = "비밀번호", example = "1234")
    private String password;
}
