package com.kdt.KDT_PJT.employee.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<CmmnMap> getUserList();

    void toggleActive(CmmnMap params);
    void insertEmployee(CmmnMap params);
    CmmnMap getEmployeeBySeq(int empSeq);
    void updateEmployee(CmmnMap params);
    EmployeeDto getDetail(EmployeeDto dto);
    int update(EmployeeDto dto);

}
