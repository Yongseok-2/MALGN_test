package com.malgn.repository;

import com.malgn.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findByTitleContaining(String title, Pageable pageable);
}
