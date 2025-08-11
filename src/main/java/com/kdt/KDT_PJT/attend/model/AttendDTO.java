package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDate;
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


	private String stateType;

	private String modifiedBy;               // 수정자 ID
	private LocalDateTime modifiedAt;        // 수정일시
	private String modificationReason;      // 수정 사유
	String startDay, endDay;	

    // ✅ 페이징 파라미터 추가
    int limit = 0;
    int offset = 0;
	
//	work_hours DECIMAL(4,1) NULL,           -- 근무시간 (예: 8.0)
//	ADD COLUMN is_normal_work BOOLEAN DEFAULT FALSE,   -- 정상근무 여부
//	ADD COLUMN modified_by VARCHAR(20) NULL,           -- 수정자 ID
//	ADD COLUMN modified_at DATETIME NULL,              -- 수정일
//	ADD COLUMN modification_reason VARCHAR(255) NULL;

	 
	public String getWorkDate() {
       return checkInTime != null ? checkInTime.toLocalDate().toString() : "";
	}
	
	public String getWorkYear() {
	    return checkInTime != null ? String.valueOf(checkInTime.toLocalDate().getYear()) : "";
	}

	public String getWorkMonth() {
	    return checkInTime != null ? String.format("%02d", checkInTime.toLocalDate().getMonthValue()) : "";
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
    
    
    // 기본 조회 시작일과 종료일을 LocalDate로 반환
    public LocalDate getStartLocalDate() {
        return startDay != null ? LocalDate.parse(startDay) : null;
    }

    public LocalDate getEndLocalDate() {
        return endDay != null ? LocalDate.parse(endDay) : null;
    }

    // 이전달 시작일 (기준 startDay 기준)
    public String getPrevMonthStartDay() {
        LocalDate base = getStartLocalDate();
        if (base == null) return null;
        return base.minusMonths(1).withDayOfMonth(1).toString();
    }

    // 이전달 종료일
    public String getPrevMonthEndDay() {
        LocalDate base = getStartLocalDate();
        if (base == null) return null;
        LocalDate prevMonth = base.minusMonths(1);
        return prevMonth.withDayOfMonth(prevMonth.lengthOfMonth()).toString();
    }

    // 다음달 시작일
    public String getNextMonthStartDay() {
        LocalDate base = getStartLocalDate();
        if (base == null) return null;
        return base.plusMonths(1).withDayOfMonth(1).toString();
    }

    // 다음달 종료일
    public String getNextMonthEndDay() {
        LocalDate base = getStartLocalDate();
        if (base == null) return null;
        LocalDate nextMonth = base.plusMonths(1);
        return nextMonth.withDayOfMonth(nextMonth.lengthOfMonth()).toString();
    }

    
    
}
