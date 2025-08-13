package com.kdt.KDT_PJT.attend.ctl;


import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.attend.di.Leave;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.attend.model.LeaveReqDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;

@Controller
@RequestMapping("/attend")
@NoArgsConstructor
public class LeaveController {
	
	@ModelAttribute("navUrl")
	String navUrl() {
		return "attend/nav";
	}

	
	@Resource
	LeaveMapper mapper;
	
	@Resource
	private Leave leaveService;

	   
    @Scheduled(cron = "0 0 3 1 * ?") // 매월 1일 새벽 3시에 자동 실행
   // @Scheduled(cron = "0 */1 * * * ?") // 매 1분마다 실행
    public void scheduleAutoLeave() {
        System.out.println("[스케줄 시작] 전월 근무율 80% 이상 직원 자동 연차 부여 시작");
       leaveService.autoGiveLeaveForQualifiedEmployees();
        System.out.println("[스케줄 종료] 자동 연차 부여 완료");
    }
    
    
	//연차 관리(사용자용)
    @GetMapping("/leaveList")
    public String leaveList(HttpSession session,Model model) {
    	
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
        System.out.println("연차 관리 페이지");
        
        LeaveDTO dto = mapper.getAnnualLeaveOne(loginUser); 
        System.out.println("/attend/leave : "+dto);
        // 홈에서 뜨는 화면 연결
        model.addAttribute("mainUrl", "attend/leave/leaveList");
        
        model.addAttribute("listData", dto);
        
        
        return "navTap"; 
    }
    
    //연차 사용 신청(사용자용)
    @GetMapping("/insert")
    public String insert(HttpSession session,Model model) {
    	System.out.println("연차 신청 페이지");
    	
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	// 주석처리요망 -->
    	//me = new EmployeeDto();
    	//me.setEmployeeId("20250001");
    	//  <--
    									//잔여연차 가져오기
    	List<LeaveDTO> restData = mapper.annualLeaveRest(loginUser); 

    	
    	model.addAttribute("restData", restData);
    	
    	model.addAttribute("mainUrl", "attend/leave/insert");
        return "navTap"; 
    }
 
    //연차 사용 신청(사용자용) 정보 보내기
    @PostMapping("/insert")
    @Transactional
    public String insertReg(HttpSession session, Model model, LeaveReqDTO reqDto) {
    	
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	//reqDto = requestDto :연차 사용 요청
    	reqDto.dataCalc();
    	System.out.println("연차 신청 페이지 : "+reqDto.getArr().size());
    	
    	
    	
    	for (LeaveDTO dto : reqDto.getArr()) {
    		mapper.approvalList(dto);
		}
    	
    	return "redirect:/attend/leaveList";
    }
    
    //연차 관리(관리자용)
    @GetMapping("/leaveListMng")
    public String leaveListMng(HttpSession session, Model model,
    							HttpServletRequest request) {
        System.out.println("연차 관리자용");
        
        EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
        
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
            // log.warn("페이지 번호 파싱 실패, 기본값 사용", e);
        }
        
        // ✅ 여기서 페이징 시작
        PageHelper.startPage(pageNum, pageSize);

        // ✅ 하나의 경로로 조회 (필요 시 dto.workDate를 오늘 날짜로 채워 기본 동작 만들기)
        List<LeaveDTO> dto = mapper.mngLeaveList(); 

        // ✅ PageInfo로 래핑
        PageInfo<LeaveDTO> page = new PageInfo<>(dto);

        // 뷰로 전달
        model.addAttribute("page", page);           // 전체 페이징 메타데이터
        model.addAttribute("mainData", page.getList()); // 현재 페이지 데이터
        model.addAttribute("pageNum", pageNum);    // ✅ 현재 페이지
        model.addAttribute("pageSize", pageSize);  // ✅ 페이지 크기
        model.addAttribute("mainUrl", "attend/leave/leaveListMng");
        
  
        
    
        System.out.println("/attend/leave/leaveListMng : "+dto);
        

        
        model.addAttribute("listMngData", dto);
        return "navTap"; 
    }

}
