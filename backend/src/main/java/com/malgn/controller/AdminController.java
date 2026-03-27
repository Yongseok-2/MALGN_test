package com.malgn.controller;

import com.malgn.dto.ContentResponseDto;
import com.malgn.service.AdminService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/restoreContents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> restoreContents(@RequestBody List<Long> ids, @AuthenticationPrincipal UserDetails userDetails) {
        adminService.restoreContent(ids, checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deletedContents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ContentResponseDto>> findDeletedAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.findDeletedAll(keyword, pageable));
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
