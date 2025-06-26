package com.kdt.KDT_PJT.sample.ctl;


// HttpServletRequest 사용을 위함
// import javax.servlet.http.HttpServletRequest; // Spring Boot 2.x
import jakarta.servlet.http.HttpServletRequest;	// Spring Boot 3.x 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.sample.svc.SampleService;

@Controller
@RequestMapping("/sampleMvc")
public class SampleCtl {

	
	@Autowired // 객체 생성과 연결을 Spring이 대신 해줌, 서비스자동 주입
	SampleService sampleService;
	
	// JSP 예제
	// 서버 -> VIEW (JSP로 값 넘기기)
	@GetMapping("/api/test")
	public String helloJSP(HttpServletRequest request
						   , Model model) {

		
		// 서비스 호출
		sampleService.test();
		
		// TODO 리스트에 담기
		
		model.addAttribute("name", "너의 이름은?");
		return "test"; // KDT_PJT\src\main\webapp\WEB-INF\views
		
		// 프로퍼티스 파일에 아래와 같이 선언됨
		// spring.mvc.view.prefix=/WEB-INF/views/

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// HTML 예제
	
	@GetMapping("/hello")
	public String helloHTML(Model model) {
		model.addAttribute("name", "홍길동");
		return "index"; // → templates/index.html
	}

}
