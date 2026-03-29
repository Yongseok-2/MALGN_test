package com.malgn.controller;

import com.malgn.document.ContentApiDocumentation;
import com.malgn.domain.Attachment;
import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
import com.malgn.dto.ContentSearchQueryDto;
import com.malgn.dto.FileDownloadDto;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.service.AttachmentService;
import com.malgn.service.ContentService;
import com.malgn.util.FileUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Content API", description = "게시글 작성, 조회, 수정, 삭제(Soft Delete) 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;
    private final AttachmentService attachmentService;

    @ContentApiDocumentation.SaveDoc
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> save(
            @RequestPart("content") ContentRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        if (files != null && files.size() > 10) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_LIMIT);
        }

        return ResponseEntity.ok(contentService.save(requestDto, userDetails.getUsername(), files));
    }

    @ContentApiDocumentation.ViewDoc
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentResponseDto> view(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.findById(id, pageable, userDetails.getUsername()));
    }

    @ContentApiDocumentation.UpdateDoc
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody ContentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        contentService.update(id, requestDto, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @ContentApiDocumentation.DeleteDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        contentService.delete(id, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @ContentApiDocumentation.FindAllDoc
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ContentResponseDto>> findAll(
            @ModelAttribute ContentSearchQueryDto searchQuery,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.findAll(searchQuery, pageable));
    }

    @GetMapping("/download/{storeFileName:.+}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable String storeFileName) throws MalformedURLException {

        FileDownloadDto downloadDto = attachmentService.getDownloadFile(storeFileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, downloadDto.getContentDisposition())
                .body(downloadDto.getResource());
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
