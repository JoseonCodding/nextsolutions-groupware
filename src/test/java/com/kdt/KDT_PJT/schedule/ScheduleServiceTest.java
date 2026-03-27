package com.kdt.KDT_PJT.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;
import com.kdt.KDT_PJT.schedule.service.ScheduleService;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    ScheduleMapper scheduleMapper;

    @InjectMocks
    ScheduleService scheduleService;

    @Test
    @DisplayName("getScheduleList → 월간 일정 + 프로젝트 목록 합산")
    void getScheduleList_mergesScheduleAndProject() {
        ScheduleDTO dto = new ScheduleDTO();

        ScheduleDTO s1 = new ScheduleDTO(); s1.setTitle("일정1");
        ScheduleDTO p1 = new ScheduleDTO(); p1.setTitle("프로젝트1");

        when(scheduleMapper.getScheduleListByMonth(dto)).thenReturn(new ArrayList<>(List.of(s1)));
        when(scheduleMapper.getProjectListByMonth(dto)).thenReturn(List.of(p1));

        List<ScheduleDTO> result = scheduleService.getScheduleList(dto);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ScheduleDTO::getTitle)
                          .containsExactlyInAnyOrder("일정1", "프로젝트1");
    }

    @Test
    @DisplayName("일정 삭제 → mapper 결과 반환")
    void delete_returnsMapperResult() {
        ScheduleDTO dto = new ScheduleDTO();
        when(scheduleMapper.delete(dto)).thenReturn(1);

        int result = scheduleService.delete(dto);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("알림 조회 → mapper 위임")
    void getActiveNotifications() {
        ScheduleDTO dto = new ScheduleDTO();
        when(scheduleMapper.getActiveAllDayNotifications(dto)).thenReturn(List.of());

        List<ScheduleDTO> result = scheduleService.getActiveNotifications(dto);

        assertThat(result).isNotNull();
        verify(scheduleMapper).getActiveAllDayNotifications(dto);
    }
}
