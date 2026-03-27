package com.malgn.controller;

import com.malgn.document.ContentApiDocumentation;
import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
import com.malgn.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Content API", description = "게시글 작성, 조회, 수정, 삭제(Soft Delete) 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    @ContentApiDocumentation.SaveDoc
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> save(
            @RequestBody ContentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.save(requestDto, userDetails.getUsername()));
    }

    @ContentApiDocumentation.ViewDoc
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentResponseDto> view(
             @PathVariable Long id,
             @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.findById(id, userDetails.getUsername()));
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
    public ResponseEntity<Page<ContentResponseDto>> findAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
                return ResponseEntity.ok(contentService.findAll(keyword, pageable));
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
