package com.malgn.controller;

import com.malgn.dto.ContentResponseDto;
import com.malgn.service.AuthService;
import com.malgn.service.UserService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/contents/me")
    public ResponseEntity<Page<ContentResponseDto>> findMyContents(
            @PageableDefault(size = 10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {


        return ResponseEntity.ok(userService.findMyContents(userDetails.getUsername(), pageable));
    }
}
