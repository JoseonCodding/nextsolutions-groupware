package com.kdt.KDT_PJT.attend.di;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
            double hours = minutes / 60.0;
            attend.setWorkHours(hours);

            // 정상근무 판정
            boolean isNormal = 
                !todayAttend.getCheckInTime().toLocalTime().isAfter(LocalTime.of(9,0)) &&
                !attend.getCheckOutTime().toLocalTime().isBefore(LocalTime.of(18,0)) &&
                hours >= 8.0;

            attend.setNormalWork(isNormal);
        }
        mapper.updateAttendance(attend);
    }
    
    //사용자 본인의 출퇴근 기록
    public List<AttendDTO> getUserAttendData(EmployeeDto loginUser) {
    	return mapper.userAttendList(loginUser.getEmployeeId());
    }
    
    //모든 사용자의 출퇴근 기록 (관리자용)
    public List<AttendDTO> getAttendData() {
    	return mapper.attendList();
    }
    
//  근태 관리자 페이지 - 출퇴근 기록 조회에 검색 기능 추가시 필요 (오류나서 주석 처리 ,추후 해결하기)
//    public List<AttendDTO> getTodayAttendData() {
//        return mapper.getTodayAttendList();
//    }
//
//    public List<AttendDTO> searchAttendData(String workDate, String empNm, String modifiedBy) {
//        return mapper.searchAttendList(workDate, empNm, modifiedBy);
//    }

}
