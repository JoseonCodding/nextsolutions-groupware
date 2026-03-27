package com.kdt.KDT_PJT.attend.di;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.CommuteMapper;

import jakarta.annotation.Resource;

@Service
public class Leave {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private CommuteMapper commuteMapper;

    public void autoGiveLeaveForQualifiedEmployees() {

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        AttendDTO param = new AttendDTO();

        Date startDay = new Date(today.getYear(), today.getMonth() - 1, 1);
        Date endDay   = new Date(today.getYear(), today.getMonth(), 0);

        param.setStartDay(sdf.format(startDay));
        param.setEndDay(sdf.format(endDay));
        param.setMonth(startDay.getMonth() + 1);
        YearMonth yearMonth = YearMonth.of(startDay.getYear() + 1900, startDay.getMonth() + 1);

        // 해당 월 회사 휴무일 수 조회
        int totalOffDays = commuteMapper.getHolidays(param);

        int workDays = 0;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                workDays++;
            }
        }

        int normalWorkDays = workDays - totalOffDays;

        List<AttendDTO> allEmployees = commuteMapper.getLastMonthTotalWorkDays(param);

        for (AttendDTO dto : allEmployees) {
            int workedDays = dto.getWorkCnt();
            double workRate = (double) workedDays / normalWorkDays;
            dto.setReason(param.getMonth() + "월 근무율 80% 이상 자동 부여");

            if (workRate >= 0.8) {
                commuteMapper.insertAutoLeave(dto);
                log.info("연차 부여 완료: {}", dto.getEmployeeId());
            }
        }
    }
}
