package com.kdt.KDT_PJT.attend.di;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.CommuteMapper;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;

import jakarta.annotation.Resource;

@Service
public class Leave {

    @Resource
    private CommuteMapper commuteMapper;

    @Resource
    private LeaveMapper leaveMapper;

    public void autoGiveLeaveForQualifiedEmployees() {
    	
    	Date today = new Date();
    	
    	AttendDTO param = new AttendDTO();
    	
    	param.setStartDay("2025-07-01");
    	param.setEndDay("2025-07-30");
    	
    	List<AttendDTO> allEmployees = commuteMapper.getLastMonthTotalWorkDays(param);

        for (AttendDTO dto : allEmployees) {
            
        	System.out.println(dto);

        }
    }
}
