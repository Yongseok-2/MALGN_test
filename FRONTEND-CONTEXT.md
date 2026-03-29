# 📑 MALGN CMS REST API - 프론트엔드 인수인계 명세서

## 1. 프로젝트 및 서버 기본 정보

- API 서버 기본 URL: http://localhost:8888
- API 버전: v1.0.0
- 인증 방식: 쿠키(Cookie) 기반 인증
- 통신 주의사항: 로그인 후 발급되는 accessTokenCookie와 refreshTokenCookie 처리를 위해 클라이언트 요청 시 반드시 withCredentials: true 설정이 필요합니다.

## 2. API 엔드포인트 총정리

### 2.1 Auth API (회원가입/로그인)

- POST /api/auth/signup: 회원가입
  - Body: username, password 전송
- POST /api/auth/login: 로그인
  - Body: username, password 전송 (성공 시 헤더에 쿠키 발급)
- POST /api/auth/logout: 로그아웃
  - 인증 쿠키 삭제 처리

### 2.2 Content API (게시글 처리)

- GET /api/content: 게시글 목록 전체 조회 및 검색
  - Query Params: page(기본 0), size(기본 10), sort, type(TITLE, DESCRIPTION, CREATED_BY 중 택 1), keyword
- GET /api/content/{id}: 게시글 상세 보기
  - Path Variable: id
  - Query Params: page, size, sort (해당 글의 댓글 페이징 용도)
- POST /api/content/add: 게시글 작성 (파일 업로드 포함)
  - Content-Type: multipart/form-data
  - Body: content(JSON 데이터), files(바이너리 파일 배열, 최대 10개)
- PUT /api/content/{id}: 게시글 수정
  - Body: title, description 전송
- DELETE /api/content/{id}: 게시글 삭제 (논리 삭제 처리)

### 2.3 Comment API (댓글 처리)

- POST /api/content/{contentId}/comments: 특정 게시글에 댓글 작성
  - Body: text 전송
- PATCH /api/comments/{commentId}: 댓글 수정 (Shallow Nesting 적용)
  - Body: text 전송
- DELETE /api/comments/{commentId}: 댓글 삭제 (Shallow Nesting 적용)

### 2.4 User API (사용자 개인 기능)

- GET /api/contents/me: 로그인한 사용자가 작성한 글 목록 조회
  - Query Params: page, size, sort

### 2.5 Admin API (관리자 전용 기능)

- GET /api/admin/deletedContents: 삭제된 게시글 목록 조회
  - Query Params: keyword, page, size, sort
- PATCH /api/admin/restoreContents: 삭제된 게시글 일괄 복구
  - Body: [1, 2, 3] 형태의 복구할 ID 배열 전송

## 3. 핵심 DTO (Data Transfer Object) 구조

```json
// 게시글 작성 및 수정 (ContentRequestDto)
{
  "title": "제목입니다.",
  "description": "내용입니다."
}

// 댓글 작성 및 수정 (CommentRequestDto)
{
  "text": "댓글 내용입니다."
}

// 회원가입 및 로그인 (SignUpRequestDto / LoginRequestDto)
{
  "username": "user1",
  "password": "password123"
}
```

## 4. 프론트엔드 작업 시 필수 참고 가이드

- Multipart 전송 규칙: /api/content/add 호출 시, content 부분은 application/json 타입의 Blob으로 감싸고, files는 File 객체의 배열 형태로 폼 데이터(FormData)에 append 하여 전송해야 합니다.
- 에러 코드 핸들링:
  - 400: 유효성 검사 실패 (예: 파일 10개 초과, 중복 아이디)
  - 401: 로그인 필요 (인증되지 않은 사용자)
  - 403: 권한 부족 (토큰 만료, 또는 해당 글/댓글에 대한 권한 없음, 비밀번호 불일치)
  - 404: 리소스 없음 (게시글이나 댓글을 찾을 수 없음)
- 동적 검색 처리: 게시글 목록 화면에서 검색창 구현 시, 사용자가 드롭다운으로 type을 선택하고 입력창에 keyword를 넣도록 UI를 구성해야 합니다.
- REST 경로 단순화: 댓글 수정(PATCH)과 삭제(DELETE)는 부모 게시글의 ID(contentId) 없이, 수정/삭제할 댓글의 고유 ID(commentId)만 URL 경로에 넣어 호출해야 합니다.
