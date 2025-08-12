package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
	
	// (공통) 게시판 유형으로 board_id 찾기
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);
    
    // 댓글/좋아요/글쓰기 차단할 때 필요
    @Select("SELECT board_id FROM board_post WHERE post_id = #{postId}")
    Long findBoardIdByPostId(@Param("postId") Long postId);

	/// ========= 커스텀 =========
    @Select("""
      SELECT board_type
      FROM board_board
      WHERE board_id = #{boardId} AND is_active = TRUE
    """)
    String findBoardTypeById(@Param("boardId") Integer boardId);
    
    @Select("""
    		  <script>
    		  SELECT p.post_id, p.board_id, p.employee_id, p.title, p.content,
    		         p.created_at, p.updated_at, p.view_count, p.like_count, p.is_deleted,
    		         e.emp_nm AS empNm
    		  FROM board_post p
    		  LEFT JOIN employee e ON employeeId = p.employee_id
    		  WHERE p.board_id = #{boardId}
    		    AND p.is_deleted = false
    		  ORDER BY COALESCE(p.updated_at, p.created_at) DESC, p.post_id DESC
    		  LIMIT #{limit} OFFSET #{offset}
    		  </script>
    		""")
    		List<BoardDTO> selectCustomPosts(BoardDTO dto);
    
    @Select("""
    		  SELECT p.post_id, p.board_id, p.employee_id, p.title, p.content,
    		         p.created_at, p.updated_at, p.view_count, p.like_count, p.is_deleted,
    		         e.emp_nm AS empNm
    		  FROM board_post p
    		  LEFT JOIN employee e ON e.employeeId = p.employee_id
    		  WHERE p.post_id = #{postId} AND p.is_deleted = false
    		""")
    		BoardDTO selectPostById(@Param("postId") Integer postId);
    
    // 총 건수
    @Select("""
      SELECT COUNT(*)
      FROM board_post
      WHERE board_id = #{boardId} AND is_deleted = FALSE
    """)
    int customTotalCnt(@Param("boardId") Integer boardId);



    
    /// ========= 공지(보드=1) =========
    // 공지 목록 (완료만) - 페이징
    @Select("""
            <script>
            SELECT p.*, e.emp_nm AS emp_nm FROM board_post p
    		LEFT JOIN employee e ON e.employeeId = p.employee_id
            WHERE p.board_id=1 AND p.status='완료' AND p.is_deleted=false
            ORDER BY COALESCE(p.published_at,p.updated_at,p.created_at) DESC, p.post_id DESC
            LIMIT <choose><when test="limit != null">#{limit}</when><otherwise>10</otherwise></choose>
            OFFSET <choose><when test="offset != null">#{offset}</when><otherwise>0</otherwise></choose>
            </script>
          """)
          List<BoardDTO> selectNoticePosts(BoardDTO dto);
    
    @Select("""
            SELECT COUNT(*) FROM board_post p
            WHERE p.board_id=1 AND p.status='완료' AND p.is_deleted=false
          """)
          int noticeTotalCnt();

    // 공지 상세 (board_id=1, 완료만)
    @Select("""
           SELECT p.* FROM board_post p
           WHERE p.post_id=#{postId} AND p.board_id=1 AND p.status='완료' AND p.is_deleted=false
         """)
    BoardDTO findNoticeApprovedById(BoardDTO dto);

    // 초안(대기) 저장
    @Insert("""
      INSERT INTO board_post
        (board_id, employee_id, title, content, created_at, updated_at, view_count, like_count, is_deleted, status, docType)
      VALUES
        (1, #{employeeId}, #{title}, #{content}, NOW(), NOW(), 0, 0, FALSE, '대기', '공지사항')
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
            SELECT p.*, e.emp_nm AS emp_nm FROM board_post p
    		LEFT JOIN employee e ON e.employeeId = p.employee_id
            WHERE p.board_id=2 AND p.is_deleted=false
            ORDER BY p.post_id DESC
            LIMIT <choose><when test="limit != null">#{limit}</when><otherwise>10</otherwise></choose>
            OFFSET <choose><when test="offset != null">#{offset}</when><otherwise>0</otherwise></choose>
            </script>
          """)
          List<BoardDTO> selectFreePosts(BoardDTO dto);
    
    @Select("SELECT COUNT(*) FROM board_post p WHERE p.board_id=2 AND p.is_deleted=false")
    int freeTotalCnt();

    // 게시글 상세 조회
    @Select("""
           SELECT p.post_id AS postId, p.board_id AS boardId, p.employee_id AS employeeId,
                  p.title, p.content, p.created_at AS createdAt, p.updated_at AS updatedAt,
                  p.view_count AS viewCount, p.like_count AS likeCount, p.is_deleted AS isDeleted
           FROM board_post p
           WHERE p.post_id = #{postId} AND p.is_deleted = false
         """)
    BoardDTO detail(BoardDTO dto);

    // 게시글 등록
    @Insert("""
           INSERT INTO board_post
             (board_id, employee_id, title, content, created_at, view_count, like_count, is_deleted)
           VALUES
             (#{boardId}, #{employeeId}, #{title}, #{content}, NOW(), 0, 0, false)
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
    
    // 관리자(보드타입 무관) 삭제
    @Update("""
      UPDATE board_post
      SET is_deleted = TRUE, updated_at = NOW()
      WHERE post_id = #{postId}
    """)
    int adminDelete(@Param("postId") Integer postId);
    
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
    

    /************* 관리자 ****************/
    // 전체 목록 (관리자 화면)
    @Select("""
        SELECT board_id, board_name, board_type, access_role,
               use_comment, use_like, is_active, created_at, updated_at
        FROM board_board
        ORDER BY board_id DESC
    """)
    List<BoardDTO> selectBoards();

    // 활성 탭용
    @Select("""
        SELECT board_id, board_name, board_type, access_role,
               use_comment, use_like, is_active, created_at, updated_at
        FROM board_board
        WHERE is_active = TRUE
        ORDER BY board_id ASC
    """)
    List<BoardDTO> selectActiveBoards();

    // 단건 조회
    @Select("""
        SELECT board_id, board_name, board_type, access_role,
               use_comment, use_like, is_active, created_at, updated_at
        FROM board_board
        WHERE board_id = #{boardId}
    """)
    BoardDTO selectBoardById(Integer boardId);

    // 생성: NOTICE/FREE는 미리 있으니 커스텀만 생성
    @Insert("""
        INSERT INTO board_board
          (board_name, board_type, access_role, use_comment, use_like, is_active, created_at, updated_at)
        VALUES
          (#{boardName}, 'CUSTOM', #{accessRole}, #{useComment}, #{useLike}, TRUE, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "boardId")
    int insertBoard(BoardDTO b);

    // 수정(공지 NOTICE는 컨트롤러에서 차단)
    @Update("""
        UPDATE board_board
        SET board_name = #{boardName},
            access_role = #{accessRole},
            use_comment = #{useComment},
            use_like = #{useLike},
            updated_at = NOW()
        WHERE board_id = #{boardId}
    """)
    int updateBoard(BoardDTO b);

    // 커스텀 보드 생성
    @Select("""
            SELECT board_id,
                   board_name,
                   COALESCE(board_type,'custom') AS board_type,
                   access_role, use_comment, use_like, is_active, created_at, updated_at
            FROM board_board
            WHERE is_active = TRUE
            ORDER BY CASE UPPER(COALESCE(board_type,'CUSTOM'))
                       WHEN 'NOTICE' THEN 0
                       WHEN 'FREE'   THEN 1
                       ELSE 2
                     END,
                     board_name
          """)
          List<BoardDTO> selectAllBoardsForTabs();
    
    // 게시판 활성/비활성
    @Update("UPDATE board_board SET is_active = #{active} WHERE board_id = #{boardId}")
    int updateBoardActive(@Param("boardId") Integer boardId, @Param("active") Integer active);
    
    // (옵션) 통계
    @Select("""
      SELECT s.stat_id, s.board_id, b.board_name, s.view_date, s.view_count
      FROM board_view_stats s
      JOIN board_board b ON b.board_id = s.board_id
      ORDER BY s.view_date DESC, s.board_id ASC
    """)
    List<Map<String,Object>> selectBoardViewStats();

}