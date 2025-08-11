package com.kdt.KDT_PJT.attend.ctl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/attend")
@RequiredArgsConstructor
public class AttendController {

	@ModelAttribute("navUrl")
	String navUrl() {
		return "attend/nav";
	}
	
	@Autowired
    Attendance service;
	
	@Autowired
    private final AttendMapper attendMapper;

	//사용자 본인의 출퇴근 기록 (근태관리 메인 페이지)
    @GetMapping
    String showAttendancePage(HttpSession session, Model model) {
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	//List<AttendDTO> attendList = service.userAttendList(); 
    	List<AttendDTO> attendList =  attendMapper.userAttendList(loginUser); 
 
    	//System.out.println("사용자 인식: "+empDto.getEmployeeId());
    	System.out.println("showAttendancePage:"+attendList);
    	
        model.addAttribute("mainData", attendList);
        model.addAttribute("mainUrl", "attend/check");
        return "navTap";
        
    }

    //출근 시간 기록
    @PostMapping("/in")
    String checkIn(HttpSession session) {
    	System.out.println("checkIn 작동하나");
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

    
    //근태 관리자 페이지(출퇴근 변경이력 포함)
//    @GetMapping("/attendList")
//    public String attendListPage(Model model) {
//        List<AttendDTO> attendList = service.getAttendData();
//        model.addAttribute("mainData", attendList);
//        model.addAttribute("mainUrl", "attend/attendList");
//        return "navTap"; 
//    }
   
 // 근태 관리자 페이지 - 검색 기능 포함
    @GetMapping("/attendList")
    public String attendListPage(AttendDTO dto,
    	    Model model) {

        // 검색 조건이 없으면 오늘 출근한 사람만 보여줌
        List<AttendDTO> attendList;
        if (dto.getWorkDate() == null && dto.getEmpNm() == null && dto.getModifiedBy() == null) {
            attendList = attendMapper.getTodayAttendList();
        } else {
        	
        	System.out.println("attendListPage2 : "+dto);
            attendList = attendMapper.searchAttendList(dto);
        }

        model.addAttribute("mainData", attendList);
        model.addAttribute("mainUrl", "attend/attendList");
        return "navTap"; 
    }
    
    /**
     * 출퇴근 기록 수정 신청 저장
     * - WHERE: employeeId + DATE(check_in_time) = workDate
     * - SET: modified_by(=employeeId), modified_at(now), modification_reason(content)
     */
    @PostMapping("/save")
    public String attendSave(
            @RequestParam("workDate") String workDate,   // yyyy-MM-dd (셀렉트 value)
            @RequestParam("title") String title,         // 현재는 DB 미저장 (원하면 content에 prefix로 합치기)
            @RequestParam("content") String content,     // 수정 사유
            HttpSession session,
            RedirectAttributes ra
    ) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String employeeId = (loginUser != null) ? loginUser.getEmployeeId() : null;

        if (employeeId == null || workDate == null || workDate.isBlank()) {
            ra.addFlashAttribute("msg", "필수 정보가 누락되었습니다.");
            return "redirect:/attend";
        }

        int updated = attendMapper.updateAttendModification( // ✅ 인스턴스로, 이름도 매퍼와 동일
                employeeId,           // WHERE employeeId
                workDate,             // WHERE DATE(check_in_time) = workDate
                employeeId,           // modified_by ← employeeId 사용
                LocalDateTime.now(),  // modified_at
                content               // modification_reason
        );
        ra.addFlashAttribute("msg", updated == 1 ? "결재 신청이 등록되었습니다." : "대상 기록이 없습니다.");
        return "redirect:/attend";
    }


    

}
