package com.kdt.KDT_PJT.mapper;


import org.apache.ibatis.annotations.*;
import com.kdt.KDT_PJT.model.Board;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("""
      SELECT
        p.id,
        p.title,
        p.content,
        p.author_id   AS authorId,
        p.board_id    AS boardId,
        p.created_at  AS createdAt,
        p.view_count  AS viewCount,
        p.like_count  AS likeCount
      FROM post p
      ORDER BY p.created_at DESC
      """)
    @Results(id = "PostResultMap", value = {
      @Result(column = "id",        property = "id",        id = true),
      @Result(column = "title",     property = "title"),
      @Result(column = "content",   property = "content"),
      @Result(column = "authorId",  property = "authorId"),
      @Result(column = "boardId",   property = "boardId"),
      @Result(column = "createdAt", property = "createdAt"),
      @Result(column = "viewCount", property = "viewCount"),
      @Result(column = "likeCount", property = "likeCount")
    })
    List<Board> findAll();

    @Select("SELECT * FROM post WHERE id = #{id}")
    @ResultMap("PostResultMap")
    Board findById(Long id);

    @Insert("""
    		  INSERT INTO post(
    		    board_id,
    		    author_id,
    		    title,
    		    content,
    		    created_at,
    		    view_count,
    		    like_count
    		  ) VALUES (
    		    #{boardId},
    		    #{authorId},
    		    #{title},
    		    #{content},
    		    NOW(),
    		    0,
    		    0
    		  )
    		""")
    		@Options(useGeneratedKeys = true, keyProperty = "id")
    		void insert(Board post);

    @Delete("DELETE FROM post WHERE id = #{id}")
    void delete(Long id);
}
