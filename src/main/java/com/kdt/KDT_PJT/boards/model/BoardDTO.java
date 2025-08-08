package com.kdt.KDT_PJT.boards.model;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

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
    Date regDate;      // 작성일
    Date updDate;      // 수정일
    Integer viewCnt;   // 조회수
    Integer likeCnt;   // 좋아요 수
    boolean isDeleted; // 삭제 여부

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
