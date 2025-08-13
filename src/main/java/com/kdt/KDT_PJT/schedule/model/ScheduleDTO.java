package com.kdt.KDT_PJT.schedule.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class ScheduleDTO {


	int scheduleId, repeatCheck;
    String title, cate, alarm, content, holiday;
    Date startDate, endDate, createdAt, updatedAt, deleteDate;
    String employeeId;
    //Date startTime, endTime;
    Time startTime, endTime;
    
    LocalDate startLocalDate, now;
    LocalDate endLocalDate;
    Integer firstDayOfWeek, year, month, lastDate, startLocalDateNo, endLocalDateNo;
    
    public void monthDays() {
    	
        now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        
        this.startDate = java.sql.Date.valueOf(firstDay);
        this.endDate = java.sql.Date.valueOf(lastDay);
        this.firstDayOfWeek = firstDay.getDayOfWeek().getValue();
        
        lastDate = Calendar.getInstance().getActualMaximum(Calendar.DATE);
        
        System.out.println("monthDays : "+ now);
    }

    public void convertDatesToLocal() {
        if (startDate != null) {
        	startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
	//start_date DATETIME NOT NULL,                -- 일정 시작일
	//end_date DATETIME NOT NULL,                  -- 일정 종료일
	//cate VARCHAR(50) NOT NULL,              	 -- 일정 종류(종일 일정, 반복 일정)
	//alarm VARCHAR(50) ,      				  	 -- 알림 여부
	// created_at date, 	 	-- 일정 등록일
	// updated_at date,		-- 일정 수정일
	// delete_date date		-- 일정 삭제일
}
