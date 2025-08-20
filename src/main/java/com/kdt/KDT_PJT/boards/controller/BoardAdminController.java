package com.kdt.KDT_PJT.boards.controller;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/boards")
public class BoardAdminController {

    @Autowired
    private BoardMapper boardMapper;

    /* =========================
       1) 게시판 목록/상세/신규 폼
       ========================= */

    // 전체 게시판 관리 목록
    @GetMapping
    public String list(Model model) {
        // 관리자용 보드 목록
        List<BoardDTO> boards = boardMapper.selectBoards();
        model.addAttribute("boards", boards);

        // 화면 우측/상단 KPI용: 각 보드의 "오늘 조회수/오늘 좋아요" 집계
        // (뷰에서 쓰기 쉽게 List<Map> 형태 제공)
        List<Map<String, Object>> todayStats = new ArrayList<>();
        for (BoardDTO b : boards) {
            Integer boardId = b.getBoardId();
            if (boardId == null) continue;

            long views = boardMapper.selectTodayViews(boardId);
            long likes = boardMapper.selectTodayLikes(boardId);

            Map<String, Object> row = new HashMap<>(); 
            row.put("boardId", boardId);
            row.put("boardName", b.getBoardName());
            row.put("viewToday", views);
            row.put("likeToday", likes);
            todayStats.add(row);
        }
        model.addAttribute("todayStats", todayStats);

        // 통계 대상 보드 리스트(삭제 제외) — 별도 쿼리가 없으므로 여기서는 boards 재활용
        model.addAttribute("boardsForStats", boards);

        // 공용 템플릿 지정
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

    /* =========================
       2) 생성/수정/활성화/비활성화
       ========================= */

    // 생성
    @PostMapping("/create")
    public String create(@ModelAttribute BoardDTO form, RedirectAttributes redirect) {
        normalizeBoardMeta(form);

        // 공지/자유는 사전 존재 — 신규는 custom으로 강제
        if (!"custom".equalsIgnoreCase(form.getBoardType())) {
            form.setBoardType("custom");
        }
        
        // ✅ 최대 6개 제한
        int count = boardMapper.countBoards();
        if (count >= 7) {
            redirect.addFlashAttribute("error", "게시판은 최대 7개까지만 생성할 수 있습니다.");
            return "redirect:/admin/boards";
        }

        boardMapper.insertBoard(form);
        return "redirect:/admin/boards";
    }

    // 수정
    @PostMapping("/edit")
    public String update(@ModelAttribute BoardDTO form) {
        BoardDTO origin = boardMapper.selectBoardById(form.getBoardId());
        if (origin == null) return "redirect:/admin/boards";

        normalizeBoardMeta(form);
        boardMapper.updateBoard(form);
        return "redirect:/admin/boards";
    }

    // 활성화/비활성 토글
    @PostMapping("/toggle")
    public String toggleActive(@ModelAttribute BoardDTO dto) {
        if (dto.getBoardId() == null) return "redirect:/admin/boards";
        int active = Boolean.TRUE.equals(dto.getIsActive()) ? 1 : 0;
        boardMapper.updateBoardActive(dto.getBoardId(), active);
        return "redirect:/admin/boards";
    }

    // 삭제(소프트): 관리자 목록에서도 사라지게
    @PostMapping("/delete")
    public String delete(@RequestParam("boardId") Integer boardId) {
        BoardDTO dto = boardMapper.selectBoardById(boardId);
        if (dto == null) return "redirect:/admin/boards";
        boardMapper.softDeleteBoard(boardId);
        return "redirect:/admin/boards";
    }
    
    @GetMapping("/delete")
    public String deleteGet(@RequestParam("boardId") Integer boardId) {
        return delete(boardId);
    }


    /* =========================
       3) 조회수 통계 (옵션)
       ========================= */

    // 전체 게시판 조회수 통계
    @GetMapping("/stats")
    public String stats(Model model) {
        List<BoardDTO> boards = boardMapper.selectBoards();
        model.addAttribute("boardsForStats", boards);

        List<Map<String, Object>> todayStats = new ArrayList<>();
        for (BoardDTO b : boards) {
            Integer boardId = b.getBoardId();
            if (boardId == null) continue;

            long views = boardMapper.selectTodayViews(boardId);
            long likes = boardMapper.selectTodayLikes(boardId);

            Map<String, Object> row = new HashMap<>();
            row.put("boardId", boardId);
            row.put("boardName", b.getBoardName());
            row.put("viewToday", views);
            row.put("likeToday", likes);
            todayStats.add(row);
        }
        model.addAttribute("todayStats", todayStats);

        model.addAttribute("mainUrl", "board/admin_boards");
        return "home";
    }

    // 단일 보드의 “오늘” 통계(JSON) — 프론트에서 개별 보드 KPI로 호출
    @GetMapping("/stats/today/{boardId}")
    @ResponseBody
    public Map<String, Object> todayStatsOne(@PathVariable("boardId") Integer boardId) {
        long views = boardMapper.selectTodayViews(boardId);
        long likes = boardMapper.selectTodayLikes(boardId);

        Map<String, Object> map = new HashMap<>();
        map.put("boardId", boardId);
        map.put("viewToday", views);
        map.put("likeToday", likes);
        return map;
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

        // accessRoles(List) -> accessRole(CSV) 동기화
        if (form.getAccessRoles() == null || form.getAccessRoles().isEmpty()) {
            if (form.getAccessRole() == null || form.getAccessRole().isBlank()) {
                form.setAccessRoles(Collections.singletonList("USER"));
            } else {
                form.setAccessRole(form.getAccessRole()); // CSV만 있을 때 캐시 리빌드
                form.getAccessRoles();
            }
        } else {
            form.setAccessRoles(form.getAccessRoles());   // List 입력을 CSV로 반영
        }

        // Boolean 널 가드
        if (form.getUseComment() == null) form.setUseComment(Boolean.FALSE);
        if (form.getUseLike() == null)    form.setUseLike(Boolean.FALSE);
        if (form.getIsActive() == null)   form.setIsActive(Boolean.TRUE);
    }
}
