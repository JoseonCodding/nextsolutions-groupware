package com.kdt.KDT_PJT.boards.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDTO {


	///글쓰기, 글 목록, 글 수정 등
    Integer postId;        // 게시글 ID
    Integer boardId;   // 게시판 ID (자유게시판, 공지사항 등 구분)
    String employeeId;   // 작성자 ID (employeeId 기준)
    String title;      // 제목
    String content;    // 내용
    Date createdAt;      // 작성일
    Date updatedAt;      // 수정일
    Integer viewCount;   // 조회수
    Integer likeCount;   // 좋아요 수
    boolean isDeleted; // 삭제 여부
    
    // 전자결재를 위해 추가로 작성함 (필규)
    String docType;		// 문서 종류 (ex:프로젝트,공지사항,근태)
    String empNm;		// 사원명 (employee 테이블에서 조인해옴)
    String deptName;	// 부서명 (employee 테이블에서 조인해옴)
    String status;		// 결재 상태 (ex:대기, 진행중, 완료, 반려)
    String docId;		// 문서 번호 (ex:BOARD-0001)


    /* ===== 페이지네이션 ===== */
    private Integer page;         // ?page=2
    private Integer size;         // ?size=10
    private Integer offset;       // 내부 계산용
    private Integer limit;        // 내부 계산용

    /* ===== 보드(게시판) 메타 관리 ===== */
    private String boardName;     // 게시판명
    private String boardType;     // 게시판 타입 (NOTICE / FREE 등)
    private String accessRole;    // 접근 권한
    private Boolean useComment;   // 댓글 사용 여부
    private Boolean useLike;      // 좋아요 사용 여부
    private Boolean isActive;     // 활성화 여부

    /* ===== 기타 ===== */
    public String getContentBr() {
        return content == null ? "" : content.replaceAll("\n", "<br/>");
    }

    public BoardDTO(String title, String employeeId, String content) {
        this.title = title;
        this.employeeId = employeeId;
        this.content = content;
    }
}
