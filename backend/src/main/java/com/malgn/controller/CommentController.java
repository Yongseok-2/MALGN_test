package com.malgn.controller;

import com.malgn.document.CommentApiDocumentation;
import com.malgn.document.ContentApiDocumentation;
import com.malgn.dto.CommentRequestDto;
import com.malgn.dto.ContentRequestDto;
import com.malgn.service.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API", description = "댓글 작성, 조회, 수정, 삭제(Soft Delete) 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content/{contentId}/comments")
public class CommentController {

    private final CommentService commentService;

        @CommentApiDocumentation.SaveDoc
        @PostMapping
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Long> save(
                @PathVariable Long contentId,
                @RequestBody CommentRequestDto requestDto,
                @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
            return ResponseEntity.ok(commentService.save(contentId, requestDto, userDetails.getUsername()));
    }

    @CommentApiDocumentation.UpdateDoc
    @PatchMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> update(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        commentService.update(commentId, requestDto, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    @CommentApiDocumentation.DeleteDoc
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        commentService.delete(commentId, userDetails.getUsername(), checkAdmin(userDetails));
        return ResponseEntity.ok().build();
    }

    //관리자 권한 확인
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
