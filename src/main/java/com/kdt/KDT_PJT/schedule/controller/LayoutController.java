package com.kdt.KDT_PJT.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/schedule/{service}?scheduleId={scheduleId}")
public class LayoutController {

	@Resource
	ScheduleMapper mapper;
	
	@ModelAttribute
	Object pInfoGo(@ModelAttribute("pInfo") PageInfo pInfo,
					@ModelAttribute("dto") ScheduleDTO dto, 
					HttpServletRequest request) {
		
		pInfo.setCate("schedule");
		
		
		//schedule main
		if(pInfo.getService().equals("")) {
			return mapper.getScheduleListByMonth(dto);
		}
		
		//detail , modify
		if(pInfo.getService().equals("detail") || pInfo.getService().equals("modify")) {
			return mapper.getScheduleDetail(dto);
		}
		
		//insertReg
		if(pInfo.getService().equals("insertReg")) {
			
			int cnt = mapper.insert(dto);
			
			//pInfo.setMainUrl("alert");
			//pInfo.setMsg("작성되었습니다."+cnt);
			pInfo.setGoUrl("/schedule"+dto.getScheduleId());
		}
		
		//deleteReg
//		if(pInfo.getService().equals("scheduleDelete")) {
//					
//			//pInfo.setMainUrl("alert");
//			//pInfo.setMsg("암호불일치");
//			pInfo.setGoUrl("/"+pInfo.getCate()+"/delete/"+pInfo.getNowPage()+"/"+dto.getScheduleId());
//					
//			int cnt = mapper.delete(dto);
//			
//			if(cnt>0) {  //삭제되었다면
//				//pInfo.setMsg("삭제되었습니다.");
//				pInfo.setGoUrl("/"+pInfo.getCate()+"/list/"+pInfo.getNowPage());
//			}
//		}
		
		//modifyReg
		if(pInfo.getService().equals("scheduleModifyReg")) {
					
			//pInfo.setMainUrl("alert");
			//pInfo.setMsg("암호불일치");
			pInfo.setGoUrl("/schedule/modify?scheduleId="+dto.getScheduleId());
					
			int cnt = mapper.modify(dto);
					
			if(cnt>0) {  //수정되었다면
				//pInfo.setMsg("수정되었습니다.");
				pInfo.setGoUrl("/schedule/detail?scheduleId="+dto.getScheduleId());
			}
		}
			
		return null;
	}
}
