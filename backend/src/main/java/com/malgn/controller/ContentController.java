package com.malgn.controller;

import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
import com.malgn.service.ContentService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> save(@RequestBody ContentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.save(requestDto, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentResponseDto> view(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.findById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ContentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {

        contentService.update(id, requestDto, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {

        contentService.delete(id, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ContentResponseDto>> findAll(
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
                return ResponseEntity.ok(contentService.findAll(pageable));
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
