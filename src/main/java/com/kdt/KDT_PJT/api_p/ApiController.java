package com.kdt.KDT_PJT.api_p;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {

	
	@GetMapping("logInfo")
	ResponseEntity<?> get(HttpSession sesson) {
		
		System.out.println("/api/logInfo 진입");
		//return sesson.getAttribute("loginUser").toString();
		
		return ResponseEntity.ok(sesson.getAttribute("loginUser"));
	}
}
