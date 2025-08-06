package com.kdt.KDT_PJT.login.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.login.svc.LoginService;
import com.kdt.KDT_PJT.login.dto.UserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping
    public String loginPage() {
        return "login/loginForm"; // loginForm.html
    }

    @PostMapping
    public String login(@RequestParam String empNo,
                        @RequestParam String password,
                        HttpSession session) {
        UserDTO loginUser = loginService.login(empNo, password);

        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);
            return "redirect:/employee/list"; // 로그인 성공 시 사원 목록 페이지 이동
        } else {
            return "redirect:/login?error=true"; // 로그인 실패
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
