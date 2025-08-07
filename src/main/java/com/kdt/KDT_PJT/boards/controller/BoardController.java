package com.kdt.KDT_PJT.boards.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;

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



}
