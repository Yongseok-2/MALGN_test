package com.malgn.service;

import com.malgn.domain.Comment;
import com.malgn.domain.Content;
import com.malgn.dto.CommentRequestDto;
import com.malgn.dto.CommentResponseDto;
import com.malgn.dto.ContentRequestDto;
import com.malgn.dto.ContentResponseDto;
import com.malgn.exception.BusinessException;
import com.malgn.exception.ErrorCode;
import com.malgn.repository.CommentRepository;
import com.malgn.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public Long save(Long contentId, CommentRequestDto requestDto, String username) {

        Content content = contentRepository.findByIdAndDeletedFalse(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        Comment comment = Comment.builder()
                .text(requestDto.getText())
                .createdBy(username)
                .content(content)
                .build();

        content.addComment(comment);

        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findAll(Long contentId, Pageable pageable, String username) {

        Content content = contentRepository.findByIdAndDeletedFalse(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        Page<Comment> commentPage;

        commentPage = commentRepository.findAllByContentIdAndDeletedFalse(contentId, pageable);

        return commentPage.map(comment -> new CommentResponseDto(comment, username));
    }

    @Transactional
    public void update(Long id, CommentRequestDto requestDto, String username, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if(!isAdmin && !comment.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        comment.update(requestDto.getText());
    }

    @Transactional
    public void delete(Long id, String username, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if(!isAdmin && !comment.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        comment.delete();
    }
}
