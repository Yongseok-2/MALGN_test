package com.malgn.service;

import com.malgn.domain.Attachment;
import com.malgn.domain.Content;
import com.malgn.dto.*;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.repository.AttachmentRepository;
import com.malgn.repository.ContentRepository;
import com.malgn.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentService commentService;
    private final FileUtil fileUtil;
    private final AttachmentService attachmentService;

    @Transactional
    public Long save(ContentRequestDto requestDto, String username, List<MultipartFile> files) throws IOException{
        Content content = Content.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .createdBy(username)
                .build();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachment attachment = fileUtil.storeFile(file);
                if (attachment != null) {
                    content.addAttachment(attachment);
                }
            }
        }
        return contentRepository.save(content).getId();
    }

    @Transactional
    public ContentResponseDto findById(Long id, Pageable pageable, String currentUsername) {
        Content content = contentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        Page<CommentResponseDto> comments = commentService.findAll(id, pageable, currentUsername);

        List<AttachmentResponseDto> attachments = attachmentService.findByContentId(content.getId());
        content.incrementViewCount();

        return new ContentResponseDto(content, comments, attachments, currentUsername);
    }

    @Transactional
    public void update(Long id, ContentRequestDto requestDto, String username, boolean isAdmin) {
        Content content = contentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!isAdmin && !content.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        content.update(requestDto.getTitle(), requestDto.getDescription());
    }

    @Transactional
    public void delete(Long id, String username, boolean isAdmin) {
        Content content = contentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!isAdmin && !content.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        content.delete();
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDto> findAll(ContentSearchQueryDto searchQuery, Pageable pageable) {

        Page<Content> contentPage = contentRepository.search(searchQuery, pageable);

        return contentPage.map(content -> new ContentResponseDto(content, null, null, null));
    }
}
