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

    //삭제되지 않은 댓글 목록 조회
    @Select("SELECT comment_id, post_id, employee_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE post_id = #{postId} AND is_deleted = FALSE ORDER BY created_at ASC")

    List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    //댓글 조회?? 삭제여부 무관 
    @Select("SELECT comment_id, post_id, employee_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE comment_id = #{commentId}")
    CommentDTO selectCommentById(@Param("commentId") Long commentId);

//    @Update("UPDATE board_comment SET content = #{content} WHERE comment_id = #{commentId}")
//    int updateCommentContent(@Param("commentId") Long commentId, @Param("content") String content);

    //물리 삭제 => 데이터 삭제
    @Update("delete from board_comment WHERE comment_id = #{commentId}")
    int deleteComment(CommentDTO dto);
}