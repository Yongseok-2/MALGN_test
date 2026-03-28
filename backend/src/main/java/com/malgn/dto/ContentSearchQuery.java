package com.malgn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글 검색 조건 DTO")
public class ContentSearchQuery {

    @Schema(description = "제목 검색어", example = "제목")
    private String title;

    @Schema(description = "내용 검색어", example = "내용")
    private String description;

    @Schema(description = "작성자 검색어", example = "user1")
    private String createdBy;

}