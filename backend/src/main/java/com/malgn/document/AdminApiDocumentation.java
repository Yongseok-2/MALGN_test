package com.malgn.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface AdminApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "삭제된 게시글 되돌리기",
            description = "되돌릴 게시글의 id(하나 또는 여러개)를 받아 삭제를 취소합니다",
            parameters = {
                    @Parameter(name = "ids", description = "되돌릴 게시글 id", example = "[1, 2]")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 복구 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "[CT001]해당 콘텐츠를 찾을 수 없습니다.", content = @Content)
    })
    @interface RestoreContentsDoc {
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "삭제된 게시글 조회",
            description = "keyword 입력 시 제목에 해당 keyword를 포함한 삭제된 게시글이 반환됩니다. keyword를 입력하지 않으면 모든 삭제된 게시글을 반환합니다.",
            parameters = {
                    @Parameter(name = "keyword", description = "검색할 단어", example = "테스트", required = false),
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "한 페이지당 게시글 수", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준 (필드명,ASC|DESC)", example = "id,DESC")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content)
    })
    @interface FindDeletedAllDoc {
    }
}