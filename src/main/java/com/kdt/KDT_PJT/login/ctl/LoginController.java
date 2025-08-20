package com.kdt.KDT_PJT.login.ctl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.login.svc.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    
    // log 사용을 위함
  	private final Logger log = LoggerFactory.getLogger(getClass());

    // 로그인 페이지 이동
    @GetMapping
    public String loginPage() {
        return "login/loginForm";
    }

    
    @PostMapping("/process")
    public String loginProcess(@RequestParam("employeeId") String employeeId,
                               @RequestParam("password") String password,
                               HttpSession session) {

        EmployeeDto user = loginService.login(employeeId, password);
        
        log.info("user.getActive() " + user.getActive());
        	
        if (user.getActive() == 0) {
        	
        	 return "login/loginErrForm";
        	
        }
        
        
    	
    	
        if (user != null) {
        	
        	if (user.getActive() == 1) { 
            session.setAttribute("loginUser", user);
         
           //return ResponseEntity.ok(dto);
            return "redirect:/rc";
            //return "redirect:/attend";
        	}
        }
        
    	
        if (user == null) {
        	return "redirect:/login?error=true";
        }
        
        

        //System.out.println("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ " +user.getString("employeeId"));
        

        //return ResponseEntity.status(401).body("로그인 필요");
        return "redirect:/login?error=true";
    }   
    
    

    
    
    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}