package com.kdt.KDT_PJT.documentMng.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DocumentMngDTO {
    private Long versionId;        // PK 등 추가 필드가 있다면 포함
    private Long originalId;
    private String pjtNm;
    private String employeeId;   
    private String employeeName;   
    private LocalDate pjtBgngDt;
    private LocalDate pjtEndDt;
    private String pjtSttsCd;
    private String content;
    private String atchFileSn;     // ATCH_FILE_SN (UUID 파일명)
    private String orgFileNm;      // ORG_FILE_NM (원본 파일명)
    private String versionName;
    private LocalDateTime versionCreatedAt;
}
