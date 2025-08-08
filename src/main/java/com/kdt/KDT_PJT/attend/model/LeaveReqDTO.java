package com.kdt.KDT_PJT.attend.model;

import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class LeaveReqDTO {
	
	List<LeaveDTO> arr;
	
	String reason;
	
	public void dataCalc() {
		
		Iterator<LeaveDTO> it = arr.iterator();
		
		while(it.hasNext()) {
			LeaveDTO dto = it.next();
			
			if(!dto.useChk) {  //사용하지 않는다면 제거 -- 날짜 null 로 체크하여 삭제 가능
				it.remove();
			}else {
				// 사용할때 연차사유 넣기
				dto.setUsedReason(reason);
			}
		}
		
	}
}
