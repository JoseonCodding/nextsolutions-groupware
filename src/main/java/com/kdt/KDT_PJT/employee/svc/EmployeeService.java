package com.kdt.KDT_PJT.employee.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public PageInfo<CmmnMap> getUserList(int pageNum, int pageSize, String keyword) {
    	
    	PageHelper.startPage(pageNum, pageSize);
    	
    	List<CmmnMap> list = employeeMapper.getUserList(pageNum, pageSize, keyword);
    	
        return new PageInfo<>(list);
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

   

}

