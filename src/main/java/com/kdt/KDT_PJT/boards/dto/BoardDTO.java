package com.kdt.KDT_PJT.boards.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDTO {

    private Integer id;        // 게시글 ID
    private Integer boardId;   // 게시판 ID (자유게시판, 공지사항 등 구분)
    private String authorId;   // 작성자 ID (employeeId 기준)
    private String title;      // 제목
    private String content;    // 내용
    private String pw;         // 비밀번호 (수정/삭제용)
    private String upfile;     // 업로드된 실제 파일 이름
    private Date regDate;      // 작성일
    private Date updDate;      // 수정일
    private Integer viewCnt;   // 조회수
    private Integer likeCnt;   // 좋아요 수
    private boolean isDeleted; // 삭제 여부

    // 업로드용 (사용자 업로드 파일 받아오기)
    private MultipartFile upFF;

    // 줄바꿈 <br/> 처리용 (뷰에 사용)
    public String getContentBr() {
        return content == null ? "" : content.replaceAll("\n", "<br/>");
    }

    // 이미지인지 판단 (뷰에서 미리보기 표시할 때 사용)
    public boolean isImg() {
        if (getUpfile() == null) return false;

        String ext = upfile.toLowerCase();
        ext = ext.substring(ext.lastIndexOf('.') + 1);
        return ext.matches("jpg|jpeg|bmp|png|gif");
    }

    // 공백일 경우 null 처리
    public String getUpfile() {
        if (upfile != null && upfile.trim().equals("")) {
            upfile = null;
        }
        return upfile;
    }

    // 생성자 (테스트/간편 생성용)
    public BoardDTO(String title, String authorId, String content, String pw) {
        this.title = title;
        this.authorId = authorId;
        this.content = content;
        this.pw = pw;
    }
}
