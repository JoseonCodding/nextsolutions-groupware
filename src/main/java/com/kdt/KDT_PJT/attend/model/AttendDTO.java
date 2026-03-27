package com.kdt.KDT_PJT.attend.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AttendDTO {

	private Long id;
	private Integer companyId;
	private String employeeId, service, empNm, mbNm, deptName, position, mbPosition, workingHours;
	private LocalDateTime checkInTime, checkOutTime;
	int workCnt;

	private String status;

	private String modifiedBy;
	private LocalDateTime modifiedAt;
	private String modificationReason;
	String startDay, endDay;

    // 페이징
    int limit = 0;
    int offset = 0;
    int pageNum = 1;
    int pageSize = 10;

    String keyword;

    // AttendDTO2 흡수 필드
    private String reason;
    private int month;
    private String startDayStr;
    private String yearMonth;
    boolean nowIsHoliday = false;
    boolean todayCheckIn = false;
    boolean todayCheckOut = false;
    Date usedDate;
    String title;
    

    public String getUsedDateStr() {
        if (usedDate == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(usedDate);
    }

    public String getWorkDate() {
        // checkInTime이 있을 때만 yyyy-MM-dd 반환
        return (checkInTime != null) ? checkInTime.toLocalDate().toString() : null;
    }
	
	public String getWorkYear() {
	    return checkInTime != null ? String.valueOf(checkInTime.toLocalDate().getYear()) : "";
	}

	public String getWorkMonth() {
	    return checkInTime != null ? String.format("%02d", checkInTime.toLocalDate().getMonthValue()) : "";
	}
	
	public void setWorkDate(String workDate) {
	    if (workDate == null || workDate.isBlank()) {
	        // 검색조건 미지정: checkInTime을 건드리지 않거나 null로 초기화
	        this.checkInTime = null;
	        return;
	    }
	    // "yyyy-MM-dd"만 받아서 자정으로 세팅
	    this.checkInTime = LocalDate.parse(workDate).atStartOfDay();
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
