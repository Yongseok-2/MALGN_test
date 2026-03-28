package com.malgn.repository;

import com.malgn.domain.Content;
import com.malgn.dto.ContentSearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {
    Page<Content> search(ContentSearchQuery searchQuery, Pageable pageable);
}
