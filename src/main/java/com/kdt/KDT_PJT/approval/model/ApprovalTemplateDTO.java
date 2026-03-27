package com.kdt.KDT_PJT.approval.model;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApprovalTemplateDTO {
    private Integer templateId;
    private Integer companyId;
    private String title;
    private String content;
    private String createdBy;
    private Date createdAt;
}
