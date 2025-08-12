package com.kdt.KDT_PJT.boards.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    @Autowired
    BoardMapper boardMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    BoardLikeMapper likeMapper;
    
    // /board/{boardId} → NOTICE/FREE는 기존 라우트로, 그 외는 커스텀으로만 보냄
    @GetMapping("/{boardId:\\d+}")
    public String routeByBoardId(@PathVariable("boardId") Integer boardId) {
        String type = boardMapper.findBoardTypeById(boardId); // null이어도 에러 처리 안 함
        if ("NOTICE".equalsIgnoreCase(type)) return "redirect:/board/notice";
        if ("FREE".equalsIgnoreCase(type))   return "redirect:/board/free";
        // 나머지는 전부 커스텀으로
        return "redirect:/board/custom/" + boardId;
    }
    
    // 커스텀 게시판 목록
    @GetMapping("/custom/{boardId:\\d+}")
    public String customBoardList(@PathVariable("boardId") Integer boardId,
                                  BoardDTO dto,
                                  Model model,
                                  HttpSession session) {

        int size = (dto.getSize()==null || dto.getSize()<=0) ? 10 : dto.getSize();
        int page = (dto.getPage()==null || dto.getPage()<=0) ? 1  : dto.getPage();
        dto.setBoardId(boardId);
        dto.setLimit(size);
        dto.setOffset((page-1)*size);

        List<BoardDTO> boards = boardMapper.selectCustomPosts(dto);

        int total = boardMapper.customTotalCnt(boardId);
        int totalPages = Math.max(1, (int)Math.ceil(total/(double)size));

        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        BoardDTO boardMeta = boardMapper.selectBoardById(boardId);
        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();

        model.addAttribute("board", boardMeta); // 현재 보드 정보
        model.addAttribute("canWrite", boardMeta != null && boardMeta.canWriteBy(me));
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("activeBoardId", boardId); // 탭 활성화용
        model.addAttribute("mainUrl", "board/custom_list"); 
        return "home";
    }


    // 커스텀 작성 폼
    @GetMapping("/custom/{boardId:\\d+}/write")
    public String customWriteForm(@PathVariable("boardId") Integer boardId, Model model) {
        model.addAttribute("board", boardMapper.selectBoardById(boardId)); // null이어도 그대로 내려감
        model.addAttribute("boardDTO", new BoardDTO());  
        model.addAttribute("mainUrl", "board/custom_writeform");// 폼 바인딩용
        return "home";                                   // 템플릿 이름
    }

    // 커스텀 게시판 저장
    @PostMapping("/custom/{boardId}/save")
    public String saveCustomPost(@PathVariable("boardId") Integer boardId,
                                 @ModelAttribute BoardDTO dto,
                                 HttpSession session) {

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        dto.setBoardId(boardId);
        dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setCreatedAt(new Date());
        dto.setViewCount(0);
        dto.setLikeCount(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/custom/" + boardId;
    }

    // 커스텀 상세
    @GetMapping("/custom/{boardId:\\d+}/detail")
    public String customDetail(@PathVariable("boardId") Integer boardId,
                               @RequestParam("postId") Integer postId,
                               BoardLikeDTO likeDto,
                               Model model,
                               HttpSession session) {
        BoardDTO post = boardMapper.selectPostById(postId);
        model.addAttribute("board", post);

        EmployeeDto login = (EmployeeDto) session.getAttribute("loginUser");
        String loginEmployeeId = (login != null) ? login.getEmployeeId() : null;

        likeDto.setPostId(postId.longValue());
        likeDto.setEmployeeId(loginEmployeeId);
        boolean likedByMe = loginEmployeeId != null && likeMapper.exists(likeDto);

        // 🔹 게시판 메타에서 댓글/좋아요 사용 여부 내려주기
        BoardDTO boardMeta = boardMapper.selectBoardById(boardId);
        model.addAttribute("useComment", boardMeta != null && boardMeta.useCommentOrFalse());
        model.addAttribute("useLike",    boardMeta != null && boardMeta.useLikeOrFalse());

        model.addAttribute("comments", commentMapper.selectCommentsByPostId(postId.longValue()));
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoardId", boardId);
        model.addAttribute("mainUrl", "board/custom_detail");
        return "home";
    }

    
    // 커스텀 게시글 수정 폼
    @GetMapping("/custom/{boardId:\\d+}/modify")
    public String modifyCustomForm(@PathVariable("boardId") Integer boardId,
                                   BoardDTO dto,   // postId 파라미터 바인딩용
                                   Model model,
                                   HttpSession session) {
        if (dto.getPostId() == null) return "redirect:/board/custom/" + boardId;

        BoardDTO post = boardMapper.detail(dto); // detail(BoardDTO dto) 사용
        if (post == null || post.isDeleted() || !post.getBoardId().equals(boardId)) {
            return "redirect:/board/custom/" + boardId;
        }

        // 권한: 본인만
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (!post.getEmployeeId().equals(loginUser.getEmployeeId())) {
            return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId();
        }

        model.addAttribute("board", post);     
        model.addAttribute("activeBoard", "custom");// 수정 폼에 바인딩
        model.addAttribute("activeBoardId", boardId);   // 탭 활성화용
        model.addAttribute("mainUrl", "board/board_modifyform"); // 자유/공지와 같은 폼 재사용
        return "home";
    }

    // 커스텀 게시글 수정 저장
    @PostMapping("/custom/{boardId:\\d+}/modify")
    public String modifyCustomSubmit(@PathVariable("boardId") Integer boardId,
                                     @ModelAttribute BoardDTO form,
                                     HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

        BoardDTO dto = new BoardDTO();
        dto.setPostId(form.getPostId());
        dto.setTitle(form.getTitle());
        dto.setContent(form.getContent());
        dto.setEmployeeId(loginUser.getEmployeeId()); // 소유자 체크용

        boardMapper.modify(dto); // 자유 게시판에서 쓰던 동일 메서드 사용
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + form.getPostId();
    }
    
    // 커스텀 게시판 삭제 (POST)
    @PostMapping("/custom/{boardId}/delete")
    public String deleteCustomPost(@PathVariable("boardId") Integer boardId,
                                   @RequestParam("postId") Integer postId, // postId 확실히 받기
                                   HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250002".equals(me)) {
            boardMapper.adminDelete(postId);
        } else {
            BoardDTO dto = new BoardDTO();
            dto.setPostId(postId);
            dto.setEmployeeId(me);
            boardMapper.delete(dto);
        }
        return "redirect:/board/custom/" + boardId;
    }

    // 커스텀 게시판 삭제 (GET)
    @GetMapping("/custom/{boardId}/delete")
    public String deleteCustomPostGet(@PathVariable("boardId") Integer boardId,
                                      @RequestParam("postId") Integer postId,
                                      HttpSession session) {
        return deleteCustomPost(boardId, postId, session);
    }
    
    // 커스텀 댓글 등록
    @PostMapping("/custom/{boardId}/comment/save")
    public String saveCustomComment(@PathVariable("boardId") Integer boardId,
                                    @ModelAttribute CommentDTO dto,
                                    HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR

        // dto.postId 는 폼에서 hidden으로 넘어옴
        commentMapper.insertComment(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId();
    }

    // 커스텀 대댓글(답글) 등록
    @PostMapping("/custom/{boardId}/comment/reply")
    public String replyCustomComment(@PathVariable("boardId") Integer boardId,
                                     @ModelAttribute CommentDTO dto,
                                     HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR
        // dto.parentCommentId 가 반드시 세팅되어 있어야 함 (폼 hidden)

        // 필요시 ‘답글에는 또 답글 금지’ 체크를 쓰려면:
        // if (commentMapper.replyComment(dto) == 0) { return ... }  // 스킵 가능

        commentMapper.insertComment(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId();
    }

    // 커스텀 댓글 삭제 (POST로 권장; GET도 원하면 하나 더 추가)
    @PostMapping("/custom/{boardId}/comment/delete")
    public String deleteCustomComment(@PathVariable("boardId") Integer boardId,
                                      @RequestParam("commentId") Long commentId,
                                      @RequestParam("postId") Integer postId) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(commentId);
        commentMapper.deleteComment(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + postId;
    }
    
 // 커스텀 좋아요 토글
    @PostMapping("/custom/{boardId}/like/toggle")
    public String toggleCustomLike(@PathVariable("boardId") Integer boardId,
                                   @ModelAttribute BoardLikeDTO dto,
                                   HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login"; // 필요 없으면 제거
        }
        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR

        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }

        // 실제 like_count와 동기화
        boardMapper.syncLikeCount(dto);

        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId();
    }



    
