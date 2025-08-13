package com.kdt.KDT_PJT.login.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.login.svc.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // 로그인 페이지 이동
    @GetMapping
    public String loginPage() {
        return "login/loginForm";
    }

    // 로그인 처리
//    @PostMapping("/process")
//    public String loginProcess(@RequestParam("employeeId") String employeeId,
//                               @RequestParam("password") String password,
//                               HttpSession session) {
//
//        CmmnMap user = loginService.login(employeeId, password);
//
//        //System.out.println("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ " +user.getString("employeeId"));
//        
//        if (user != null) {
//        	
//        	EmployeeDto dto = new EmployeeDto();
//        	 // 필요한 값만 세션에 저장
//        	dto.setEmployeeId(user.getString("employeeId"));
//        	dto.setEmpNm(user.getString("empNm"));
//       
//            session.setAttribute("loginUser", dto);
//         
//
//            return "redirect:/employee/list";
//        } else {
//            return "redirect:/login?error=true";
//        }
//    }

   
    
    @PostMapping("/process")
    public String loginProcess(@RequestParam("employeeId") String employeeId,
                               @RequestParam("password") String password,
                               HttpSession session) {

        CmmnMap user = loginService.login(employeeId, password);

        //System.out.println("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ " +user.getString("employeeId"));
        
        if (user != null) {
        	
        	EmployeeDto dto = new EmployeeDto();
        	 // 필요한 값만 세션에 저장
        	dto.setEmployeeId(user.getString("employeeId"));
        	dto.setEmpNm(user.getString("empNm"));
        	dto.setRole(user.getString("role"));
       
            session.setAttribute("loginUser", dto);
         
        	//return ResponseEntity.ok(dto);
            return "redirect:/rc";
            //return "redirect:/attend";
        }
        
        //return ResponseEntity.status(401).body("로그인 필요");
        return "redirect:/login?error=true";
    }   
    
//    @GetMapping("/testSession")
//    @ResponseBody
//    public String testSession(HttpSession session) {ww a
//        return "세션에 저장된 ID: " + session.getAttribute("employeeId");
//    }
    
    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

