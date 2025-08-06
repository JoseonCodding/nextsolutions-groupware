package com.kdt.KDT_PJT.employee.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;
import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /** 사원 목록 페이지 */
    @GetMapping("/employee/list")
    public String employeeList(Model model) {
        List<CmmnMap> list = employeeService.getUserList();
        model.addAttribute("employees", list);
        return "employee/list";
    }

    /** 활성/비활성 토글 */
    @PostMapping("/employee/toggle")
    @ResponseBody
    public String toggleActive(@RequestParam("emp_seq") int empSeq) {
        CmmnMap params = new CmmnMap();
        params.put("emp_seq", empSeq);
        employeeService.toggleActive(params);
        return "OK";
    }

    /** 회원가입 폼 */
    @GetMapping("/employee/register")
    public String registerForm() {
        return "employee/register";
    }

    /** 회원가입 처리 */
    @PostMapping("/employee/register")
    public String registerEmployee(@RequestParam("employeeId") String employeeId,
                                   @RequestParam("password") String password,
                                   @RequestParam("emp_nm") String empNm) {
        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);
        params.put("emp_nm", empNm);
        employeeService.insertEmployee(params);
        return "redirect:/employee/list";
    }
    

}
