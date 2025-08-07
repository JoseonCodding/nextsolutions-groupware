package com.kdt.KDT_PJT.boards.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.mapper.CommentMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardInfoDTO;
import com.kdt.KDT_PJT.boards.model.CommentDTO;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardMapper boardMapper;
    @Autowired
    CommentMapper commentMapper;

    // 공지사항 목록
    @GetMapping("/notice")
    public String noticeList(Model model) {
        List<BoardDTO> boards = boardMapper.selectNoticePosts();
        model.addAttribute("boards", boards);
        model.addAttribute("activeBoard", "notice");
    
        model.addAttribute("mainUrl", "board/notice_list");
        return "home";
    }

    // 자유게시판 목록
    @GetMapping("/free")
    public String freeBoardList(
        @RequestParam(name = "sort", required = false) String sort,
        @RequestParam(name = "keyword", required = false) String keyword,
        Model model) {
        
        List<BoardDTO> boards = boardMapper.selectFreePosts();
        model.addAttribute("boards", boards);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeBoard", "free");
        
        model.addAttribute("mainUrl", "board/free_list");
        return "home";
    }

    // 자유게시판 글쓰기 폼
    @GetMapping("/free/write")
    public String writeFreeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "free");
        return "board/free_writeform"; // 이 파일 이름
    }

    // 자유게시판 저장
    @PostMapping("/free/save")
    public String saveFreePost(@ModelAttribute BoardDTO dto) {
        Integer boardId = boardMapper.findBoardIdByType("free");
        dto.setBoardId(boardId);

        dto.setRegDate(new Date());
        dto.setViewCnt(0);
        dto.setLikeCnt(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/free";
    }
    
    // 자유게시판 상세보기
    @GetMapping("/free/detail")
    public String freeDetail(@RequestParam("id") int id, Model model) {
        BoardDTO board = boardMapper.detail(id);
        List<CommentDTO> comments = commentMapper.selectCommentsByPostId((long) id);
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("activeBoard", "free");
        return "board/free_detail";
    }
    
    // 댓글 저장 처리
    @PostMapping("/free/comment/save")
    public String saveComment(
            @RequestParam("postId") Long postId,
            @RequestParam(value = "parentCommentId", required = false) Long parentId,
            @RequestParam("content") String content,
            HttpSession session) {

        // 1. 세션에서 로그인 사용자 정보 꺼내기
        CmmnMap loginUser = (CmmnMap) session.getAttribute("loginUser");
        // Map 구조에 따라 꺼내는 키가 다를 수 있는데, 보통 "employeeId" 로 저장되어 있습니다.
        String authorId = loginUser.getString("employeeId");

        CommentDTO dto = new CommentDTO();
        dto.setPostId(postId);
        dto.setAuthorId(authorId);
        
        // 0이거나 음수면 null로 치환
        if (parentId != null && parentId > 0) {
            dto.setParentCommentId(parentId);
        } else {
            dto.setParentCommentId(null);
        }
        dto.setContent(content);

        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?id=" + postId;
    }
    
    //댓글삭제
    @RequestMapping("/commentDelete")
    public String commentDelete(CommentDTO dto, Model model) {
        commentMapper.deleteComment(dto);
        
        return "redirect:/board/free/detail?id=" + dto.getPostId();
    }

}
