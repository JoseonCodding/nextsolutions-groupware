package com.kdt.KDT_PJT.boards.controller;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Autowired BoardMapper boardMapper;
    @Autowired CommentMapper commentMapper;
    @Autowired BoardLikeMapper likeMapper;

    /* =================== 공통: Summernote HTML 세정 헬퍼 =================== */
    private static final Safelist SAFE = Safelist.basic()
        // 줄바꿈 관련 태그 명시
        .addTags("br","p","pre")
        // 테이블 관련
        .addTags("table","thead","tbody","tfoot","tr","th","td","col","colgroup","caption")
        .addAttributes("table","style","border","cellpadding","cellspacing","width","height")
        .addAttributes("th","style","colspan","rowspan","width","height")
        .addAttributes("td","style","colspan","rowspan","width","height")
        .addAttributes("tr","style")
        .addAttributes("thead","style")
        .addAttributes("tbody","style")
        .addAttributes("tfoot","style")
        .addAttributes("col","style","span","width")
        .addAttributes("colgroup","span","width","style")
        .addAttributes("caption","style")
        /* 이미지/링크
        .addAttributes("img","style","src","alt","width","height")
        .addProtocols("img","src","data","http","https")
        .addTags("a")
        .addAttributes("a","href","title","target","rel")
        .addProtocols("a","href","http","https","mailto")  */
        // 인라인 스타일 허용
        .addAttributes(":all","style");

    private static String sanitize(String html) {
        return Jsoup.clean(html == null ? "" : html, SAFE);
    }

    // 목록 파라미터를 그대로 보존하는 QS 빌더
    private String listQS(BoardDTO q, boolean hasQuery) {
        int page = (q.getPage() == null || q.getPage() < 1) ? 1 : q.getPage();
        int size = (q.getSize() == null || q.getSize() < 1) ? 10 : q.getSize();
        UriComponentsBuilder b = UriComponentsBuilder.newInstance()
            .queryParam("page", page)
            .queryParam("size", size);
        if (q.getSort() != null && !q.getSort().isBlank()) b.queryParam("sort", q.getSort());
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) b.queryParam("keyword", q.getKeyword());

        String qs = b.build().encode(StandardCharsets.UTF_8).toUriString(); // "?page=2&..."
        if (hasQuery) {
            return qs.replaceFirst("^\\?", "&"); // detail처럼 이미 ?가 있으면 &로 변환
        }
        return qs; // 목록처럼 base URL 뒤에 그냥 붙일 때
    }


    /* ===================================================================== */

    // /board/{boardId} → NOTICE/FREE는 기존 라우트로, 그 외는 커스텀으로만 보냄
    @GetMapping("/{boardId:\\d+}")
    public String routeByBoardId(@PathVariable("boardId") Integer boardId) {
        String type = boardMapper.findBoardTypeById(boardId); // null이어도 에러 처리 안 함
        if ("NOTICE".equalsIgnoreCase(type)) return "redirect:/board/notice";
        if ("FREE".equalsIgnoreCase(type))   return "redirect:/board/free";
        // 나머지는 전부 커스텀으로
        return "redirect:/board/custom/" + boardId;
    }
    
    
    @ModelAttribute("navUrl")
	String navUrl() {
		return "board/boardNav";
	}

    /* ============================ 커스텀 게시판 ============================ */

    // 커스텀 게시판 목록
    @GetMapping("/custom/{boardId:\\d+}")
    public String customBoardList(@PathVariable("boardId") Integer boardId, BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()==null || dto.getSize()<=0) ? 10 : dto.getSize();
        int page = (dto.getPage()==null || dto.getPage()<=0) ? 1 : dto.getPage();

        dto.setBoardId(boardId);
        dto.setLimit(size);
        dto.setOffset((page-1)*size);

        // 1) 요청 원본 sort는 따로 보관 (UI 표시용)
        String rawSort = (dto.getSort() == null || dto.getSort().isBlank()) ? null : dto.getSort();

        // 2) 쿼리용 정렬값만 기본치 적용 (예: newest)
        String sortForQuery = (rawSort == null ? "newest" : rawSort);

        // 3) 매퍼 호출 직전에만 쿼리용 값을 세팅
        dto.setSort(sortForQuery);

        List<BoardDTO> boards = boardMapper.selectCustomPosts(dto);
        int total = boardMapper.customTotalCnt(dto);
        int totalPages = Math.max(1, (int)Math.ceil(total/(double)size));
        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage = Math.min(startPage + win - 1, totalPages);

        BoardDTO boardMeta = boardMapper.selectBoardById(boardId);
        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();

        model.addAttribute("offset", dto.getOffset());
        model.addAttribute("keyword", dto.getKeyword());
        
        // 4) 화면에는 원본(raw) sort를 내려보내 “선택”이 선택되도록 함
        model.addAttribute("sort", rawSort == null ? "" : rawSort);
        
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
        
        //return "home";
        return "navTap";
    }

    // 커스텀 상세
    @GetMapping("/custom/{boardId:\\d+}/detail")
    public String customDetail(@PathVariable("boardId") Integer boardId,
                               @RequestParam("postId") Integer postId,
                               @ModelAttribute BoardDTO q,
                               BoardLikeDTO likeDto,
                               Model model,
                               HttpSession session) {

        // 1) 조회수 증가 (로그인 최초 1회만, 비로그인은 매번)
        EmployeeDto login = (EmployeeDto) session.getAttribute("loginUser");
        String me = (login != null) ? login.getEmployeeId() : null;

        boolean needIncrease = false;
        if (me != null && !me.isBlank()) {
            BoardDTO v = new BoardDTO();
            v.setPostId(postId);
            v.setEmployeeId(me);
            int inserted = boardMapper.recordView(v); // UNIQUE(post_id, employee_id) 가정
            if (inserted > 0) needIncrease = true; // 최초 1회만 +1
        } else {
            needIncrease = true; // 비로그인은 매번 +1
        }
        if (needIncrease) {
            BoardDTO v = new BoardDTO();
            v.setPostId(postId);
            boardMapper.increaseViewCount(v); // 게시글 view_count +1
            boardMapper.upsertBoardDailyView(boardId); // 보드 일일 통계 +1
        }

        // 2) 본문/메타/댓글/좋아요 상태 조회
        BoardDTO post = boardMapper.selectPostById(postId); // 증가 반영 후 재조회
        model.addAttribute("board", post);

        likeDto.setPostId(postId.longValue());
        likeDto.setEmployeeId(me);
        int likeCount = likeMapper.countByPostId(likeDto);
        boolean likedByMe = (me != null) && likeMapper.exists(likeDto);

        // 게시판 메타에서 댓글/좋아요 사용 여부
        BoardDTO boardMeta = boardMapper.selectBoardById(boardId);

        // 상세보기 들어갔다와도 페이지 그대로
        int page = (q.getPage()==null || q.getPage()<1) ? 1 : q.getPage();
        int size = (q.getSize()==null || q.getSize()<1) ? 10 : q.getSize();

        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", q.getSort());
        model.addAttribute("keyword", q.getKeyword());

        model.addAttribute("useComment", boardMeta != null && boardMeta.useCommentOrFalse());
        model.addAttribute("useLike", boardMeta != null && boardMeta.useLikeOrFalse());
        model.addAttribute("comments", commentMapper.selectCommentsByPostId(postId.longValue()));
        int commentCount = commentMapper.countAliveByPostId(postId.longValue());
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("me", me);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("activeBoardId", boardId);
        model.addAttribute("mainUrl", "board/custom_detail");
        return "navTap";
    }

    // 커스텀 작성 폼
    @GetMapping("/custom/{boardId:\\d+}/write")
    public String customWriteForm(@PathVariable("boardId") Integer boardId, Model model) {
        model.addAttribute("board", boardMapper.selectBoardById(boardId)); // null이어도 그대로 내려감
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("mainUrl", "board/custom_writeform");// 폼 바인딩용
        return "navTap";// 템플릿 이름
    }

    // 커스텀 게시글 저장
    @PostMapping("/custom/{boardId}/save")
    public String saveCustomPost(@PathVariable("boardId") Integer boardId,
                                 @ModelAttribute BoardDTO dto,
                                 HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setContent(sanitize(dto.getContent()));
        dto.setBoardId(boardId);
        dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setCreatedAt(new Date());
        dto.setViewCount(0);
        dto.setLikeCount(0);
        dto.setDeleted(false);
        boardMapper.insert(dto);

        return "redirect:/board/custom/" + boardId;
    }

    // 커스텀 게시글 수정 폼
    @GetMapping("/custom/{boardId:\\d+}/modify")
    public String modifyCustomForm(@PathVariable("boardId") Integer boardId,
                                   BoardDTO dto, // postId 파라미터 바인딩 + 목록QS 유지
                                   Model model,
                                   HttpSession session) {
        if (dto.getPostId() == null) return "redirect:/board/custom/" + boardId;

        BoardDTO post = boardMapper.detail(dto); // detail(BoardDTO dto) 사용
        if (post == null || post.isDeleted() || !post.getBoardId().equals(boardId)) {
            return "redirect:/board/custom/" + boardId;
        }

        // 권한: 본인만
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null || !post.getEmployeeId().equals(loginUser.getEmployeeId())) {
        	return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId();
        }

        // ✅ 목록 파라미터를 모델에 주입 (hidden에 쓰일 값)
        int page = (dto.getPage()==null || dto.getPage()<1) ? 1 : dto.getPage();
        int size = (dto.getSize()==null || dto.getSize()<1) ? 10 : dto.getSize();
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", dto.getSort());
        model.addAttribute("keyword", dto.getKeyword());
        
        model.addAttribute("board", post);
        model.addAttribute("activeBoard", "custom");// 수정 폼에 바인딩
        model.addAttribute("activeBoardId", boardId); // 탭 활성화용
        model.addAttribute("mainUrl", "board/board_modifyform"); // 자유/공지와 같은 폼 재사용
        return "navTap";
    }

    // 커스텀 게시글 수정 저장
    @PostMapping("/custom/{boardId:\\d+}/modify")
    public String modifyCustomSubmit(@PathVariable("boardId") Integer boardId,
                                     @ModelAttribute BoardDTO dto,
                                     HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BoardDTO submit = new BoardDTO();
        submit.setPostId(dto.getPostId());
        submit.setTitle(dto.getTitle());
        submit.setContent(sanitize(dto.getContent()));
        submit.setEmployeeId(loginUser.getEmployeeId()); // 소유자 체크용
        boardMapper.modify(submit);

        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId() + listQS(dto, true);
    }

    // 커스텀 게시판 삭제 (POST) → 리스트로 복귀
    @PostMapping("/custom/{boardId}/delete")
    public String deleteCustomPost(@PathVariable("boardId") Integer boardId,
                                   @RequestParam("postId") Integer postId, // postId 확실히 받기
                                   @ModelAttribute BoardDTO q,
                                   HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250004".equals(me)) {
            boardMapper.adminDelete(postId);
        } else {
            BoardDTO dto = new BoardDTO();
            dto.setPostId(postId);
            dto.setEmployeeId(me);
            boardMapper.delete(dto);
        }
        return "redirect:/board/custom/" + boardId + listQS(q, false);
    }

    // 커스텀 게시판 삭제 (GET) → 리스트로 복귀
    @GetMapping("/custom/{boardId}/delete")
    public String deleteCustomPostGet(@PathVariable("boardId") Integer boardId,
                                      @RequestParam("postId") Integer postId,
                                      @ModelAttribute BoardDTO q,
                                      HttpSession session) {
        return deleteCustomPost(boardId, postId, q, session);
    }

    // 커스텀 댓글 등록
    @PostMapping("/custom/{boardId}/comment/save")
    public String saveCustomComment(@PathVariable("boardId") Integer boardId,
                                    @ModelAttribute CommentDTO dto,
                                    @ModelAttribute BoardDTO q,
                                    HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR
        commentMapper.insertComment(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    // 커스텀 대댓글(답글) 등록
    @PostMapping("/custom/{boardId}/comment/reply")
    public String replyCustomComment(@PathVariable("boardId") Integer boardId,
                                     @ModelAttribute CommentDTO dto,
                                     @ModelAttribute BoardDTO q,
                                     HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR
        commentMapper.insertComment(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    // 커스텀 댓글 삭제 (POST)
    @PostMapping("/custom/{boardId}/comment/delete")
    public String deleteCustomComment(@PathVariable("boardId") Integer boardId,
                                      @RequestParam("commentId") Long commentId,
                                      @RequestParam("postId") Integer postId,
                                      @ModelAttribute BoardDTO q,
                                      HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        String me = loginUser.getEmployeeId(); // ✅ 본인만 삭제 (소유자 불일치면 0건 업데이트)
        int affected = commentMapper.deleteByOwner(commentId, me);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + postId + listQS(q, true);
    }

    // 커스텀 좋아요 토글
    @PostMapping("/custom/{boardId}/like/toggle")
    public String toggleCustomLike(@PathVariable("boardId") Integer boardId,
                                   @ModelAttribute BoardLikeDTO dto,
                                   @ModelAttribute BoardDTO q,
                                   HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) { return "redirect:/login"; }

        dto.setEmployeeId(loginUser.getEmployeeId()); // VARCHAR
        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        boardMapper.syncLikeCount(dto);
        return "redirect:/board/custom/" + boardId + "/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    /******************* 공지게시판 **********************/

    // 공지사항 목록
    @GetMapping("/notice")
    public String noticeList(BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()!=null && dto.getSize()>0) ? dto.getSize() : 10;
        int page = (dto.getPage()!=null && dto.getPage()>0) ? dto.getPage() : 1;

        // 1) 요청 원본 sort는 따로 보관 (UI 표시용)
        String rawSort = (dto.getSort() == null || dto.getSort().isBlank()) ? null : dto.getSort();

        // 2) 쿼리용 정렬값만 기본치 적용 (예: newest)
        String sortForQuery = (rawSort == null ? "newest" : rawSort);
        
        // ► 총 건수 먼저 구함 (정렬과 무관)
        int total = boardMapper.noticeTotalCnt(dto);

        // ► 총 페이지: 최소 1 보장
        int totalPages = Math.max(1, (int)Math.ceil(total / (double) size));

        // ► 현재 페이지 보정(1..totalPages)
        page = Math.min(Math.max(1, page), totalPages);

        // ► offset 재계산 후 DTO 세팅
        int offset = (page - 1) * size;
        dto.setLimit(size);
        dto.setOffset(offset);
        // 3) 매퍼 호출 직전에만 쿼리용 값을 세팅
        dto.setSort(sortForQuery);

        // ► 목록 조회
        var boards = boardMapper.selectNoticePosts(dto);
        // ► 페이지네이션 계산
        int win = 5;
        int startPage = ((page - 1) / win) * win + 1;
        int endPage   = Math.min(startPage + win - 1, totalPages);

        Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
        BoardDTO boardMeta = boardMapper.selectBoardById(noticeBoardId);
        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();
        
        // 4) 화면에는 원본(raw) sort를 내려보내 “선택”이 선택되도록 함
        model.addAttribute("sort", rawSort == null ? "" : rawSort);

        model.addAttribute("canWrite", boardMeta != null && boardMeta.canWriteBy(me));
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("offset", dto.getOffset());
        model.addAttribute("keyword", dto.getKeyword());
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_list");
        return "navTap";
    }

    // 공지사항 상세보기
    @GetMapping("/notice/detail")
    public String noticeDetail(BoardDTO dto, Model model, HttpSession session) {
        // 1) 조회수 증가 (로그인 최초 1회만, 비로그인은 매번)
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        boolean needIncrease = false;
        if (me != null && dto.getPostId() != null) {
            BoardDTO v = new BoardDTO();
            v.setPostId(dto.getPostId());
            v.setEmployeeId(me);
            int inserted = boardMapper.recordView(v);
            if (inserted > 0) needIncrease = true;
        } else if (dto.getPostId() != null) {
            needIncrease = true;
        }
        if (needIncrease) {
            boardMapper.increaseNoticeView(dto); // board_id=1, status='완료' 조건 포함
            Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
            if (noticeBoardId != null) boardMapper.upsertBoardDailyView(noticeBoardId);
        }

        // 2) 본문 재조회 (증가 반영)
        BoardDTO board = boardMapper.findNoticeApprovedById(dto);
        model.addAttribute("board", board);

        // 3) 좋아요 상태
        boolean likedByMe = false;
        if (u != null && dto.getPostId() != null) {
            BoardLikeDTO likeDto = new BoardLikeDTO();
            likeDto.setPostId(dto.getPostId().longValue());
            likeDto.setEmployeeId(u.getEmployeeId());
            likedByMe = likeMapper.exists(likeDto);
        }
        model.addAttribute("likedByMe", likedByMe);

        // 4) 메타 (없으면 true)
        Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
        BoardDTO boardMeta = (noticeBoardId == null) ? null : boardMapper.selectBoardById(noticeBoardId);
        boolean useLike = (boardMeta != null) ? boardMeta.useLikeOrFalse() : true;
        boolean useComment = (boardMeta != null) ? boardMeta.useCommentOrFalse() : true;
        model.addAttribute("useLike", useLike);
        model.addAttribute("useComment", useComment);

        // 5) 좋아요 카운트
        int likeCount = 0;
        if (dto.getPostId() != null) {
            BoardLikeDTO countDto = new BoardLikeDTO();
            countDto.setPostId(dto.getPostId().longValue());
            likeCount = likeMapper.countByPostId(countDto);
        }

        // ✅ 목록 상태 유지용 파라미터
        int page = (dto.getPage()==null || dto.getPage()<1) ? 1 : dto.getPage();
        int size = (dto.getSize()==null || dto.getSize()<1) ? 10 : dto.getSize();
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", dto.getSort());
        model.addAttribute("keyword", dto.getKeyword());

        model.addAttribute("likeCount", likeCount);
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_detail");
        return "navTap";
    }

    // 공지사항 좋아요
    @PostMapping("/notice/like/toggle")
    public String toggleNoticeLike(@ModelAttribute BoardLikeDTO dto,
    							   @ModelAttribute BoardDTO q,
                                   HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setEmployeeId(loginUser.getEmployeeId());

        Integer noticeBoardId = boardMapper.findBoardIdByType("notice");
        BoardDTO meta = (noticeBoardId == null) ? null : boardMapper.selectBoardById(noticeBoardId);
        boolean likeEnabled = (meta != null) ? meta.useLikeOrFalse() : true; // 메타가 비어도 기본 허용
        if (!likeEnabled) {
        	return "redirect:/board/notice/detail?postId=" + dto.getPostId();
        }

        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        boardMapper.syncLikeCount(dto);

        return "redirect:/board/notice/detail?postId=" + dto.getPostId()+ listQS(q, true);
    }

    // 공지사항 글쓰기 폼
    @GetMapping("/notice/write")
    public String writenoticeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "notice");
        model.addAttribute("mainUrl", "board/notice_writeform");
        return "navTap";
    }

    // 공지 저장: 초안만 저장(대기). 상신 단계 없음.
    @PostMapping("/notice/save")
    public String saveNotice(@ModelAttribute BoardDTO dto,
                             HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        if (u != null) dto.setEmployeeId(u.getEmployeeId());
        dto.setContent(sanitize(dto.getContent()));
        boardMapper.insertNoticeDraft(dto); // status='대기'
        return "redirect:/board/notice";
    }

    // 승인 → 완료
    @PostMapping("/notice/approve")
    public String approveNotice(@ModelAttribute BoardDTO dto) {
        boardMapper.approveNotice(dto); // status='대기' → '완료', published_at=NOW()
        return "redirect:/board/notice/detail?postId=" + dto.getPostId();
    }

    // 반려 → 반려
    @PostMapping("/notice/reject")
    public String rejectNotice(@ModelAttribute BoardDTO dto) {
        boardMapper.rejectNotice(dto); // status='대기' → '반려'
        return "redirect:/board/notice";
    }

    // 공지 게시글 삭제 (POST) → 리스트로 복귀
    @PostMapping("/notice/delete")
    public String deleteNotice(@RequestParam("postId") Integer postId,
                               @ModelAttribute BoardDTO q,
                               HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250004".equals(me)) {
            boardMapper.adminDelete(postId);
        } else {
            // 필요 시 일반 사용자 삭제 로직 추가
        }
        return "redirect:/board/notice" + listQS(q, false);
    }

    // 게시글 삭제 (GET) → 리스트로 복귀
    @GetMapping("/notice/delete")
    public String deleteNoticeGet(@RequestParam("postId") Integer postId,
                                  @ModelAttribute BoardDTO q,
                                  HttpSession session) {
        return deleteNotice(postId, q, session);
    }

    /******************* 자유게시판 **********************/

    // 자유게시판 목록
    @GetMapping("/free")
    public String freeBoardList(BoardDTO dto, Model model, HttpSession session) {
        int size = (dto.getSize()==null || dto.getSize()<=0) ? 10 : dto.getSize();
        int page = (dto.getPage()==null || dto.getPage()<=0) ? 1 : dto.getPage();

        dto.setLimit(size);
        dto.setOffset((page-1)*size);

        // 1) 요청 원본 sort는 따로 보관 (UI 표시용)
        String rawSort = (dto.getSort() == null || dto.getSort().isBlank()) ? null : dto.getSort();

        // 2) 쿼리용 정렬값만 기본치 적용 (예: newest)
        String sortForQuery = (rawSort == null ? "newest" : rawSort);

        // 3) 매퍼 호출 직전에만 쿼리용 값을 세팅
        dto.setSort(sortForQuery);

        List<BoardDTO> boards = boardMapper.selectFreePosts(dto);
        int total = boardMapper.freeTotalCnt(dto);
        int totalPages = Math.max(1, (int)Math.ceil(total/(double)size));
        int win = 5;
        int startPage = ((page-1)/win)*win + 1;
        int endPage = Math.min(startPage + win - 1, totalPages);

        Integer freeBoardId = boardMapper.findBoardIdByType("free");
        BoardDTO boardMeta = boardMapper.selectBoardById(freeBoardId);

        String me = null;
        var loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser != null) me = loginUser.getEmployeeId();
        
        // 4) 화면에는 원본(raw) sort를 내려보내 “선택”이 선택되도록 함
        model.addAttribute("sort", rawSort == null ? "" : rawSort);

        model.addAttribute("canWrite", boardMeta != null && boardMeta.canWriteBy(me));
        model.addAttribute("boards", boards);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("offset", dto.getOffset());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", dto.getKeyword());
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_list");
        return "navTap";
    }

    // 자유게시판 글쓰기 폼
    @GetMapping("/free/write")
    public String writeFreeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_writeform");
        return "navTap";
    }

    // 자유게시판 저장
    @PostMapping("/free/save")
    public String saveFreePost(@ModelAttribute BoardDTO dto,
                               HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Integer boardId = boardMapper.findBoardIdByType("free");
        dto.setBoardId(boardId);
        dto.setEmployeeId(loginUser.getEmployeeId());
        dto.setContent(sanitize(dto.getContent()));
        dto.setCreatedAt(new Date());
        dto.setViewCount(0);
        dto.setLikeCount(0);
        dto.setDeleted(false);
        boardMapper.insert(dto);

        return "redirect:/board/free";
    }

    // 자유게시판 상세보기
    @GetMapping("/free/detail")
    public String freeDetail(@RequestParam("postId") Integer postId, // ✅ 명시적으로 받기
                             BoardDTO dto,
                             BoardLikeDTO likeDto,
                             Model model,
                             HttpSession session) {
        if (postId == null) return "redirect:/board/free"; // ✅ 가드
        dto.setPostId(postId); // ✅ DTO에 주입

        // 1) 로그인 사용자 아이디 확보 (계정 기준 1회만 +1)
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String me = (loginUser != null) ? loginUser.getEmployeeId() : null;

        // 2) 조회수 증가 (로그인 최초 1회만, 비로그인은 매번)
        boolean needIncrease = false;
        if (me != null && !me.isBlank()) {
            dto.setEmployeeId(me);
            int inserted = boardMapper.recordView(dto); // UNIQUE(post_id, employee_id) 가정
            if (inserted > 0) needIncrease = true; // 최초 1회만 +1
        } else {
            needIncrease = true; // 비로그인
        }

        if (needIncrease) {
            boardMapper.increaseViewCount(dto); // UPDATE board_post SET view_count = view_count + 1 WHERE post_id = #{postId}
            // postId → boardId 얻어서 일일 통계 +1
            Integer bid = null;
            Long b = boardMapper.findBoardIdByPostId(dto.getPostId().longValue());
            if (b != null) bid = b.intValue();
            if (bid != null) boardMapper.upsertBoardDailyView(bid); // ✅ boardId만 받는 버전
        }

        // 3) 본문 재조회 (증가 반영)
        BoardDTO board = boardMapper.detail(dto);
        if (board == null || board.isDeleted()) return "redirect:/board/free";

        // 4) 댓글
        List<CommentDTO> comments = commentMapper.selectCommentsByPostId(dto.getPostId().longValue());
        int commentCount = commentMapper.countAliveByPostId(dto.getPostId().longValue());
        model.addAttribute("commentCount", commentCount);

        // 5) 좋아요 상태
        likeDto.setPostId(dto.getPostId().longValue());
        likeDto.setEmployeeId(me);
        boolean likedByMe = (me != null) && likeMapper.exists(likeDto);
        int likeCount = likeMapper.countByPostId(likeDto);

        // 6) 게시판 메타
        Integer freeBoardId = boardMapper.findBoardIdByType("free");
        BoardDTO boardMeta = (freeBoardId != null) ? boardMapper.selectBoardById(freeBoardId) : null;
        model.addAttribute("useComment", boardMeta != null && boardMeta.useCommentOrFalse());
        model.addAttribute("useLike", boardMeta != null && boardMeta.useLikeOrFalse());

        // ✅ 목록 상태 유지용 파라미터 추가
        int page = (dto.getPage()==null || dto.getPage()<1) ? 1 : dto.getPage();
        int size = (dto.getSize()==null || dto.getSize()<1) ? 10 : dto.getSize();
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", dto.getSort());
        model.addAttribute("keyword", dto.getKeyword());

        // 7) 모델
        model.addAttribute("me", me);
        model.addAttribute("board", board); // ← viewCount 증가 반영됨
        model.addAttribute("comments", comments);
        model.addAttribute("likedByMe", likedByMe);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/free_detail");
        return "navTap";
    }

    // 게시글 수정 폼
    @GetMapping("/free/modify")
    public String modifyFreeForm(BoardDTO dto, Model model, HttpSession session) {
        if (dto.getPostId() == null) return "redirect:/board/free";

        BoardDTO board = boardMapper.detail(dto); // detail(BoardDTO dto) 시그니처 기준
        if (board == null || board.isDeleted()) {
            return "redirect:/board/free";
        }

        // 권한: 본인만
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null || !board.getEmployeeId().equals(loginUser.getEmployeeId())) {
        	return "redirect:/board/free/detail?postId=" + dto.getPostId();
        }
        
        // ✅ 목록 파라미터 모델 주입 (hidden에 쓰임)
        int page = (dto.getPage()==null || dto.getPage()<1) ? 1 : dto.getPage();
        int size = (dto.getSize()==null || dto.getSize()<1) ? 10 : dto.getSize();
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", dto.getSort());
        model.addAttribute("keyword", dto.getKeyword());

        model.addAttribute("board", board);
        model.addAttribute("activeBoard", "free");
        model.addAttribute("mainUrl", "board/board_modifyform");
        return "navTap";
    }

    // 수정 저장
    @PostMapping("/free/modify")
    public String modifyFreeSubmit(@ModelAttribute BoardDTO form,
    							   @ModelAttribute BoardDTO q,
                                   HttpSession session,
                                   Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        BoardDTO dto = new BoardDTO();
        dto.setPostId(form.getPostId());
        dto.setTitle(form.getTitle());
        dto.setContent(sanitize(form.getContent()));
        dto.setEmployeeId(loginUser.getEmployeeId()); // 소유자 체크용
        boardMapper.modify(dto);

        return "redirect:/board/free/detail?postId=" + form.getPostId() + listQS(form, true);
    }

    // 게시글 삭제 (POST) → 리스트로 복귀
    @PostMapping("/free/delete")
    public String deleteFree(@RequestParam("postId") Integer postId,
                             @ModelAttribute BoardDTO q,
                             HttpSession session) {
        EmployeeDto u = (EmployeeDto) session.getAttribute("loginUser");
        String me = (u != null) ? u.getEmployeeId() : null;

        if ("20250004".equals(me)) {
            // 관리자: postId만으로 삭제
            boardMapper.adminDelete(postId);
        } else {
            // 본인 글만 삭제 (employee_id는 VARCHAR)
            BoardDTO dto = new BoardDTO();
            dto.setPostId(postId);
            dto.setEmployeeId(me);
            boardMapper.delete(dto);
        }
        return "redirect:/board/free" + listQS(q, false);
    }

    // 게시글 삭제 (GET) → 리스트로 복귀
    @GetMapping("/free/delete")
    public String deleteFreeGet(@RequestParam("postId") Integer postId,
                                @ModelAttribute BoardDTO q,
                                HttpSession session) {
        return deleteFree(postId, q, session);
    }

    // 댓글 저장 처리
    @PostMapping("/free/comment/save")
    public String saveComment(@ModelAttribute CommentDTO dto,
    						  @ModelAttribute BoardDTO q,
                              HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId());
        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    // 답글 달기
    @PostMapping("/free/commentReply")
    public String reply(@ModelAttribute CommentDTO dto,
    				    @ModelAttribute BoardDTO q,
                        HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        dto.setEmployeeId(loginUser.getEmployeeId());
        commentMapper.insertComment(dto);
        return "redirect:/board/free/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    // 댓글삭제
    @PostMapping("/commentDelete")
    public String commentDelete(@ModelAttribute CommentDTO dto,
    						    @ModelAttribute BoardDTO q,
                                Model model,
                                HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        String me = loginUser.getEmployeeId();
        int affected = commentMapper.deleteByOwner(dto.getCommentId(), me);
        return "redirect:/board/free/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

    // 좋아요 토글(자유)
    @PostMapping("/free/like/toggle")
    public String toggleLike(@ModelAttribute BoardLikeDTO dto,
    					     @ModelAttribute BoardDTO q,
                             HttpSession session) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dto.setEmployeeId(loginUser.getEmployeeId());
        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        // ★ 항상 동기화(가장 안전)
        boardMapper.syncLikeCount(dto);

        return "redirect:/board/free/detail?postId=" + dto.getPostId() + listQS(q, true);
    }

}
