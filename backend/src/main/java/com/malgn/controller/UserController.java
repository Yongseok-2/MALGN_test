package com.malgn.controller;

import com.malgn.document.UserApiDocumentation;
import com.malgn.dto.ContentResponseDto;
import com.malgn.service.AuthService;
import com.malgn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "사용자에 관련된 작업을 수행합니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @UserApiDocumentation.FindMyContentsDoc
    @GetMapping("/contents/me")
    public ResponseEntity<Page<ContentResponseDto>> findMyContents(
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {


        return ResponseEntity.ok(userService.findMyContents(userDetails.getUsername(), pageable));
    }
}
