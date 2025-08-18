package com.kdt.KDT_PJT.approval.model;

import lombok.Data;

@Data
public class ApproverDTO {
    String employeeId;
    String name;      // emp_nm
    String position;
    String role;      // '근태','게시판','프로젝트','대표' 등
    Integer signOrder; // 1,2,3... (표시 순서용)
}
