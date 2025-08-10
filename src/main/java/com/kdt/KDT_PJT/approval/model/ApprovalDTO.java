package com.kdt.KDT_PJT.approval.model;

import java.util.Date;

import lombok.Data;

@Data
public class ApprovalDTO {

	String docId, docType, title, content, deptName, status, writer, empNm;
	Date createdAt;
	
}
