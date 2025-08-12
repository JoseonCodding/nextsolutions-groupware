package com.kdt.KDT_PJT.documentMng.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.documentMng.model.DocumentMngDTO;
import com.kdt.KDT_PJT.documentMng.model.DocumentVersionSummaryDTO;

@Mapper
public interface DocumentMngMapper {

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
		        ATCH_FILE_SN AS atchFileSn,
		        ORG_FILE_NM AS orgFileNm,
		        versionName,
		        versionCreatedAt
		    FROM TB_PJT_BASC_VERSION v
		    LEFT JOIN employee e ON v.employeeId = e.employeeId
		    ORDER BY versionCreatedAt DESC
		""")
		List<DocumentMngDTO> selectAll();

    
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
    	        ATCH_FILE_SN AS atchFileSn,
    	        ORG_FILE_NM AS orgFileNm,
    	        versionName,
    	        versionCreatedAt
    	    FROM TB_PJT_BASC_VERSION v
    	    LEFT JOIN employee e ON v.employeeId = e.employeeId
    	    WHERE v.version_id = #{versionId}
    	""")
    	DocumentMngDTO selectByVersionId(Long versionId);
    
    // 버전 복구 기능
	    // 특정 문서의 버전 목록 조회
	    @Select("""
	        SELECT version_id AS versionId,
	               original_id AS originalId,
	               versionName,
	               versionCreatedAt
	        FROM TB_PJT_BASC_VERSION
	        WHERE original_id = #{originalId}
	        ORDER BY versionCreatedAt DESC
	    """)
	    List<DocumentVersionSummaryDTO> selectVersionListByOriginalId(Long originalId);
	
	    // 특정 버전 상세 조회
	    @Select("""
	        SELECT version_id AS versionId,
	               original_id AS originalId,
	               PJT_NM AS pjtNm,
	               employeeId,
	               PJT_BGNG_DT AS pjtBgngDt,
	               PJT_END_DT AS pjtEndDt,
	               PJT_STTS_CD AS pjtSttsCd,
	               content,
	               ATCH_FILE_SN AS atchFileSn,
	               ORG_FILE_NM AS orgFileNm,
	               versionName,
	               versionCreatedAt
	        FROM TB_PJT_BASC_VERSION
	        WHERE version_id = #{versionId}
	    """)
	    DocumentMngDTO selectVersionById(Long versionId);
	
	 // 복원 SQL (기존 restoreVersion + 필요 시 수정)
	    @Update("""
	        UPDATE TB_PJT_BASC t
	        JOIN TB_PJT_BASC_VERSION v ON t.PJT_SN = v.original_id
	        SET t.PJT_NM        = v.PJT_NM,
	            t.employeeId    = v.employeeId,
	            t.PJT_BGNG_DT   = v.PJT_BGNG_DT,
	            t.PJT_END_DT    = v.PJT_END_DT,
	            t.PJT_STTS_CD   = v.PJT_STTS_CD,
	            t.content       = v.content,
	            t.ATCH_FILE_SN  = v.ATCH_FILE_SN,
	            t.ORG_FILE_NM   = v.ORG_FILE_NM,
	            t.LAST_MDFCN_DT = DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'),
	            t.LAST_MDFR_ID  = 'RESTORE_SYS'
	        WHERE v.version_id = #{versionId}
	    """)
	    void restoreVersion(Long versionId);
	    
	 // versionId → originalId 조회
	    @Select("""
	        SELECT original_id
	        FROM TB_PJT_BASC_VERSION
	        WHERE version_id = #{versionId}
	    """)
	    Long getOriginalIdByVersionId(Long versionId);


}
