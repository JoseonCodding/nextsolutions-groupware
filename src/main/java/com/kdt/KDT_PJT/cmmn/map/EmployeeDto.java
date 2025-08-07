package com.kdt.KDT_PJT.cmmn.map;

import java.util.Date;

import lombok.Data;

@Data
public class EmployeeDto {
	int empSeq, active;
	String employeeId, password, empNm, phone, deptName, position, role;
	Date birth, hireDate, resignDate;

}
