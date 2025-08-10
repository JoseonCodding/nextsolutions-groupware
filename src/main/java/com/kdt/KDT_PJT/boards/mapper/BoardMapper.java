package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 1) 게시판 유형으로 board_id 찾기 — 단순 Integer 반환, 매핑 제거
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);

    
    // 3) 공지사항 게시글 목록 조회
    @Select("""
        SELECT p.*
          FROM board_post p
          JOIN board_board b ON p.board_id = b.board_id
         WHERE b.board_type = 'notice'
           AND p.is_deleted = false
         ORDER BY p.post_id DESC
    """)
    List<BoardDTO> selectNoticePosts();


    // 4) 자유게시판 게시글 목록 조회
   
    @Select("""
        SELECT p.*
          FROM board_post p
          JOIN board_board b ON p.board_id = b.board_id
         WHERE b.board_type = 'free'
           AND p.is_deleted = false
         ORDER BY p.post_id DESC
    """)
    List<BoardDTO> selectFreePosts();


    // 5) 게시글 상세 조회 (삭제된 글은 조회 불가)
    
    @Select("""
    	    SELECT
    	      p.post_id     AS postId,
    	      p.board_id    AS boardId,
    	      p.employee_id AS employeeId,
    	      p.title,
    	      p.content,
    	      p.created_at  AS createdAt,
    	      p.updated_at  AS updatedAt,
    	      p.view_count  AS viewCount,
    	      p.like_count  AS likeCount,
    	      p.is_deleted  AS isDeleted
    	    FROM board_post p
    	    WHERE p.post_id = #{postId}
    	      AND p.is_deleted = FALSE
    	""")
    	BoardDTO detail(BoardDTO dto);



    // 6) 게시글 등록 (AUTO_INCREMENT 키 가져오기)
    @Insert("""
    	    INSERT INTO board_post
    	      (board_id, employee_id, title, content, created_at, view_count, like_count, is_deleted)
    	    VALUES
    	      (#{boardId}, #{employeeId}, #{title}, #{content}, NOW(), 0, 0, FALSE)
    	""")
    	@Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    	int insert(BoardDTO dto);

    
    // 7) 게시글 수정 (비밀번호 일치 시에만)
    @Update("""
        UPDATE board_post
           SET title      = #{title},
               content    = #{content},
               updated_at = NOW()
         WHERE post_id   = #{postId}
            AND employee_id = #{employeeId}
    		AND is_deleted = FALSE
    """)
    int modify(BoardDTO dto);


    // 8) 게시글 삭제 (soft delete)
    @Update("""
    		  UPDATE board_post
    		     SET is_deleted = TRUE,
    		         updated_at = NOW()
    		   WHERE post_id = #{postId}
    		     AND (is_deleted = FALSE OR is_deleted IS NULL)   -- ✅ NULL도 포함
    		     AND employee_id = #{employeeId}                  -- 권한자 없으면 이 조건만
    		""")
    		int delete(BoardDTO dto);
    
    @Select("""
    		 SELECT p.post_id, p.board_id, p.employee_id, p.title, p.content,
    		        p.created_at, p.updated_at, p.view_count, p.like_count, p.is_deleted
    		   FROM board_post p
    		  WHERE p.post_id = #{postId}
    		    AND p.is_deleted = FALSE
    		""")
    		BoardDTO deleteCommentsByPost(BoardDTO dto);


    // 9) 자유게시판 게시글 총 개수
    @Select("""
        SELECT COUNT(*)
          FROM board_post p
          JOIN board_board b ON p.board_id = b.board_id
         WHERE b.board_type = 'free'
           AND p.is_deleted = false
    """)
    int totalCnt();
    
 // 조회수 +1 (BoardDTO로 받기)
    @Options(flushCache = Options.FlushCachePolicy.TRUE, useCache = false)
    @Update("""
      UPDATE board_post
         SET view_count = view_count + 1
       WHERE post_id = #{postId}
    """)
    int increaseViewCount(BoardDTO dto);

    // 좋아요 카운트 증감 (원하면 사용) — BoardLikeDTO로 받기
    @Update("UPDATE board_post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    int incrementLikeCount(BoardLikeDTO dto);

    @Update("""
        UPDATE board_post
           SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END
         WHERE post_id = #{postId}
    """)
    int decrementLikeCount(BoardLikeDTO dto);

    // ★ 실제 board_like 기준으로 동기화 — 이것만 호출해도 충분
    @Update("""
    	    UPDATE board_post p
    	       SET like_count = (SELECT COUNT(*) FROM board_like l WHERE l.post_id = p.post_id)
    	     WHERE p.post_id = #{postId}
    	""")
    int syncLikeCount(BoardLikeDTO dto);
     
}
