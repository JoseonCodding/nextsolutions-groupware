package com.kdt.KDT_PJT.schedule.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public List<ScheduleDTO> getScheduleList(ScheduleDTO dto) {
        List<ScheduleDTO> list = scheduleMapper.getScheduleListByMonth(dto);
        list.addAll(scheduleMapper.getProjectListByMonth(dto));
        return list;
    }

    public List<ScheduleDTO> getScheduleListForApi(ScheduleDTO dto) {
        List<ScheduleDTO> list = scheduleMapper.getScheduleListRepeatEmpty(dto);
        list.addAll(scheduleMapper.getProjectListByMonth(dto));
        return list;
    }

    public ScheduleDTO getDetail(ScheduleDTO dto) {
        return scheduleMapper.getScheduleDetail(dto);
    }

    @Transactional
    public void insert(ScheduleDTO dto) {
        scheduleMapper.insert(dto);
    }

    @Transactional
    public int modify(ScheduleDTO dto) {
        return scheduleMapper.modify(dto);
    }

    @Transactional
    public int delete(ScheduleDTO dto) {
        return scheduleMapper.delete(dto);
    }

    public List<ScheduleDTO> getActiveNotifications(ScheduleDTO dto) {
        return scheduleMapper.getActiveAllDayNotifications(dto);
    }

    @Transactional
    public void markNotificationSent(int scheduleId) {
        scheduleMapper.markNotificationAsSent(scheduleId);
    }
}
