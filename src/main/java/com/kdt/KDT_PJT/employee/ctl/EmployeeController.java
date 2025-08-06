package com.kdt.KDT_PJT.employee.ctl;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /** 사원 목록 */
    @GetMapping("/list")
    public String listEmployees(Model model) {
        List<CmmnMap> employees = employeeService.getUserList();
        model.addAttribute("employees", employees);
        return "employee_list"; // templates/employee_list.html
    }

    /** 신규 사원 등록 */
    @PostMapping("/add")
    public String addEmployee(@RequestParam("employeeId") String employeeId,
                              @RequestParam("password") String password) {

        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);

        employeeService.saveProc(params); // emp_seq 자동 생성 후 저장
        return "redirect:/employee/list";
    }

    /** 사원 정보 수정 */
    @PostMapping("/update")
    public String updateEmployee(@RequestParam("emp_seq") int empSeq,
                                 @RequestParam("employeeId") String employeeId,
                                 @RequestParam("password") String password) {

        CmmnMap params = new CmmnMap();
        params.put("emp_seq", empSeq);
        params.put("employeeId", employeeId);
        params.put("password", password);

        employeeService.updateUser(params); // updateUser로 호출
        return "redirect:/employee/list";
    }

    /** 사원 활성화/비활성화 토글 */
    @PostMapping("/toggle")
    public String toggleActive(@RequestParam("emp_seq") int empSeq) {
        employeeService.toggleActive(empSeq); // int로 바로 전달 가능
        return "redirect:/employee/list";
    }
}
