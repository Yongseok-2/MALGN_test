package com.malgn.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface UserApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "내가 작성한 글 조회",
            description = "삭제된 게시글을 제외한 내가 작성한 글의 목록을 가져옵니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "한 페이지당 게시글 수", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준 (필드명,ASC|DESC)", example = "id,DESC")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A001] 로그인이 필요한 서비스입니다. \n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다."),
    })
    @interface FindMyContentsDoc {
    }
}
