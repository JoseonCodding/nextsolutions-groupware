package com.kdt.KDT_PJT.employee.ctl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;

import java.lang.System.Logger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    /** 사원 목록 페이지 */
    @GetMapping("/employee/list")
    public String employeeList(Model model) {
        List<CmmnMap> list = employeeService.getUserList();
        model.addAttribute("employees", list);
        model.addAttribute("mainUrl", "employee/list");
        return "home";
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

    /** 사원 수정 폼 */
    @GetMapping("/employee/edit")
    public String editEmployee(Model model, EmployeeDto dto) {
    	
    	EmployeeDto res = employeeMapper.getDetail(dto);
        model.addAttribute("employee", res);
        System.out.println("/employee/edit : "+res);
        return "employee/edit";  // templates/employee/edit.html
    }

    /** 사원 수정 처리 */
    @PostMapping("/employee/update")
    public String updateEmployee(EmployeeDto dto) {
    	
    	 // 컨트롤러 메서드 안에서 바로 변환
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String birthStr = dto.getBirth() != null ? sdf.format(dto.getBirth()) : null;
        String hireStr  = dto.getHireDate() != null ? sdf.format(dto.getHireDate()) : null;
        String resignStr= dto.getResignDate() != null ? sdf.format(dto.getResignDate()) : null;
    	
    	 System.out.println("아이디"+dto.getEmployeeId());
    	 System.out.println("비번"+dto.getPassword());
    	 System.out.println("생년월일"+birthStr);
    	 System.out.println("입사일자"+hireStr);
    	 System.out.println("퇴사일자"+resignStr);
    	 System.out.println("부서명"+dto.getDeptName());
    	 System.out.println("권한"+dto.getRole());
    	 employeeMapper.update(dto);

        return "redirect:/employee/list";
    }
    
    
    
}

