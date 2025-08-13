package com.kdt.KDT_PJT.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface LoginMapper {
	EmployeeDto getUserByIdAndPassword(CmmnMap params);
}
