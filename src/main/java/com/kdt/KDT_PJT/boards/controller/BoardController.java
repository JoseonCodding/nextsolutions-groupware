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

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardInfoDTO;


@Controller
@RequestMapping("/board")
public class BoardController {
	
	@Autowired
	BoardMapper boardMapper;
	

	@GetMapping
    public String list(Model model) {
		List<BoardDTO> boards = boardMapper.list();

        model.addAttribute("boards", boards);

        return "board/board_list";
    }
	
	@GetMapping("/create")
	public String createForm(Model model) {
	    model.addAttribute("boardDTO", new BoardDTO()); // 폼 바인딩용 객체
	    return "board/board_writeform"; // → board/create.html로 이동
	}

	@PostMapping("/save")
	public String saveBoard(@ModelAttribute BoardDTO boardDTO) {
	    // 기본 검증 로직 (예: 제목/내용 유효성 등)
	    if (boardDTO.getTitle() == null || boardDTO.getContent() == null) {
	        return "redirect:/board/create?error=1";
	    }

	    // 등록일 자동 세팅 (필요시)
	    boardDTO.setRegDate(new Date());

	    // 기본 조회수/좋아요 초기화
	    boardDTO.setViewCnt(0);
	    boardDTO.setLikeCnt(0);
	    boardDTO.setDeleted(false);

	    // DB 저장
	    boardMapper.insert(boardDTO); // ← MyBatis insert 메서드 호출

	    return "redirect:/board"; // 저장 후 목록으로 리다이렉트
	}


}
