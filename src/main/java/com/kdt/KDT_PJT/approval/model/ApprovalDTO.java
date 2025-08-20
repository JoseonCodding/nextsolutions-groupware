package com.kdt.KDT_PJT.approval.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ApprovalDTO {

	String docId, docType, title, content, deptName, status, writer, empNm;
	Date createdAt, pjtBgngDt, pjtEndDt;
	
	// 프로젝트 파일 첨부 전용 추가 필드
	String attachFileUuid1;      // 1번 UUID (기존)
	String attachFileOrgName1;   // 1번 원본명 (기존)

	String attachFileUuid2;     // 2번 UUID
	String attachFileOrgName2;  // 2번 원본명

	String attachFileUuid3;     // 3번 UUID
	String attachFileOrgName3;  // 3번 원본명
    
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
    List<ApproverDTO> approvers; // 결재자 목록 추가
    String approvedBy; 			// 최종 결재자 employeeId (프로젝트/공지/연차/근태 공통)
    String approvedByName;     // 결재자 이름(emp_nm)
    String approvedByPosition; // 결재자 직책(position)

    
}
