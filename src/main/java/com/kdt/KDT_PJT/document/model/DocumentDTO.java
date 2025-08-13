package com.kdt.KDT_PJT.document.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DocumentDTO {
	private Long pjtSn;
	private String pjtNm;
	private String pjtSttsCd;    // '대기' | '완료' 등
	private String employeeId;
	private String useYn;        // 'Y' 최신, 'N' 과거
	private String frstRgtrId;
	private LocalDateTime frstRegDt;
	private String lastMdfrId;
	private LocalDateTime lastMdfcnDt;
	private LocalDate pjtBgngDt;
	private LocalDate pjtEndDt;
	private String tbPjtApr;
	private String tbPjtBasccol;
	private String docType;
	private String content;
	private Long atchFileSn1;
	private String orgFileNm1;
	private Long atchFileSn2;
	private Long atchFileSn3;
	private String orgFileNm2;
	private String orgFileNm3;
	private String tbPjtBasccol1;
	private String gid;          // 버전군 식별자
	private BigDecimal ver;      // ex) 1.0, 1.1
}
