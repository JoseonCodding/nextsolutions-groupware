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
    
    
    
    @RequestMapping("/htmlcalendar")
    public String showCalendarHtml() {
        return "schedule";
    }
    

    @RequestMapping("/calendar")
    public String showCalendar() {
        return "schedule/schedule_todoList";
    }
    

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

    
    
    @RequestMapping("/event")
    @ResponseBody
    public CmmnMap createEvent(@RequestBody CmmnMap params) {

        return calendarService.createEvent(params);
    }
    
    
    
    @RequestMapping("/event/update")
    @ResponseBody
    public CmmnMap updateEvent(@RequestBody CmmnMap params) {
        return calendarService.updateEvent(params);
    }
    
    @RequestMapping("/event/delete")
    @ResponseBody
    public CmmnMap deleteEvent(@RequestBody CmmnMap params) {
        calendarService.deleteEvent(params);
        return new CmmnMap().put("status", "OK");
    }
}