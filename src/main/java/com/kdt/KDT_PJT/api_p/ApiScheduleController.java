package com.kdt.KDT_PJT.api_p;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiScheduleController {
	
	@Autowired
	ScheduleMapper mapper;
	
	@GetMapping("logInfo")
	Object get(HttpSession sesson) {
		
		System.out.println("/api/logInfo 진입");
		return sesson.getAttribute("loginUser");
	}

	
	@GetMapping("schedules")
	Object schedules(HttpSession sesson) {
		ScheduleDTO schDto = new ScheduleDTO();
		schDto.monthDays();
		
		System.out.println("/api/schedules 진입");
		return mapper.getScheduleListByMonth(schDto);
	}

    @GetMapping("schedulealert")	
    Object sendNotifications(HttpSession sesson) {
    	 List<ScheduleDTO> res = mapper.getActiveNotifications();
    	System.out.println("/api/schedulealert 진입"+res);
    	
        return res;
    }
    
}
