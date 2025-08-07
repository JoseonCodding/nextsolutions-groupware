package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.CommentDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("""
      SELECT comment_id, post_id, author_id, parent_comment_id, content, created_at, is_deleted
        FROM board_comment
       WHERE post_id = #{postId}
         AND is_deleted = false
       ORDER BY created_at ASC
    """)
    @Results(id = "CommentResultMap", value = {
        @Result(property = "commentId",       column = "comment_id",        id = true),
        @Result(property = "postId",          column = "post_id"),
        @Result(property = "authorId",        column = "author_id"),
        @Result(property = "parentCommentId", column = "parent_comment_id"),
        @Result(property = "content",         column = "content"),
        @Result(property = "createdAt",       column = "created_at"),
        @Result(property = "isDeleted",       column = "is_deleted")
    })
    List<CommentDTO> selectByPostId(@Param("postId") Long postId);

    @Insert("""
      INSERT INTO board_comment
        (post_id, author_id, parent_comment_id, content)
      VALUES
        (#{postId}, #{authorId}, #{parentCommentId}, #{content})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "commentId", keyColumn = "comment_id")
    int insert(CommentDTO comment);

    @Update("""
      UPDATE board_comment
         SET is_deleted = true
       WHERE comment_id = #{commentId}
    """)
    int softDelete(@Param("commentId") Long commentId);
}
