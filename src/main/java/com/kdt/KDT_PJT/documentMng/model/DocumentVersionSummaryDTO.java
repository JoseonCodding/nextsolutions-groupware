package com.kdt.KDT_PJT.documentMng.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DocumentVersionSummaryDTO {
    private Long versionId;         // TB_PJT_BASC_VERSION PK
    private Long originalId;        // 원본 프로젝트 ID
    private String versionName;     // 버전명 (v1.0, v1.1 ...)
    private LocalDateTime versionCreatedAt; // 생성일
}
