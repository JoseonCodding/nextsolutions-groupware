package com.kdt.KDT_PJT.notification;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationMapper notificationMapper;

    @GetMapping("/unread")
    public Map<String, Object> unreadCount(HttpSession session) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        int count = (user != null) ? notificationMapper.countUnread(user.getEmployeeId()) : 0;
        return Map.of("count", count);
    }

    @GetMapping("/list")
    public List<NotificationDTO> list(HttpSession session) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        if (user == null) return List.of();
        return notificationMapper.getRecent(user.getEmployeeId());
    }

    @PostMapping("/readAll")
    public void readAll(HttpSession session) {
        EmployeeDto user = (EmployeeDto) session.getAttribute("loginUser");
        if (user != null) notificationMapper.markAllRead(user.getEmployeeId());
    }
}
