package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 1) 게시판 유형으로 board_id 찾기 — 단순 Integer 반환, 매핑 제거
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);


    // 2) 공통 결과 매핑 정의 (첫 번째 메서드에만 id 지정)
    @Results(id = "PostResultMap", value = {
        @Result(property = "id",        column = "post_id"),
        @Result(property = "boardId",   column = "board_id"),
        @Result(property = "authorId",  column = "author_id"),
        @Result(property = "title",     column = "title"),
        @Result(property = "content",   column = "content"),
        @Result(property = "regDate",   column = "created_at"),
        @Result(property = "updDate",   column = "updated_at"),
        @Result(property = "viewCnt",   column = "view_count"),
        @Result(property = "likeCnt",   column = "like_count"),
        @Result(property = "isDeleted", column = "is_deleted")
    })

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
    @ResultMap("PostResultMap")
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
    @ResultMap("PostResultMap")
    @Select("""
        SELECT *
          FROM board_post
         WHERE post_id = #{id}
           AND is_deleted = false
    """)
    BoardDTO detail(int id);


    // 6) 게시글 등록 (AUTO_INCREMENT 키 가져오기)
    @Insert("""
    	    INSERT INTO board_post
    	      (board_id, author_id, title, content, created_at, view_count, like_count, is_deleted)
    	    VALUES
    	      (#{boardId}, #{authorId}, #{title}, #{content}, NOW(), 0, 0, FALSE)
    	""")
    	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "post_id")
    	int insert(BoardDTO dto);

    
    // 7) 게시글 수정 (비밀번호 일치 시에만)
    @Update("""
        UPDATE board_post
           SET title      = #{title},
               content    = #{content},
               updated_at = NOW()
         WHERE post_id   = #{id}
    """)
    int modify(BoardDTO dto);


    // 8) 게시글 삭제 (soft delete)
    @Update("""
        UPDATE board_post
           SET is_deleted = true
         WHERE post_id   = #{id}
    """)
    int delete(BoardDTO dto);


    // 9) 자유게시판 게시글 총 개수
    @Select("""
        SELECT COUNT(*)
          FROM board_post p
          JOIN board_board b ON p.board_id = b.board_id
         WHERE b.board_type = 'free'
           AND p.is_deleted = false
    """)
    int totalCnt();
}
