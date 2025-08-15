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

  // ---------------------------------------------------------------------------
  // A. 문서 목록(최신버전만) : gid별 MAX(ver) 1건 + 상태 필터('진행중','완료')
  // ---------------------------------------------------------------------------
	@Select("""
			SELECT
			    t1.PJT_SN,
			    t1.gid,
			    t1.PJT_NM,
			    e.emp_nm AS empNm,
			    t1.FRST_REG_DT,
			    t2.max_ver AS ver,
			    t1.PJT_STTS_CD
			FROM TB_PJT_BASC t1
			JOIN (
			    SELECT gid, MAX(ver) AS max_ver
			    FROM TB_PJT_BASC
			    GROUP BY gid
			) t2
			  ON t1.gid = t2.gid
			 AND t1.ver = t2.max_ver
			JOIN employee e
			  ON e.employeeId = t1.employeeId
			WHERE t1.PJT_STTS_CD IN ('진행중','완료')
			  AND (#{isAdmin} = TRUE OR t1.employeeId = #{employeeId})
			ORDER BY t1.FRST_REG_DT DESC
			LIMIT #{limit} OFFSET #{offset}
			""")
			List<DocumentDTO> findDocsForManage(@Param("employeeId") String employeeId,
			                                    @Param("isAdmin") boolean isAdmin,
			                                    @Param("limit") int limit,
			                                    @Param("offset") int offset);



  // ---------------------------------------------------------------------------
  // A-1. 목록 총개수(페이징용) : 목록 쿼리와 '동일한 의미'로 카운트
  //   - 주의: MAX(ver) 계산은 상태 필터 없이 하고, 필터는 최종행(t)에 적용
  // ---------------------------------------------------------------------------
	@Select("""
			  SELECT COUNT(*) FROM (
		    SELECT 1
		    FROM TB_PJT_BASC t
		    JOIN ( SELECT gid, MAX(ver) AS max_ver
		           FROM TB_PJT_BASC
		           GROUP BY gid ) m
		      ON m.gid = t.gid
		     AND t.ver = m.max_ver
		    WHERE t.PJT_STTS_CD IN ('진행중','완료')
		      AND (#{isAdmin} = TRUE OR t.employeeId = #{employeeId})
		  ) x
		  """)
		  int countDocsForManage(@Param("employeeId") String employeeId,
		                         @Param("isAdmin") boolean isAdmin);


  // ---------------------------------------------------------------------------
  // B. 상세보기(해당 gid의 최신버전 1건)
  //   - 목록과 동일 규칙: gid별 MAX(ver) 조인 + 상태 IN
  // ---------------------------------------------------------------------------
	@Select("""
			  SELECT t.*, e.emp_nm AS empNm
			  FROM TB_PJT_BASC t
			  JOIN ( SELECT gid, MAX(ver) AS max_ver
			         FROM TB_PJT_BASC
			         WHERE gid = #{gid} ) m
			    ON m.gid = t.gid
			   AND t.ver = m.max_ver
			  LEFT JOIN employee e ON e.employeeId = t.employeeId
			  WHERE t.PJT_STTS_CD IN ('진행중','완료')
			  LIMIT 1
			  """)
			  DocumentDTO findLatestApprovedByGid(@Param("gid") String gid);

  // ---------------------------------------------------------------------------
  // C. 버전 목록(히스토리) : 같은 gid의 모든 버전, 최신 → 과거
  // ---------------------------------------------------------------------------
	@Select("""
			  SELECT t.*, e.emp_nm AS empNm
			  FROM TB_PJT_BASC t
			  LEFT JOIN employee e ON e.employeeId = t.employeeId
			  WHERE t.gid = #{gid}
			  ORDER BY t.ver DESC
			  """)
			  List<DocumentDTO> findVersions(@Param("gid") String gid);

  // ---------------------------------------------------------------------------
  // D. 특정 gid + ver 상세
  // ---------------------------------------------------------------------------
	@Select("""
			  SELECT t.*, e.emp_nm AS empNm
			  FROM TB_PJT_BASC t
			  LEFT JOIN employee e ON e.employeeId = t.employeeId
			  WHERE t.gid = #{gid} AND t.ver = #{ver}
			  LIMIT 1
			  """)
			  DocumentDTO findByGidAndVer(@Param("gid") String gid, @Param("ver") Integer ver);

  // ---------------------------------------------------------------------------
  // E. 최신 ver 잠금(for update) : 경합 방지용 (트랜잭션 내에서 호출)
  // ---------------------------------------------------------------------------
	@Select("""
			  SELECT ver
			  FROM TB_PJT_BASC
			  WHERE gid = #{gid}
			  ORDER BY ver DESC
			  LIMIT 1 FOR UPDATE
			  """)
			  Integer findLatestVerForUpdate(@Param("gid") String gid);

  // ---------------------------------------------------------------------------
  // F. 새 버전 INSERT
  //   - 비즈니스 규칙에 따라 초기 상태 값은 조정하세요.
  //     * 예: 수정 생성 시 '진행중'으로 시작 -> 결재 승인 시 '완료'
  //   - NOW()로 등록일 설정
  //   - TB_PJT_BASC에 존재하지 않는 컬럼(예: emp_nm)은 제외
  // ---------------------------------------------------------------------------
	@Insert("""
			  INSERT INTO TB_PJT_BASC (
			    PJT_NM, PJT_STTS_CD, employeeId, FRST_REG_DT, content,
			    ATCH_FILE_SN1, ORG_FILE_NM1, ATCH_FILE_SN2, ATCH_FILE_SN3, ORG_FILE_NM2, ORG_FILE_NM3,
			    gid, ver
			  ) VALUES (
			    #{pjtNm}, '진행중', #{employeeId}, NOW(), #{content},
			    #{atchFileSn1}, #{orgFileNm1}, #{atchFileSn2}, #{atchFileSn3}, #{orgFileNm2}, #{orgFileNm3},
			    #{gid}, #{ver}
			  )
			  """)
			  @Options(useGeneratedKeys = true, keyProperty = "pjtSn")
			  int insertNewVersion(DocumentDTO doc);

  // ---------------------------------------------------------------------------
  // G. 전자결재 승인: 해당 gid의 최신버전 1건만 '완료'로 변경
  // ---------------------------------------------------------------------------
	@Update("""
			  UPDATE TB_PJT_BASC t
			  JOIN ( SELECT gid, MAX(ver) AS max_ver
			         FROM TB_PJT_BASC
			         WHERE gid = #{gid} ) m
			    ON t.gid = m.gid AND t.ver = m.max_ver
			  SET t.PJT_STTS_CD = '완료'
			  """)
			  int markApprovedLatest(@Param("gid") String gid);
			
}
