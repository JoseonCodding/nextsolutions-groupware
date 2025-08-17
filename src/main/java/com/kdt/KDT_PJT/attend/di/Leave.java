package com.kdt.KDT_PJT.attend.di;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
    	
    	Date today = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	System.out.println("autoGiveLeaveForQualifiedEmployees : "+ today);
    	
    	AttendDTO param = new AttendDTO();
    	
    	Date startDay = new Date(today.getYear(),today.getMonth()-1,1);
    	Date endDay = new Date(today.getYear(),today.getMonth(),0);
    	
    	param.setStartDay(sdf.format(startDay));
    	param.setEndDay(sdf.format(endDay));
    	
    	// 해당 월 회사 휴무일 수 조회
        int totalOffDays = commuteMapper.getHolidays(param);
        System.out.println("totalOffDays : "+totalOffDays);

        

        YearMonth yearMonth = YearMonth.of(startDay.getYear()+1900,startDay.getMonth()+1);

        int workDays = 0;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            DayOfWeek dow = date.getDayOfWeek();
            
            // 토요일, 일요일 제외
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                workDays++;
            }
        }

        System.out.println("평일 개수 = " + workDays);
        
        int normalWorkDays = workDays - totalOffDays;

    	
    	List<AttendDTO> allEmployees = commuteMapper.getLastMonthTotalWorkDays(param);

        for (AttendDTO dto : allEmployees) {
            
        	System.out.println("테스트 목록"+dto);

        }
        
        for (AttendDTO dto : allEmployees) {
            int workedDays = dto.getWorkCnt(); // 정상 근무일 수
            double workRate = (double) workedDays / normalWorkDays;

            if (workRate >= 0.8) {
                
                commuteMapper.insertAutoLeave(dto);
                System.out.println("연차 부여 완료: " + dto.getEmployeeId());
            }
        }
    	
    }
}
