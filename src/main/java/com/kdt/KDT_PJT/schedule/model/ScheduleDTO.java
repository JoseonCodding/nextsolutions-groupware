package com.kdt.KDT_PJT.schedule.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class ScheduleDTO {


	int scheduleId, repeatCheck;
    String title, cate, alarm, content, holiday, msg;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date startDate=new Date(), endDate=new Date(), createdAt=new Date(), updatedAt=new Date(), deleteDate=new Date();
    String employeeId;
    //Date startTime, endTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Time startTime, endTime;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate startLocalDate= LocalDate.now(), now= LocalDate.now();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate endLocalDate= LocalDate.now();
    
    Integer firstDayOfWeek, year, month, lastDate, startLocalDateNo, endLocalDateNo;
    
    
    /**
     * 현재 날짜 기준으로 해당 월의 시작일, 종료일, 시작 요일, 마지막 날짜를 계산하여 필드에 저장한다.
     * - startDate: 해당 월 1일 (java.sql.Date)
     * - endDate: 해당 월 마지막 일자 (java.sql.Date)
     * - firstDayOfWeek: 해당 월 1일의 요일 값 (월=1, 일=7)
     * - lastDate: 해당 월의 마지막 일자 (정수)
     */
    public void monthDays() {
    	
        //now = LocalDate.now();
    	
    	LocalDate target;
        if(year != null && month != null){
            target = LocalDate.of(year, month, 1);
        } else {
            target = LocalDate.now();
        }
        this.now = target;
    	
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        
        this.startDate = java.sql.Date.valueOf(firstDay);
        this.endDate = java.sql.Date.valueOf(lastDay);
        this.firstDayOfWeek = firstDay.getDayOfWeek().getValue();
        
        //lastDate = Calendar.getInstance().getActualMaximum(Calendar.DATE);
        this.lastDate = lastDay.getDayOfMonth(); // 기존 Calendar보다 정확
        
        System.out.println("monthDays : "+ now);
    }

    // DB에서 가져온 날짜(Date 타입)를 시스템 로컬 타임존(LocalDate) 기준으로 변환, 해당 날짜의 ‘일(day)’ 숫자만 별도로 저장
    public void convertDatesToLocal() {
        if (startDate != null) {
        	// Date → Instant
        	startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        	// LocalDate에서 '일(dayOfMonth)'만 추출 (예: 2025-08-13 → 13)
        	startLocalDateNo = startLocalDate.getDayOfMonth();
        }
        if (endDate != null) { 
        	endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        	endLocalDateNo = endLocalDate.getDayOfMonth();
        }
    }
    
    public void setStartDateStr(String ttt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = sdf.parse(ttt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
    
    public void setEndDateStr(String ttt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			endDate = sdf.parse(ttt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
    

    public String getStartDateStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return  sdf.format(startDate);
		
	}
    
    public String getEndDateStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return  sdf.format(endDate);
	}

    public String getStartTimeStr() {
    	if (startTime != null) { 
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(startTime);
        }
        return "";
    }
    
    public void setStartTimeStr(String ttt) {
        if (ttt != null && !ttt.isEmpty()) {
            this.startTime = Time.valueOf(ttt + ":00"); // "HH:mm" → "HH:mm:ss"
        }
    }

    public void setEndTimeStr(String ttt) {
        if (ttt != null && !ttt.isEmpty()) {
            this.endTime = Time.valueOf(ttt + ":00");
        }
    }

    public String getEndTimeStr() {
    	if (endTime != null) { 
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(endTime);
        }
        return "";
    }
    
	//start_date    -- 일정 시작일
	//end_date      -- 일정 종료일
	//cate          -- 종일 일정
	//alarm VARCHAR(50) 	-- 알림 여부
	// created_at date, 	-- 일정 등록일
	// updated_at date,		-- 일정 수정일
}
