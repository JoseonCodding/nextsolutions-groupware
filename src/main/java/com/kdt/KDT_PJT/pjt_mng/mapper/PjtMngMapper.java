package com.kdt.KDT_PJT.pjt_mng.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import java.util.List;

@Mapper
public interface PjtMngMapper {
    // ✅ static 지우고, 몸체(=메서드 본문) 없이 선언만 남긴다
    List<CmmnMap> getApproverList();
}
