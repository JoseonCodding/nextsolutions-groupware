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
    
    // 공지사항게시판 상세보기
    @GetMapping("/notice/detail")
    public String noticeDetail(BoardDTO dto, Model model, HttpSession session) {
    	 BoardDTO board = boardMapper.detail(dto);
        
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId());
        
        model.addAttribute("board", board);
        model.addAttribute("activeBoard", "notice");
        
        model.addAttribute("mainUrl", "board/notice_detail");
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
        dto.setCreatedAt(new Date());
        dto.setViewCount(0);
        dto.setLikeCount(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/free";
    }
    
    // 자유게시판 상세보기
    @GetMapping("/free/detail")
    public String freeDetail(BoardDTO dto, BoardLikeDTO likeDto,
                             Model model,
                             HttpSession session) {

        // 1) 상세 진입 시 조회수 +1
    	boardMapper.increaseViewCount(dto);
    	
    	// 2) 증가 후 본문 재조회 (DTO 파라미터)
        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";
    	
        // 3) 댓글
        List<CommentDTO> comments = commentMapper.selectCommentsByPostId(dto.getPostId().longValue());

        // 3) 로그인 사용자
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;

        // 4) 좋아요 (필요 최소만 계산: 개수, 내가 눌렀는지)
        likeDto.setPostId(dto.getPostId().longValue());
        likeDto.setEmployeeId(me);

        int likeCount = likeMapper.countByPostId(likeDto);                 // 총 개수
        boolean likedByMe = (me != null) && likeMapper.exists(likeDto);    // 내가 눌렀는지

        // 5) 모델
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
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
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }
    
    // 답글 달기
    @PostMapping("/commentReply")
    public String reply(
    		CommentDTO dto,
            HttpSession session) {

    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
    	dto.setEmployeeId(loginUser.getEmployeeId());
    	
    	commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }
    
    //댓글삭제
    @RequestMapping("/commentDelete")
    public String commentDelete(CommentDTO dto, Model model) {
        commentMapper.deleteComment(dto);
        
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }
    
    //좋아요 저장
    @PostMapping("/free/like/toggle")
    public String toggleLike( BoardLikeDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId());
        
        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        
        // ★ 항상 동기화(가장 안전)
        boardMapper.syncLikeCount(dto);
        
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }
    
    // 게시글 수정
    @GetMapping("/free/modify")
    public String modifyFreeForm(BoardDTO dto,
                                 Model model,
                                 HttpSession session) {
    	if (dto.getPostId() == null) return "redirect:/board/free";

    	BoardDTO board = boardMapper.detail(dto); // detail(BoardDTO dto) 시그니처 기준
        if (board == null || board.isDeleted()) {
            return "redirect:/board/free";
        }
        
        // 권한: 본인만
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (!board.getEmployeeId().equals(loginUser.getEmployeeId())) {
            return "redirect:/board/free/detail?postId=" + dto.getPostId();
        }

        model.addAttribute("board", board);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_modifyform");
        return "home";
    }

    
    // 수정 저장
    @PostMapping("/free/modify")
    public String modifyFreeSubmit(@ModelAttribute BoardDTO form,
                                   HttpSession session,
                                   Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        BoardDTO dto = new BoardDTO();
        dto.setPostId(form.getPostId());
        dto.setTitle(form.getTitle());
        dto.setContent(form.getContent());
        dto.setEmployeeId(loginUser.getEmployeeId()); // 소유자 체크용

        boardMapper.modify(dto);
        return "redirect:/board/free/detail?postId=" + form.getPostId();
    }
    
    //게시글 삭제
    @PostMapping("/free/delete")
    public String deleteFree(@RequestParam("postId") int postId,
                             HttpSession session, 
                             BoardDTO dto, Model model) {
    	
    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
    	
    	dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setPostId(postId);
        dto.setEmployeeId(loginUser.getEmployeeId());
        boardMapper.delete(dto);
  
        return "redirect:/board/free";
    }
    
    @GetMapping("/free/delete")
    public String deleteFreeGet(@RequestParam("postId") int postId,
            					HttpSession session, BoardDTO dto) {
    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

    	dto.setPostId(postId);
    	dto.setEmployeeId(loginUser.getEmployeeId());

    	boardMapper.delete(dto);

    	return "redirect:/board/free";
    }


}
