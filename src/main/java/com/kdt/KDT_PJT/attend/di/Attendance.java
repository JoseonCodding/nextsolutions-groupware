package com.kdt.KDT_PJT.attend.di;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Component
public class Attendance {

	@Resource	
	AttendMapper mapper;

	//출근 시간 기록
    public void recordCheckIn(HttpSession session) {
    	//세션에서 loginUser 꺼내기
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
        //loginUser.getEmpNm(); // 이름 가져오기

    	System.out.println("recordCheckIn 되고있냐아");
    	AttendDTO attend = new AttendDTO();
    	
        //attend.setEmployeeId("test_user"); // 로그인 미구현
        attend.setCheckInTime(LocalDateTime.now());
        attend.setEmployeeId(loginUser.getEmployeeId());  
        mapper.insertAttendance(attend);
    }
    
    //퇴근 시간 기록
    public void recordCheckOut(HttpSession session) {
    	//세션에서 loginUser 꺼내기
    	EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");

    	System.out.println("recordCheckOut :퇴근");
    	AttendDTO attend = new AttendDTO();
    	
        attend.setCheckOutTime(LocalDateTime.now());
        attend.setEmployeeId(loginUser.getEmployeeId());
        
        // 출근 시간 가져오기
        AttendDTO todayAttend = mapper.findTodayAttendance(loginUser.getEmployeeId());

        if (todayAttend != null && todayAttend.getCheckInTime() != null) {
            long minutes = Duration.between(todayAttend.getCheckInTime(), attend.getCheckOutTime()).toMinutes();
         
        }
        mapper.updateAttendance(attend);
    }

}
