package com.kdt.KDT_PJT.attend.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveDTO {

	Integer leaveId ; 
	
	boolean useChk;
    
    //leave_hours 
    String createReason,usedReason, leaveType, employeeId, empNm;
    Integer total, used;
    
    Date createDate , usedDate, approvalDate;
    
    String stateType; 
//	approval_id int AUTO_INCREMENT PRIMARY KEY,
//  approval_type VARCHAR(50),  -- 기안 종류(연차, 근태, 프로젝트, 게시판)
//  employeeId INT NOT NULL,                      -- FK
//  approval_date DATE NOT NULL,  -- 상신일    
//  state_type ENUM('대기',대기중, '승인', 반려) NOT NULL,  -- 대기 or 승인
//  title VARCHAR(50),
//  content VARCHAR(500),

	
	public int getRest() {
		int t = (total == null) ? 0 : total;
	    int u = (used == null) ? 0 : used;
		return t - u;
	}
	
	public String getCreateDateStr() {
		if (createDate == null) {
	        return "";
	    }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(createDate);
	}
	
	public String getUsedDateStr() {
		if (usedDate == null) {
	        return "";
	    }
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(usedDate);
		
	}
	
	public void setUsedDateStr(String ttt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			usedDate = sdf.parse(ttt);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  
}
