package com.kdt.KDT_PJT.employee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface EmployeeMapper {
    List<CmmnMap> getUserList(int pageNum, int pageSize, @Param("keyword") String keyword);
    void toggleActive(CmmnMap params);
    void insertEmployee(CmmnMap params);
    CmmnMap getEmployeeBySeq(int empSeq);
    EmployeeDto getEmployeeDetail(int empSeq);
    void updateEmployee(CmmnMap params);
    EmployeeDto getDetail(EmployeeDto dto);
    int update(EmployeeDto dto);    
}
