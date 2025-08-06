package com.kdt.KDT_PJT.sample.ctl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.sample.svc.CalendarService;

@RequestMapping("/schedule")
@Controller
public class CalendarController {

    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

    @Autowired
    private CalendarService calendarService;

    /** 일정 메인 HTML */
    @RequestMapping("/htmlcalendar")
    public String showCalendarHtml() {
        // /views/schedule.html
        return "schedule";
    }

    /** 일정 투두리스트 페이지 */
    @RequestMapping("/calendar")
    public String showCalendar() {
        // /views/schedule/schedule_todoList.html
        return "schedule/schedule_todoList";
    }

    /** 모든 이벤트 조회 */
    @RequestMapping("/events")
    @ResponseBody
    public CmmnMap getAllEvents() {
        CmmnMap result = new CmmnMap();
        List<CmmnMap> events = calendarService.getAllEvents();
        result.put("events", events);
        logger.info("Returning {} events", events.size());
        for (CmmnMap event : events) {
            logger.info("Event: {}", event);
        }
        return result;
    }

    /** 이벤트 생성 */
    @RequestMapping("/event")
    @ResponseBody
    public CmmnMap createEvent(@RequestBody CmmnMap params) {
        return calendarService.createEvent(params);
    }

    /** 이벤트 수정 */
    @RequestMapping("/event/update")
    @ResponseBody
    public CmmnMap updateEvent(@RequestBody CmmnMap params) {
        return calendarService.updateEvent(params);
    }

    /** 이벤트 삭제 */
    @RequestMapping("/event/delete")
    @ResponseBody
    public CmmnMap deleteEvent(@RequestBody CmmnMap params) {
        calendarService.deleteEvent(params);
        return new CmmnMap().put("status", "OK");
    }
}
