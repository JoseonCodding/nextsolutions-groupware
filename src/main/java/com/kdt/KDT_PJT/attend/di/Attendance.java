package com.kdt.KDT_PJT.attend.di;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;

import jakarta.annotation.Resource;

@Component
public class Attendance {

	@Resource	
	AttendMapper mapper;

    public void recordCheckIn() {
    	
    	System.out.println("recordCheckIn 되고있냐아");
    	AttendDTO attend = new AttendDTO();
    	
        attend.setEmployeeId("test_user"); // 로그인 미구현
        attend.setCheckInTime(LocalDateTime.now());

        mapper.insertAttendance(attend);
    }
    
    public void recordCheckOut() {
    	
    	System.out.println("recordCheckOut :퇴근");
    	AttendDTO attend = new AttendDTO();
    	
        attend.setEmployeeId("test_user"); // 로그인 미구현
        attend.setCheckOutTime(LocalDateTime.now());

        mapper.updateAttendance(attend);
    }
    
    public List<AttendDTO> getAttendData() {
    	return mapper.sellectAttendList();
    }
}
