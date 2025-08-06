package com.kdt.KDT_PJT.employee.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<CmmnMap> getUserList();
    void toggleActive(CmmnMap params);
    void insertEmployee(CmmnMap params);
}
