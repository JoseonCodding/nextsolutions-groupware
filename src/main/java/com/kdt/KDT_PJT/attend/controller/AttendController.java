package com.kdt.KDT_PJT.attend.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.attend.di.Attendance;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.attend.model.AttendMapper2;
import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.PageDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    String showAttendancePage(HttpSession session, Model model,AttendDTO attendance) {
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
    	
    	// 현재 조회 기간이 없으면 기본값 세팅 (이번달 시작일 ~ 이번달 말일)
        if(attendance.getStartDay() == null || attendance.getEndDay() == null) {
            LocalDate now = LocalDate.now();  
            attendance.setStartDay(now.withDayOfMonth(1).toString());      // 이번달 1일
            attendance.setEndDay(now.withDayOfMonth(now.lengthOfMonth()).toString()); // 이번달 마지막 날
        }

        // 로그인 사용자 아이디 세팅
        attendance.setEmployeeId(loginUser.getEmployeeId());
        attendance.setCompanyId(loginUser.getCompanyId());
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
    	
    	log.debug("today={}, todayAttend={}, hasCheckIn={}", today, todayAttend, hasCheckIn);

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
        
    	//model.addAttribute("leaveDate", leaveDate);
        model.addAttribute("mainData", attendMonthList);
        model.addAttribute("mainUrl", "attend/check"); 
       
        
        // DTO에 구현한 메서드 호출
        model.addAttribute("prevMonthStartDay", attendance.getPrevMonthStartDay());
        model.addAttribute("prevMonthEndDay", attendance.getPrevMonthEndDay());
        model.addAttribute("nextMonthStartDay", attendance.getNextMonthStartDay());
        model.addAttribute("nextMonthEndDay", attendance.getNextMonthEndDay());
        model.addAttribute("currentStartDay", attendance.getStartDay());
        model.addAttribute("attendDTO2", attendance);

        return "navTap";
        
    }
    


    
    //출근 시간 기록
    @PostMapping("/in")
    String checkIn(HttpSession session) {
        service.recordCheckIn(session);
        return "redirect:/attend";
    }

    //퇴근 시간 기록
    @PostMapping("/out")
    String checkOut(HttpSession session) {
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

        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        AttendDTO attendance = new AttendDTO();
        
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
                                 HttpServletRequest request, PageDTO pDTO) {

 
        

        // ✅ 여기서 페이징 시작
        PageHelper.startPage(pDTO.getPage(), pDTO.getSize());

        // ✅ 하나의 경로로 조회 (필요 시 dto.workDate를 오늘 날짜로 채워 기본 동작 만들기)
        dto.setCompanyId(com.kdt.KDT_PJT.cmmn.context.CompanyContext.get());
        List<AttendDTO> rows = attendMapper.searchAttendListPage(dto);
        
       
        


        // ✅ PageInfo로 래핑
        PageInfo<AttendDTO> page = new PageInfo<>(rows);
        
        pDTO.setTotalCount((int)page.getTotal());
        
        // 뷰로 전달
        model.addAttribute("page", page);           // 전체 페이징 메타데이터
        model.addAttribute("mainData", page.getList()); // 현재 페이지 데이터
        model.addAttribute("criteria", dto);       // ✅ 검색 조건 바인딩
        model.addAttribute("pDTO", pDTO);    // ✅ 현재 페이지
        
        model.addAttribute("mainUrl", "attend/attendList");
        


        return "navTap";
    }
    

    // === 출퇴근 기록 수정 신청 ===
    @Autowired
    AttendMapper2 attendMapper2;

    @PostMapping("/save")
    public String attendSave(
            @RequestParam("workDate") String workDate,
            @RequestParam("content") String content,
            @RequestParam(value = "actions", required = false) String[] actionsArr,
            HttpSession session,
            RedirectAttributes ra
    ) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String employeeId = (loginUser != null) ? loginUser.getEmployeeId() : null;

        if (employeeId == null || workDate == null || workDate.isBlank() || content == null || content.isBlank()) {
            ra.addFlashAttribute("msg", "필수 정보가 누락되었습니다.");
            return "redirect:/attend/attendTimeInsert";
        }

        List<String> actions = (actionsArr == null) ? List.of() : Arrays.asList(actionsArr);
        if (actions.isEmpty()) {
            ra.addFlashAttribute("msg", "정정 항목(정상출근/정상퇴근)을 선택하세요.");
            return "redirect:/attend/attendTimeInsert";
        }

        int inUpdated = 0, outUpdated = 0;
        if (actions.contains("IN"))  inUpdated  = attendMapper2.fixInByEmpAndDate(employeeId, workDate, content);
        if (actions.contains("OUT")) outUpdated = attendMapper2.fixOutByEmpAndDate(employeeId, workDate, content);

        int total = inUpdated + outUpdated;
        if (total > 0) {
            ra.addFlashAttribute("msg", "정상 처리되었습니다. (출근 수정: " + inUpdated + "건, 퇴근 수정: " + outUpdated + "건)");
        } else {
            ra.addFlashAttribute("msg", "대상 기록이 없거나 상태가 '대기'가 아닙니다.");
        }
        return "redirect:/attend";
    }

    // === PDF 내보내기 ===
    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void attendListPdf(AttendDTO dto, HttpServletResponse resp) throws IOException {
        List<AttendDTO> list = attendMapper2.searchAttendListHistoryPaged(dto);

        String fileBase = "attendance_changes";
        String encoded = URLEncoder.encode(fileBase, StandardCharsets.UTF_8).replace("+", "%20");
        resp.reset();
        resp.setContentType(MediaType.APPLICATION_PDF_VALUE);
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileBase + ".pdf\"; filename*=UTF-8''" + encoded + ".pdf");

        writePdf(list, resp);
    }

    private void writePdf(List<AttendDTO> list, HttpServletResponse resp) {
        try (java.io.OutputStream os = resp.getOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, os);
            doc.open();

            BaseFont base = loadBaseFont();
            Font titleFont  = new Font(base, 16, Font.BOLD);
            Font headerFont = new Font(base, 10, Font.BOLD);
            Font cellFont   = new Font(base, 9,  Font.NORMAL);

            Paragraph title = new Paragraph("근태 변경 이력", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            doc.add(title);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12, 12, 12, 10, 10, 12, 12, 20});

            addPdfHeader(table, headerFont, "근무일", "사번", "이름", "출근", "퇴근", "수정자", "수정일", "사유");

            java.time.format.DateTimeFormatter d  = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.time.format.DateTimeFormatter hm = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

            for (AttendDTO a : list) {
                addPdfCell(table, cellFont, formatDate(toLdt(a.getCheckInTime()), d));
                addPdfCell(table, cellFont, nvl(a.getEmployeeId()));
                addPdfCell(table, cellFont, nvl(a.getEmpNm()));
                addPdfCell(table, cellFont, formatTime(toLdt(a.getCheckInTime()), hm));
                addPdfCell(table, cellFont, formatTime(toLdt(a.getCheckOutTime()), hm));
                addPdfCell(table, cellFont, nvl(a.getModifiedBy()));
                addPdfCell(table, cellFont, formatDate(toLdt(a.getModifiedAt()), d));
                addPdfCell(table, cellFont, nvl(a.getModificationReason()));
            }

            doc.add(table);
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("PDF export failed: " + e.getMessage(), e);
        }
    }

    private BaseFont loadBaseFont() throws Exception {
        String[] fontPaths = {
            "static/fonts/NotoSans-Regular.otf",
            "fonts/NotoSans-Regular.otf",
            "static/fonts/NotoSans-Regular.ttf",
            "fonts/NotoSans-Regular.ttf"
        };
        for (String fontPath : fontPaths) {
            try {
                ClassPathResource fontRes = new ClassPathResource(fontPath);
                if (fontRes.exists()) {
                    try (java.io.InputStream fis = fontRes.getInputStream()) {
                        byte[] fontBytes = fis.readAllBytes();
                        return BaseFont.createFont(
                                fontPath.substring(fontPath.lastIndexOf('/') + 1),
                                BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
                    }
                }
            } catch (Exception ignored) {}
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    private static java.time.LocalDateTime toLdt(Object t) {
        if (t == null) return null;
        if (t instanceof java.time.LocalDateTime ldt) return ldt;
        if (t instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        if (t instanceof java.util.Date d) return java.time.LocalDateTime.ofInstant(d.toInstant(), java.time.ZoneId.systemDefault());
        return null;
    }

    private static String formatDate(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter fmt) {
        return ldt == null ? "" : ldt.toLocalDate().format(fmt);
    }

    private static String formatTime(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter fmt) {
        return ldt == null ? "" : ldt.toLocalTime().format(fmt);
    }

    private static String nvl(Object o) { return o == null ? "" : String.valueOf(o); }

    private static void addPdfHeader(PdfPTable t, Font f, String... cols) {
        for (String c : cols) {
            var cell = new PdfPCell(new Phrase(c, f));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new java.awt.Color(240, 240, 240));
            t.addCell(cell);
        }
    }

    private static void addPdfCell(PdfPTable t, Font f, String val) {
        t.addCell(new Phrase(val == null ? "" : val, f));
    }

} // ← 클래스 닫기
