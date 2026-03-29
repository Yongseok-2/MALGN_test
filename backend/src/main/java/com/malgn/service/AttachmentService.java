package com.malgn.service;

import com.malgn.dto.AttachmentResponseDto;
import com.malgn.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Transactional(readOnly = true)
    public List<AttachmentResponseDto>  findByContentId(Long contentId) {
        List<AttachmentResponseDto> attachments = attachmentRepository.findByContentId(contentId)
                .stream()
                .map(AttachmentResponseDto::new)
                .toList();
        return attachments;
    }
}
