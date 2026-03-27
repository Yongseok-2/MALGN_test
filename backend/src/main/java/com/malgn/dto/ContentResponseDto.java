package com.malgn.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.malgn.domain.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Schema(description = "게시글 응답 정보")
public class ContentResponseDto {

    @Schema(description = "게시글 고유 번호", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "제목입니다.")
    private final String title;

    @Schema(description = "내용", example = "내용입니다.")
    private final String description;

    @Schema(description = "조회수", example = "1")
    private final Long viewCount;

    @Schema(description = "작성자", example = "user1")
    private final String createdBy;

    @Schema(description = "작성 일자", example = "2026-03-27 17:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdDate;

    @Schema(description = "마지막 수정 일자", example = "2026-03-27 17:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime lastModifiedDate;

    @Schema(description = "로그인한 사용자가 작성자인지 여부", example = "false")
    private final boolean isAuthor;

    public ContentResponseDto(Content content, String currentUsername) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.description = content.getDescription();
        this.viewCount = content.getViewCount();
        this.createdBy = content.getCreatedBy();
        this.createdDate = content.getCreatedDate();
        this.lastModifiedDate = content.getLastModifiedDate();
        this.isAuthor = content.getCreatedBy().equals(currentUsername);
    }
}
