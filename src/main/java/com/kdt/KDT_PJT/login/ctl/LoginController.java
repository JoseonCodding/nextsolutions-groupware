package com.kdt.KDT_PJT.login.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.login.svc.LoginService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping
    public String loginPage() {
        return "login/loginForm";
    }

    @PostMapping("/process")
    public String loginProcess(@RequestParam("employeeId") String employeeId,
                               @RequestParam("password") String password,
                               HttpSession session) {
        CmmnMap user = loginService.login(employeeId, password);

        if (user != null) {
            session.setAttribute("loginUser", user);
            return "redirect:/employee/list";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

