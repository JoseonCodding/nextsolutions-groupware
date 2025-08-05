package com.kdt.KDT_PJT.approval.model;

import java.util.Date;

import lombok.Data;

@Data
public class ApprovalDTO {

	String docId, docType, title, content, department, creatorId, status, writer;
	Date createdAt;
	
	@Override
	public String toString() {
		return "ApprovalDTO [docId=" + docId + ", docType=" + docType + ", title=" + title + ", content=" + content
				+ ", department=" + department + ", creatorId=" + creatorId + ", status=" + status + ", writer="
				+ writer + ", createdAt=" + createdAt + "]";
	}
	
}
