package com.kdt.KDT_PJT.approval.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApprovalDocDTO {
    private Integer docId;
    private Integer companyId;
    private Integer templateId;
    private String templateTitle;
    private String title;
    private String content;
    private String writerId;
    private String writerName;
    private String approverId;
    private String approverName;
    private String status;
    private String rejectReason;
    private Date firstSign;
    private Date createdAt;
    private Integer isDeleted;

    public String getCreatedAtStr() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(createdAt);
    }

    public String getFirstSignStr() {
        if (firstSign == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(firstSign);
    }
}
