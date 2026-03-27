package com.kdt.KDT_PJT.config;

import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 컨트롤러 메서드 실행 전, 파라미터로 넘어온 DTO에 companyId를 자동으로 주입한다.
 * setCompanyId(Integer) 메서드가 있는 DTO라면 무조건 적용된다.
 * 덕분에 각 컨트롤러에서 dto.setCompanyId(loginUser.getCompanyId()) 를 반복할 필요 없다.
 */
@Aspect
@Component
public class CompanyContextAspect {

    @Before("within(com.kdt.KDT_PJT..ctl..*) || within(com.kdt.KDT_PJT..controller..*)")
    public void injectCompanyId(JoinPoint jp) {
        Integer companyId = CompanyContext.get();
        if (companyId == null) return;

        for (Object arg : jp.getArgs()) {
            if (arg == null) continue;
            try {
                Method setter = arg.getClass().getMethod("setCompanyId", Integer.class);
                setter.invoke(arg, companyId);
            } catch (NoSuchMethodException ignored) {
                // setCompanyId가 없는 파라미터는 무시
            } catch (Exception e) {
                // 주입 실패 시에도 요청은 계속 처리
            }
        }
    }
}
