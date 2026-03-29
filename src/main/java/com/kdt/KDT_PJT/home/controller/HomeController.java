package com.kdt.KDT_PJT.home.controller;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.service.BoardService;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.notification.NotificationDTO;
import com.kdt.KDT_PJT.notification.NotificationMapper;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;
import com.kdt.KDT_PJT.pjt_mng.svc.ProjectMngService;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private ProjectMngService projectService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private AttendMapper attendMapper;

    @Autowired
    private PjtMngMapper pjtMngMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @RequestMapping("/")
    String home() {
        return "login/loginForm";
    }

    @RequestMapping("/dashboard")
    String dashboard(HttpSession session, Model model) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Integer companyId = loginUser.getCompanyId();
        String employeeId = loginUser.getEmployeeId();

        // 공지사항 최근 5개
        List<BoardDTO> notices = new ArrayList<>();
        try {
            Integer boardId = boardService.findBoardIdByType("notice", companyId);
            if (boardId != null) {
                BoardDTO boardDto = new BoardDTO();
                boardDto.setBoardId(boardId);
                boardDto.setCompanyId(companyId);
                boardDto.setPage(1);
                boardDto.setSize(5);
                notices = boardService.getNoticePosts(boardDto);
            }
        } catch (Exception ignored) {}
        model.addAttribute("notices", notices);

        // 프로젝트 현황
        try {
            model.addAttribute("pjtTotal",    projectService.getTotalCount());
            model.addAttribute("pjtProgress", projectService.getProgressCount());
            model.addAttribute("pjtPending",  projectService.getPendingCount());
            model.addAttribute("pjtComplete", projectService.getCompleteCount());
            model.addAttribute("myPjtCount",  projectService.getMyProjectCount(employeeId));
            model.addAttribute("myPending",   projectService.countMyPendingApprovals(employeeId));
        } catch (Exception ignored) {}

        // 이번달 일정
        List<ScheduleDTO> schedules = new ArrayList<>();
        try {
            ScheduleDTO schDto = new ScheduleDTO();
            schDto.setEmployeeId(employeeId);
            schDto.setCompanyId(companyId);
            schedules = scheduleService.getScheduleList(schDto);
        } catch (Exception ignored) {}
        model.addAttribute("schedules", schedules);

        // 미결재 전자결재 수 (내가 결재자인 것)
        try {
            int pendingApprovals = approvalMapper.countPendingForApprover(employeeId, companyId);
            model.addAttribute("pendingApprovals", pendingApprovals);
        } catch (Exception ignored) {}

        // 오늘 출근 현황
        try {
            AttendDTO todayAttend = attendMapper.findTodayAttendance(employeeId);
            model.addAttribute("todayAttend", todayAttend);
        } catch (Exception ignored) {}

        // 내 미완료 태스크
        try {
            List<CmmnMap> myTasks = pjtMngMapper.selectMyIncompleteTasks(employeeId);
            model.addAttribute("myTasks", myTasks);
        } catch (Exception ignored) {}

        // 최근 알림
        try {
            List<NotificationDTO> notifications = notificationMapper.getRecent(employeeId);
            int unreadCount = notificationMapper.countUnread(employeeId);
            model.addAttribute("notifications", notifications);
            model.addAttribute("unreadCount", unreadCount);
        } catch (Exception ignored) {}

        // 온보딩 wizard (신규 회사 가입 후 첫 진입)
        if (Boolean.TRUE.equals(session.getAttribute("showOnboarding"))) {
            model.addAttribute("onboarding", true);
            session.removeAttribute("showOnboarding");
        }

        model.addAttribute("mainUrl", "home/dashboard");
        return "navTap";
    }
}
