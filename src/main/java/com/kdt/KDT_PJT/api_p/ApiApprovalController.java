package com.kdt.KDT_PJT.api_p;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiApprovalController {

    @Autowired
    private ApprovalMapper approvalMapper;

    @GetMapping("approval")
    public Object schedules(HttpSession session) {

        // 1. 로그인 유저 확인
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 안 함 → 빈 리스트 리턴
            return List.of();
        }

        // 2. 파라미터 세팅
        String role = loginUser.getRole();
        String employeeId = loginUser.getEmployeeId();
        String type = null;   // API에선 type 필터 없음
        String status = null; // 상태 필터 없음
        int page = 1;
        int size = 5; // 최신글 5개
        int offset = (page - 1) * size;

        // 3. 데이터 조회
        List<ApprovalDTO> approvalData =
                approvalMapper.approvalDataByRole(offset, size, role, type, status, employeeId);

        // 4. React에서 바로 받도록 데이터만 반환
        return approvalData;
    }

}
