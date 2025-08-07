package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 공통 결과 매핑
    @Results(id = "PostResultMap", value = {
        @Result(property = "id", column = "post_id"),
        @Result(property = "boardId", column = "board_id"),
        @Result(property = "authorId", column = "author_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "content", column = "content"),
        @Result(property = "pw", column = "pw"),
        @Result(property = "upfile", column = "upfile"),
        @Result(property = "regDate", column = "created_at"),
        @Result(property = "updDate", column = "updated_at"),
        @Result(property = "viewCnt", column = "view_count"),
        @Result(property = "likeCnt", column = "like_count"),
        @Result(property = "isDeleted", column = "is_deleted")
    })

    // 게시판 유형으로 board_id 찾기
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);


    // 공지사항 게시글 목록 조회
    @Select("""
        SELECT p.*
        FROM board_post p
        JOIN board_board b ON p.board_id = b.board_id
        WHERE b.board_type = 'notice'
          AND p.is_deleted = false
        ORDER BY p.post_id DESC
    """)
    @ResultMap("PostResultMap")
    List<BoardDTO> selectNoticePosts();


    // 자유게시판 게시글 목록 조회
    @Select("""
        SELECT p.*
        FROM board_post p
        JOIN board_board b ON p.board_id = b.board_id
        WHERE b.board_type = 'free'
          AND p.is_deleted = false
        ORDER BY p.post_id DESC
    """)
    @ResultMap("PostResultMap")
    List<BoardDTO> selectFreePosts();


    // 게시글 상세 조회 (삭제된 글은 조회 불가)
    @Select("""
        SELECT *
        FROM board_post
        WHERE post_id = #{id}
          AND is_deleted = false
    """)
    @ResultMap("PostResultMap")
    BoardDTO detail(int id);


    // 게시글 등록
    @Insert("""
        INSERT INTO board_post 
        (board_id, author_id, title, content, pw, upfile, created_at, view_count, like_count, is_deleted)
        VALUES 
        (#{boardId}, #{authorId}, #{title}, #{content}, #{pw}, #{upfile}, NOW(), 0, 0, false)
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "post_id")
    int insert(BoardDTO dto);


    // 게시글 수정 (비밀번호 일치 시에만)
    @Update("""
        UPDATE board_post
        SET title = #{title},
            content = #{content},
            upfile = #{upfile},
            updated_at = NOW()
        WHERE post_id = #{id}
          AND pw = #{pw}
    """)
    int modify(BoardDTO dto);


    // 게시글 삭제 (soft delete)
    @Update("""
        UPDATE board_post
        SET is_deleted = true
        WHERE post_id = #{id}
          AND pw = #{pw}
    """)
    int delete(BoardDTO dto);


    // 📌 자유게시판 게시글 총 개수
    @Select("""
        SELECT COUNT(*)
        FROM board_post p
        JOIN board_board b ON p.board_id = b.board_id
        WHERE b.board_type = 'free'
          AND p.is_deleted = false
    """)
    int totalCnt();
}
