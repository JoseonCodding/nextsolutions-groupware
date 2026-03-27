package com.kdt.KDT_PJT.employee.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmployeeController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "employee/layout/empTap";
	}

    @Autowired
    private EmployeeService employeeService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/employee/list")
    public String employeeList(@RequestParam(required = false, name = "keyword") String keyword,
    						   HttpSession session, Model model,
    						   @RequestParam(name = "page", defaultValue = "1") int page) {

    	EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");

    	String role = user.getRole();
    	if (!"대표".equals(role) && !"사원".equals(role)) {
    	    return "redirect:/employee/edit?empSeq=" + user.getEmpSeq();
    	}

       int size = 10;
       int offset = (page - 1) * size;

       int totalCount = employeeService.getUserListTotalCount(keyword);
       int totalPages = (int) Math.ceil((double) totalCount / size);

       int startPage = Math.max(1, page - 2);
       int endPage = Math.min(totalPages, startPage + 4);
       if ((endPage - startPage + 1) < 5 && (endPage == totalPages || startPage == 1)) {
           startPage = Math.max(1, endPage - 4);
       }
       if (totalPages == 0) endPage = 1;

       List<EmployeeDto> employeeDto = employeeService.getUserList(offset, size, keyword);

       model.addAttribute("employees", employeeDto);
       model.addAttribute("page", page);
       model.addAttribute("totalPages", totalPages);
       model.addAttribute("startPage", startPage);
       model.addAttribute("endPage", endPage);
       model.addAttribute("keyword", keyword);
       model.addAttribute("mainUrl", "employee/list");
       return "navTap";
    }

    /** 활성/비활성 토글 */
    @PostMapping("/employee/toggle")
    @ResponseBody
    public String toggleActive(@RequestParam("emp_seq") int empSeq, HttpSession session) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        if (user == null) return "FORBIDDEN";
        String role = user.getRole();
        if (!"대표".equals(role) && !"사원".equals(role)) return "FORBIDDEN";

        // 대상 사원이 같은 회사인지 검증
        EmployeeDto target = employeeService.getEmployeeDetail(empSeq);
        if (target == null || target.getCompanyId() != user.getCompanyId()) return "FORBIDDEN";

        CmmnMap params = new CmmnMap();
        params.put("emp_seq", empSeq);
        employeeService.toggleActive(params);
        return "OK";
    }

    /** 회원가입 폼 */
    @GetMapping("/employee/register")
    public String registerForm(HttpSession session, Model model) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        String role = user.getRole();
        if (!"대표".equals(role) && !"사원".equals(role)) return "redirect:/rc";
        model.addAttribute("mainUrl", "employee/register");
        return "navTap";
    }

    /** 회원가입 처리 */
    @PostMapping("/employee/register")
    public String registerEmployee(@RequestParam("employeeId") String employeeId,
            @RequestParam("password") String password,
            @RequestParam("empNm") String empNm,
            @RequestParam("phone") String phone,
            @RequestParam("deptName") String deptName,
            @RequestParam("position") String position,
            @RequestParam(value="role", required=false) String role,
            @RequestParam(value="birth", required=false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date birth,
            @RequestParam(value="hireDate", required=false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date hireDate,
            @RequestParam(value="resignDate", required=false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date resignDate) {

        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", new BCryptPasswordEncoder().encode(password));
        params.put("empNm", empNm);
        params.put("phone", phone);
        params.put("birth", birth);
        params.put("deptName", deptName);
        params.put("position", position);
        params.put("role", (role == null || role.isBlank()) ? null : role);
        params.put("hireDate", hireDate);
        params.put("resignDate", resignDate);
        params.put("companyId", CompanyContext.get());

        log.info("birth={}, hireDate={}, resignDate={}", birth, hireDate, resignDate);

        employeeService.insertEmployee(params);
        return "redirect:/employee/list";
    }

    /** 사원 수정 폼 */
    @GetMapping("/employee/edit")
    public String editEmployee(Model model, EmployeeDto dto, HttpSession session) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        EmployeeDto res = employeeService.getDetail(dto);
        if (res == null) return "redirect:/employee/list";
        // 같은 회사 사원만 수정 가능
        if (res.getCompanyId() != user.getCompanyId()) return "redirect:/rc";
        model.addAttribute("employee", res);
        model.addAttribute("mainUrl", "employee/edit");
        return "navTap";
    }

    /** 사원 수정 처리 */
    @PostMapping("/employee/update")
    public String updateEmployee(EmployeeDto dto, HttpSession session) {

    	EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
    	String updaterRole = user.getRole();
    	boolean isAdmin = "대표".equals(updaterRole) || "사원".equals(updaterRole);

    	EmployeeDto existing = employeeService.getDetail(dto);
    	if (existing == null) return "redirect:/employee/list";

    	if (isAdmin) {
    	    // 관리자: role null/blank/일반/-이면 null로 저장 허용
    	    if (dto.getRole() == null || dto.getRole().isBlank()
    	        || "일반".equals(dto.getRole()) || "-".equals(dto.getRole())) {
    	        dto.setRole(null);
    	    }
    	} else {
    	    // 비관리자 자기정보 수정: disabled 필드 기존값 유지
    	    dto.setDeptName(existing.getDeptName());
    	    dto.setPosition(existing.getPosition());
    	    dto.setRole(existing.getRole());
    	}

    	// 비밀번호 입력 시 BCrypt 인코딩, 빈칸이면 기존 비밀번호 유지
    	if (dto.getPassword() == null || dto.getPassword().isBlank()) {
    	    dto.setPassword(existing.getPassword());
    	} else {
    	    dto.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
    	}

    	employeeService.update(dto);

    	if (!isAdmin) {
    	    return "redirect:/rc";
    	}
        return "redirect:/employee/list";
    }

    /** 사번 중복체크 */
    @ResponseBody
    @GetMapping("/employee/idChk")
    public Object idChk(EmployeeDto dto) {
        return employeeService.getIdChk(dto);
    }

    /** 휴대폰 중복체크 */
    @GetMapping("/employee/phoneChk")
    @ResponseBody
    public boolean phoneChk(@RequestParam("phone") String phone) {
        return employeeService.existsByPhone(phone);
    }

    /** 마이페이지 */
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        EmployeeDto detail = employeeService.findByEmployeeId(user.getEmployeeId());
        model.addAttribute("employee", detail);
        model.addAttribute("mainUrl", "employee/mypage");
        return "navTap";
    }

    /** 연락처 변경 */
    @PostMapping("/mypage/updatePhone")
    public String updatePhone(@RequestParam("phone") String phone,
                              HttpSession session,
                              RedirectAttributes ra) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        employeeService.updatePhone(user.getEmployeeId(), phone);
        user.setPhone(phone);
        session.setAttribute("loginUser", user);
        ra.addFlashAttribute("msg", "연락처가 변경되었습니다.");
        return "redirect:/mypage";
    }

    /** 비밀번호 변경 */
    @PostMapping("/mypage/updatePassword")
    public String updatePassword(@RequestParam("currentPw") String currentPw,
                                  @RequestParam("newPw") String newPw,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        EmployeeDto detail = employeeService.findByEmployeeId(user.getEmployeeId());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(currentPw, detail.getPassword())) {
            ra.addFlashAttribute("pwError", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage";
        }
        if (!newPw.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()_\\-+={}\\[\\]|\\\\:;\"'<>,.?/]).{8,}$")) {
            ra.addFlashAttribute("pwError", "8자 이상, 대/소문자/숫자/특수문자를 모두 포함해야 합니다.");
            return "redirect:/mypage";
        }
        employeeService.updatePassword(user.getEmployeeId(), encoder.encode(newPw));
        ra.addFlashAttribute("msg", "비밀번호가 변경되었습니다.");
        return "redirect:/mypage";
    }

}
