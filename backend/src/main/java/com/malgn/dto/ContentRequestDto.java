package com.malgn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 요청 정보")
public class ContentRequestDto {

    @Schema(description = "제목", example = "제목입니다.")
    private String title;

    @Schema(description = "내용", example = "내용입니다.")
    private String description;
}
