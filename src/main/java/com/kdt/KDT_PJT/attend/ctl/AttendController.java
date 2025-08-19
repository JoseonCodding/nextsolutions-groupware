package com.kdt.KDT_PJT.attend.ctl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendDTO2;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/attend")
public class AttendController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "attend/nav";
	}
	
	@Autowired
    Attendance service;
	
	@Autowired
    AttendMapper attendMapper;

   // log 사용을 위함
   private final Logger log = LoggerFactory.getLogger(getClass());
   
	//사용자 본인의 출퇴근 기록 
    @GetMapping
    String showAttendancePage(HttpSession session, Model model,AttendDTO2 attendance) {
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	// 현재 조회 기간이 없으면 기본값 세팅 (이번달 시작일 ~ 이번달 말일)
        if(attendance.getStartDay() == null || attendance.getEndDay() == null) {
            LocalDate now = LocalDate.now();
            attendance.setStartDay(now.withDayOfMonth(1).toString());      // 이번달 1일
            attendance.setEndDay(now.withDayOfMonth(now.lengthOfMonth()).toString()); // 이번달 마지막 날
        }

        // 로그인 사용자 아이디 세팅
        attendance.setEmployeeId(loginUser.getEmployeeId());
    	
    	List<AttendDTO> attendMonthList =  attendMapper.userAttendMonthList(attendance); 
    	
    	System.out.println("showAttendancePage:"+attendMonthList);
    	
        model.addAttribute("mainData", attendMonthList);
        model.addAttribute("mainUrl", "attend/check");
        model.addAttribute("attendDTO", attendance);   // 조회 조건 유지용
       
        
        // DTO에 구현한 메서드 호출
        model.addAttribute("prevMonthStartDay", attendance.getPrevMonthStartDay());
        model.addAttribute("prevMonthEndDay", attendance.getPrevMonthEndDay());
        model.addAttribute("nextMonthStartDay", attendance.getNextMonthStartDay());
        model.addAttribute("nextMonthEndDay", attendance.getNextMonthEndDay());
        model.addAttribute("currentStartDay", attendance.getStartDay());
        return "navTap";
        
    }
    
    //출근 시간 기록
    @PostMapping("/in")
    String checkIn(HttpSession session) {
    	System.out.println("checkIn 작동");
        service.recordCheckIn(session);
        return "redirect:/attend";
    }
    
    //퇴근 시간 기록
    @PostMapping("/out")
    String checkOut(HttpSession session) {
    	System.out.println("checkOut 작동");
        service.recordCheckOut(session);
        return "redirect:/attend";
    }
    
    //출퇴근 기록 수정 신청 
    @GetMapping("/attendTimeInsert")
    String attendTimeInsert(HttpSession session, Model model) {
        System.out.println("attendTimeInsert 작동하나");

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        List<AttendDTO> attendInfoList =  attendMapper.userAttendList(loginUser);

        // 날짜만 표시 (yyyy-MM-dd), value는 id
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Map<String, Object>> attendOptions = attendInfoList.stream()
            .filter(a -> a.getCheckInTime() != null) // 날짜 없는 데이터 제외
            .map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", a.getId()); // <option value>
                map.put("date", a.getCheckInTime().toLocalDate().format(dateFmt)); // 표시용 텍스트(날짜만)
                return map;
            })
            // 필요 시 최신 날짜 먼저 보이게 정렬 (선택)
            .sorted((m1, m2) -> ((String)m2.get("date")).compareTo((String)m1.get("date")))
            .toList();

        model.addAttribute("attendOptions", attendOptions);
        model.addAttribute("mainUrl", "attend/attendTimeInsert");
        return "navTap";
    }

    
    // 관리자용
    @GetMapping("/attendList")
    public String attendListPage(AttendDTO dto,
                                 Model model,
                                 HttpServletRequest request) {

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
        List<AttendDTO> rows = attendMapper.searchAttendListPage(dto);

        // ✅ PageInfo로 래핑
        PageInfo<AttendDTO> page = new PageInfo<>(rows);

        // 뷰로 전달
        model.addAttribute("page", page);           // 전체 페이징 메타데이터
        model.addAttribute("mainData", page.getList()); // 현재 페이지 데이터
        model.addAttribute("criteria", dto);       // ✅ 검색 조건 바인딩
        model.addAttribute("pageNum", pageNum);    // ✅ 현재 페이지
        model.addAttribute("pageSize", pageSize);  // ✅ 페이지 크기
        model.addAttribute("mainUrl", "attend/attendList");
        


        return "navTap";
    }
    

} // ← 클래스 닫기
