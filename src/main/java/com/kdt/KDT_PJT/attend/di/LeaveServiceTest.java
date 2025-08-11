package com.kdt.KDT_PJT.attend.di;

import org.springframework.beans.factory.annotation.Autowired;


public class LeaveServiceTest {

    @Autowired
    private Leave leaveService;

    
    public void testAutoGiveLeaveForQualifiedEmployees() {
    	System.out.println("연차 자동 부여");
        leaveService.autoGiveLeaveForQualifiedEmployees();
        // DB에서 INSERT 되었는지 SELECT로 검증
    }
}
