package com.kdt.KDT_PJT.attend.ctl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendDTO2;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;

import jakarta.annotation.Resource;
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
    	
    	// 연차 날짜 가져오기용
    	List<LeaveDTO> leaveDate = attendMapper.searchLeaveDate(attendance); 
    	
    	
    	

    	
    	
    	
    	
    	
    	
    	// 달력 라이브러리 - Map 형태로 변환 --->
        Map<String, AttendDTO> attendMap = new HashMap<>();
        for (AttendDTO dto : attendMonthList) {
            attendMap.put(dto.getWorkDate(), dto);
        }
        
        Map<String, LeaveDTO> attendMapLeave = new HashMap<>();
        
        
        // 오늘 날짜 문자열
    	String today = LocalDate.now().toString();
    	
    	

    	// 오늘 출퇴근 기록 가져오기
    	AttendDTO todayAttend = attendMap.get(today);
    	
    	

    	boolean hasCheckIn = todayAttend != null && todayAttend.getCheckInTime() != null;
    	boolean hasCheckOut = todayAttend != null  && todayAttend.getCheckInTime() != null && todayAttend.getCheckOutTime() == null;
    	
    	System.out.println(today);
    	System.out.println(todayAttend);
    	System.out.println(hasCheckIn);
    	//System.out.println(todayAttend.getCheckInTime());

    	// DTO에 상태 저장
    	attendance.setTodayCheckIn(hasCheckIn);
    	attendance.setTodayCheckOut(hasCheckOut);
        
        
        //// 출퇴근 버튼 보이기
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowStr = sdf.format(now);
        
        // 주말에 안 보이게
        if(now.getDay()==0 ||now.getDay()==6 ) {
        	attendance.setNowIsHoliday(true);
        	
        }
        
        // 연차
        for (LeaveDTO leave : leaveDate) {
            attendMapLeave.put(leave.getUsedDateStr(), leave);
            //System.out.println(leave.getUsedDateStr()+" : "+nowStr);
            
            // 연차가 오늘이면 출퇴근 버튼 안보이게
            if(leave.getUsedDateStr().equals(nowStr)) {
            	attendance.setNowIsHoliday(true);
            }
        }
        
        
     
    	//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ holiday  :"+holiday);
        
        
        model.addAttribute("attendMap", attendMap); // 날짜 기준 Map 전달
        model.addAttribute("attendMapLeave", attendMapLeave);
        
        List<ScheduleDTO> holidayArr = attendMapper.searchHoliday(attendance);
        
        for (ScheduleDTO holiday : holidayArr) {
			if(holiday.getStartDateStr().compareTo(nowStr)<= 0 && 
					holiday.getEndDateStr().compareTo(nowStr)> 0) {  //fullcalender가 인지하는 마지막 날 때문에 =은 제외
				attendance.setNowIsHoliday(true);
			}
		}
        
        model.addAttribute("holidayArr", holidayArr);
        
        

        model.addAttribute("StartDay", attendance.getStartDay());
        model.addAttribute("EndDay", attendance.getEndDay());
        
        //<---
        
    	//System.out.println("showAttendancePage:"+attendMonthList);
    	
    	//model.addAttribute("leaveDate", leaveDate);
        model.addAttribute("mainData", attendMonthList);
        model.addAttribute("mainUrl", "attend/check"); 
        //model.addAttribute("attendDTO", attendance);   // 조회 조건 유지용
       
        
        // DTO에 구현한 메서드 호출
        model.addAttribute("prevMonthStartDay", attendance.getPrevMonthStartDay());
        model.addAttribute("prevMonthEndDay", attendance.getPrevMonthEndDay());
        model.addAttribute("nextMonthStartDay", attendance.getNextMonthStartDay());
        model.addAttribute("nextMonthEndDay", attendance.getNextMonthEndDay());
        model.addAttribute("currentStartDay", attendance.getStartDay());

        return "navTap";
        
    }
    
//    //FullCalendar - 
//    @PostMapping("/attend/json")
//    @ResponseBody
//    public Map<String, Object> getAttendanceJson(HttpSession session, @RequestBody AttendDTO2 attendance) {
//        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
//        attendance.setEmployeeId(loginUser.getEmployeeId());
//
//        // 해당 기간 데이터 조회
//        List<AttendDTO> attendMonthList = attendMapper.userAttendMonthList(attendance);
//        List<LeaveDTO> leaveDate = attendMapper.searchLeaveDate(attendance);
//
//        // 배열 형태로 변환
//        List<Map<String, String>> attendArray = new ArrayList<>();
//        for (AttendDTO dto : attendMonthList) {
//            Map<String, String> map = new HashMap<>();
//            map.put("date", dto.getWorkDate());
//            map.put("checkIn", dto.getCheckInHourMinute());
//            map.put("checkOut", dto.getCheckOutHourMinute());
//            attendArray.add(map);
//        }
//
//        
//        
//        List<Map<String, String>> leaveArray = new ArrayList<>();
//        for (LeaveDTO leave : leaveDate) {
//            Map<String, String> map = new HashMap<>();
//            map.put("date", leave.getUsedDateStr());
//
//            leaveArray.add(map);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("attendArray", attendArray);
//        result.put("leaveArray", leaveArray);
//
//        return result;
//    }



    
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
//    @GetMapping("/attendTimeInsert")
//    String attendTimeInsert(HttpSession session, Model model) {
//        System.out.println("attendTimeInsert 작동하나");
//
//        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
//        List<AttendDTO> attendInfoList =  attendMapper.userAttendList(loginUser);
//
//        // 날짜만 표시 (yyyy-MM-dd), value는 id
//        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        List<Map<String, Object>> attendOptions = attendInfoList.stream()
//            .filter(a -> a.getCheckInTime() != null) // 날짜 없는 데이터 제외
//            .map(a -> {
//                Map<String, Object> map = new HashMap<>();
//                map.put("id", a.getId()); // <option value>
//                map.put("date", a.getCheckInTime().toLocalDate().format(dateFmt)); // 표시용 텍스트(날짜만)
//                return map;
//            })
//            // 필요 시 최신 날짜 먼저 보이게 정렬 (선택)
//            .sorted((m1, m2) -> ((String)m2.get("date")).compareTo((String)m1.get("date")))
//            .toList();
//
//        model.addAttribute("attendOptions", attendOptions);
//        model.addAttribute("mainUrl", "attend/attendTimeInsert");
//        return "navTap";
//    }
    
    @GetMapping("/attendTimeInsert")
    String attendTimeInsert(HttpSession session, Model model) {
        System.out.println("attendTimeInsert 작동하나");

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        AttendDTO2 attendance = new AttendDTO2();
        
        attendance.setEmployeeId(loginUser.getEmployeeId());
        
        if(attendance.getStartDay() == null || attendance.getEndDay() == null) {
            LocalDate now = LocalDate.now();  
            attendance.setStartDay(now.withDayOfMonth(1).toString());      // 이번달 1일
            attendance.setEndDay(now.withDayOfMonth(now.lengthOfMonth()).toString()); // 이번달 마지막 날
        }
        
        List<AttendDTO> attendOptions =  attendMapper.userAttendMonthAppr(attendance);

        

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
