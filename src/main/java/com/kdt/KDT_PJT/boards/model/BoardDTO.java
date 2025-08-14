package com.kdt.KDT_PJT.boards.model;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDTO {

    /* ===== 게시글 필드 ===== */
    private Integer postId;       // 게시글 ID
    private Integer boardId;      // 게시판 ID
    private String  employeeId;   // 작성자 ID
    private String  title;        // 제목
    private String  content;      // 내용
    private Date    createdAt;    // 작성일
    private Date    updatedAt;    // 수정일
    private Integer viewCount;    // 조회수
    private Integer likeCount;    // 좋아요 수
    private boolean isDeleted;    // 삭제 여부
    
    /* ===== 목록 필터/정렬용 추가 ===== */
    private String keyword;   // 제목/작성자 통합 검색어
    private String sort;      // newest | views | likes

    /* ===== 전자결재 연계 ===== */
    private String docType;       // 문서 종류 (ex: 프로젝트, 공지사항, 근태)
    private String empNm;         // 사원명
    private String deptName;      // 부서명
    private String status;        // 결재 상태 (대기/진행중/완료/반려)
    private String docId;         // 문서 번호 (ex: BOARD-0001)

    /* ===== 페이지네이션 ===== */
    private Integer page;         // ?page=2
    private Integer size;         // ?size=10
    private Integer offset;       // 내부 계산용
    private Integer limit;        // 내부 계산용

    /* ===== 게시판 메타 ===== */
    private String boardName;     // 게시판명
    private String boardType;     // 게시판 타입 (notice/free/custom) - 비교는 ignoreCase
    /** DB 저장용 CSV("USER,ADMIN,NOTICE_MANAGER") */
    private String accessRole;
    /** 폼 바인딩/뷰 편의용 (중복선택) */
    private List<String> accessRoles;

    private Boolean useComment;   // 댓글 사용 여부
    private Boolean useLike;      // 좋아요 사용 여부
    private Boolean isActive;     // 활성화 여부

    /* ===== 상수 ===== */
    public static final String ROLE_USER            = "USER";
    public static final String ROLE_ADMIN           = "ADMIN";
    public static final String ROLE_NOTICE_MANAGER  = "NOTICE_MANAGER";

    public static final String ADMIN_EMP_ID         = "20250004";
    public static final String NOTICE_MANAGER_EMP_ID= "20250007";

    /* ===== 편의 메서드 ===== */
    public String getContentBr() {
        return content == null ? "" : content.replace("\n", "<br/>");
    }

    /** boardType 헬퍼 */
    public boolean isNotice() { return "notice".equalsIgnoreCase(boardType); }
    public boolean isFree()   { return "free".equalsIgnoreCase(boardType); }
    public boolean isCustom() { return "custom".equalsIgnoreCase(boardType); }

    /** Boolean 널가드 */
    public boolean useCommentOrFalse() { return Boolean.TRUE.equals(useComment); }
    public boolean useLikeOrFalse()    { return Boolean.TRUE.equals(useLike); }
    public boolean isActiveOrFalse()   { return Boolean.TRUE.equals(isActive); }
    public boolean isDeletedOrFalse()  { return Boolean.TRUE.equals(isDeleted); }

    /* ===== accessRole(String CSV) ↔ accessRoles(List) 싱크 ===== */
    /** CSV 직접 세팅 시 캐시 무효화 + 정규화 */
    public void setAccessRole(String csv) {
        this.accessRole = (csv == null) ? "" : csv;
        this.accessRoles = null; // 캐시 무효화
    }

    /** List → CSV 세팅 (대문자 정규화) */
    public void setAccessRoles(List<String> roles) {
        if (roles == null) {
            this.accessRoles = Collections.emptyList();
            this.accessRole = "";
            return;
        }
        this.accessRoles = roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
        this.accessRole = this.accessRoles.isEmpty() ? "" : String.join(",", this.accessRoles);
    }

    /** CSV → List 변환 (캐시) */
    public List<String> getAccessRoles() {
        if (accessRoles != null) return accessRoles;
        if (accessRole == null || accessRole.isBlank()) return Collections.emptyList();
        accessRoles = Arrays.stream(accessRole.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
        return accessRoles;
    }

    /** 글쓰기 가능 여부 판단 (뷰/컨트롤러 양쪽에서 재사용) */
    public boolean canWriteBy(String currentEmpId) {
        final List<String> roles = getAccessRoles();

        // 1) 공지 게시판: 공지담당자만 글쓰기 허용
        if (isNotice()) {
            return NOTICE_MANAGER_EMP_ID.equals(currentEmpId);
            // 만약 "공지 게시판도 accessRole 설정을 따르게" 하려면 아래로 교체:
            // return roles.contains(ROLE_NOTICE_MANAGER) && NOTICE_MANAGER_EMP_ID.equals(currentEmpId);
        }

        // 2) 그 외 게시판: 멀티 권한 합집합
        if (roles.contains(ROLE_USER)) return true; // 모두 허용
        if (roles.contains(ROLE_ADMIN) && ADMIN_EMP_ID.equals(currentEmpId)) return true;
        if (roles.contains(ROLE_NOTICE_MANAGER) && NOTICE_MANAGER_EMP_ID.equals(currentEmpId)) return true;

        return false;
    }

    /* ===== 선택: boardType 입력 정규화 하고 싶으면 사용 ===== */
    public void setBoardType(String boardType) {
        this.boardType = boardType;
        // 필요 시 소문자/대문자 정규화 원하면 아래 한 줄 사용
        // this.boardType = (boardType == null) ? null : boardType.toLowerCase(Locale.ROOT);
    }

    // 선택: 잘못된 키 들어오면 기본값 보정
    public String sortOrDefault() {
        if (sort == null) return "newest";
        switch (sort.toLowerCase()) {
            case "views": return "views";
            case "likes": return "likes";
            default: return "newest";
        }
    }


}
