package com.malgn.service;

import com.malgn.domain.Content;
import com.malgn.dto.CommentResponseDto;
import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
import com.malgn.dto.ContentSearchQuery;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final CommentService commentService;

    @Transactional
    public Long save(ContentRequestDto requestDto, String username) {
        Content content = Content.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .createdBy(username)
                .build();
        return contentRepository.save(content).getId();
    }

    @Transactional
    public ContentResponseDto findById(Long id, Pageable pageable, String currentUsername) {
        Content content = contentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        Page<CommentResponseDto> comments = commentService.findAll(id, pageable, currentUsername);
        content.incrementViewCount();

        return new ContentResponseDto(content, comments, currentUsername);
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
    public Page<ContentResponseDto> findAll(ContentSearchQuery searchQuery, Pageable pageable) {

        Page<Content> contentPage = contentRepository.search(searchQuery, pageable);

        return contentPage.map(content -> new ContentResponseDto(content, null, null));
    }
}
