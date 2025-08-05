package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.approval.model.ApprovalDTO;

@Mapper
public interface ApprovalMapper {
	
	@Select("SELECT * FROM Approval_TEST")
	List<ApprovalDTO> selectAll();
}
