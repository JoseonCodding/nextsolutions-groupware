package com.kdt.KDT_PJT.sample.ctl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.sample.dto.SampleDTO;
import com.kdt.KDT_PJT.sample.svc.SampleService;

// HttpServletRequest 사용을 위함
// import javax.servlet.http.HttpServletRequest; // Spring Boot 2.x
import jakarta.servlet.http.HttpServletRequest;	// Spring Boot 3.x 



@Controller
@RequestMapping("/sampleJspMvc")
public class SampleJspCtl {

	
	@Autowired // 객체 생성과 연결을 Spring이 대신 해줌, 서비스자동 주입
	SampleService sampleService;
	
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());

	
	/**
	* JSP 예제
	* 서버 -> VIEW (JSP로 값 넘기기)
	* 호출URI : (로컬8080 기준) localhost:8080/sampleJspMvc/index
	* @methodName    : helloJSP
	* @author        : 이의찬/대리
	* @date          : 2025.07.01
	* @param request
	* @param model
	* @return
	* Description 	 :
	 */
	@GetMapping("/index")
	public String helloJSP(HttpServletRequest request
						   , Model model) {
		
		
		log.info("helloJSP >>>> Called INFO");
		log.debug("helloJSP >>>> Called Debug");
						
		// index.jsp 에 값을 넘겨주기위해 Model 사용
		// name 이라는 변수명에 9기" 값을 담음
		model.addAttribute("name", "수강생");
						
		return "index"; // kdtpjt\src\main\webapp\WEB-INF\views\index.jsp		
		// 프로퍼티스 파일에 아래와 같이 선언되어있어 알아서 jsp경로 및 파일을 찾아 리턴함
		// spring.mvc.view.prefix=/WEB-INF/views/

	}
	
	
	@GetMapping("/home")
	public String helloPagingJSP(HttpServletRequest request, Model model) {
	    log.info("helloPagingJSP >>>> Called INFO");

	    // ① 페이지 번호/사이즈 받아오기 (없으면 기본값)
	    int pageNum = 1;
	    int pageSize = 10;

	    try {
	        if (request.getParameter("pageNum") != null) {
	            pageNum = Integer.parseInt(request.getParameter("pageNum"));
	        }
	        if (request.getParameter("pageSize") != null) {
	            pageSize = Integer.parseInt(request.getParameter("pageSize"));
	        }
	    } catch (Exception e) {
	        log.warn("페이지 번호 파싱 실패, 기본값 사용");
	    }

	    // ② 서비스 호출
	    PageInfo<SampleDTO> pageInfo = sampleService.getPagedUserList(pageNum, pageSize);

	    // ③ model에 값 담기
	    model.addAttribute("userList", pageInfo.getList());   // 현재 페이지 데이터
	    model.addAttribute("pageInfo", pageInfo);             // 페이징 정보

	    return "/main/home"; // .jsp 생략됨
	}
	


	
	
	/**
	 * home.jsp의 form mvcSample 에서 보낸 데이터 받아 DB에 저장하는 예제
	* @methodName    : saveProc
	* @author        : 이의찬/대리
	* @date          : 2025.07.01
	* @param request
	* @return
	* Description 	 :
	 */
	@PostMapping("/api/saveProc")
	public String saveProc(HttpServletRequest request
							, SampleDTO sampleDTO) {
		
		log.info("saveProc >>>> Called INFO");
		log.debug("saveProc >>>> Called Debug");
				
		
		sampleDTO.setKornFlnm(request.getParameter("kornFlnm"));
		sampleDTO.setMblTelno(request.getParameter("mblTelno"));		
		sampleDTO.setEmlAddr(request.getParameter("emailAddr"));
		
		sampleService.saveProc(sampleDTO);
		
		
		return "saveProc";
	}	
	

	/**
	 * JSP에서 AJAX 사용에쪠
	* @methodName    : ajaxTest
	* @author        : 이의찬/대리
	* @date          : 2025.07.01
	* @param request
	* Description 	 :
	 */
	// @RequestMapping(value="/ajax/saveProc", method=RequestMethod.POST)
	@PostMapping("/ajax/saveProc")
	@ResponseBody // <-- 없을경우 view를 반환하게됨
	public void ajaxTest(HttpServletRequest request
						, SampleDTO sampleDTO) {
		
		log.info("ajaxTest >>>> Called INFO");
		log.debug("ajaxTest >>>> Called Debug");				

		
		log.info("kornFlnm>>>>" + request.getParameter("kornFlnm"));
		log.info("mblTelno>>>>" + request.getParameter("mblTelno")); 
		log.info("emailAddr>>>>" + request.getParameter("emailAddr")); 
		
		
		
		//TODO 서비스호출
		sampleDTO.setKornFlnm(request.getParameter("kornFlnm"));
		sampleDTO.setMblTelno(request.getParameter("mblTelno"));		
		sampleDTO.setEmlAddr(request.getParameter("emailAddr"));
		
		sampleService.saveProc(sampleDTO);
	}
	
	
	@PostMapping("/ajax/updateProc")
	@ResponseBody // <-- 없을경우 view를 반환하게됨
	public void ajaxUpdateProc(HttpServletRequest request
								, SampleDTO sampleDTO) {
	
		log.info("ajaxUpdateProc >>>> Called INFO");
		log.debug("ajaxUpdateProc >>>> Called Debug");
		
		log.info("kornFlnm>>>>" + request.getParameter("kornFlnm"));
		log.info("mblTelno>>>>" + request.getParameter("mblTelno")); 
		log.info("emailAddr>>>>" + request.getParameter("emailAddr")); 
		

		sampleDTO.setKornFlnm(request.getParameter("kornFlnm"));
		sampleDTO.setMblTelno(request.getParameter("mblTelno"));		
		sampleDTO.setEmlAddr(request.getParameter("emailAddr"));
		sampleDTO.setCustSn(Integer.parseInt(request.getParameter("custSn")));

		
		sampleService.updateProcProc(sampleDTO);
	}


	@DeleteMapping("/ajax/deleteProc")
	@ResponseBody // <-- 없을경우 view를 반환하게됨
	public void ajaxDeleteProc(HttpServletRequest request
								, SampleDTO sampleDTO) {

		sampleDTO.setCustSn(Integer.parseInt(request.getParameter("custSn")));
		sampleService.deleteProc(sampleDTO);
	}	
}
