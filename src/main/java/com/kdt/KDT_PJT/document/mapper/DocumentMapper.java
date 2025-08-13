package com.kdt.KDT_PJT.document.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kdt.KDT_PJT.document.model.DocumentDTO;

@Mapper
public interface DocumentMapper {

	@Select("""
		    SELECT 
		        version_id AS versionId,
		        original_id AS originalId,
		        PJT_NM AS pjtNm,
		        v.employeeId,
		        e.emp_nm AS employeeName,
		        PJT_BGNG_DT AS pjtBgngDt,
		        PJT_END_DT AS pjtEndDt,
		        PJT_STTS_CD AS pjtSttsCd,
		        content,
		        ATCH_FILE_SN1 AS atchFileSn1,
		        ORG_FILE_NM1 AS orgFileNm1,
		        versionName,
		        versionCreatedAt
		    FROM TB_PJT_BASC_VERSION v
		    LEFT JOIN employee e ON v.employeeId = e.employeeId
		    ORDER BY versionCreatedAt DESC
		""")
		List<DocumentDTO> selectAll();

    
    @Select("""
    	    SELECT
    	        version_id AS versionId,
    	        original_id AS originalId,
    	        PJT_NM AS pjtNm,
    	        v.employeeId,
    	        e.emp_nm AS employeeName,
    	        PJT_BGNG_DT AS pjtBgngDt,
    	        PJT_END_DT AS pjtEndDt,
    	        PJT_STTS_CD AS pjtSttsCd,
    	        content,
    	        ATCH_FILE_SN1 AS atchFileSn1,
    	        ORG_FILE_NM1 AS orgFileNm1,
    	        versionName,
    	        versionCreatedAt
    	    FROM TB_PJT_BASC_VERSION v
    	    LEFT JOIN employee e ON v.employeeId = e.employeeId
    	    WHERE v.version_id = #{versionId}
    	""")
    	DocumentDTO selectByVersionId(Long versionId);
}
