package com.malgn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 작성 / 수정 요청")
public class CommentRequestDto {

    @Schema(description = "댓글 내용", example = "댓글 내용입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;
}
