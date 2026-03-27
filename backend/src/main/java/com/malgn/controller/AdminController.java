package com.malgn.controller;

import com.malgn.document.AdminApiDocumentation;
import com.malgn.dto.ContentResponseDto;
import com.malgn.service.AdminService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin API", description = "관리자 페이지의 기능(삭제된 게시글 조회, 삭제된 게시물 되돌리기)을 수행합니다.")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class AdminController {

    private final AdminService adminService;

    @AdminApiDocumentation.RestoreContentsDoc
    @PatchMapping("/restoreContents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> restoreContents(
            @RequestBody List<Long> ids,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        adminService.restoreContent(ids, checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @AdminApiDocumentation.FindDeletedAllDoc
    @GetMapping("/deletedContents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ContentResponseDto>> findDeletedAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(adminService.findDeletedAll(keyword, pageable, checkAdmin(userDetails)));
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
