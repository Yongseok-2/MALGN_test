package com.malgn.repository;

import com.malgn.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {
    // 삭제되지 않은 것만 페이징 조회
    Page<Content> findAllByDeletedFalse(Pageable pageable);

    // 제목 검색 시 삭제되지 않은 것만(일반 게시판)
    Page<Content> findByTitleContainingAndDeletedFalse(String title, Pageable pageable);

    // 상세 조회 시에도 체크
    Optional<Content> findByIdAndDeletedFalse(Long id);

    // 제목 검색 시 삭제된 것만(관리자 페이지)
    Page<Content> findByTitleContainingAndDeletedTrue(String keyword, Pageable pageable);

    // 삭제된 것만 조회(관리자 페이지)
    Page<Content> findAllByDeletedTrue(Pageable pageable);
}
