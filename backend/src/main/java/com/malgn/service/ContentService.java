package com.malgn.service;

import com.malgn.domain.Content;
import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
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
    public ContentResponseDto findById(Long id, String currentUsername) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        content.incrementViewCount();

        return new ContentResponseDto(content, currentUsername);
    }

    @Transactional
    public void update(Long id, ContentRequestDto requestDto, String username, boolean isAdmin) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!isAdmin && !content.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        content.update(requestDto.getTitle(), requestDto.getDescription());
    }

    @Transactional
    public void delete(Long id, String username, boolean isAdmin) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!isAdmin && !content.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        contentRepository.delete(content);
    }

    @Transactional(readOnly = true)
    public Page<ContentResponseDto> findAll(String keyword, Pageable pageable) {
        Page<Content> contentPage;

        if(keyword != null && !keyword.isEmpty()) {
            contentPage = contentRepository.findByTitleContaining(keyword, pageable);
        }else {
            contentPage = contentRepository.findAll(pageable);
        }

        return contentPage.map(content -> new ContentResponseDto(content, null));
    }
}
