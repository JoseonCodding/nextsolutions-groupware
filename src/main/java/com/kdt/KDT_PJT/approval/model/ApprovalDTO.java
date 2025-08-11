package com.kdt.KDT_PJT.approval.model;

import java.util.Date;

import lombok.Data;

@Data
public class ApprovalDTO {

	String docId, docType, title, content, deptName, status, writer, empNm;
	Date createdAt;
	
	// 프로젝트 파일 첨부 전용 추가 필드
    String attachFileUuid;		// 파일 이름 난수화
    String attachFileOrgName;	// 파일 원래 이름 = 뷰어에 표시될 이름 = 다운로드 시 저장되는 이름
    
    // 연차 전용 추가 필드
    Date leaveCreateDate; // 연차 발생일(l.create_date)
    String leaveUsedReason; // 사용 이유(l.used_reason)
    Date leaveUsedDate; // 휴가 사용일(l.used_date)
	
}
