package com.kdt.KDT_PJT.attend.di;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.attend.model.CommuteMapper;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;

import jakarta.annotation.Resource;

@Service
public class Leave {

    @Resource
    private CommuteMapper commuteMapper;

    @Resource
    private LeaveMapper leaveMapper;

    public void autoGiveLeaveForQualifiedEmployees() {
        List<String> allEmployees = commuteMapper.getAllEmployeeIds();

        for (String employeeId : allEmployees) {
            int totalDays = commuteMapper.getLastMonthTotalWorkDays(employeeId);
            int normalDays = commuteMapper.getLastMonthNormalWorkDays(employeeId);

            if (totalDays == 0) {
                continue; // 근무일수 0이면 스킵
            }

            double ratio = (double) normalDays / totalDays;

            if (ratio >= 0.8) {
                leaveMapper.insertAutoLeave(employeeId);
                System.out.println("[자동 부여 완료] employeeId: " + employeeId +
                                   ", 근무율: " + String.format("%.2f", ratio * 100) + "%");
            }
        }
    }
}
