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
 
    	// 승인된 공지만 노출
    	BoardDTO board = boardMapper.findNoticeApprovedById(dto);
    	
    	// (선택) 좋아요 하트 표시만 필요할 때는 null-safe로
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        boolean likedByMe = false;
        if (u != null) {
            BoardLikeDTO likeDto = new BoardLikeDTO();
            likeDto.setPostId(dto.getPostId().longValue());
            likeDto.setEmployeeId(u.getEmployeeId());
            likedByMe = likeMapper.exists(likeDto);
        }
        
        // 조회수 + 통계 (옵션)
        boardMapper.increaseNoticeView(dto);
        boardMapper.bumpNoticeDailyView();
        
        model.addAttribute("board", board);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_detail");
        return "home";
    }
    
    // 공지사항 좋아요
    @PostMapping("/notice/like/toggle")
    public String toggleNoticeLike(BoardLikeDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setEmployeeId(loginUser.getEmployeeId());

        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        boardMapper.syncLikeCount(dto); // 실제 카운트와 동기화

        return "redirect:/board/notice/detail?postId=" + dto.getPostId();
    }
    
    // 공지사항 글쓰기 폼
    @GetMapping("/notice/write")
    public String writenoticeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "notice");
        
        model.addAttribute("mainUrl", "board/notice_writeform");
        return "home"; // 이 파일 이름
    }
    
    // 공지 저장: 초안만 저장(대기). 상신 단계 없음.
    @PostMapping("/notice/save")
    public String saveNotice(BoardDTO dto, HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        if (u != null) dto.setEmployeeId(u.getEmployeeId());
        boardMapper.insertNoticeDraft(dto);   // status='대기'
        return "redirect:/board/notice";      // 결재관리 페이지가 '대기' 글을 조회
    }

    // 승인 → 완료
    @PostMapping("/notice/approve")
    public String approveNotice(BoardDTO dto) {
        boardMapper.approveNotice(dto);       // status='대기' → '완료', published_at=NOW()
        return "redirect:/board/notice/detail?postId=" + dto.getPostId();
    }

    // 반려 → 반려
    @PostMapping("/notice/reject")
    public String rejectNotice(BoardDTO dto) {
        boardMapper.rejectNotice(dto);        // status='대기' → '반려'
        return "redirect:/board/notice";
    }



/******************* 자유게시판 **********************/
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

    	 // 1) 로그인 사용자 아이디 확보 (계정 기준 1회만 +1)
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;

        // 2) 조회수 증가: 트리거 방식 => 오직 recordView만 호출 (중복이면 0 리턴, +1 안 됨)
        if (me != null && !me.isBlank()) {
            dto.setEmployeeId(me);
            boardMapper.recordView(dto);    // 1: 최초 조회, 0: 이미 조회
            // log.debug("recordView inserted={}", inserted);
        }

        // 3) 본문 재조회
        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";

        // 4) 댓글
        List<CommentDTO> comments =
            commentMapper.selectCommentsByPostId(dto.getPostId().longValue());

        // 5) 좋아요 상태 (개수는 board.likeCount로 화면에서 사용)
        likeDto.setPostId(dto.getPostId().longValue());
        likeDto.setEmployeeId(me);
        boolean likedByMe = (me != null) && likeMapper.exists(likeDto);

        // 6) 모델
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_detail");
        return "home";
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
    public String deleteFree(BoardDTO dto, HttpSession session) {
    	EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;
        dto.setEmployeeId(me);
        if ("20250002".equals(me)) boardMapper.adminDeleteFree(dto);
        else boardMapper.delete(dto);
        return "redirect:/board/free";
    }

    @GetMapping("/free/delete")
    public String deleteFreeGet(BoardDTO dto, HttpSession session) {
    	EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;
        dto.setEmployeeId(me);
        if ("20250002".equals(me)) boardMapper.adminDeleteFree(dto);
        else boardMapper.delete(dto);
        return "redirect:/board/free";
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
    

    

}
