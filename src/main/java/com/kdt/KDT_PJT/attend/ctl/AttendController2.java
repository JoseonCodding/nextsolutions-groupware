package com.kdt.KDT_PJT.attend.ctl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    @PostMapping("/save")
    public String attendSave(
            @RequestParam("workDate") String workDate,                // yyyy-MM-dd
            @RequestParam("title") String title,                      // 제목
            @RequestParam("content") String content,                  // 사유
            @RequestParam(value = "actions", required = false) String[] actionsArr,
            HttpSession session,
            RedirectAttributes ra
    ) {
        // 세션 사용자 (WHERE에는 employeeId 사용, modified_by는 매퍼에서 emp_nm으로 설정)
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String employeeId = (loginUser != null) ? loginUser.getEmployeeId() : null;

        // 기본 검증
        if (employeeId == null || workDate == null || workDate.isBlank()) {
            ra.addFlashAttribute("msg", "필수 정보가 누락되었습니다.");
            return "redirect:/attend";
        }

        List<String> actions = (actionsArr == null) ? List.of() : Arrays.asList(actionsArr);
        if (actions.isEmpty()) {
            ra.addFlashAttribute("msg", "정정 항목(정상출근/정상퇴근)을 선택하세요.");
            return "redirect:/attend";
        }

        // 처리
        LocalDateTime now = LocalDateTime.now();
        int inUpdated = 0;
        int outUpdated = 0;

        if (actions.contains("IN")) {
            inUpdated = attendMapper.fixInByEmpAndDate(
                    employeeId,   // WHERE employeeId
                    workDate,     // WHERE DATE(check_in_time) = workDate AND state_type='대기'
                    now,          // modified_at
                    title,        // 제목
                    content       // modification_reason (매퍼에서 [title] content 형태로 저장)
            );
        }
        if (actions.contains("OUT")) {
            outUpdated = attendMapper.fixOutByEmpAndDate(
                    employeeId,
                    workDate,
                    now,
                    title,
                    content
            );
        }

        int total = inUpdated + outUpdated;

        if (total > 0) {
            StringBuilder sb = new StringBuilder("정상 처리되었습니다.");
            sb.append(" (출근 수정: ").append(inUpdated).append("건, 퇴근 수정: ").append(outUpdated).append("건)");
            ra.addFlashAttribute("msg", sb.toString());
        } else {
            // 매퍼 WHERE에 state_type='대기' 조건이 있으므로 이 메시지로 안내
            ra.addFlashAttribute("msg", "대상 기록이 없거나 상태가 ‘대기’가 아닙니다.");
        }

        return "redirect:/attend";
    }
}

