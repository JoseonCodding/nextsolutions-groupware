package com.kdt.KDT_PJT.ai;

import lombok.Data;

@Data
public class MeetingActionDTO {
    private int    actionSn;
    private int    minutesSn;
    private String task;
    private String ownerName;
    private String ownerEmployeeId;
    private String dueDate;
    private int    notified;
    private String createdAt;
}
