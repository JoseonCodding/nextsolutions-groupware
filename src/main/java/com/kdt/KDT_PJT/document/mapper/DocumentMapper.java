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

	// 목록
	@Select("""
	select t1.*, vers, emp_nm from TB_PJT_BASC t1,
	(select gid, group_concat(ver SEPARATOR ',') as vers, max(ver) as max_ver  from TB_PJT_BASC
	group by gid) t2, employee
	where t1.gid = t2.gid and t1.ver = t2.max_ver and t1.PJT_STTS_CD = '진행중'
	and t1.employeeId = employee.employeeId
    and (#{isAdmin}=TRUE OR t1.employeeId=#{employeeId})
	  order by t1.FRST_REG_DT DESC
	  LIMIT #{limit} OFFSET #{offset}
	""")
	List<DocumentDTO> findDocsForManage(@Param("employeeId") String employeeId,
	                                    @Param("isAdmin") boolean isAdmin,
	                                    @Param("limit") int limit,
	                                    @Param("offset") int offset);

	// 목록 총개수(페이징)
	@Select("""
	  SELECT COUNT(*) FROM (
	    SELECT 1
	    FROM TB_PJT_BASC t
	    JOIN ( SELECT gid, MAX(ver) AS ver
	           FROM TB_PJT_BASC
	           WHERE PJT_STTS_CD='완료'
	           GROUP BY gid ) m
	      ON m.gid=t.gid AND m.ver=t.ver
	    WHERE (#{isAdmin}=TRUE OR t.employeeId=#{employeeId})
	  ) x
	""")
	int countDocsForManage(@Param("employeeId") String employeeId,
	                       @Param("isAdmin") boolean isAdmin);
  
  // 상세보기(최신 버전만)
  @Select("""
		  SELECT t.*, e.emp_nm AS empNm
		  FROM TB_PJT_BASC t
		  LEFT JOIN employee e ON e.employeeId = t.employeeId
		  WHERE t.gid = #{gid} AND t.PJT_STTS_CD = '완료'
		  ORDER BY t.ver DESC
		  LIMIT 1
		""")
		DocumentDTO findLatestApprovedByGid(@Param("gid") String gid);

  /**
   * B. 특정 gid의 모든 버전 목록 (최신 → 과거)
   *  - employee 조인으로 empNm 조회
   */
  @Select("""
    SELECT
      t.*,
      e.emp_nm AS empNm
    FROM TB_PJT_BASC t
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.gid = #{gid}
    ORDER BY t.ver DESC
  """)
  List<DocumentDTO> findVersions(@Param("gid") String gid);

  /**
   * C. 특정 gid + ver 상세
   *  - employee 조인으로 empNm 조회
   */
  @Select("""
    SELECT
      t.*,
      e.emp_nm AS empNm
    FROM TB_PJT_BASC t
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.gid = #{gid} AND t.ver = #{ver}
    LIMIT 1
  """)
  DocumentDTO findByGidAndVer(@Param("gid") String gid, @Param("ver") BigDecimal ver);

  /**
   * D. 최신 ver 잠금 조회 (경합 방지용)
   *  - @Transactional 범위 내에서 호출해야 FOR UPDATE 유효
   */
  @Select("""
    SELECT ver
    FROM TB_PJT_BASC
    WHERE gid = #{gid}
    ORDER BY ver DESC
    LIMIT 1 FOR UPDATE
  """)
  BigDecimal findLatestVerForUpdate(@Param("gid") String gid);

  /**
   * E. 새 버전 INSERT
   *  - emp_nm 컬럼은 TB_PJT_BASC에 없으므로 INSERT 대상에서 제외
   *  - employeeId는 그대로 저장
   *  - frst_reg_dt는 NOW()
   */
  @Insert("""
		    INSERT INTO TB_PJT_BASC (
		      PJT_NM, PJT_STTS_CD, employeeId, FRST_REG_DT, content,
		      ATCH_FILE_SN1, ORG_FILE_NM1, ATCH_FILE_SN2, ATCH_FILE_SN3, ORG_FILE_NM2, ORG_FILE_NM3,
		      gid, ver
		    ) VALUES (
		      #{pjtNm}, '완료', #{employeeId}, NOW(), #{content},
		      #{atchFileSn1}, #{orgFileNm1}, #{atchFileSn2}, #{atchFileSn3}, #{orgFileNm2}, #{orgFileNm3},
		      #{gid}, #{ver}
		    )
		  """)
		  @Options(useGeneratedKeys = true, keyProperty = "pjtSn")
		  int insertNewVersion(DocumentDTO doc);

  /**
   * F. 전자결재 승인: 해당 gid의 최신버전만 '완료'로
   */
  @Update("""
    UPDATE TB_PJT_BASC t
    JOIN (
      SELECT gid, MAX(ver) AS ver
      FROM TB_PJT_BASC
      WHERE gid = #{gid}
    ) m ON t.gid = m.gid AND t.ver = m.ver
    SET t.PJT_STTS_CD = '완료'
  """)
  int markApprovedLatest(@Param("gid") String gid);

 
}
