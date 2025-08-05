package com.kdt.KDT_PJT.login.ctl;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.login.svc.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /** 로그인 페이지 */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /** 로그인 처리 */
    @PostMapping("/login")
    public String loginProcess(@RequestParam("employeeId") String employeeId,
                               @RequestParam("password") String password,
                               Model model) {

        boolean success = loginService.loginCheck(employeeId, password);

        if (success) {
            return "redirect:/employee/list";
        } else {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login";
        }
    }

    /** 회원가입 페이지 */
    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }

    /** 회원가입 처리 */
    @PostMapping("/join")
    public String joinProcess(@RequestParam("employeeId") String employeeId,
                              @RequestParam("password") String password) {

        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);

        loginService.saveProc(params);
        return "redirect:/login";
    }

    /** 사원 목록 페이지 */
    @GetMapping("/login/employeeList")
    public String employeeList(Model model) {
        model.addAttribute("employees", loginService.getUserList());
        return "employeeList";
    }

}
