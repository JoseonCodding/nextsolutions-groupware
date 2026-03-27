package com.kdt.KDT_PJT.attend.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveDTO {

	Integer leaveId, holidayCount, companyId;
	
	boolean useChk;
    
    String createReason,usedReason, leaveType, employeeId, empNm,deptName,position;
    Integer total, used;
    
    Date createDate , usedDate, approvalDate;
    
    String stateType; 
    
	
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
			// 파싱 실패 시 usedDate는 null 유지
		}
	}
	
}
