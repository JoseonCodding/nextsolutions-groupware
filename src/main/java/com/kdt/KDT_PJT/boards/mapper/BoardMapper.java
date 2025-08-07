package com.kdt.KDT_PJT.boards.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardInfoDTO;

@Mapper
public interface BoardMapper {

    // ✅ 게시글 등록 (입력용 DTO)
    @Insert("INSERT INTO board_post (title, content, author_id, reg_date) " +
            "VALUES (#{title}, #{content}, #{pname}, NOW())")
    int insert(BoardDTO dto);

    // ✅ 게시글 리스트 (간단한 DTO)
    @Select("SELECT * FROM board_post ORDER BY post_id DESC")
    List<BoardDTO> list();

    // ✅ 게시판 정보 리스트
    @Select("SELECT * FROM board_board")
    List<BoardInfoDTO> boardList();

    // ✅ 게시글 상세 조회
    @Select("SELECT * FROM board_post WHERE post_id = #{id}")
    BoardDTO detail(int id);

    // ✅ 게시글 수정
    @Update("UPDATE board_post SET title=#{title}, content=#{content} WHERE post_id=#{id}")
    int update(BoardDTO dto);

    // ✅ 게시글 삭제
    @Delete("DELETE FROM board_post WHERE post_id=#{id}")
    int delete(int id);

   
}
