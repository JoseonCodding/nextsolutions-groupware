package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.CommentDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

	//댓글 등록
    @Insert("INSERT INTO board_comment (post_id, employee_id, parent_comment_id, content, is_deleted) " +
            "VALUES (#{postId}, #{employeeId}, #{parentCommentId}, #{content}, FALSE)")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insertComment(CommentDTO comment);
    
    //답글에는 답글 x
    @Select("SELECT COUNT(1)\r\n " +
    		"FROM board_comment\r\n " +
    		"WHERE comment_id = #{parentCommentId}\r\n " +
    		"AND post_id    = #{postId}\r\n " +
    		"AND parent_comment_id IS NULL\r\n " +
    		"AND is_deleted = FALSE " )
    int replyComment(CommentDTO dto);

    //삭제되지 않은 댓글 목록 조회
    @Select("SELECT comment_id, post_id, employee_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE post_id = #{postId} AND is_deleted = FALSE ORDER BY " + 
    		"IFNULL(parent_comment_id, comment_id), CASE WHEN parent_comment_id IS NULL THEN 0 ELSE 1 END, " + 
            "created_at")
    List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    //댓글 조회?? 삭제여부 무관 
    @Select("SELECT comment_id, post_id, employee_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE comment_id = #{commentId}")
    int selectCommentById(CommentDTO dto);

//    @Update("UPDATE board_comment SET content = #{content} WHERE comment_id = #{commentId}")
//    int updateCommentContent(@Param("commentId") Long commentId, @Param("content") String content);

    //물리 삭제 => 데이터 삭제
    @Update("delete from board_comment WHERE comment_id = #{commentId}")
    int deleteComment(CommentDTO dto);
}