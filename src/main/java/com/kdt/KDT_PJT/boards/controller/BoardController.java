package com.kdt.KDT_PJT.boards.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.boards.mapper.BoardLikeMapper;
import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.mapper.CommentMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;
import com.kdt.KDT_PJT.boards.model.CommentDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardMapper boardMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    BoardLikeMapper likeMapper;

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
        
        model.addAttribute("mainUrl", "board/free_writeform");
        return "home"; // 이 파일 이름
    }

    // 자유게시판 저장
    @PostMapping("/free/save")
    public String saveFreePost(@ModelAttribute BoardDTO dto, HttpSession session) {
    	
    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

    	
        Integer boardId = boardMapper.findBoardIdByType("free");
        dto.setBoardId(boardId);
        dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setRegDate(new Date());
        dto.setViewCnt(0);
        dto.setLikeCnt(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/free";
    }
    
    // 자유게시판 상세보기
    @GetMapping("/free/detail")
    public String freeDetail(@RequestParam("id") int id, Model model, HttpSession session) {
        BoardDTO board = boardMapper.detail(id);
        
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
 
        List<CommentDTO> comments = commentMapper.selectCommentsByPostId((long) id);
        List<BoardLikeDTO> likes = likeMapper.selectLikesByPostId((long)id);
        
        final String me = loginUser != null ? loginUser.getEmployeeId() : null;

        boolean likedByMe = me != null && likes.stream()
                .anyMatch(l -> me.equals(l.getEmployeeId()));
        int likeCount = likes.size();
        
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likes", likes);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoard", "free");
        
        model.addAttribute("mainUrl", "board/free_detail");
        return "home";
    }
    
    // 댓글 저장 처리
    @PostMapping("/free/comment/save")
    public String saveComment(
    		CommentDTO dto,
            HttpSession session) {

        // 1. 세션에서 로그인 사용자 정보 꺼내기
    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
    	
    	dto.setEmployeeId(loginUser.getEmployeeId());
        
        
        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?id=" + dto.getPostId();
    }
    
    // 답글 달기
    @PostMapping("/commentReply")
    public String reply(
    		CommentDTO dto,
            HttpSession session) {

    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
    	dto.setEmployeeId(loginUser.getEmployeeId());
    	
    	commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?id=" + dto.getPostId();
    }
    
    //댓글삭제
    @RequestMapping("/commentDelete")
    public String commentDelete(CommentDTO dto, Model model) {
        commentMapper.deleteComment(dto);
        
        return "redirect:/board/free/detail?id=" + dto.getPostId();
    }
    
    //좋아요 저장
    @PostMapping("/free/like/toggle")
    public String toggleLike( BoardLikeDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        
        loginUser = new EmployeeDto();
    	loginUser.setEmployeeId("20250002");
    	
        dto.setEmployeeId(loginUser.getEmployeeId());
        
        if (likeMapper.exists(dto)) {
        	// 이미 내가 좋아요를 누른 상태라면
            likeMapper.delete(dto);         // 좋아요 기록(행) 삭제
            boardMapper.decrementLikeCnt(dto);
        } else {
            try {
                
            	likeMapper.insert(dto);
            } catch (DuplicateKeyException ignore) {
                // 동시 클릭 등으로 UNIQUE 충돌나면 무시
            }
        }
        return "redirect:/board/free/detail?id=" + dto.getPostId();
    }
    
    

}
