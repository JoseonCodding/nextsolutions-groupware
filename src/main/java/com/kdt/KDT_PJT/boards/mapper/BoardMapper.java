package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // (공통) 게시판 유형으로 board_id 찾기
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);

    
    /// ========= 공지(보드=1) =========
    // 공지 목록 (완료만) - 페이징
    @Select("""
    		  <script>
    		  SELECT p.* FROM board_post p
    		  WHERE p.board_id=1 AND p.status='완료' AND p.is_deleted=FALSE
    		  ORDER BY COALESCE(p.published_at,p.updated_at,p.created_at) DESC, p.post_id DESC
    		  LIMIT <choose><when test="limit != null">#{limit}</when><otherwise>10</otherwise></choose>
    		  OFFSET <choose><when test="offset != null">#{offset}</when><otherwise>0</otherwise></choose>
    		  </script>
    		""")
    		List<BoardDTO> selectNoticePosts(BoardDTO dto);
    
    @Select("""
    		  SELECT COUNT(*) FROM board_post p
    		  WHERE p.board_id=1 AND p.status='완료' AND p.is_deleted=FALSE
    		""")
    		int noticeTotalCnt();

    // 공지 상세 (board_id=1, 완료만)
    @Select("""
    	    SELECT p.* FROM board_post p
    	    WHERE p.post_id=#{postId} AND p.board_id=1 AND p.status='완료' AND p.is_deleted=FALSE
    	  """)
    BoardDTO findNoticeApprovedById(BoardDTO dto);

    // 초안(대기) 저장
    @Insert("""
      INSERT INTO board_post
        (board_id, employee_id, title, content, created_at, updated_at, view_count, like_count, is_deleted, status)
      VALUES
        (1, #{employeeId}, #{title}, #{content}, NOW(), NOW(), 0, 0, FALSE, '대기')
    """)
    @Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    int insertNoticeDraft(BoardDTO dto);

    // 승인: 대기 -> 완료
    @Update("""
      UPDATE board_post
         SET status='완료', published_at=NOW(), updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND status='대기'
         AND is_deleted=FALSE
    """)
    int approveNotice(BoardDTO dto);

    // 반려: 대기 -> 반려
    @Update("""
      UPDATE board_post
         SET status='반려', updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND status='대기'
         AND is_deleted=FALSE
    """)
    int rejectNotice(BoardDTO dto);

    // 공지 삭제(관리자용)
    @Update("""
      UPDATE board_post
         SET is_deleted=TRUE, updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND is_deleted=FALSE
    """)
    int adminDeleteNotice(BoardDTO dto);
    
    // 공지 상세 진입 시 조회수 +1
    @Update("UPDATE board_post SET view_count = view_count + 1 WHERE post_id = #{postId} AND board_id=1 AND status='완료'")
    int increaseNoticeView(BoardDTO dto);

    // 공지 일자별 뷰 통계 업서트
    @Insert("""
      INSERT INTO board_view_stats (board_id, view_date, view_count)
      VALUES (1, CURDATE(), 1)
      ON DUPLICATE KEY UPDATE view_count = view_count + 1
    """)
    int bumpNoticeDailyView();


    /// ========= 자유(보드=2) =========
    // 자유 목록
    @Select("""
    		  <script>
    		  SELECT p.* FROM board_post p
    		  WHERE p.board_id=2 AND p.is_deleted=FALSE
    		  ORDER BY p.post_id DESC
    		  LIMIT <choose><when test="limit != null">#{limit}</when><otherwise>10</otherwise></choose>
    		  OFFSET <choose><when test="offset != null">#{offset}</when><otherwise>0</otherwise></choose>
    		  </script>
    		""")
    		List<BoardDTO> selectFreePosts(BoardDTO dto);
    
    @Select("SELECT COUNT(*) FROM board_post p WHERE p.board_id=2 AND p.is_deleted=FALSE")
    int freeTotalCnt();

    // 게시글 상세 조회
    @Select("""
    	    SELECT p.post_id AS postId, p.board_id AS boardId, p.employee_id AS employeeId,
    	           p.title, p.content, p.created_at AS createdAt, p.updated_at AS updatedAt,
    	           p.view_count AS viewCount, p.like_count AS likeCount, p.is_deleted AS isDeleted
    	    FROM board_post p
    	    WHERE p.post_id = #{postId} AND p.is_deleted = FALSE
    	  """)
    BoardDTO detail(BoardDTO dto);

    // 게시글 등록
    @Insert("""
    	    INSERT INTO board_post
    	      (board_id, employee_id, title, content, created_at, view_count, like_count, is_deleted)
    	    VALUES
    	      (#{boardId}, #{employeeId}, #{title}, #{content}, NOW(), 0, 0, FALSE)
    	  """)
    	  @Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    int insert(BoardDTO dto);

    // 게시글 수정
    @Update("""
    	    UPDATE board_post
    	       SET title=#{title}, content=#{content}, updated_at=NOW()
    	     WHERE post_id=#{postId} AND employee_id=#{employeeId} AND is_deleted=FALSE
    	  """)
    	  int modify(BoardDTO dto);

    // 작성자 본인 삭제
    @Update("""
      UPDATE board_post
         SET is_deleted=TRUE, updated_at=NOW()
       WHERE post_id=#{postId}
         AND (is_deleted=FALSE OR is_deleted IS NULL)
         AND employee_id=#{employeeId}
    """)
    int delete(BoardDTO dto);
    
    // 자유게시판 관리자 삭제(소유자 무시)
    @Update("""
      UPDATE board_post
         SET is_deleted = TRUE, updated_at = NOW()
       WHERE post_id = #{postId}
         AND board_id = 2
         AND (is_deleted = FALSE OR is_deleted IS NULL)
    """)
    int adminDeleteFree(BoardDTO dto);
    
    // 조회수(자유)
    @Options(flushCache = Options.FlushCachePolicy.TRUE, useCache = false)
    @Update("UPDATE board_post SET view_count = view_count + 1 WHERE post_id = #{postId}")
    int increaseViewCount(BoardDTO dto);
    
    // 최초 1회 조회 기록(자유)
    @Insert("""
      INSERT IGNORE INTO board_post_view(post_id, employee_id, viewed_at)
      VALUES (#{postId}, #{employeeId}, NOW())
    """)
    int recordView(BoardDTO dto);

    
    // 좋아요 카운트 증감
    @Update("UPDATE board_post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    int incrementLikeCount(BoardLikeDTO dto);

    // 좋아요: 실제 테이블 기준 동기화만 사용
    @Update("""
      UPDATE board_post p
         SET like_count = (SELECT COUNT(*) FROM board_like l WHERE l.post_id = p.post_id)
       WHERE p.post_id = #{postId}
    """)
    int syncLikeCount(BoardLikeDTO dto);
    

    /* ============ 관리자 ============ */
    /** 게시판명 중복 체크 */
    @Select("SELECT COUNT(1) FROM board_board WHERE board_name = #{name}")
    int countBoardName(@Param("name") String name);

    /** 보드 생성 */
    @Insert("""
      INSERT INTO board_board
        (board_name, board_type, access_role, use_comment, use_like, is_active, created_at, updated_at)
      VALUES
        (#{boardName}, #{boardType}, #{accessRole}, #{useComment}, #{useLike}, 
         COALESCE(#{isActive}, TRUE), NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "boardId", keyColumn = "board_id")
    int insertBoardMeta(BoardDTO dto);

    /** 보드 상세 */
    @Select("""
      SELECT board_id, board_name, board_type, access_role,
             use_comment, use_like, is_active, created_at, updated_at
      FROM board_board
      WHERE board_id = #{boardId}
    """)
    BoardDTO findBoardMetaById(BoardDTO dto);

    /** 보드 수정(이름/권한/댓글/좋아요/활성만) */
    @Update("""
      UPDATE board_board
         SET board_name = #{boardName},
             access_role = #{accessRole},
             use_comment = #{useComment},
             use_like = #{useLike},
             is_active = COALESCE(#{isActive}, is_active),
             updated_at = NOW()
       WHERE board_id = #{boardId}
    """)
    int updateBoardMeta(BoardDTO dto);

    /** 보드 활성/비활성 토글 */
    @Update("""
      UPDATE board_board
         SET is_active = #{active}, updated_at = NOW()
       WHERE board_id = #{boardId}
    """)
    int toggleBoardActive(@Param("boardId") Integer  boardId, @Param("active") Boolean active);
 
    /** 게시판 메타 목록 조회 (검색/활성필터) */
    @Select("""
        SELECT board_id, board_name, board_type, access_role,
               use_comment, use_like, is_active, created_at, updated_at
        FROM board_board
        WHERE (#{activeOnly} IS NULL OR is_active = #{activeOnly})
          AND (#{q} IS NULL OR board_name LIKE CONCAT('%', #{q}, '%'))
        ORDER BY created_at DESC
    """)
    List<BoardDTO> findBoards(@Param("activeOnly") Boolean activeOnly,
                              @Param("q") String q);

    /* ==== 동적 보드 공용 ==== */

    @Select("""
            SELECT board_id, board_name, board_type, access_role,
                   use_comment, use_like, is_active, created_at, updated_at
            FROM board_board
            WHERE is_active = 1
            ORDER BY created_at ASC
        """)
        List<BoardDTO> selectActiveBoards();

        @Select("""
            SELECT post_id, board_id, employee_id, title, content,
                   created_at, updated_at, view_count, like_count, is_deleted
            FROM board_post
            WHERE board_id = #{boardId}
              AND is_deleted = 0
            ORDER BY created_at DESC
            LIMIT #{size} OFFSET #{offset}
        """)
        List<BoardDTO> selectPostsByBoardId(@Param("boardId") Integer boardId,
                                            @Param("size") int size,
                                            @Param("offset") int offset);

        @Select("""
            SELECT COUNT(1)
            FROM board_post
            WHERE board_id = #{boardId}
              AND is_deleted = 0
        """)
        int countPostsByBoardId(@Param("boardId") Integer boardId);

}
