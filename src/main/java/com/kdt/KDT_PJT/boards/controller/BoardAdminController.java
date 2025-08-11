package com.kdt.KDT_PJT.boards.controller;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/boards")
public class BoardAdminController {

    private final BoardMapper boardMapper;

    /** 목록 + 검색/활성필터 (쿼리도 DTO로) */
    @GetMapping
    public String list(@ModelAttribute AdminBoardListReq req, Model model) {
        List<BoardDTO> boards = boardMapper.findBoards(req.getActiveOnly(), req.getQ());
        model.addAttribute("boards", boards);
        model.addAttribute("q", req.getQ());
        model.addAttribute("activeOnly", req.getActiveOnly());
        model.addAttribute("mainUrl", "board/admin_boards");
        return "home";
    }

    /** 보드 생성 (전부 DTO로 수신) */
    @PostMapping("/create")
    public String create(@ModelAttribute BoardDTO dto) {
        if (dto.getBoardName() == null || dto.getBoardName().isBlank())
            throw new IllegalArgumentException("게시판명은 필수입니다.");
        if (boardMapper.countBoardName(dto.getBoardName()) > 0)
            throw new IllegalArgumentException("이미 존재하는 게시판명입니다.");

        // 기본값
        if (dto.getIsActive() == null) dto.setIsActive(true);
        if (dto.getUseComment() == null) dto.setUseComment(true);
        if (dto.getUseLike() == null) dto.setUseLike(true);

        boardMapper.insertBoardMeta(dto);
        return "redirect:/admin/boards";
    }

    /** 보드 수정 (boardId 포함된 DTO만 받음) */
    @PostMapping("/edit")
    public String edit(@ModelAttribute BoardDTO dto) {
        if (dto.getBoardId() == null) throw new IllegalArgumentException("boardId는 필수입니다.");

        BoardDTO existing = boardMapper.findBoardMetaById(dto);
        if (existing == null) throw new IllegalArgumentException("게시판이 없습니다.");
        // 공지 보드는 읽기 전용
        if ("NOTICE".equalsIgnoreCase(existing.getBoardType()))
            throw new IllegalStateException("공지 게시판은 수정할 수 없습니다.");

        // 이름 중복 체크(자기 자신 제외)
        if (dto.getBoardName() != null && !dto.getBoardName().equals(existing.getBoardName())) {
            if (boardMapper.countBoardName(dto.getBoardName()) > 0)
                throw new IllegalArgumentException("이미 존재하는 게시판명입니다.");
        }

        dto.setBoardType(existing.getBoardType()); // 타입은 고정
        boardMapper.updateBoardMeta(dto);
        return "redirect:/admin/boards";
    }

    /** 활성/비활성 토글 (boardId, isActive 담긴 DTO) */
    @PostMapping("/toggle")
    public String toggle(@ModelAttribute BoardDTO dto) {
        if (dto.getBoardId() == null || dto.getIsActive() == null)
            throw new IllegalArgumentException("boardId와 isActive는 필수입니다.");

        boardMapper.toggleBoardActive(dto.getBoardId(), dto.getIsActive());
        return "redirect:/admin/boards";
    }

    /** 목록 필터 DTO */
    public static class AdminBoardListReq {
        private String q;
        private Boolean activeOnly;
        public String getQ() { return q; }
        public void setQ(String q) { this.q = q; }
        public Boolean getActiveOnly() { return activeOnly; }
        public void setActiveOnly(Boolean activeOnly) { this.activeOnly = activeOnly; }
    }
}
