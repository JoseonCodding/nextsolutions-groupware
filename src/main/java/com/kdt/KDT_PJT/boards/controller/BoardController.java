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

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardInfoDTO;


@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardMapper boardMapper;

 // 🔷 공지사항 목록
    @GetMapping("/notice")
    public String noticeList(Model model) {
        List<BoardDTO> boards = boardMapper.selectNoticePosts();
        model.addAttribute("boards", boards);
        model.addAttribute("activeBoard", "notice");
        //
        model.addAttribute("mainUrl", "board/notice_list");
        return "home";
    }

    // 🔷 자유게시판 목록
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
        return "board/free_list";
    }


    // 🔷 공지사항 글쓰기 폼 (읽기전용이면 생략 가능)
    @GetMapping("/notice/write")
    public String writeNoticeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        return "board/notice_writeform";  // 🔹 별도 뷰
    }

    // 🔷 자유게시판 글쓰기 폼
    @GetMapping("/free/write")
    public String writeFreeForm(Model model) {
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("activeBoard", "free");
        return "board/free_writeform"; // 이 파일 이름
    }

    // 🔷 자유게시판 저장
    @PostMapping("/free/save")
    public String saveFreePost(@ModelAttribute BoardDTO dto) {
        Integer boardId = boardMapper.findBoardIdByType("free");
        dto.setBoardId(boardId);

        dto.setRegDate(new Date());
        dto.setViewCnt(0);
        dto.setLikeCnt(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/free";
    }

    // 🔷 공지사항 저장 (관리자일 경우)
    @PostMapping("/notice/save")
    public String saveNoticePost(@ModelAttribute BoardDTO dto) {
        Integer boardId = boardMapper.findBoardIdByType("notice");
        dto.setBoardId(boardId);

        dto.setRegDate(new Date());
        dto.setViewCnt(0);
        dto.setLikeCnt(0);
        dto.setDeleted(false);

        boardMapper.insert(dto);
        return "redirect:/board/notice";
    }
}