/******************* 공지게시판 **********************/
    // 공지사항 목록
    @GetMapping("/notice")
    public String noticeList(BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()!=null && dto.getSize()>0) ? dto.getSize() : 10;
        int page = (dto.getPage()!=null && dto.getPage()>0) ? dto.getPage() : 1;
        dto.setLimit(size);
        dto.setOffset((page-1)*size);

        var boards = boardMapper.selectNoticePosts(dto);
        int total   = boardMapper.noticeTotalCnt();
        int totalPages = (int)Math.ceil(total /(double) size);

        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
        BoardDTO boardMeta = boardMapper.selectBoardById(noticeBoardId);
        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();

        model.addAttribute("canWrite", boardMeta != null && boardMeta.canWriteBy(me));
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_list");
        return "home";
    }

    
    // 공지사항 상세보기
    @GetMapping("/notice/detail")
    public String noticeDetail(BoardDTO dto, Model model, HttpSession session) {
        // 승인된 공지만 노출
        BoardDTO board = boardMapper.findNoticeApprovedById(dto);

        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        boolean likedByMe = false;
        if (u != null) {
            BoardLikeDTO likeDto = new BoardLikeDTO();
            likeDto.setPostId(dto.getPostId().longValue());
            likeDto.setEmployeeId(u.getEmployeeId());
            likedByMe = likeMapper.exists(likeDto);
        }

        // 조회수 + 통계
        boardMapper.increaseNoticeView(dto);
        boardMapper.bumpNoticeDailyView();

        // 게시판 메타에서 댓글/좋아요 사용 여부 내려주기
        Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
        BoardDTO boardMeta = boardMapper.selectBoardById(noticeBoardId);
        model.addAttribute("useComment", boardMeta != null && boardMeta.useCommentOrFalse());
        model.addAttribute("useLike",    boardMeta != null && boardMeta.useLikeOrFalse());

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
    
    //게시글 삭제 (POST)
    @PostMapping("/notice/delete")
    public String deleteNotice(@RequestParam("postId") Integer postId,
                             HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250002".equals(me)) {
            // 관리자: postId만으로 삭제
            boardMapper.adminDelete(postId);
        } else {
        }
        return "redirect:/board/notice";
    }

    // 게시글 삭제 저장 (GET)
    @GetMapping("/notice/delete")
    public String deleteNoticeGet(@RequestParam("postId") Integer postId,
                                HttpSession session) {
        return deleteNotice(postId, session);
    }



