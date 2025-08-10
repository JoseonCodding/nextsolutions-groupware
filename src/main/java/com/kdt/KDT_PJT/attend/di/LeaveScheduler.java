package com.kdt.KDT_PJT.attend.di;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class LeaveScheduler {
	
	 @Resource
	    private Leave leaveService;

	    /**
	     * 매월 1일 새벽 3시에 자동 실행
	     */
	    //@Scheduled(cron = "0 0 3 1 * ?")
	    @Scheduled(cron = "0 */1 * * * ?") // 매 1분마다 실행
	    public void scheduleAutoLeave() {
	        System.out.println("[스케줄 시작] 전월 근무율 80% 이상 직원 자동 연차 부여 시작");
	        leaveService.autoGiveLeaveForQualifiedEmployees();
	        System.out.println("[스케줄 종료] 자동 연차 부여 완료");
	    }
}
