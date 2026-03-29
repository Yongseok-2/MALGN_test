package com.malgn.dto;

import com.malgn.domain.Role;
import com.malgn.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 인증 응답 정보")
public class UserInfoResponseDto {
    private String username;
    private String role;
}
