package com.kdt.KDT_PJT.api_p;

import java.text.SimpleDateFormat;
import java.util.Date;
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
		//schDto.monthDays();
		
		System.out.println("/api/schedules 진입");
		
		List<ScheduleDTO> scheduleList = mapper.getScheduleListRepeatEmpty();
		scheduleList.addAll(mapper.getProjectListByMonth());
		return scheduleList;
	}

    @GetMapping("schedulealert")	
    Object sendNotifications(HttpSession sesson) {
    	ScheduleDTO schDto = new ScheduleDTO();
    	//schDto.setCurr(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    	System.out.println("/api/schedulealert 진입 1 "+schDto);
    	List<ScheduleDTO> res = mapper.getActiveAllDayNotifications(schDto);
    	System.out.println("/api/schedulealert 진입 2 "+res);
    	
        return res;
    }
    
}
