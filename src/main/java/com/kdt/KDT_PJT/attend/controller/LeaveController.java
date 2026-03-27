package com.kdt.KDT_PJT.attend.controller;


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
import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.attend.model.LeaveReqDTO;
import com.kdt.KDT_PJT.attend.model.PageDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import com.kdt.KDT_PJT.notification.NotificationMapper;

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

	@Resource
	private EmployeeMapper employeeMapper;

	@Resource
	private NotificationMapper notificationMapper;


    @Scheduled(cron = "0 0 3 1 * ?") // 매월 1일 새벽 3시에 자동 실행
    //@Scheduled(cron = "0 */1 * * * ?") // 매 1분마다 실행
    public void scheduleAutoLeave() {
       leaveService.autoGiveLeaveForQualifiedEmployees();
    }


	// 내 연차 (사용자용)
    @GetMapping("/leaveList")
    public String leaveList(HttpSession session,Model model) {

    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");

        LeaveDTO dto = mapper.getAnnualLeaveOne(loginUser);
        List<LeaveDTO> dtoList = mapper.annualLeave(loginUser);

        dto.setDeptName(loginUser.getDeptName());
        dto.setPosition(loginUser.getPosition());

        model.addAttribute("mainUrl", "attend/leave/leaveList");
        model.addAttribute("listData", dto);
        model.addAttribute("detailData", dtoList);

        return "navTap";
    }

    //연차 사용 신청(사용자용)
    @GetMapping("/insert")
    public String insert(HttpSession session,Model model) {
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");

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

    	reqDto.dataCalc();

    	for (LeaveDTO dto : reqDto.getArr()) {
    		mapper.approvalList(dto);
		}

    	// 연차 신청 알림 → 근태/대표 권한자에게
    	employeeMapper.findApproverIds(loginUser.getCompanyId()).forEach(approverId -> {
    	    if (!approverId.equals(loginUser.getEmployeeId())) {
    	        notificationMapper.insert(
    	            loginUser.getCompanyId(),
    	            approverId,
    	            loginUser.getEmpNm(),
    	            "LEAVE_REQUEST",
    	            loginUser.getEmpNm() + "님이 연차를 신청했습니다.",
    	            "/approval/main?type=연차&status=대기"
    	        );
    	    }
    	});

    	return "redirect:/attend/leaveList";
    }


    //연차 관리(관리자용)
    @GetMapping("/leaveListMng")
    public String leaveListMng(HttpSession session, Model model,
    							HttpServletRequest request, PageDTO pDTO) {
        EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");

        PageHelper.startPage(pDTO.getPage(), pDTO.getSize());

        List<LeaveDTO> dto = mapper.mngLeaveList(loginUser.getCompanyId());

        PageInfo<LeaveDTO> page = new PageInfo<>(dto);

        pDTO.setTotalCount((int)page.getTotal());

        model.addAttribute("page", page);
        model.addAttribute("mainData", page.getList());
        model.addAttribute("pDTO", pDTO);
        model.addAttribute("mainUrl", "attend/leave/leaveListMng");
        model.addAttribute("listMngData", dto);
        return "navTap";
    }

    //연차 상세 조회(관리자용)
    @RequestMapping("/leaveListMng/leaveMngDetail")
    public String leaveListMngDetail(HttpSession session, Model model,
    							HttpServletRequest request, LeaveDTO ddd) {
        EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");

        LeaveDTO dttt = mapper.mngLeaveListOne(ddd);
        List<LeaveDTO> one = mapper.annualLeaveOneMMM(ddd);

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

        PageHelper.startPage(pageNum, pageSize);

        List<LeaveDTO> dto = mapper.mngLeaveList(loginUser.getCompanyId());

        PageInfo<LeaveDTO> page = new PageInfo<>(dto);

        model.addAttribute("page", page);
        model.addAttribute("mainData", page.getList());
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("mainUrl", "attend/leave/leaveMngDetail");
        model.addAttribute("MngDetail", dttt);
        model.addAttribute("MngDetail2", one);
        return "navTap";
    }

}
