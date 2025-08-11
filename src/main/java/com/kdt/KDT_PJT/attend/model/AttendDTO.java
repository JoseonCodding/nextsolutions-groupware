package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AttendDTO {

	private Long id;
	private String employeeId, service, empNm;
	private LocalDateTime checkInTime, checkOutTime;
	int workCnt;

	private Double workHours;                // 근무시간 (ex: 8.0)
	private Boolean normalWork;              // 정상근무 여부
	private String modifiedBy;               // 수정자 ID
	private LocalDateTime modifiedAt;        // 수정일시
	private String modificationReason;      // 수정 사유
	String startDay, endDay;
	
	
//	work_hours DECIMAL(4,1) NULL,           -- 근무시간 (예: 8.0)
//	ADD COLUMN is_normal_work BOOLEAN DEFAULT FALSE,   -- 정상근무 여부
//	ADD COLUMN modified_by VARCHAR(20) NULL,           -- 수정자 ID
//	ADD COLUMN modified_at DATETIME NULL,              -- 수정일
//	ADD COLUMN modification_reason VARCHAR(255) NULL;
	 
	public String getWorkDate() {
       return checkInTime != null ? checkInTime.toLocalDate().toString() : "";
	}
	
	public void setWorkDate(String workDate) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		checkInTime  = LocalDateTime.parse(workDate+" 00:00:00", formatter);
		System.out.println("setWorkDate 실행 : "+ workDate);
	}

    public String getCheckInHourMinute() {
       return checkInTime != null ? checkInTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }

    public String getCheckOutHourMinute() {
       return checkOutTime != null ? checkOutTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    
    public String getNormalWorkStatus() {
        return Boolean.TRUE.equals(normalWork) ? "정상근무" : "비정상근무";
    }
}
