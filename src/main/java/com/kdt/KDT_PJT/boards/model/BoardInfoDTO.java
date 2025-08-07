package com.kdt.KDT_PJT.boards.model;

import java.util.Date;

import lombok.Data;

@Data
public class BoardInfoDTO {
	
	/// 게시판 관리 기능 (관리자만 접근)
    private Long boardId;         // 게시판 고유 ID
    private String boardName;     // 게시판명
    private String boardType;     // 게시판 유형 (free, notice, custom)
    private String accessRole;    // 접근 권한자 (예: admin, user)
    private boolean useComment;   // 댓글 사용 여부
    private boolean useLike;      // 좋아요 사용 여부
    private boolean isActive;     // 게시판 활성화 여부
    private Date createdAt;       // 생성일
    private Date updatedAt;       // 수정일
}
