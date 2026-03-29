package com.malgn.document;

import com.malgn.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ContentApiDocumentation {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "게시글 등록",
            description = "제목(title)과 본문(description)을 받아 게시글을 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = @Encoding(name = "content", contentType = MediaType.APPLICATION_JSON_VALUE) // 핵심!
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 등록 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "[F001]파일을 최대 10개까지 업로드 가능합니다.", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content)

    })
    @interface SaveDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "게시글 상세보기",
            description = "게시글의 고유 id를 통해 내용과 정보를 반환합니다. author:true 라면 글 삭제, 수정 가능",
            parameters = {
                    @Parameter(name = "id", description = "조회할 게시글의 ID", example = "1", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "[CT001]해당 콘텐츠를 찾을 수 없습니다.", content = @Content)
    })
    @interface ViewDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "게시글 수정",
            description = "제목(title)과 본문(description)을 받아 게시글을 수정합니다.",
            parameters = {
                    @Parameter(name = "id", description = "조회할 게시글의 ID", example = "1", required = true),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "[CT001]해당 콘텐츠를 찾을 수 없습니다.", content = @Content)
    })
    @interface UpdateDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "게시글 삭제",
            description = "게시글을 삭제합니다. 삭제된 게시물을 DB에서 완전 삭제되지 않으며 관리자는 조회가 가능합니다.",
            parameters = {
                    @Parameter(name = "id", description = "조회할 게시글의 ID", example = "1", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "[CT001]해당 콘텐츠를 찾을 수 없습니다.", content = @Content)
    })
    @interface DeleteDoc {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "모든 게시글 불러오기 및 게시글 검색",
            description = "keyword 입력 시 제목에 해당 keyword를 포함한 게시글이 반환됩니다. keyword를 입력하지 않으면 삭제된 게시글을 제외한 모든 게시글을 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                    @Parameter(name = "size", description = "한 페이지당 게시글 수", example = "10"),
                    @Parameter(name = "sort", description = "정렬 기준 (필드명, ASC|DESC), [예시: id,DESC]", example = "id,DESC")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 불러오기 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "[A001] 로그인이 필요한 서비스입니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 부족 에러:\n" +
                    "- [A002] 토큰이 만료되었습니다. \n" +
                    "- [Z001] 접근 권한이 없습니다.", content = @Content)
    })
    @interface FindAllDoc {
    }
}