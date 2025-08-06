package com.kdt.KDT_PJT.attend.model;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveDTO {

	int leave_id ; 
    Date leave_date;                 
    //leave_type 
    //leave_hours 
    String reason, leave_type, employeeId, empNm;
    int total, use;
    
    LocalDateTime created_at, updated_at;

	public LeaveDTO(String employeeId, Date leave_date) {
		super();
		this.employeeId = employeeId;
		this.leave_date = leave_date;
	}
	

	//잔여 연차
	public int getRest() {
		return total - use;
	}
    
    
}
