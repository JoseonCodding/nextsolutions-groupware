package com.kdt.KDT_PJT.employee.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<EmployeeDto> getUserList(int offset, int size, String keyword) {
    	Integer companyId = CompanyContext.get();
    	return employeeMapper.getUserList(offset, size, keyword, companyId);
    }


	public int getUserListTotalCount(String keyword) {
		Integer companyId = CompanyContext.get();
		return employeeMapper.getUserListTotalCount(keyword, companyId);
	}

    

    public void toggleActive(CmmnMap params) {
        employeeMapper.toggleActive(params);
    }

    public void insertEmployee(CmmnMap params) {
        employeeMapper.insertEmployee(params);
    }
    
    public CmmnMap getEmployeeBySeq(int empSeq) {
        return employeeMapper.getEmployeeBySeq(empSeq);
    }
    
    public EmployeeDto getEmployeeDetail(int empSeq) {   	
        return employeeMapper.getEmployeeDetail(empSeq);
    }

    public void updateEmployee(CmmnMap params) {
        employeeMapper.updateEmployee(params);
    }

	public EmployeeDto getDetail(EmployeeDto dto) {
		return employeeMapper.getDetail(dto);
	}

	public int update(EmployeeDto dto) {
		return employeeMapper.update(dto);
	}

	public EmployeeDto getIdChk(EmployeeDto dto) {
		return employeeMapper.getIdChk(dto);
	}

	public String getEmpNameById(String employeeId) {
		return employeeMapper.selectEmpNameById(employeeId);
	}

    public boolean existsByPhone(String phone) {
        return employeeMapper.countByPhone(phone) > 0;
    }

    public EmployeeDto findByEmployeeId(String employeeId) {
        return employeeMapper.findByEmployeeId(employeeId);
    }

    public int updatePhone(String employeeId, String phone) {
        return employeeMapper.updatePhone(employeeId, phone);
    }

    public int updatePassword(String employeeId, String encodedPassword) {
        return employeeMapper.updatePassword(employeeId, encodedPassword);
    }

}

