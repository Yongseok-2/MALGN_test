package com.malgn.repository;

import com.malgn.domain.Attachment;
import com.malgn.dto.AttachmentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByContentId(@Param("contentId") Long contentId);
}
