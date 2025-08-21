package com.kdt.KDT_PJT.document.model;

import java.util.Date;

import lombok.Data;

@Data
public class DocumentDTO {
	  private Long pjtSn;                 // 프로젝트 번호(행 PK)
	  private String pjtNm;               // 프로젝트명
	  private String pjtSttsCd;           // '진행중' | '완료'
	  private String employeeId;          // 사번
	  private String empNm;               // 사원명 (테이블에 없으면 조인으로 매핑)
	  private Date frstRegDt;    		  // 등록일
	  private String content;             // 내용
	  // 첨부
	  private String atchFileSn1;
	  private String orgFileNm1;
	  private String atchFileSn2;
	  private String atchFileSn3;
	  private String orgFileNm2;
	  private String orgFileNm3;
	  //private String vers;

	  private String gid;                 // 버전군 식별자
	  private Integer ver;             // 1.0, 1.1 ...
}
