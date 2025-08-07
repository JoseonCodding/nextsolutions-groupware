package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.CommentDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO board_comment (post_id, author_id, parent_comment_id, content, is_deleted) " +
            "VALUES (#{postId}, #{authorId}, #{parentCommentId}, #{content}, FALSE)")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insertComment(CommentDTO comment);

    @Select("SELECT comment_id, post_id, author_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE post_id = #{postId} AND is_deleted = FALSE ORDER BY created_at ASC")
    @Results(id = "CommentResult", value = {
        @Result(column = "comment_id", property = "commentId"),
        @Result(column = "post_id", property = "postId"),
        @Result(column = "author_id", property = "authorId"),
        @Result(column = "parent_comment_id", property = "parentCommentId"),
        @Result(column = "content", property = "content"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "is_deleted", property = "isDeleted")
    })
    List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    @Select("SELECT comment_id, post_id, author_id, parent_comment_id, content, created_at, is_deleted " +
            "FROM board_comment WHERE comment_id = #{commentId}")
    @ResultMap("CommentResult")
    CommentDTO selectCommentById(@Param("commentId") Long commentId);

    @Update("UPDATE board_comment SET content = #{content} WHERE comment_id = #{commentId}")
    int updateCommentContent(@Param("commentId") Long commentId, @Param("content") String content);

    /**
     * 논리삭제: is_deleted = TRUE
     */
    @Update("delete from board_comment WHERE comment_id = #{commentId}")
    int deleteComment(CommentDTO dto);
}