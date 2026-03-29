package com.malgn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글 검색 조건 DTO (title, description, createdBy 중 하나만 사용 가능)")
public class ContentSearchQueryDto {

    @Schema(description = "검색 조건", implementation = SearchType.class)
    private SearchType type; // ENUM (TITLE, DESCRIPTION, CREATED_BY)

    @Schema(description = "검색어", example = "검색내용")
    private String keyword;

    public enum SearchType {
        TITLE, DESCRIPTION, CREATED_BY
    }
}

