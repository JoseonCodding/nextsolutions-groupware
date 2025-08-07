package com.kdt.KDT_PJT.employee.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<CmmnMap> getUserList() {
        return employeeMapper.getUserList();
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

    public void updateEmployee(CmmnMap params) {
        employeeMapper.updateEmployee(params);
    }
    
   

}

