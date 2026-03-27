package com.kdt.KDT_PJT.notification;

import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NotificationMapper {

    @Insert("INSERT INTO notification (company_id, receiver_id, sender_nm, type, message, ref_url) " +
            "VALUES (#{companyId}, #{receiverId}, #{senderNm}, #{type}, #{message}, #{refUrl})")
    void insert(@Param("companyId") int companyId,
                @Param("receiverId") String receiverId,
                @Param("senderNm") String senderNm,
                @Param("type") String type,
                @Param("message") String message,
                @Param("refUrl") String refUrl);

    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id = #{receiverId} AND is_read = 0")
    int countUnread(@Param("receiverId") String receiverId);

    @Select("SELECT * FROM notification WHERE receiver_id = #{receiverId} ORDER BY created_at DESC LIMIT 10")
    List<NotificationDTO> getRecent(@Param("receiverId") String receiverId);

    @Update("UPDATE notification SET is_read = 1 WHERE receiver_id = #{receiverId}")
    void markAllRead(@Param("receiverId") String receiverId);
}
