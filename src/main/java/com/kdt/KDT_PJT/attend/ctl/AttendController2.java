package com.kdt.KDT_PJT.attend.ctl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attend")
@RequiredArgsConstructor
public class AttendController2 {

    private final AttendMapper attendMapper;

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
