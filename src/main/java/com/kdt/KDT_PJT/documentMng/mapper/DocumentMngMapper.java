package com.kdt.KDT_PJT.documentMng.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.kdt.KDT_PJT.documentMng.model.DocumentMngDTO;

@Mapper
public interface DocumentMngMapper {

    @Select("""
        SELECT 
            v.original_id AS originalId,
            v.PJT_NM AS pjtNm,
            v.employeeId,
            e.emp_nm AS employeeName,
            v.PJT_STTS_CD AS pjtSttsCd,
            v.PJT_BGNG_DT AS pjtBgngDt,
            v.PJT_END_DT AS pjtEndDt,
            v.versionName
        FROM TB_PJT_BASC_VERSION v
        LEFT JOIN employee e 
            ON v.employeeId = e.employeeId
    """)
    List<DocumentMngDTO> selectAll();
}
