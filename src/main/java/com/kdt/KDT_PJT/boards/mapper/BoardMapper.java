package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 1) 게시판 유형으로 board_id 찾기 — 단순 Integer 반환, 매핑 제거
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);

    
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
    	      (board_id, employee_id, title, content, created_at, view_count, like_count, is_deleted)
    	    VALUES
    	      (#{boardId}, #{employeeId}, #{title}, #{content}, NOW(), 0, 0, FALSE)
    	""")
    	@Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
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
    
    // 좋아요 증감형
    @Update("UPDATE board_post SET like_cnt = like_cnt + 1 WHERE post_id = #{postId}")
    int incrementLikeCnt(BoardLikeDTO dto);

    @Update("UPDATE board_post SET like_cnt = CASE WHEN like_cnt > 0 THEN like_cnt - 1 ELSE 0 END WHERE post_id = #{postId}")
    int decrementLikeCnt(BoardLikeDTO dto);
}
