package com.kdt.KDT_PJT.document.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.document.model.DocumentDTO;

@Mapper
public interface DocumentMapper {

	  // 1) 문서관리 목록 (최신버전 + 상태완료, 권한필터, 페이징)
	  @Select("""
	    SELECT *
	    FROM TB_PJT_BASCcol
	    WHERE PJT_STTS_CD = '완료'
	      AND USE_YN = 'Y'
	      AND (#{isAdmin} = TRUE OR employeeId = #{employeeId})
	    ORDER BY LAST_MDFCN_DT DESC
	    LIMIT #{limit} OFFSET #{offset}
	  """)
	  List<DocumentMapper> findDocsForManage(
	      @Param("employeeId") String employeeId,
	      @Param("isAdmin") boolean isAdmin,
	      @Param("limit") int limit,
	      @Param("offset") int offset
	  );

	  // 2) 특정 gid 최신버전 조회
	  @Select("""
	    SELECT *
	    FROM TB_PJT_BASCcol
	    WHERE gid = #{gid} AND USE_YN = 'Y'
	    LIMIT 1
	  """)
	  DocumentMapper findLatestByGid(@Param("gid") String gid);

	  // 3) 버전 목록
	  @Select("""
	    SELECT *
	    FROM TB_PJT_BASCcol
	    WHERE gid = #{gid}
	    ORDER BY ver DESC
	  """)
	  List<DocumentMapper> findVersions(@Param("gid") String gid);

	  // 4) 특정 버전 상세
	  @Select("""
	    SELECT *
	    FROM TB_PJT_BASCcol
	    WHERE gid = #{gid} AND ver = #{ver}
	    LIMIT 1
	  """)
	  DocumentMapper findByGidAndVer(@Param("gid") String gid, @Param("ver") BigDecimal ver);

	  // 5) 다음 버전 계산
	  @Select("""
	    SELECT COALESCE(MAX(ver), 1.0) FROM TB_PJT_BASCcol WHERE gid = #{gid}
	  """)
	  DocumentMapper findMaxVer(@Param("gid") String gid);

	  // 6) 최신버전 USE_YN 해제
	  @Update("""
	    UPDATE TB_PJT_BASCcol
	    SET USE_YN = 'N', LAST_MDFR_ID = #{modifierId}, LAST_MDFCN_DT = NOW()
	    WHERE gid = #{gid} AND USE_YN = 'Y'
	  """)
	  int deactivateLatest(@Param("gid") String gid, @Param("modifierId") String modifierId);

	  // 7) 새 버전 Insert
	  @Insert("""
	    INSERT INTO TB_PJT_BASCcol (
	      PJT_SN, PJT_NM, PJT_STTS_CD, employee_id, USE_YN,
	      FRST_RGTR_ID, FRST_REG_DT, LAST_MDFR_ID, LAST_MDFCN_DT,
	      PJT_BGNG_DT, PJT_END_DT, TB_PJT_APR, TB_PJT_BASCcol,
	      docType, content,
	      ATCH_FILE_SN1, ORG_FILE_NM1, ATCH_FILE_SN2, ATCH_FILE_SN3, ORG_FILE_NM2, ORG_FILE_NM3,
	      TB_PJT_BASCcol1, gid, ver
	    )
	    VALUES (
	      #{pjtSn}, #{pjtNm}, #{pjtSttsCd}, #{employeeId}, 'Y',
	      #{frstRgtrId}, NOW(), #{lastMdfrId}, NOW(),
	      #{pjtBgngDt}, #{pjtEndDt}, #{tbPjtApr}, #{tbPjtBasccol},
	      #{docType}, #{content},
	      #{atchFileSn1}, #{orgFileNm1}, #{atchFileSn2}, #{atchFileSn3}, #{orgFileNm2}, #{orgFileNm3},
	      #{tbPjtBasccol1}, #{gid}, #{ver}
	    )
	  """)
	  int insertNewVersion(DocumentMapper doc);

	  // 8) 전자결재 승인 반영 (최신만 ‘완료’로)
	  @Update("""
	    UPDATE TB_PJT_BASCcol
	    SET PJT_STTS_CD = '완료', LAST_MDFR_ID = #{approverId}, LAST_MDFCN_DT = NOW()
	    WHERE gid = #{gid} AND USE_YN = 'Y'
	  """)
	  int markApproved(@Param("gid") String gid, @Param("approverId") String approverId);
	}
