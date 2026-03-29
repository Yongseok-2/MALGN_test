package com.malgn.service;

import com.malgn.domain.Content;
import com.malgn.dto.ContentResponseDto;
import com.malgn.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public Page<ContentResponseDto> findMyContents(String username, Pageable pageable) {
        Page<Content> contentPage = contentRepository.findByCreatedByAndDeletedFalse(username, pageable);

        return contentPage.map(content -> new ContentResponseDto(content, null, null, username));

    }
}
