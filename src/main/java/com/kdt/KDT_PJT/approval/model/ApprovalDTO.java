package com.kdt.KDT_PJT.approval.model;

import java.util.Date;

import lombok.Data;

@Data
public class ApprovalDTO {

	String docId, docType, title, content, deptName, status, writer, empNm;
	Date createdAt, pjtBgngDt, pjtEndDt;
	
	// 프로젝트 파일 첨부 전용 추가 필드
    String attachFileUuid;		// 파일 이름 난수화
    String attachFileOrgName;	// 파일 원래 이름 = 뷰어에 표시될 이름 = 다운로드 시 저장되는 이름
    
    // 연차 전용 추가 필드
    Date leaveCreateDate; // 연차 발생일(l.create_date)
    String leaveUsedReason; // 사용 이유(l.used_reason)
    Date leaveUsedDate; // 휴가 사용일(l.used_date)
	
    // 근태 전용 추가 필드
    Date checkInTime;          // 출근 시간
    Date checkOutTime;         // 퇴근 시간
    String modifiedBy;         // 결재자 (중간결재자, 최종결재자)
    Date modifiedAt;           // 수정 일자
    String modificationReason; // 수정 사유
    String timeInout; 			// 수정 항목 (출근,퇴근,출퇴근)
    
    // 권한처리 중에 늘어난 추가필드
    String writerId;			// 상신자
    String approverName;		// 중간결재자
    String managerName;			// 최종결재자
    Date firstSign;    			// 1차 결재 날짜
    Date secondSign;   			// 2차 결재 날짜
    String writerPosition;		// 상신자 직책
    String approverPosition;	// 중간결재자 직책
    String managerPosition;		// 최종 결재자 직책
    
}
