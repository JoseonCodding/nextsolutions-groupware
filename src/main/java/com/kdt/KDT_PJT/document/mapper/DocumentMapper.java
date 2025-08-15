package com.kdt.KDT_PJT.document.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kdt.KDT_PJT.document.model.DocumentDTO;

@Mapper
public interface DocumentMapper {

  // A. 문서관리 목록: '완료' + gid별 최신(MAX ver)만, 권한필터(관리자/본인)
  @Select("""
    SELECT t.*
    FROM TB_PJT_BASCcol t
    JOIN (
      SELECT gid, MAX(ver) AS ver
      FROM TB_PJT_BASCcol
      WHERE PJT_STTS_CD = '완료'
      GROUP BY gid
    ) m ON m.gid = t.gid AND m.ver = t.ver
    WHERE (#{isAdmin} = TRUE OR t.employee_id = #{employeeId})
    ORDER BY t.frst_reg_dt DESC
    LIMIT #{limit} OFFSET #{offset}
  """)
  List<DocumentDTO> findDocsForManage(
      @Param("employeeId") String employeeId,
      @Param("isAdmin") boolean isAdmin,
      @Param("limit") int limit,
      @Param("offset") int offset
  );

  // B. 특정 프로젝트의 버전 목록
  @Select("""
    SELECT *
    FROM TB_PJT_BASCcol
    WHERE gid = #{gid}
    ORDER BY ver DESC
  """)
  List<DocumentDTO> findVersions(@Param("gid") String gid);

  // C. 특정 버전 상세
  @Select("""
    SELECT *
    FROM TB_PJT_BASCcol
    WHERE gid = #{gid} AND ver = #{ver}
    LIMIT 1
  """)
  DocumentDTO findByGidAndVer(@Param("gid") String gid, @Param("ver") BigDecimal ver);

  // D. 최신 ver 잠금 조회(경합 방지)
  @Select("""
    SELECT ver
    FROM TB_PJT_BASCcol
    WHERE gid = #{gid}
    ORDER BY ver DESC
    LIMIT 1 FOR UPDATE
  """)
  BigDecimal findLatestVerForUpdate(@Param("gid") String gid);

  // E. 새 버전 INSERT (pjt_sn은 DB auto)
  @Insert("""
    INSERT INTO TB_PJT_BASCcol (
      pjt_nm, pjt_stts_cd, employee_id, emp_nm, frst_reg_dt, content,
      atch_file_sn1, org_file_nm1, atch_file_sn2, atch_file_sn3, org_file_nm2, org_file_nm3,
      gid, ver
    ) VALUES (
      #{pjtNm}, #{pjtSttsCd}, #{employeeId}, #{empNm}, NOW(), #{content},
      #{atchFileSn1}, #{orgFileNm1}, #{atchFileSn2}, #{atchFileSn3}, #{orgFileNm2}, #{orgFileNm3},
      #{gid}, #{ver}
    )
  """)
  @Options(useGeneratedKeys = true, keyProperty = "pjtSn")
  int insertNewVersion(DocumentDTO doc);

  // F. 전자결재 승인: gid의 최신 한 건만 '완료'로
  @Update("""
    UPDATE TB_PJT_BASCcol t
    JOIN (
      SELECT gid, MAX(ver) AS ver
      FROM TB_PJT_BASCcol
      WHERE gid = #{gid}
    ) m ON t.gid = m.gid AND t.ver = m.ver
    SET t.pjt_stts_cd = '완료'
  """)
  int markApprovedLatest(@Param("gid") String gid);

 
}
