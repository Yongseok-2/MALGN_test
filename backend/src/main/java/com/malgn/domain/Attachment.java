package com.malgn.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String originalFileName;

    @Column(nullable = false)
    private String storeFileName;

    @Column(nullable = false, length = 500)
    private String filePath;

    private Long fileSize;
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    public Attachment(String originalFileName, String storeFileName, String filePath, Long fileSize, String contentType) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}