package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveDTO {

	int leaveId ; 
    Date leave_date;                 
    //leave_hours 
    String reason, leaveType, employeeId, empNm;
    int total, used;
    
    LocalDateTime created_at, updated_at;

	public LeaveDTO(String employeeId, Date leave_date) {
		super();
		this.employeeId = employeeId;
		this.leave_date = leave_date;
	}
	

	//잔여 연차
	public int getRest() {
		return total - used;
	}
    
	// <TEST> 필규 작성 - 전자결재에서 데이터 끌어오기 위해서 필요
	String createReason, stateType, usedReason;
	Date createDate, usedDate;
}
