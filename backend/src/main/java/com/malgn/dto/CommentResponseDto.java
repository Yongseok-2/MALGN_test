package com.malgn.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.malgn.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "댓글 응답 정보")
public class CommentResponseDto {

    @Schema(description = "댓글 고유 번호", example = "1")
    private final Long id;

    @Schema(description = "작성자", example = "user1")
    private final String createdBy;

    @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
    private final String text;

    @Schema(description = "작성일시", example = "2026-03-27 20:50:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdDate;

    @Schema(description = "로그인한 사용자가 작성자인지 여부", example = "false")
    private final boolean isAuthor;

    public CommentResponseDto(Comment comment, String currentUsername) {
        this.id = comment.getId();
        this.createdBy = comment.getCreatedBy();
        this.text = comment.getText();
        this.createdDate = comment.getCreatedDate();
        this.isAuthor = comment.getCreatedBy().equals(currentUsername);
    }
}
