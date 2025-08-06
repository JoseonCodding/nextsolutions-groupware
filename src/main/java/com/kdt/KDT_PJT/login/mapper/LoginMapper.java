package com.kdt.KDT_PJT.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;

@Mapper
public interface LoginMapper {
    CmmnMap getUserByIdAndPassword(CmmnMap params);
}
