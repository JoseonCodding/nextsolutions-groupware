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

    // 줄바꿈 <br/> 처리용 (뷰에 사용)
    public String getContentBr() {
        return content == null ? "" : content.replaceAll("\n", "<br/>");
    }


    // 생성자 (테스트/간편 생성용)
    public BoardDTO(String title, String employeeId, String content, String pw) {
        this.title = title;
        this.employeeId = employeeId;
        this.content = content;
    }
}
