package com.kdt.KDT_PJT.attend.model;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

@Mapper
public interface LeaveMapper {

	//annual_leave (연차) 테이블에서 가져오기
	@Select("select * from annual_leave where employeeId = #{employeeId};")
	List<LeaveDTO> annualLeave(); 
	
	//annual_leave 테이블에서 사용가능한 연차 가져오기
	@Select("select * from annual_leave where employeeId = #{employeeId} and leave_type='발생'  and (state_type is null or state_type = '반려')")
	List<LeaveDTO> annualLeaveRest(EmployeeDto dto); 
	
	
	//emp_seq 기준으로 특정 사용자의 총 연차 정보를 조회
	@Select("select total, ifnull(use_cnt,0) as used, t3.* from  "
			+ "(select count(*) as total from annual_leave  "
			+ "where employeeId =  #{employeeId} ) t1, "
			+ "(select count(*) as use_cnt from annual_leave "
			+ "where leave_type = '사용' and employeeId =  #{employeeId} ) t2, "
			+ "(select employeeId, emp_nm from employee where  employeeId =  #{employeeId} ) t3 "
			+ "; ")
	LeaveDTO getAnnualLeaveOne(EmployeeDto dto);
	
	//관리자용 연차 조회
	@Select("select t1.*, ifnull(use_cnt,0) as used from "
			+ "(select employeeId, count(*) as total from annual_leave "
			+ "group by employeeId ) t1 "
			+ "left outer join "
			+ "(select employeeId, count(*) use_cnt from annual_leave "
			+ "where leave_type = '사용' "
			+ "group by employeeId) t2 "
			+ "on t1.employeeId = t2.employeeId ")
	List<LeaveDTO> mngLeaveList(); 
	
	//연차 사용 신청
	@Insert("<script> "+

				"update annual_leave set approval_date = now(), used_reason = #{usedReason} ,"+
				" used_date=  #{usedDate} , state_type = '대기' where leave_id = #{leaveId} "+
			
			"</script> "
			)
	int approvalList(LeaveDTO dto); 

}
