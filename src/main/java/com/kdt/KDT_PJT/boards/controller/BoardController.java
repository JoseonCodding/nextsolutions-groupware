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
import org.springframework.web.bind.annotation.PathVariable;

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

    /* =========================================================
     *  공통: 상단 탭/인덱스/동적 목록
     * ========================================================= */

    // 상단 탭: 활성 보드 전부 주입 (공지/자유 포함)
    @ModelAttribute("navBoards")
    public List<BoardDTO> navBoards() {
        return boardMapper.selectActiveBoards();
    }

    // /board -> 첫 번째 활성 보드로 이동 (없으면 공지로)
    @GetMapping({"", "/"})
    public String boardIndex() {
        List<BoardDTO> active = boardMapper.selectActiveBoards();
        if (active == null || active.isEmpty()) return "redirect:/board/notice";
        return "redirect:/board/" + active.get(0).getBoardId();
    }

    // /board/{boardId} : 목록 라우팅 분기 (FREE/NOTICE는 기존 템플릿, 그 외는 다이나믹)
    @GetMapping("/{boardId:\\d+}")
    public String dynamicBoard(BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()==null || dto.getSize()<=0) ? 10 : dto.getSize();
        int page = (dto.getPage()==null || dto.getPage()<=0) ? 1  : dto.getPage();
        int offset = (page-1)*size;

        BoardDTO meta = boardMapper.findBoardMetaById(dto);
        if (meta == null || Boolean.FALSE.equals(meta.getIsActive())) {
            return "redirect:/board"; // 또는 404 처리 가능
        }

        // 목록/카운트
        List<BoardDTO> boards = boardMapper.selectPostsByBoardId(meta.getBoardId(), size, offset);
        int total = boardMapper.countPostsByBoardId(meta.getBoardId());
        int totalPages = Math.max(1, (int)Math.ceil(total/(double)size));

        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        model.addAttribute("boardMeta", meta);
        model.addAttribute("boardMeta", meta);
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("activeBoard", String.valueOf(meta.getBoardId()));
        
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        model.addAttribute("canWrite", canWriteByAccess(u, meta.getAccessRole()));

        String type = meta.getBoardType() != null ? meta.getBoardType().toUpperCase() : "";
        if ("FREE".equals(type)) {
            model.addAttribute("mainUrl", "board/free_list");
            return "home";
        }
        if ("NOTICE".equals(type)) {
            model.addAttribute("mainUrl", "board/notice_list");
            return "home";
        }

        // 그 외는 다이나믹
        model.addAttribute("mainUrl", "board/dyn_list");
        return "home";
    }

    /* =========================================================
     *  리다이렉트: 공지/자유 목록 → 동적 보드로 포워딩
     * ========================================================= */

    @GetMapping("/notice")
    public String noticeRedirect() {
        Integer id = boardMapper.findBoardIdByType("notice");
        if (id == null) id = boardMapper.findBoardIdByType("NOTICE");
        if (id != null) return "redirect:/board/" + id;
        List<BoardDTO> active = boardMapper.selectActiveBoards();
        return (active != null && !active.isEmpty()) ? "redirect:/board/" + active.get(0).getBoardId() : "redirect:/board";
    }

    @GetMapping("/free")
    public String freeRedirect() {
        Integer id = boardMapper.findBoardIdByType("free");
        if (id == null) id = boardMapper.findBoardIdByType("FREE");
        if (id != null) return "redirect:/board/" + id;
        List<BoardDTO> active = boardMapper.selectActiveBoards();
        return (active != null && !active.isEmpty()) ? "redirect:/board/" + active.get(0).getBoardId() : "redirect:/board";
    }

    /* =========================================================
     *  공지: 상세/좋아요/작성/저장/승인/반려 (기존 유지)
     * ========================================================= */

    // 공지사항 상세보기 (승인된 공지만 노출)
    @GetMapping("/notice/detail")
    public String noticeDetail(BoardDTO dto, Model model, HttpSession session) {
        BoardDTO board = boardMapper.findNoticeApprovedById(dto);

        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        boolean likedByMe = false;
        if (u != null && dto.getPostId() != null) {
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

    // 공지사항 좋아요 토글
    @PostMapping("/notice/like/toggle")
    public String toggleNoticeLike(BoardLikeDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 세션 기준으로 덮어쓰기
        dto.setEmployeeId(loginUser.getEmployeeId());
        if (dto.getPostId() == null) return "redirect:/board/notice";

        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }

        boardMapper.syncLikeCount(dto);
        return "redirect:/board/notice/detail?postId=" + dto.getPostId();
    }

    // 공지사항 글쓰기 폼
    @GetMapping("/notice/write")
    public String writenoticeForm(Model model, HttpSession session) {
    	Integer id = boardMapper.findBoardIdByType("notice");
        if (id == null) id = boardMapper.findBoardIdByType("NOTICE");
        if (id == null) return "redirect:/board";

        BoardDTO meta = new BoardDTO(); meta.setBoardId(id);
        meta = boardMapper.findBoardMetaById(meta);

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (!canWriteByAccess(loginUser, meta.getAccessRole())) return "redirect:/board/" + id;

        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_writeform");
        model.addAttribute("canWrite", true);
        return "home";
    }

    // 공지 저장: 초안만 저장(대기). 상신 단계 없음.
    @PostMapping("/notice/save")
    public String saveNotice(BoardDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) dto.setEmployeeId(loginUser.getEmployeeId());
        boardMapper.insertNoticeDraft(dto);   // status='대기'
        return "redirect:/board/notice";      // 결재관리 페이지가 '대기' 글을 조회
    }

    // 승인 → 완료
    @PostMapping("/notice/approve")
    public String approveNotice(BoardDTO dto) {
        boardMapper.approveNotice(dto); // status='대기' → '완료', published_at=NOW()
        return "redirect:/board/notice/detail?postId=" + dto.getPostId();
    }

    // 반려 → 반려
    @PostMapping("/notice/reject")
    public String rejectNotice(BoardDTO dto) {
        boardMapper.rejectNotice(dto); // status='대기' → '반려'
        return "redirect:/board/notice";
    }

    /* =========================================================
     *  자유: 작성/상세/수정/삭제/댓글/좋아요 (기존 유지)
     * ========================================================= */

    // 자유게시판 글쓰기 폼
    @GetMapping("/free/write")
    public String writeFreeForm(Model model, HttpSession session) {
    	Integer id = boardMapper.findBoardIdByType("free");
        if (id == null) id = boardMapper.findBoardIdByType("FREE");
        if (id == null) return "redirect:/board";

        BoardDTO meta = new BoardDTO(); meta.setBoardId(id);
        meta = boardMapper.findBoardMetaById(meta);

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (!canWriteByAccess(loginUser, meta.getAccessRole())) return "redirect:/board/" + id;
    	
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_writeform");
        return "home";
    }

    // 자유게시판 저장
    @PostMapping("/free/save")
    public String saveFreePost(BoardDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Integer boardId = boardMapper.findBoardIdByType("free");
        if (boardId == null) boardId = boardMapper.findBoardIdByType("FREE");
        if (boardId == null) return "redirect:/board/free"; // 못 찾으면 안전 폴백

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
    public String freeDetail(BoardDTO dto, Model model, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;

        // 조회수 증가: recordView(중복 방지)
        if (me != null && !me.isBlank() && dto.getPostId() != null) {
            dto.setEmployeeId(me);
            boardMapper.recordView(dto);
        }

        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";

        List<CommentDTO> comments = commentMapper.selectCommentsByPostId(dto.getPostId().longValue());

        // 좋아요 상태
        boolean likedByMe = false;
        if (dto.getPostId() != null) {
            BoardLikeDTO likeDto = new BoardLikeDTO();
            likeDto.setPostId(dto.getPostId().longValue());
            likeDto.setEmployeeId(me);
            likedByMe = (me != null) && likeMapper.exists(likeDto);
        }

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_detail");
        return "home";
    }

    // 게시글 수정 폼
    @GetMapping("/free/modify")
    public String modifyFreeForm(BoardDTO dto, Model model, HttpSession session) {
        if (dto.getPostId() == null) return "redirect:/board/free";

        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";

        // 권한: 본인만
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null || !board.getEmployeeId().equals(loginUser.getEmployeeId())) {
            return "redirect:/board/free/detail?postId=" + dto.getPostId();
        }

        model.addAttribute("board", board);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_modifyform");
        return "home";
    }

    // 수정 저장
    @PostMapping("/free/modify")
    public String modifyFreeSubmit(@ModelAttribute BoardDTO form, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BoardDTO dto = new BoardDTO();
        dto.setPostId(form.getPostId());
        dto.setTitle(form.getTitle());
        dto.setContent(form.getContent());
        dto.setEmployeeId(loginUser.getEmployeeId()); // 소유자 체크용

        boardMapper.modify(dto);
        return "redirect:/board/free/detail?postId=" + form.getPostId();
    }

    // 게시글 삭제(POST 전용)
    @PostMapping("/free/delete")
    public String deleteFree(BoardDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;
        dto.setEmployeeId(me);
        if ("20250002".equals(me)) boardMapper.adminDeleteFree(dto);
        else boardMapper.delete(dto);
        return "redirect:/board/free";
    }

    // 댓글 저장 처리
    @PostMapping("/free/comment/save")
    public String saveComment(CommentDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        dto.setEmployeeId(loginUser.getEmployeeId());
        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }

    // 답글 달기
    @PostMapping("/commentReply")
    public String reply(CommentDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        dto.setEmployeeId(loginUser.getEmployeeId());
        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }

    // 댓글 삭제
    @PostMapping("/commentDelete")
    public String commentDelete(CommentDTO dto) {
        commentMapper.deleteComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }

    // 자유게시판 좋아요 토글
    @PostMapping("/free/like/toggle")
    public String toggleLike(BoardDTO dto, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BoardLikeDTO likeDto = new BoardLikeDTO();
        likeDto.setPostId(dto.getPostId() != null ? dto.getPostId().longValue() : null);
        likeDto.setEmployeeId(loginUser.getEmployeeId());

        if (likeMapper.exists(likeDto)) likeMapper.delete(likeDto);
        else likeMapper.insert(likeDto);

        boardMapper.syncLikeCount(likeDto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId();
    }

    /* =========================================================
     *  🔽 추가: 동적 보드 전용 상세/작성/저장/좋아요/댓글/삭제
     *     (FREE/NOTICE는 여기서 자동 우회)
     * ========================================================= */

    // 상세 (쿼리스트링 유지) : /board/{boardId}/detail?postId=###
    @GetMapping("/{boardId:\\d+}/detail")
    public String dynamicDetail(BoardDTO dto, Model model, HttpSession session) {
        if (dto.getBoardId() == null || dto.getPostId() == null) {
            return (dto.getBoardId() != null) ? "redirect:/board/" + dto.getBoardId() : "redirect:/board";
        }
        BoardDTO meta = boardMapper.findBoardMetaById(dto);
        if (meta == null || Boolean.FALSE.equals(meta.getIsActive())) return "redirect:/board";

        String type = meta.getBoardType() != null ? meta.getBoardType().toUpperCase() : "";
        if ("FREE".equals(type))   return "redirect:/board/free/detail?postId="   + dto.getPostId();
        if ("NOTICE".equals(type)) return "redirect:/board/notice/detail?postId=" + dto.getPostId();

        // ↓↓↓ 그 외만 다이나믹 상세 ↓↓↓
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;
        if (me != null && !me.isBlank()) { dto.setEmployeeId(me); boardMapper.recordView(dto); }

        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/" + meta.getBoardId();

        boolean likedByMe = false;
        if (me != null) {
            BoardLikeDTO likeDto = new BoardLikeDTO();
            likeDto.setPostId(dto.getPostId().longValue());
            likeDto.setEmployeeId(me);
            likedByMe = likeMapper.exists(likeDto);
        }
        List<CommentDTO> comments = commentMapper.selectCommentsByPostId(dto.getPostId().longValue());

        model.addAttribute("boardMeta", meta);
        model.addAttribute("board", board);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("likeCount", board.getLikeCount());
        model.addAttribute("comments", comments);
        model.addAttribute("activeBoard", String.valueOf(meta.getBoardId()));
        model.addAttribute("mainUrl", "board/dyn_detail");
        return "home";
    }

    // 작성 폼: /board/{boardId}/write
    @GetMapping("/{boardId:\\d+}/write")
    public String dynamicWriteForm(BoardDTO dto, Model model, HttpSession session) {
        if (dto.getBoardId() == null) return "redirect:/board";
        BoardDTO meta = boardMapper.findBoardMetaById(dto);
        if (meta == null || Boolean.FALSE.equals(meta.getIsActive())) return "redirect:/board";

        String type = meta.getBoardType() != null ? meta.getBoardType().toUpperCase() : "";
        if ("FREE".equals(type))   return "redirect:/board/free/write";
        if ("NOTICE".equals(type)) return "redirect:/board/notice/write";
        
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (!canWriteByAccess(loginUser, meta.getAccessRole())) return "redirect:/board/" + meta.getBoardId();

        model.addAttribute("boardMeta", meta);
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", String.valueOf(meta.getBoardId()));
        model.addAttribute("canWrite", true);
        model.addAttribute("mainUrl", "board/dyn_write");
        return "home";
    }

    // 저장: /board/{boardId}/save  (다이나믹 보드 전용)
    @PostMapping("/{boardId:\\d+}/save")
    public String dynamicSave(BoardDTO dto, HttpSession session) {
    	EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        
        BoardDTO meta = boardMapper.findBoardMetaById(dto);
        if (meta == null || !canWriteByAccess(loginUser, meta.getAccessRole())) {
            return "redirect:/board/" + (dto.getBoardId()!=null? dto.getBoardId(): "");
        }

        // boardId는 DTO에 이미 바인딩됨
        dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setCreatedAt(new Date());
        dto.setViewCount(0);
        dto.setLikeCount(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/" + dto.getBoardId();
    }

    // 좋아요 토글: /board/{boardId}/like/toggle (다이나믹 전용)
    @PostMapping("/{boardId:\\d+}/like/toggle")
    public String toggleDynamicLike(BoardLikeDTO dto, @PathVariable Integer boardId, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null || dto.getPostId() == null) return "redirect:/login";

        dto.setEmployeeId(loginUser.getEmployeeId());
        if (likeMapper.exists(dto)) likeMapper.delete(dto);
        else likeMapper.insert(dto);

        boardMapper.syncLikeCount(dto);
        return "redirect:/board/" + boardId + "/detail?postId=" + dto.getPostId();
    }

    // 댓글 저장: /board/{boardId}/comment/save (다이나믹 전용)
    @PostMapping("/{boardId:\\d+}/comment/save")
    public String saveDynamicComment(CommentDTO dto, @PathVariable Integer boardId, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        if (dto.getPostId() == null) return "redirect:/board/" + boardId;
        dto.setEmployeeId(loginUser.getEmployeeId());
        commentMapper.insertComment(dto);
        return "redirect:/board/" + boardId + "/detail?postId=" + dto.getPostId();
    }

    // 댓글 삭제: /board/{boardId}/comment/delete (다이나믹 전용)
    @PostMapping("/{boardId:\\d+}/comment/delete")
    public String deleteDynamicComment(CommentDTO dto, @PathVariable Integer boardId) {
        if (dto.getPostId() == null) return "redirect:/board/" + boardId;
        commentMapper.deleteComment(dto);
        return "redirect:/board/" + boardId + "/detail?postId=" + dto.getPostId();
    }

    // 삭제: /board/{boardId}/delete (다이나믹 전용, 반드시 POST로 호출)
    @PostMapping("/{boardId:\\d+}/delete")
    public String deleteDynamic(BoardDTO dto, @PathVariable Integer boardId, HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;
        dto.setEmployeeId(me);

        // 기존 삭제 로직 재사용(관리자 강삭제 / 일반 소유자 삭제)
        if ("20250002".equals(me)) boardMapper.adminDeleteFree(dto);
        else boardMapper.delete(dto);

        return "redirect:/board/" + boardId;
    }
    
    // ===== access_role 기반 권한 헬퍼 =====
    private static final String ADMIN_EMP_ID = "20250002";         // 관리자
    private static final String NOTICE_MGR_EMP_ID = "20250003";     // 공지담당자

    /** access_role: USER | ADMIN | NOTICE_MANAGER (null/기타는 USER 취급) */
    private boolean canWriteByAccess(EmployeeDto loginUser, String accessRole) {
        if (loginUser == null) return false;                     // 로그인 필수
        String ar = (accessRole == null ? "USER" : accessRole).toUpperCase();
        switch (ar) {
            case "ADMIN":           return ADMIN_EMP_ID.equals(loginUser.getEmployeeId());
            case "NOTICE_MANAGER":  return NOTICE_MGR_EMP_ID.equals(loginUser.getEmployeeId());
            default:                return true;         // USER(=모두 허용)
        }
    }


    
}
