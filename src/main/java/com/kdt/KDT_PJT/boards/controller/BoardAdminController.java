package com.kdt.KDT_PJT.boards.controller;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/boards")
@RequiredArgsConstructor
public class BoardAdminController {

    @Autowired
    private BoardMapper boardMapper;

    /* =========================
       1) 게시판 목록/상세/신규 폼
       ========================= */

    // 전체 게시판 관리 목록
    @GetMapping
    public String list(Model model) {
        // ✔ 이미 있는 메서드명으로 맞춰 사용
        // 예) selectAllBoards() 또는 selectBoards()
        List<BoardDTO> boards = boardMapper.selectBoards(); // 없으면 selectAllBoards() 같은 네이밍으로 사용
        model.addAttribute("boards", boards);
        model.addAttribute("mainUrl", "board/admin_boards");
        return "home";
    }

    // 신규 생성 폼
    @GetMapping("/new")
    public String newForm(Model model) {
        BoardDTO dto = new BoardDTO();
        dto.setBoardType("custom"); // 기본값
        dto.setAccessRoles(Collections.singletonList("USER")); // 기본: 전체 글쓰기 허용
        dto.setUseComment(Boolean.TRUE);
        dto.setUseLike(Boolean.TRUE);
        dto.setIsActive(Boolean.TRUE);

        model.addAttribute("board", dto);
        model.addAttribute("mainUrl", "board/admin_boards"); // 공용 폼(신규/수정 겸용)
        return "home";
    }

    // 수정 폼
    @GetMapping("/edit")
    public String editForm(@PathVariable Integer boardId, Model model) {
        BoardDTO dto = boardMapper.selectBoardById(boardId);
        if (dto == null) return "redirect:/admin/boards";

        // accessRole CSV -> accessRoles(List) 동기화는 BoardDTO.getAccessRoles()가 처리
        model.addAttribute("board", dto);
        model.addAttribute("mainUrl", "board/admin_boards");
        return "home";
    }

    /* =========================
       2) 생성/수정/활성화/비활성화
       ========================= */

    // 생성
    @PostMapping("/create")
    public String create(@ModelAttribute BoardDTO form) {
        // 체크박스 바인딩: name="accessRoles" 로 들어오면 자동 List 바인딩
        // BoardDTO.setAccessRoles(...)가 CSV 세팅까지 수행
        normalizeBoardMeta(form);

        // 공지/자유는 이미 있으니 신규 생성은 custom 위주를 권장 (막고 싶으면 아래 조건 추가)
        if (!"custom".equalsIgnoreCase(form.getBoardType())) form.setBoardType("custom");

        boardMapper.insertBoard(form);

        return "redirect:/admin/boards";
    }

    // 수정
    @PostMapping("/edit")
    public String update(@RequestParam("boardId") Integer boardId,
            			 @ModelAttribute BoardDTO form) {
        BoardDTO origin = boardMapper.selectBoardById(boardId);
        if (origin == null) return "redirect:/admin/boards";

        normalizeBoardMeta(form);
        form.setBoardId(boardId);

        // 읽기 전용 처리(선택): 공지/자유의 boardType은 잠그고 싶다면 주석 해제
        // if ("notice".equalsIgnoreCase(origin.getBoardType()) || "free".equalsIgnoreCase(origin.getBoardType())) {
        //     form.setBoardType(origin.getBoardType());
        // }

        boardMapper.updateBoard(form);

        return "redirect:/admin/boards";
    }

    // 활성화/비활성 토글
    @PostMapping("/toggle")
    public String toggleActive(@RequestParam("boardId") Integer boardId,
            				   @RequestParam("isActive") Boolean isActive) {
        BoardDTO dto = boardMapper.selectBoardById(boardId);
        if (dto == null) return "redirect:/admin/boards";

        boardMapper.updateBoardActive(boardId, Boolean.TRUE.equals(isActive) ? 1 : 0);
        return "redirect:/admin/boards";
    }

    // 삭제(실제 스키마엔 is_deleted가 없으므로 비활성화로 대체하는 걸 권장)
    @PostMapping("/delete")
    public String delete(@PathVariable Integer boardId) {
        BoardDTO dto = boardMapper.selectBoardById(boardId);
        if (dto == null) return "redirect:/admin/boards";

        // 권장: 비활성화로 대체
        boardMapper.updateBoardActive(boardId, 0);

        // 물리 삭제가 꼭 필요하면 별도 메서드에서 외래키 제약/자식데이터 고려해 구현
        // boardMapper.deleteBoard(boardId);

        return "redirect:/admin/boards";
    }

    /* =========================
       3) 조회수 통계 (옵션)
       ========================= */

    // 전체 게시판 조회수 통계
    @GetMapping("/stats")
    public String stats(Model model) {
        // ✔ 이미 있는 메서드명으로 맞춰 사용
        // 예) selectBoardViewStats() : board_view_stats join board_board
        model.addAttribute("stats", boardMapper.selectBoardViewStats());
        //model.addAttribute("mainUrl", "board/board_admin_stats");
        model.addAttribute("mainUrl", "board/admin_boards");
        
        return "home";
    }

    /* =========================
       4) 내부 유틸
       ========================= */

    /** 폼에서 넘어온 값들을 안전하게 정규화 */
    private void normalizeBoardMeta(BoardDTO form) {
        // boardType 정규화
        if (form.getBoardType() == null || form.getBoardType().isBlank()) {
            form.setBoardType("custom");
        }

        // accessRoles(List) -> accessRole(CSV) 는 BoardDTO.setAccessRoles가 처리
        // 만약 accessRoles가 null인데 accessRole이 채워져 온 경우(수정폼 직후 등)를 대비
        if (form.getAccessRoles() == null || form.getAccessRoles().isEmpty()) {
            // 아무것도 체크 안 했으면 기본 USER 허용(원치 않으면 빈 권한으로 저장)
            if (form.getAccessRole() == null || form.getAccessRole().isBlank()) {
                form.setAccessRoles(Collections.singletonList("USER"));
            } else {
                // CSV만 존재하면 List 캐시 싱크
                form.setAccessRole(form.getAccessRole()); // 캐시 무효화
                form.getAccessRoles(); // 캐시 로드
            }
        } else {
            // 체크박스로 넘어온 List를 CSV로 동기화
            form.setAccessRoles(form.getAccessRoles());
        }

        // Boolean 널 가드 (NULL → false)
        if (form.getUseComment() == null) form.setUseComment(Boolean.FALSE);
        if (form.getUseLike() == null)    form.setUseLike(Boolean.FALSE);
        if (form.getIsActive() == null)   form.setIsActive(Boolean.TRUE);

        // 공지 게시판은 사용자 읽기 전용이므로 기능 제한 원하면 아래 사용(선택)
        if ("notice".equalsIgnoreCase(form.getBoardType())) {
            // 공지 담당자만 글쓰기 가능(실제 권한은 canWriteBy에서 empId로도 제한됨)
            // 댓글/좋아요를 기본적으로 끄고 싶다면:
            // form.setUseComment(Boolean.FALSE);
            // form.setUseLike(Boolean.FALSE);
        }
    }
}
