package com.kdt.KDT_PJT.notification;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long notifId;
    private int companyId;
    private String receiverId;
    private String senderNm;
    private String type;
    private String message;
    private String refUrl;
    private int isRead;
    private LocalDateTime createdAt;
}
