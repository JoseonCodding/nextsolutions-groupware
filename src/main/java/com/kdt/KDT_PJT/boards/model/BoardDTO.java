package com.kdt.KDT_PJT.boards.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDTO {

    /* ===== 게시글(Post) 관련 ===== */
    private Integer postId;       // 게시글 ID
    private Integer boardId;      // 게시판 ID (자유게시판, 공지사항 등 구분)
    private String employeeId;    // 작성자 ID
    private String title;         // 제목
    private String content;       // 내용
    private Date createdAt;       // 작성일
    private Date updatedAt;       // 수정일
    private Integer viewCount;    // 조회수
    private Integer likeCount;    // 좋아요 수
    private boolean isDeleted;    // 삭제 여부

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
