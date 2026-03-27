package com.malgn.dto;

import com.malgn.domain.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ContentResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final Long viewCount;
    private final String createdBy;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
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
