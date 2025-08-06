package com.kdt.KDT_PJT.mapper;

import com.kdt.KDT_PJT.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("""
        SELECT * FROM comment
        WHERE post_id = #{postId}
        ORDER BY created_at ASC
    """)
    List<Comment> findByPostId(Long postId);

    @Insert("""
        INSERT INTO comment (post_id, author_id, content, created_at)
        VALUES (#{postId}, #{authorId}, #{content}, NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);
}
