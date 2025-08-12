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

    /** 목록  */
    @GetMapping
    public String page(Model model) {
        model.addAttribute("boards", boardMapper.selectBoards());
        model.addAttribute("mainUrl", "board/admin_boards");
        return "home";
    }

    /** 보드 생성 (전부 DTO로 수신) */
    @PostMapping("/create")
    public String create(BoardDTO form) {
        // 체크박스 name="accessRoles"로 넘어오면 DTO setter가 CSV(accessRole) 자동 생성
        if (form.getAccessRoles() == null || form.getAccessRoles().isEmpty()) {
            form.setAccessRoles(List.of("USER")); // 기본값
        }
        if (form.getUseComment() == null) form.setUseComment(false);
        if (form.getUseLike() == null) form.setUseLike(false);
        form.setBoardType("CUSTOM");
        form.setIsActive(true);
        boardMapper.insertBoard(form);
        return "redirect:/admin/boards";
    }

    /** 보드 수정 */
    @PostMapping("/edit")
    public String edit(BoardDTO form) {
        boardMapper.selectBoardById(form.getBoardId());
        boardMapper.updateBoard(form);
        return "redirect:/admin/boards";
    }

    /** 활성/비활성 토글 (boardId, isActive 담긴 DTO) */
    @PostMapping("/toggle")
    public String toggle(@RequestParam Integer boardId, @RequestParam Boolean isActive) {
        boardMapper.updateBoardActive(boardId, isActive);
        return "redirect:/admin/boards";
    }

}
