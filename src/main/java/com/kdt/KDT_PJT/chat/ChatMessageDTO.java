package com.kdt.KDT_PJT.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageDTO {

    private Long messageId;
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
    private Integer companyId;
    private String messageType; // text / image / file
    private String fileUrl;
    private String fileName;
    private Boolean isDeleted;
    private Boolean isEdited;
    private String eventType;  // message / delete / edit (WS 이벤트 구분용)

    public String getSentAtStr() {
        if (sentAt == null) return "";
        return sentAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
