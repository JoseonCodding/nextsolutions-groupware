package com.kdt.KDT_PJT.schedule.model;

import java.util.Date;

import lombok.Data;

@Data
public class ScheduleDTO {


	int scheduleId;
    String title, cate, alarm, content;
    Date startDate,endDate, createdAt,updatedAt,deleteDate;
    

   
	//start_date DATETIME NOT NULL,                -- 일정 시작일
	//end_date DATETIME NOT NULL,                  -- 일정 종료일
	//cate VARCHAR(50) NOT NULL,              	 -- 일정 종류(종일 일정, 반복 일정)
	//alarm VARCHAR(50) ,      				  	 -- 알림 여부
	// created_at date, 	 	-- 일정 등록일
	// updated_at date,		-- 일정 수정일
	// delete_date date		-- 일정 삭제일
}
