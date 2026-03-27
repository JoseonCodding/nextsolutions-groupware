package com.kdt.KDT_PJT.api_p;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.service.ScheduleService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiScheduleController {

	@Autowired
	ScheduleService scheduleService;

	@GetMapping("logInfo")
	Object get(HttpSession session) {
		return session.getAttribute("loginUser");
	}

	@GetMapping("schedules")
	Object schedules(HttpSession session) {
		ScheduleDTO schDto = new ScheduleDTO();
		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
		if (loginUser != null) {
		    schDto.setCompanyId(loginUser.getCompanyId());
		}
		return scheduleService.getScheduleListForApi(schDto);
	}

    @GetMapping("schedulealert")
    Object sendNotifications(HttpSession session) {
    	ScheduleDTO schDto = new ScheduleDTO();
    	List<ScheduleDTO> res = scheduleService.getActiveNotifications(schDto);
        return res;
    }

}
