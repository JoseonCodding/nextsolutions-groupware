package com.kdt.KDT_PJT.payroll.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SalarySettingDTO {

    private Integer salaryId;
    private String  employeeId;
    private String  empNm;
    private String  deptName;
    private String  position;
    private Integer companyId;
    private long    baseSalary;
    private long    mealAllowance;
    private long    transportAllowance;
    private String  effectiveFrom;   // 'yyyy-MM-dd'
}
