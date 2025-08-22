package com.kdt.KDT_PJT.employee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface EmployeeMapper {

    List<CmmnMap> getUserList();

   // List<CmmnMap> getUserList(int pageNum, int pageSize, @Param("keyword") String keyword);

    List<EmployeeDto> getUserList(@Param("offset") Integer offset
    							, @Param("size") Integer size
    							, @Param("keyword") String keyword);
    
    void toggleActive(CmmnMap params);
    void insertEmployee(CmmnMap params);
    CmmnMap getEmployeeBySeq(int empSeq);
    EmployeeDto getEmployeeDetail(int empSeq);
    void updateEmployee(CmmnMap params);
    
    EmployeeDto getDetail(EmployeeDto dto);
    
    EmployeeDto getIdChk(EmployeeDto dto);
    
    EmployeeDto getPhoneChk(EmployeeDto dto);
    
    // 1) 개수로 체크
    @Select("SELECT COUNT(*) FROM employee WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);

    // 2) 존재 여부로 체크 (원하면)
    @Select("SELECT EXISTS(SELECT 1 FROM employee WHERE phone = #{phone})")
    boolean existsByPhone(@Param("phone") String phone);

    int update(EmployeeDto dto);

	static String selectEmpNameById(String employeeId) {
		// TODO Auto-generated method stub
		return null;
	}

	int getUserListTotalCount(String keyword);
	
}
 

