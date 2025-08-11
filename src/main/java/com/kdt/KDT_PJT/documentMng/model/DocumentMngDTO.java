package com.kdt.KDT_PJT.documentMng.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class DocumentMngDTO {
    private Long originalId;
    private String pjtNm;
    private String employeeId;   // 사번
    private String employeeName; // 사원명
    private String pjtSttsCd;
    private LocalDate pjtBgngDt;
    private LocalDate pjtEndDt;
    private String versionName;
}