/******************* 자유게시판 **********************/
    // 자유게시판 목록
    @GetMapping("/free")
    public String freeBoardList(BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()==null || dto.getSize()<=0) ? 10 : dto.getSize();
        int page = (dto.getPage()==null || dto.getPage()<=0) ? 1  : dto.getPage();
        dto.setLimit(size);
        dto.setOffset((page-1)*size);

        List<BoardDTO> boards = boardMapper.selectFreePosts(dto);

        int total = boardMapper.freeTotalCnt();
        int totalPages = Math.max(1, (int)Math.ceil(total/(double)size));

        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        Integer freeBoardId = boardMapper.findBoardIdByType("free");
        BoardDTO boardMeta = boardMapper.selectBoardById(freeBoardId);
        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();

        model.addAttribute("canWrite", boardMeta != null && boardMeta.canWriteBy(me));
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
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

        // 2) 조회수 증가
        if (me != null && !me.isBlank()) {
            dto.setEmployeeId(me);
            boardMapper.recordView(dto);
        }

        // 3) 본문 재조회
        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";

        // 4) 댓글
        List<CommentDTO> comments =
            commentMapper.selectCommentsByPostId(dto.getPostId().longValue());

        // 5) 좋아요 상태
        likeDto.setPostId(dto.getPostId().longValue());
        likeDto.setEmployeeId(me);
        boolean likedByMe = (me != null) && likeMapper.exists(likeDto);

        // 6) 게시판 메타에서 댓글/좋아요 사용 여부 내려주기
        Integer freeBoardId = boardMapper.findBoardIdByType("free");
        BoardDTO boardMeta = boardMapper.selectBoardById(freeBoardId);
        model.addAttribute("useComment", boardMeta != null && boardMeta.useCommentOrFalse());
        model.addAttribute("useLike",    boardMeta != null && boardMeta.useLikeOrFalse());

        // 7) 모델
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
        model.addAttribute("mainUrl", "board/board_modifyform");
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
    
    //게시글 삭제 (POST)
    @PostMapping("/free/delete")
    public String deleteFree(@RequestParam("postId") Integer postId,
                             HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250002".equals(me)) {
            // 관리자: postId만으로 삭제
            boardMapper.adminDelete(postId);
        } else {
            // 본인 글만 삭제 (employee_id는 VARCHAR)
            BoardDTO dto = new BoardDTO();
            dto.setPostId(postId);
            dto.setEmployeeId(me);
            boardMapper.delete(dto);
        }
        return "redirect:/board/free";
    }

    // 게시글 삭제 저장 (GET)
    @GetMapping("/free/delete")
    public String deleteFreeGet(@RequestParam("postId") Integer postId,
                                HttpSession session) {
        return deleteFree(postId, session);
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
