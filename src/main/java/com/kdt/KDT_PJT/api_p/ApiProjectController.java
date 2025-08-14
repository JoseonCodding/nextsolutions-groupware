package com.kdt.KDT_PJT.api_p;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.pjt_mng.svc.ProjectMngService;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiProjectController {
	
	@Autowired
	ScheduleMapper mapper;
	
	@Autowired
	private ProjectMngService projectMngService;

	 @GetMapping(value = "projects", produces = "application/json;charset=UTF-8")
	 public ResponseEntity<?> schedules(HttpSession session) {

	        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	        if (loginUser == null) {
	            // 세션 없음 → 401
	            Map<String, Object> err = Map.of(
	                "success", false,
	                "message", "로그인이 필요합니다."
	            );
	            return ResponseEntity.status(401).body(err);
	        }

	        // ▼ 서비스 호출 (네가 이미 만든 메서드들 그대로 사용)
	        int totalCount          = projectMngService.getTotalCount();
	        int progressCount       = projectMngService.getProgressCount();
	        int completeCount       = projectMngService.getCompleteCount();
	        int pendingCount        = projectMngService.getPendingCount();
	        int myProjectCount      = projectMngService.countMyProjects(loginUser.getEmployeeId());
	        int myApprovalTodoCount = projectMngService.countMyPendingApprovals(loginUser.getEmployeeId());

	        // ▼ JSON으로 보낼 바디 구성
	        Map<String, Object> data = new HashMap<>();
	        data.put("totalCount", totalCount);
	        data.put("progressCount", progressCount);
	        data.put("completeCount", completeCount);
	        data.put("pendingCount", pendingCount);
	        data.put("myProjectCount", myProjectCount);
	        data.put("myApprovalTodoCount", myApprovalTodoCount);

	        Map<String, Object> res = new HashMap<>();
	        res.put("success", true);
	        res.put("data", data);

	        return ResponseEntity.ok(res);
	    }
	}

