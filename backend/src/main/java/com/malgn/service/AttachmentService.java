package com.malgn.service;

import com.malgn.domain.Attachment;
import com.malgn.dto.AttachmentResponseDto;
import com.malgn.dto.FileDownloadDto;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Value("${file.dir}")
    private String fileDir;

    @Transactional(readOnly = true)
    public List<AttachmentResponseDto>  findByContentId(Long contentId) {
        List<AttachmentResponseDto> attachments = attachmentRepository.findByContentId(contentId)
                .stream()
                .map(AttachmentResponseDto::new)
                .toList();
        return attachments;
    }

    public FileDownloadDto getDownloadFile(String storeFileName) {

        Attachment attachment = attachmentRepository.findByStoreFileName(storeFileName)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        try {
            UrlResource resource = new UrlResource("file:" + fileDir + storeFileName);

            String originalFileName = attachment.getOriginalFileName();
            String encodedUploadFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

            return new FileDownloadDto(resource, contentDisposition);

        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로가 잘못되었습니다.", e);
        }
    }
}
