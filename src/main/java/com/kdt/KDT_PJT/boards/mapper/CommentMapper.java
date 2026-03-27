package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.CommentDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

		// 댓글 등록(기존 그대로)
	  @Insert("""
	    INSERT INTO board_comment (post_id, employee_id, parent_comment_id, content, is_deleted)
	    VALUES (#{postId}, #{employeeId}, #{parentCommentId}, #{content}, false)
	  """)
	  @Options(useGeneratedKeys = true, keyProperty = "commentId")
	  int insertComment(CommentDTO comment);
	  
	  // 본인 댓글만 삭제
	  @Update("""
			  UPDATE board_comment
			  SET is_deleted = 1
			  WHERE comment_id  = #{commentId}
			    AND employee_id = #{employeeId}
			    AND (is_deleted = 0 OR is_deleted IS NULL)
			""")
			int deleteByOwner(@Param("commentId") Long commentId,
			                  @Param("employeeId") String employeeId);
    
    //답글에는 답글 x
    @Select("SELECT COUNT(1)\r\n " +
    		"FROM board_comment\r\n " +
    		"WHERE comment_id = #{parentCommentId}\r\n " +
    		"AND post_id    = #{postId}\r\n " +
    		"AND parent_comment_id IS NULL\r\n " +
    		"AND is_deleted = false " )
    int replyComment(CommentDTO dto);

    // 삭제된 댓글도 포함해서 가져오기 (is_deleted 함께 반환)
    @Select("""
    		  SELECT c.comment_id,
    		         c.post_id,
    		         c.employee_id,
    		         c.parent_comment_id,
    		         c.content,
    		         c.created_at,
    		         (c.is_deleted = 1) AS is_deleted,
    		         EXISTS (
    		           SELECT 1
    		           FROM board_comment r
    		           WHERE r.parent_comment_id = c.comment_id
    		             AND (r.is_deleted = 0 OR r.is_deleted IS NULL)
    		         )                          AS has_child,
    		         e.emp_nm                   AS empNm
    		  FROM board_comment c
    		  LEFT JOIN employee e ON e.employeeId = c.employee_id
    		  WHERE c.post_id = #{postId}
    		  ORDER BY IFNULL(c.parent_comment_id, c.comment_id),
    		           CASE WHEN c.parent_comment_id IS NULL THEN 0 ELSE 1 END,
    		           c.created_at
    		""")
    		List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    // 단건 조회
    @Select("""
    		  SELECT comment_id, post_id, employee_id, parent_comment_id, content, created_at, is_deleted
    		  FROM board_comment
    		  WHERE comment_id = #{commentId}
    		""")
    CommentDTO selectCommentById(@Param("commentId") Long commentId);
    
    // 물리 삭제 → 소프트 삭제로 변경
    @Update("""
      UPDATE board_comment
      SET is_deleted = 1
      WHERE comment_id = #{commentId}
    """)
    int deleteComment(CommentDTO dto);
    
    // 삭제되지 않은(= is_deleted = false) 댓글 수
    @Select("""
        SELECT COUNT(*)
        FROM board_comment
        WHERE post_id = #{postId}
          AND is_deleted = FALSE
    """)
    int countAliveByPostId(@Param("postId") Long postId);



      // 댓글 수정
//    @Update("UPDATE board_comment SET content = #{content} WHERE comment_id = #{commentId}")
//    int updateCommentContent(@Param("commentId") Long commentId, @Param("content") String content);

//    //물리 삭제 => 데이터 삭제
//    @Update("delete from board_comment WHERE comment_id = #{commentId}")
//    int deleteComment(CommentDTO dto);
    
    
}