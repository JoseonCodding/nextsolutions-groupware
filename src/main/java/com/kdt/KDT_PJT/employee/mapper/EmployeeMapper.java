package com.kdt.KDT_PJT.employee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface EmployeeMapper {

    List<EmployeeDto> getUserList(@Param("offset") Integer offset
    							, @Param("size") Integer size
    							, @Param("keyword") String keyword
    							, @Param("companyId") Integer companyId);
    
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

    @Select("SELECT emp_nm FROM employee WHERE employeeId = #{employeeId} AND use_yn = 'Y' LIMIT 1")
    String selectEmpNameById(@Param("employeeId") String employeeId);

	int getUserListTotalCount(@Param("keyword") String keyword, @Param("companyId") Integer companyId);

    @Select("SELECT * FROM employee WHERE employeeId = #{employeeId}")
    EmployeeDto findByEmployeeId(@Param("employeeId") String employeeId);

    @Update("UPDATE employee SET phone = #{phone} WHERE employeeId = #{employeeId}")
    int updatePhone(@Param("employeeId") String employeeId, @Param("phone") String phone);

    @Update("UPDATE employee SET password = #{password} WHERE employeeId = #{employeeId}")
    int updatePassword(@Param("employeeId") String employeeId, @Param("password") String password);

    @Select("SELECT employeeId FROM employee WHERE company_id = #{companyId} AND (role = '근태' OR role = '대표')")
    List<String> findApproverIds(@Param("companyId") int companyId);

}
 

