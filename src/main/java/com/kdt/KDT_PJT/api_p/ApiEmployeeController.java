package com.kdt.KDT_PJT.api_p;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiEmployeeController {

	@GetMapping("employees")
	Object employees(HttpSession session) {
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
		return loginUser;
	}

}
