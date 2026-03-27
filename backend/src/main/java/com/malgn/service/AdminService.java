package com.malgn.service;

import com.malgn.domain.Content;
import com.malgn.dto.ContentResponseDto;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ContentRepository contentRepository;

    @Transactional
    public void restoreContent(List<Long> ids, boolean isAdmin) {
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        List<Content> contents = contentRepository.findAllById(ids);

        contents.forEach(content -> {
            if (content.isDeleted()) {
                content.restore();
            }
        });
    }

    public Page<ContentResponseDto> findDeletedAll(String keyword, Pageable pageable, boolean isAdmin) {
        Page<Content> contentPage;

        if (!isAdmin) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if(keyword != null && !keyword.isEmpty()) {
            contentPage = contentRepository.findByTitleContainingAndDeletedTrue(keyword, pageable);
        }else {
            contentPage = contentRepository.findAllByDeletedTrue(pageable);
        }

        return contentPage.map(content -> new ContentResponseDto(content, null, null));
    }
}
