package com.malgn.dto;

import com.malgn.domain.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "첨부파일 응답 정보")
public class AttachmentResponseDto {

    @Schema(description = "첨부파일 고유 번호", example = "1")
    private Long id;

    @Schema(description = "첨부파일 원본 이름", example = "사진.png")
    private String originalFileName;

    @Schema(description = "첨부파일 저장된 이름", example = "73af9bf9-d0a6-4408-85da-f1a2c723fc6b.png")
    private String storeFileName;

    public AttachmentResponseDto(Attachment attachment) {
        this.id = attachment.getId();
        this.originalFileName = attachment.getOriginalFileName();
        this.storeFileName = attachment.getStoreFileName();
    }
}
