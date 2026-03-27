package com.malgn.repository;

import com.malgn.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시물의 삭제되지 않은 댓글들만 페이징 조회
    Page<Comment> findAllByContentIdAndDeletedFalse(Long contentId, Pageable pageable);
}
