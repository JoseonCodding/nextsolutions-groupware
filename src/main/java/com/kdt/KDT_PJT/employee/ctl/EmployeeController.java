package com.kdt.KDT_PJT.employee.ctl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    // log 사용을 위함
 	private final Logger log = LoggerFactory.getLogger(getClass());

    /** 사원 목록 페이지 */
    @GetMapping("/employee/list")
    public String employeeList(@RequestParam(required = false, name = "keyword") String keyword
    							,HttpServletRequest request
    							,Model model) {
    	
	    // 🔍 검색어 로그 확인 (디버깅용)
	   System.out.println("getPjtList Called >>> keyword = " + keyword);
	   
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
       
       //  리스트 가져오기
       PageInfo<CmmnMap>  list = employeeService.getUserList(pageNum, pageSize, keyword);
       // ③ model에 값 담기
       
       if (list == null || list.getList() == null || list.getList().isEmpty()) {
    	    log.warn("⚠️ 프로젝트 리스트가 비어있거나 null입니다.");
    	    model.addAttribute("employees", List.of());  // 빈 리스트로 넘기기 (NPE 방지)
    	} else {
    	    log.info("✅ 프로젝트 리스트 로드 성공. 개수: " + list.getList().size());
    	    model.addAttribute("employees", list.getList());
    	}
       
       model.addAttribute("employees", list.getList());   // 현재 페이지 데이터
       model.addAttribute("pageInfo", list);             // 페이징 정보      
	   
	   
        //model.addAttribute("employees", list);
        model.addAttribute("mainUrl", "employee/list");
        //System.out.println("/employee/list : "+list);
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
                                   @RequestParam("empNm") String empNm) {
        CmmnMap params = new CmmnMap();
        params.put("employeeId", employeeId);
        params.put("password", password);
        params.put("empNm", empNm);
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
    	 System.out.println(dto);
    	 employeeMapper.update(dto);

        return "redirect:/employee/list";
    }
    
    // 직원 상세 페이지
    @GetMapping("/employee/detail")
    public String employeeDetail(@RequestParam("empSeq") int empSeq, Model model) {
        EmployeeDto emp = employeeService.getEmployeeDetail(empSeq);
        model.addAttribute("employee", emp);
        return "employee/detail"; // detail.html로 이동
    }
    
    
    
}

