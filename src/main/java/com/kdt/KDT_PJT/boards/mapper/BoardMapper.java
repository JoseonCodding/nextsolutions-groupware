package com.kdt.KDT_PJT.boards.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.kdt.KDT_PJT.boards.dto.BoardDTO;

@Mapper
public interface BoardMapper {
    @Insert("INSERT INTO board_post (title, content, author_id, pw, reg_date) " +
            "VALUES (#{title}, #{content}, #{pname}, #{pw}, NOW())")
    int insert(BoardDTO dto);

    @Select("SELECT * FROM board_post ORDER BY post_id DESC")
    List<BoardDTO> list();

    @Select("SELECT * FROM board_post WHERE post_id = #{id}")
    BoardDTO detail(int id);

    @Update("UPDATE board_post SET title=#{title}, content=#{content} WHERE post_id=#{id}")
    int update(BoardDTO dto);

    @Delete("DELETE FROM board_post WHERE post_id=#{id}")
    int delete(int id);
	
}
