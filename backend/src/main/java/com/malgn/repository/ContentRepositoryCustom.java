package com.malgn.repository;

import com.malgn.domain.Content;
import com.malgn.dto.ContentSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {
    Page<Content> search(ContentSearchQueryDto searchQuery, Pageable pageable);
}
