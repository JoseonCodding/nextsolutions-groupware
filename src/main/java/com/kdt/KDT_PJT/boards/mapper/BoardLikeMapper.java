package com.kdt.KDT_PJT.boards.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;


@Mapper
public interface BoardLikeMapper {
	
	/** 해당 게시글 좋아요 목록 */
    @Select("""
        SELECT like_id AS likeId,
               post_id AS postId,
               employee_id AS employeeId
        FROM board_like
        WHERE post_id = #{postId}
        ORDER BY like_id ASC
    """)
    List<BoardLikeDTO> selectLikesByPostId(@Param("postId") Long postId);

    /** 게시글 좋아요 수 */
    @Select("SELECT COUNT(*) FROM board_like WHERE post_id = #{postId}")
    int countByPostId(BoardLikeDTO dto);

    /** 해당 사용자가 이미 좋아요 눌렀는지 */
    @Select("""
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM board_like
            WHERE post_id = #{postId} AND employee_id = #{employeeId}
            """)
    boolean exists(BoardLikeDTO dto);

    /** 좋아요 추가 (UNIQUE(post_id,user_id)로 중복 방지) */
    @Insert("INSERT INTO board_like (post_id, employee_id) VALUES (#{postId}, #{employeeId})")
    @Options(useGeneratedKeys = true, keyProperty = "likeId")
    int insert(BoardLikeDTO dto);

    /** 좋아요 취소 */
    @Delete("DELETE FROM board_like WHERE post_id = #{postId} AND employee_id = #{employeeId}")
    int delete(BoardLikeDTO dto);
}
