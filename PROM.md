# 프론트엔드 아키텍처 및 개발 계획 수립 요청

## 1. 역할 (Role)

너는 React, TypeScript, Tailwind CSS 환경의 프론트엔드 아키텍트이자 시니어 개발자야.
내가 아래에 제공하는 [프로젝트 요구사항]과 [REST API 명세서]를 완벽히 숙지해.
그다음 코드를 바로 짜지 말고, 프로젝트의 전체 폴더 구조와 단계별 구현 계획을 먼저 제안해줘.

## 2. 기술 스택 (Tech Stack)

- Build Tool: Vite
- Core: React 18, TypeScript
- Styling: Tailwind CSS (v3)
- Routing: React Router DOM v6
- HTTP Client: Axios
- State Management: Context API 또는 Zustand (설계 시 더 적합한 것을 제안할 것)

## 3. 핵심 도메인 및 요구사항

- Auth: 쿠키(Cookie) 기반 인증 처리 (Axios withCredentials: true 필수 세팅)
- Board: Multipart/form-data 기반의 파일 업로드 기능이 포함된 게시판
- Search: 게시글 단일 조건(제목, 내용, 작성자) 동적 검색 및 페이징 처리
- Comment: 부모 ID 없이 고유 ID만으로 수정/삭제가 이루어지는 Shallow Nesting 구조 대응
- Admin: 다중 ID 배열을 전송하여 논리 삭제된 게시글을 일괄 복구하는 기능

## 4. 진행 원칙 (Action Items)

이 프롬프트를 읽고 다음 두 가지만 먼저 출력해. 절대 전체 코드를 한 번에 짜지 마.

1. 프로젝트 디렉토리 구조 설계도

- src/ 하위의 폴더 구조(components, pages, api, types, hooks 등)를 트리 형태로 보여주고 각 폴더의 역할을 간단히 설명해.
- 재사용 가능한 UI 컴포넌트(버튼, 인풋 등)와 도메인 컴포넌트(게시글 카드 등)를 분리하는 관점을 포함해.

2. 단계별 개발 마일스톤 (Phase 1 ~ Phase N)

- 기초 세팅부터 배포 준비까지, 논리적인 순서로 개발 단계를 나누어 제안해.
- 내가 이 계획에 동의(Confirm)하면, 그때부터 Phase 1의 코드 작성을 시작할 거야.

## 5. REST API 명세서

(참고: 서버 기본 URL은 http://localhost:8888 이며 v1.0.0 API임)

### Auth API

- POST /api/auth/signup: 회원가입 (username, password)
- POST /api/auth/login: 로그인 (성공 시 HttpOnly 쿠키 발급)
- POST /api/auth/logout: 로그아웃

### Content API

- GET /api/content: 게시글 목록/검색 (page, size, sort, type, keyword)
- GET /api/content/{id}: 게시글 상세 (댓글 페이징 포함)
- POST /api/content/add: 게시글 작성 (content(JSON) + files(Multipart))
- PUT /api/content/{id}: 게시글 수정 (title, description)
- DELETE /api/content/{id}: 게시글 삭제(Soft Delete)

### Comment API

- POST /api/content/{contentId}/comments: 댓글 작성
- PATCH /api/comments/{commentId}: 댓글 수정
- DELETE /api/comments/{commentId}: 댓글 삭제

### User & Admin API

- GET /api/contents/me: 내 글 보기
- GET /api/admin/deletedContents: 삭제된 글 목록
- PATCH /api/admin/restoreContents: 복구 (Body: [id 배열])
