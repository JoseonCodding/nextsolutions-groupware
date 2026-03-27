package com.kdt.KDT_PJT.document.mapper;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.kdt.KDT_PJT.document.model.DocumentDTO;

@Mapper
public interface DocumentMapper {

  // A. 최신버전 목록
  @Select("""
    SELECT t.*, e.emp_nm AS empNm
    , e.position AS position
    FROM TB_PJT_BASC t
    JOIN (
      SELECT gid, MAX(ver) AS max_ver
      FROM TB_PJT_BASC
      WHERE company_id = #{companyId}
      GROUP BY gid
    ) m
      ON t.gid = m.gid AND t.ver = m.max_ver
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.PJT_STTS_CD IN ('진행중','완료')
      AND t.company_id = #{companyId}
      AND (t.employeeId = #{employeeId} OR #{isAdmin})
    ORDER BY t.FRST_REG_DT DESC
    LIMIT #{limit} OFFSET #{offset}
  """)
  List<DocumentDTO> findDocsForManage(@Param("employeeId") String employeeId,
                                      @Param("isAdmin") boolean isAdmin,
                                      @Param("companyId") int companyId,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

  // A-1. 총개수
  @Select("""
    SELECT COUNT(*)
    FROM (
      SELECT 1
      FROM TB_PJT_BASC t
      JOIN (
        SELECT gid, MAX(ver) AS max_ver
        FROM TB_PJT_BASC
        WHERE company_id = #{companyId}
        GROUP BY gid
      ) m
        ON t.gid = m.gid AND t.ver = m.max_ver
      WHERE t.PJT_STTS_CD IN ('진행중','완료')
        AND t.company_id = #{companyId}
        AND (t.employeeId = #{employeeId} OR #{isAdmin})
    ) x
  """)
  int countDocsForManage(@Param("employeeId") String employeeId,
                         @Param("isAdmin") boolean isAdmin,
                         @Param("companyId") int companyId);

  // B. 최신 상세
  @Select("""
    SELECT t.*, e.emp_nm AS empNm
    , e.position AS position
    FROM TB_PJT_BASC t
    JOIN ( SELECT gid, MAX(ver) AS max_ver
           FROM TB_PJT_BASC
           WHERE gid = #{gid} AND company_id = #{companyId} ) m
      ON t.gid = m.gid AND t.ver = m.max_ver
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.PJT_STTS_CD IN ('진행중','완료')
      AND t.company_id = #{companyId}
    LIMIT 1
  """)
  DocumentDTO findLatestApprovedByGid(@Param("gid") String gid,
                                      @Param("companyId") int companyId);

  // C. 버전 목록
  @Select("""
    SELECT t.*, e.emp_nm AS empNm
    , e.position AS position
    FROM TB_PJT_BASC t
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.gid = #{gid}
      AND t.company_id = #{companyId}
    ORDER BY t.ver DESC
  """)
  List<DocumentDTO> findVersions(@Param("gid") String gid,
                                 @Param("companyId") int companyId);

  // D. 특정 버전 상세
  @Select("""
    SELECT t.*, e.emp_nm AS empNm
    , e.position AS position
    FROM TB_PJT_BASC t
    LEFT JOIN employee e ON e.employeeId = t.employeeId
    WHERE t.gid = #{gid} AND t.ver = #{ver}
      AND t.company_id = #{companyId}
    LIMIT 1
  """)
  DocumentDTO findByGidAndVer(@Param("gid") String gid,
                              @Param("ver") Integer ver,
                              @Param("companyId") int companyId);

  // E. 최신 ver 잠금
  @Select("""
    SELECT ver
    FROM TB_PJT_BASC
    WHERE gid = #{gid}
    ORDER BY ver DESC
    LIMIT 1 FOR UPDATE
  """)
  Integer findLatestVerForUpdate(@Param("gid") String gid);

  // F. 새 버전 INSERT
  @Insert("""
    INSERT INTO TB_PJT_BASC (
      PJT_NM, PJT_STTS_CD, employeeId,
      FRST_REG_DT, content,
      ATCH_FILE_SN1, ORG_FILE_NM1,
      ATCH_FILE_SN2, ORG_FILE_NM2,
      ATCH_FILE_SN3, ORG_FILE_NM3,
      gid, ver
    ) VALUES (
      #{pjtNm}, #{pjtSttsCd}, #{employeeId},
      NOW(), #{content},
      #{atchFileSn1}, #{orgFileNm1},
      #{atchFileSn2}, #{orgFileNm2},
      #{atchFileSn3}, #{orgFileNm3},
      #{gid}, #{ver}
    )
  """)
  @Options(useGeneratedKeys = true, keyProperty = "pjtSn")
  int insertNewVersion(DocumentDTO doc);

  // G. 상태 업데이트 (예: 승인)
  @Update("""
    UPDATE TB_PJT_BASC t
    JOIN ( SELECT gid, MAX(ver) AS max_ver
           FROM TB_PJT_BASC
           WHERE gid = #{gid} ) m
      ON t.gid = m.gid AND t.ver = m.max_ver
    SET t.PJT_STTS_CD = #{pjtSttsCd}
  """)
  int updateLatestStatus(@Param("gid") String gid,
                         @Param("pjtSttsCd") String pjtSttsCd);
  
//목록(최신버전) - 검색 포함
 @Select("""
   <script>
   SELECT t.*, e.emp_nm AS empNm
   , e.position AS position
   FROM TB_PJT_BASC t
   JOIN (
     SELECT gid, MAX(ver) AS max_ver
     FROM TB_PJT_BASC
     WHERE company_id = #{companyId}
     GROUP BY gid
   ) m ON t.gid = m.gid AND t.ver = m.max_ver
   LEFT JOIN employee e ON e.employeeId = t.employeeId
   WHERE t.PJT_STTS_CD IN ('진행중','완료')
     AND t.company_id = #{companyId}
   <if test="!isAdmin">
     AND t.employeeId = #{employeeId}
   </if>

   <if test="keywordType != null and keyword != null and keyword.trim() != ''">
     <choose>
       <when test="keywordType == 'writer'">
         AND e.emp_nm LIKE CONCAT('%', #{keyword}, '%')
       </when>
       <when test="keywordType == 'project'">
         AND t.PJT_NM LIKE CONCAT('%', #{keyword}, '%')
       </when>
       <when test="keywordType == 'status'">
         AND t.PJT_STTS_CD = #{keyword}
       </when>
     </choose>
   </if>

   ORDER BY t.FRST_REG_DT DESC, t.gid DESC
   LIMIT #{limit} OFFSET #{offset}
   </script>
 """)
 List<DocumentDTO> findDocsForManageWithSearch(@Param("employeeId") String employeeId,
                                               @Param("isAdmin") boolean isAdmin,
                                               @Param("companyId") int companyId,
                                               @Param("keywordType") String keywordType,
                                               @Param("keyword") String keyword,
                                               @Param("sort") String sort,
                                               @Param("limit") int limit,
                                               @Param("offset") int offset);

 	@Select("""
		    <script>
		    SELECT COUNT(*)
		    FROM (
		      SELECT 1
		      FROM TB_PJT_BASC t
		      JOIN (
		        SELECT gid, MAX(ver) AS max_ver
		        FROM TB_PJT_BASC
		        WHERE company_id = #{companyId}
		        GROUP BY gid
		      ) m ON t.gid = m.gid AND t.ver = m.max_ver
		      LEFT JOIN employee e ON e.employeeId = t.employeeId
		      WHERE t.PJT_STTS_CD IN ('진행중','완료')
		        AND t.company_id = #{companyId}
		      <if test="!isAdmin">
		        AND t.employeeId = #{employeeId}
		      </if>

		      <if test="keywordType != null and keyword != null and keyword.trim() != ''">
		        <choose>
		          <when test="keywordType == 'writer'">
		            AND e.emp_nm LIKE CONCAT('%', #{keyword}, '%')
		          </when>
		          <when test="keywordType == 'project'">
		            AND t.PJT_NM LIKE CONCAT('%', #{keyword}, '%')
		          </when>
		          <when test="keywordType == 'status'">
		            AND t.PJT_STTS_CD = #{keyword}
		          </when>
		        </choose>
		      </if>
		    ) x
		    </script>
		  """)
		  int countDocsForManageWithSearch(@Param("employeeId") String employeeId,
		                                   @Param("isAdmin") boolean isAdmin,
		                                   @Param("companyId") int companyId,
		                                   @Param("keywordType") String keywordType,
		                                   @Param("keyword") String keyword);

  
}


