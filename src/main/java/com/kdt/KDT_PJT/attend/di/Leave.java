package com.kdt.KDT_PJT.attend.di;

import java.time.YearMonth;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.CommuteMapper;

import jakarta.annotation.Resource;

@Service
public class Leave {

    @Resource
    private CommuteMapper commuteMapper;

    public void autoGiveLeaveForQualifiedEmployees() {
    	
//    	Date today = new Date();
//    	System.out.println("autoGiveLeaveForQualifiedEmployees : "+ today+"deahedrfgdgregrgraeg");
//    	AttendDTO param = new AttendDTO();
//    	
//    	param.setStartDay("2025-07-01");
//    	param.setEndDay("2025-07-30");
//    	
//    	
//    	
//    	List<AttendDTO> allEmployees = commuteMapper.getLastMonthTotalWorkDays(param);
//
//        for (AttendDTO dto : allEmployees) {
//            
//        	System.out.println(dto);
//
//        }
    	
    	Date today = new Date();
    	System.out.println("autoGiveLeaveForQualifiedEmployees : "+ today+"deahedrfgdgregrgraeg");
    	
    	AttendDTO param = new AttendDTO();
    	
    	param.setStartDay("2025-07-01");
    	param.setEndDay("2025-07-30");
    	
    	 // 해당 월 휴무일 수 조회
        int totalOffDays = commuteMapper.getHolidays();

        // 해당 월 총 일수 계산
        YearMonth yearMonth = YearMonth.of(2025, 7);
        int totalDays = yearMonth.lengthOfMonth();
        int normalWorkDays = totalDays - totalOffDays;
        
        
    	
    	List<AttendDTO> allEmployees = commuteMapper.getLastMonthTotalWorkDays(param);

        for (AttendDTO dto : allEmployees) {
            
        	System.out.println("테스트 목록"+dto);

        }
        
        for (AttendDTO dto : allEmployees) {
            int workedDays = dto.getWorkCnt(); // 정상 근무일 수
            double workRate = (double) workedDays / normalWorkDays;

            if (workRate >= 0.8) {
                
               // leaveDto.setEmployeeId(dto.getEmployeeId());
               // leaveDto.setCreateReason("전월 근무율 80% 이상 자동 부여");
               // leaveDto.setLeaveType("발생");

                commuteMapper.insertAutoLeave();
                System.out.println("연차 부여 완료: " + dto.getEmployeeId());
            }
        }
    	
    }
}
