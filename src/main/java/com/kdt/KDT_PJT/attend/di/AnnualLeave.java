package com.kdt.KDT_PJT.attend.di;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdt.KDT_PJT.attend.model.LeaveDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;

import jakarta.annotation.Resource;

@Component
public class AnnualLeave {

	@Resource
	LeaveMapper mapper;

	public LeaveDTO getAnnualLeaveOne() {
		System.out.println("한 명 연차 정보 - DB");
		
		LeaveDTO res = mapper.name();
		
		res.setLeave_type("발생");
		res.setTotal(mapper.getAnnualLeaveCnt(res));
		res.setLeave_type("사용");
		res.setUse(mapper.getAnnualLeaveCnt(res));
		
		
		return res;
	}
	
//	public LeaveDTO getAnnualLeaveAll() {
//		System.out.println("사용자들의 연차 목록 작동");
//		
//		LeaveDTO res = mapper.annualLeave();
//		
//		res.setLeave_type("발생");
//		res.setTotal(mapper.getAnnualLeaveCnt(res));
//		res.setLeave_type("사용");
//		res.setUse(mapper.getAnnualLeaveCnt(res));
//		
//		
//		return res;
//	}
	
	
}
