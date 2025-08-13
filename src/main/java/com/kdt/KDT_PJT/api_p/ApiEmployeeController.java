package com.kdt.KDT_PJT.api_p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiEmployeeController {
	
	@Autowired
	EmployeeMapper mapper;
	
	

	
	@GetMapping("employees")
	Object schedules(HttpSession session) {
		
		EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
		
		
		System.out.println("/api/employee 진입");
		return loginUser;
		
		
	}
	
}