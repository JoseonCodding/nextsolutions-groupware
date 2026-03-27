package com.kdt.KDT_PJT.chat;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Mapper
public interface ChatMapper {

    void insertMessage(ChatMessageDTO msg);

    List<ChatMessageDTO> selectRecentMessages(
            @Param("roomId") String roomId,
            @Param("companyId") Integer companyId,
            @Param("limit") int limit);

    // 채팅 목록용: 특정 룸의 마지막 메시지
    ChatMessageDTO selectLastMessage(
            @Param("roomId") String roomId,
            @Param("companyId") Integer companyId);

    // 안 읽은 메시지 수
    int selectUnreadCount(
            @Param("roomId") String roomId,
            @Param("employeeId") String employeeId,
            @Param("companyId") Integer companyId);

    // 메시지 삭제 (본인만)
    int softDeleteMessage(
            @Param("messageId") Long messageId,
            @Param("senderId") String senderId);

    // 메시지 수정 (본인만)
    int editMessage(
            @Param("messageId") Long messageId,
            @Param("senderId") String senderId,
            @Param("content") String content);

    // 마지막 읽음 시각 업데이트
    void upsertLastRead(
            @Param("roomId") String roomId,
            @Param("employeeId") String employeeId);

    // 특정 사용자의 마지막 읽음 시각 조회
    java.time.LocalDateTime selectLastReadAt(
            @Param("roomId") String roomId,
            @Param("employeeId") String employeeId);

    // 채팅 가능한 사원 목록 (본인 제외)
    List<CmmnMap> selectEmployeeListForChat(
            @Param("myId") String myId,
            @Param("companyId") Integer companyId);
}
