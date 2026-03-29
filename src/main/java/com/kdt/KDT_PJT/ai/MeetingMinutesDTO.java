package com.kdt.KDT_PJT.ai;

import lombok.Data;

@Data
public class MeetingMinutesDTO {
    private int    minutesSn;
    private int    companyId;
    private String employeeId;
    private String meetDate;
    private String participants;
    private String agenda;
    private String rawNotes;
    private String aiSummary;
    private String aiDecisions;   // JSON 배열 문자열
    private String aiMinutes;
    private String createdAt;
}
