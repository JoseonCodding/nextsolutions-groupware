package com.kdt.KDT_PJT.sample.svc;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;


@Service
public class CalendarService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    
    @Autowired
    private CmmnDao cmmnDao;
    


    public List<CmmnMap> getAllEvents() {
        CmmnMap params = new CmmnMap();
        List<CmmnMap> events = cmmnDao.selectList("Calendar.getAllEvents", params);
        if (events != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (CmmnMap event : events) {
                // 임시 변수에 값 저장
                Object evtSn = event.get("EVT_SN");
                Object calendarId = event.get("CALENDAR_ID");
                Object evtTitle = event.get("EVT_TITLE");
                Object evtCn = event.get("EVT_CN");
                Object evtCategory = event.get("EVT_CATEGORY");
                Object evtLocation = event.get("EVT_LOCATION");
                Object evtStcd = event.get("EVT_STCD");
                Object isAllDayObj = event.get("IS_ALL_DAY");
                Object isPrivateObj = event.get("IS_PRIVATE");
                String[] attendees = new String[0];
                String attendeesStr = event.getString("EVT_ATND_LST");
                if (attendeesStr != null && !attendeesStr.isEmpty()) {
                    attendees = attendeesStr.split(",");
                }
                String start = null, end = null;
                if (event.get("EVT_BGNG_DT") != null) {
                    if (event.get("EVT_BGNG_DT") instanceof java.sql.Timestamp) {
                        start = sdf.format(event.get("EVT_BGNG_DT"));
                    } else {
                        start = event.get("EVT_BGNG_DT").toString();
                    }
                }
                if (event.get("EVT_END_DT") != null) {
                    if (event.get("EVT_END_DT") instanceof java.sql.Timestamp) {
                        end = sdf.format(event.get("EVT_END_DT"));
                    } else {
                        end = event.get("EVT_END_DT").toString();
                    }
                }
                boolean isAllDay = "Y".equals(isAllDayObj);
                boolean isPrivate = "Y".equals(isPrivateObj);
                // 기존 필드 제거 후 프론트에서 기대하는 필드만 추가
                event.clear();
                event.put("id", evtSn);
                event.put("calendarId", calendarId);
                event.put("title", evtTitle);
                event.put("body", evtCn);
                event.put("start", start);
                event.put("end", end);
                event.put("isAllDay", isAllDay);
                event.put("category", evtCategory);
                event.put("location", evtLocation);
                event.put("attendees", attendees);
                event.put("state", evtStcd);
                event.put("isPrivate", isPrivate);
            }
        }
        return events;
    }

    public CmmnMap createEvent(CmmnMap params) {


        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        params.put("EVT_SN", UUID.randomUUID().toString());
        params.put("PIC_MBL_TELNO", "");
        params.put("WRTER_NM", "");
        
        params.put("CALENDAR_ID", params.getString("calendarId", "default"));
        params.put("EVT_TITLE", params.getString("title", "Untitled Event"));
        params.put("EVT_CATEGORY", params.getString("category", "time"));
        params.put("EVT_STCD", params.getString("state", "busy"));
        // location이 null이면 빈 문자열로 설정
        params.put("EVT_LOCATION", params.getString("location", ""));
        params.put("IS_ALL_DAY", params.getBoolean("isAllday", false) ? "Y" : "N");
        params.put("IS_PRIVATE", params.getBoolean("isPrivate", false) ? "Y" : "N");

        try {
            if (params.get("start") != null) {
                Date startDate = sdfInput.parse(params.getString("start"));
                params.put("EVT_BGNG_DT", new java.sql.Timestamp(startDate.getTime()));
            }
            if (params.get("end") != null) {
                Date endDate = sdfInput.parse(params.getString("end"));
                params.put("EVT_END_DT", new java.sql.Timestamp(endDate.getTime()));
            }
        } catch (Exception e) {
            logger.error("Error parsing date", e);
            Date now = new Date();
            params.put("EVT_BGNG_DT", new java.sql.Timestamp(now.getTime()));
            params.put("EVT_END_DT", new java.sql.Timestamp(now.getTime() + 3600000)); // 1시간 후
        }

        logger.info("Creating event with params: {}", params);
        cmmnDao.insert("Calendar.createEvent", params);
        return params;
    }

    public CmmnMap updateEvent(CmmnMap params) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if (params.get("EVT_BGNG_DT") != null) {
                Date startDate = sdf.parse(params.getString("EVT_BGNG_DT"));
                params.put("EVT_BGNG_DT", new java.sql.Timestamp(startDate.getTime()));
            }
            if (params.get("EVT_END_DT") != null) {
                Date endDate = sdf.parse(params.getString("EVT_END_DT"));
                params.put("EVT_END_DT", new java.sql.Timestamp(endDate.getTime()));
            }
        } catch (Exception e) {
            logger.error("Error parsing date for update", e);
        }

        params.put("IS_ALL_DAY", params.getBoolean("IS_ALL_DAY", false) ? "Y" : "N");
        params.put("IS_PRIVATE", params.getBoolean("IS_PRIVATE", false) ? "Y" : "N");
        
        // location 처리
        String location = params.getString("EVT_LOCATION");
        if (location == null) {
            location = params.getString("location", "");
        }
        params.put("EVT_LOCATION", location);
        
        // attendees 처리
        String attendees = params.getString("attendees");
        if (attendees != null) {
            params.put("EVT_ATND_LST", attendees);
        }

        logger.info("Updating event with params: {}", params);
        cmmnDao.update("Calendar.updateEvent", params);
        return params;
    }

    public void deleteEvent(CmmnMap params) {
        logger.info("Deleting event with params: {}", params);
        cmmnDao.delete("Calendar.deleteEvent", params);
    }
}