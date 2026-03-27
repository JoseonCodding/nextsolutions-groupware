package com.kdt.KDT_PJT.chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatController {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${app.upload-dir}")
    private String uploadDir;

    // 채팅 목록 화면
    @GetMapping("/chat")
    public String chatList(HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        Integer companyId = me.getCompanyId();
        String myId = me.getEmployeeId();

        // 사원 목록 (본인 제외)
        List<CmmnMap> employees = chatMapper.selectEmployeeListForChat(myId, companyId);

        // 각 사원별 1:1 룸 정보 (마지막 메시지 + 안 읽은 수)
        List<Map<String, Object>> dmList = new ArrayList<>();
        for (CmmnMap emp : employees) {
            String targetId = (String) emp.get("employeeId");
            String roomId = buildDmRoomId(myId, targetId);
            ChatMessageDTO last = chatMapper.selectLastMessage(roomId, companyId);
            int unread = chatMapper.selectUnreadCount(roomId, myId, companyId);
            emp.put("roomId", roomId);
            emp.put("lastMsg", last != null ? last.getContent() : "");
            emp.put("lastTime", last != null ? last.getSentAtStr() : "");
            emp.put("unread", unread);
        }

        // 전체 채팅 안 읽은 수
        int globalUnread = chatMapper.selectUnreadCount("global", myId, companyId);

        model.addAttribute("employees", employees);
        model.addAttribute("globalUnread", globalUnread);
        model.addAttribute("mainUrl", "chat/chat_list");
        return "navTap";
    }

    // 채팅방 화면 (전체/1:1 공통)
    @GetMapping("/chat/room")
    public String chatRoom(
            @RequestParam(defaultValue = "global") String roomId,
            HttpSession session, Model model) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        Integer companyId = me.getCompanyId();

        // roomId 검증: global 또는 dm_{id}_{id} 형식만 허용
        if (!roomId.equals("global") && !roomId.startsWith("dm_")) {
            return "redirect:/chat";
        }

        List<ChatMessageDTO> history = chatMapper.selectRecentMessages(roomId, companyId, 50);
        Collections.reverse(history);

        // 읽음 처리
        chatMapper.upsertLastRead(roomId, me.getEmployeeId());

        // 채팅방 제목
        String roomTitle = "전체 채팅";
        if (roomId.startsWith("dm_")) {
            // dm_id1_id2 에서 상대방 id 추출
            String[] parts = roomId.replace("dm_", "").split("_", 2);
            String otherId = parts[0].equals(me.getEmployeeId()) ? parts[1] : parts[0];
            List<CmmnMap> all = chatMapper.selectEmployeeListForChat("__nobody__", companyId);
            for (CmmnMap emp : all) {
                if (otherId.equals(emp.get("employeeId"))) {
                    roomTitle = emp.get("emp_nm") + "님과의 채팅";
                    break;
                }
            }
        }

        // 1:1 방이면 상대방의 마지막 읽음 시각 조회 (읽음 표시용)
        java.time.LocalDateTime otherLastRead = null;
        if (roomId.startsWith("dm_")) {
            String[] parts = roomId.replace("dm_", "").split("_", 2);
            String otherId = parts[0].equals(me.getEmployeeId()) ? parts[1] : parts[0];
            otherLastRead = chatMapper.selectLastReadAt(roomId, otherId);
        }

        model.addAttribute("chatHistory", history);
        model.addAttribute("loginUser", me);
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomTitle", roomTitle);
        model.addAttribute("otherLastRead", otherLastRead);
        model.addAttribute("mainUrl", "chat/chat_room");
        return "navTap";
    }

    // WebSocket 메시지 수신 → 저장 → 브로드캐스트
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs == null) return;
        EmployeeDto loginUser = (EmployeeDto) attrs.get("loginUser");
        if (loginUser == null) return;

        // roomId 검증
        String roomId = msg.getRoomId();
        if (roomId == null || (!roomId.equals("global") && !roomId.startsWith("dm_"))) return;

        msg.setSenderId(loginUser.getEmployeeId());
        msg.setSenderName(loginUser.getEmpNm());
        msg.setCompanyId(loginUser.getCompanyId());

        chatMapper.insertMessage(msg);

        messagingTemplate.convertAndSend("/topic/chat." + roomId, msg);
    }

    // 읽음 처리 (AJAX)
    @PostMapping("/chat/markRead")
    @ResponseBody
    public Map<String, Object> markRead(@RequestParam String roomId, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        chatMapper.upsertLastRead(roomId, me.getEmployeeId());

        // 1:1 방이면 읽음 이벤트 브로드캐스트 (상대방 화면 실시간 반영)
        if (roomId.startsWith("dm_")) {
            ChatMessageDTO event = new ChatMessageDTO();
            event.setEventType("read");
            event.setRoomId(roomId);
            event.setSenderId(me.getEmployeeId());
            messagingTemplate.convertAndSend("/topic/chat." + roomId, event);
        }
        return Map.of("success", true);
    }

    // 메시지 삭제 (WebSocket)
    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload ChatMessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs == null) return;
        EmployeeDto loginUser = (EmployeeDto) attrs.get("loginUser");
        if (loginUser == null) return;

        String roomId = msg.getRoomId();
        if (roomId == null || (!roomId.equals("global") && !roomId.startsWith("dm_"))) return;

        int updated = chatMapper.softDeleteMessage(msg.getMessageId(), loginUser.getEmployeeId());
        if (updated == 0) return;

        ChatMessageDTO event = new ChatMessageDTO();
        event.setMessageId(msg.getMessageId());
        event.setRoomId(roomId);
        event.setEventType("delete");
        messagingTemplate.convertAndSend("/topic/chat." + roomId, event);
    }

    // 메시지 수정 (WebSocket)
    @MessageMapping("/chat.edit")
    public void editMessage(@Payload ChatMessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs == null) return;
        EmployeeDto loginUser = (EmployeeDto) attrs.get("loginUser");
        if (loginUser == null) return;

        String roomId = msg.getRoomId();
        if (roomId == null || (!roomId.equals("global") && !roomId.startsWith("dm_"))) return;

        String content = msg.getContent();
        if (content == null || content.isBlank()) return;
        // XSS 방어
        content = org.jsoup.Jsoup.clean(content, org.jsoup.safety.Safelist.none());

        int updated = chatMapper.editMessage(msg.getMessageId(), loginUser.getEmployeeId(), content);
        if (updated == 0) return;

        ChatMessageDTO event = new ChatMessageDTO();
        event.setMessageId(msg.getMessageId());
        event.setRoomId(roomId);
        event.setContent(content);
        event.setEventType("edit");
        messagingTemplate.convertAndSend("/topic/chat." + roomId, event);
    }

    // 파일 업로드 (AJAX)
    @PostMapping("/chat/upload")
    @ResponseBody
    public Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "unauthorized");

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) return Map.of("error", "invalid");

        // 허용 확장자
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase()
                : "";
        java.util.Set<String> allowed = java.util.Set.of(
                ".jpg",".jpeg",".png",".gif",".webp",
                ".pdf",".doc",".docx",".xls",".xlsx",".ppt",".pptx",".txt",".zip");
        if (!allowed.contains(ext)) return Map.of("error", "notAllowed");

        // 저장
        String uuid = UUID.randomUUID().toString().replace("-", "") + ext;
        java.nio.file.Path dir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
        java.nio.file.Files.createDirectories(dir);
        file.transferTo(dir.resolve(uuid).toFile());

        String type = (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png")
                    || ext.equals(".gif") || ext.equals(".webp")) ? "image" : "file";

        return Map.of("fileUrl", uuid, "fileName", originalName, "messageType", type);
    }

    // 안 읽은 수 조회 (AJAX — 채팅 목록 배지 갱신용)
    @GetMapping("/chat/unread")
    @ResponseBody
    public Map<String, Object> unreadCount(@RequestParam String roomId, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        int count = chatMapper.selectUnreadCount(roomId, me.getEmployeeId(), me.getCompanyId());
        return Map.of("count", count);
    }

    // roomId 생성 (사번 정렬하여 항상 동일한 키 생성)
    private String buildDmRoomId(String id1, String id2) {
        return id1.compareTo(id2) < 0
                ? "dm_" + id1 + "_" + id2
                : "dm_" + id2 + "_" + id1;
    }
}
