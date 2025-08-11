package com.kdt.KDT_PJT.api_p;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
    BoardMapper boardMapper;

	
	@GetMapping("logInfo")
	Object get(HttpSession sesson) {
		
		System.out.println("/api/logInfo 진입");
		return sesson.getAttribute("loginUser");
		
		
	}
	
}