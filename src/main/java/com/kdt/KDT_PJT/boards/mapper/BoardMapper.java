package com.kdt.KDT_PJT.boards.mapper;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    // (공통) 게시판 유형으로 board_id 찾기
    @Select("SELECT board_id FROM board_board WHERE board_type = #{boardType}")
    Integer findBoardIdByType(@Param("boardType") String boardType);

    
    /// ========= 공지(보드=1) =========
    @Select("""
      SELECT p.* FROM board_post p
      WHERE p.board_id=1 AND p.status='완료' AND p.is_deleted=FALSE
      ORDER BY COALESCE(p.published_at,p.updated_at,p.created_at) DESC, p.post_id DESC
    """)
    List<BoardDTO> selectNoticePosts();

    // 공지 상세 (board_id=1, 완료만)
    @Select("""
    	    SELECT p.* FROM board_post p
    	    WHERE p.post_id=#{postId} AND p.board_id=1 AND p.status='완료' AND p.is_deleted=FALSE
    	  """)
    BoardDTO findNoticeApprovedById(BoardDTO dto);

    
    // 초안(대기) 저장
    @Insert("""
      INSERT INTO board_post
        (board_id, employee_id, title, content, created_at, updated_at, view_count, like_count, is_deleted, status, docType)
      VALUES
        (1, #{employeeId}, #{title}, #{content}, NOW(), NOW(), 0, 0, FALSE, '대기', '공지사항')
    """)
    @Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    int insertNoticeDraft(BoardDTO dto);

    // 승인: 대기 -> 완료
    @Update("""
      UPDATE board_post
         SET status='완료', published_at=NOW(), updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND status='대기'
         AND is_deleted=FALSE
    """)
    int approveNotice(BoardDTO dto);

    // 반려: 대기 -> 반려
    @Update("""
      UPDATE board_post
         SET status='반려', updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND status='대기'
         AND is_deleted=FALSE
    """)
    int rejectNotice(BoardDTO dto);

    // 공지 삭제(관리자용)
    @Update("""
      UPDATE board_post
         SET is_deleted=TRUE, updated_at=NOW()
       WHERE post_id=#{postId}
         AND board_id=1
         AND is_deleted=FALSE
    """)
    int adminDeleteNotice(BoardDTO dto);
    
    // 공지 상세 진입 시 조회수 +1
    @Update("UPDATE board_post SET view_count = view_count + 1 WHERE post_id = #{postId} AND board_id=1 AND status='완료'")
    int increaseNoticeView(BoardDTO dto);

    // 공지 일자별 뷰 통계 업서트
    @Insert("""
      INSERT INTO board_view_stats (board_id, view_date, view_count)
      VALUES (1, CURDATE(), 1)
      ON DUPLICATE KEY UPDATE view_count = view_count + 1
    """)
    int bumpNoticeDailyView();


    /// ========= 자유(보드=2) =========
    @Select("""
      SELECT p.* FROM board_post p
      WHERE p.board_id = 2 AND p.is_deleted = FALSE
      ORDER BY p.post_id DESC
    """)
    List<BoardDTO> selectFreePosts();

    // 게시글 상세 조회
    @Select("""
    	    SELECT p.post_id AS postId, p.board_id AS boardId, p.employee_id AS employeeId,
    	           p.title, p.content, p.created_at AS createdAt, p.updated_at AS updatedAt,
    	           p.view_count AS viewCount, p.like_count AS likeCount, p.is_deleted AS isDeleted
    	    FROM board_post p
    	    WHERE p.post_id = #{postId} AND p.is_deleted = FALSE
    	  """)
    BoardDTO detail(BoardDTO dto);

    // 게시글 등록
    @Insert("""
    	    INSERT INTO board_post
    	      (board_id, employee_id, title, content, created_at, view_count, like_count, is_deleted)
    	    VALUES
    	      (#{boardId}, #{employeeId}, #{title}, #{content}, NOW(), 0, 0, FALSE)
    	  """)
    	  @Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    int insert(BoardDTO dto);

    // 게시글 수정
    @Update("""
    	    UPDATE board_post
    	       SET title=#{title}, content=#{content}, updated_at=NOW()
    	     WHERE post_id=#{postId} AND employee_id=#{employeeId} AND is_deleted=FALSE
    	  """)
    	  int modify(BoardDTO dto);

    // 작성자 본인 삭제
    @Update("""
      UPDATE board_post
         SET is_deleted=TRUE, updated_at=NOW()
       WHERE post_id=#{postId}
         AND (is_deleted=FALSE OR is_deleted IS NULL)
         AND employee_id=#{employeeId}
    """)
    int delete(BoardDTO dto);
    
    // 자유게시판 관리자 삭제(소유자 무시)
    @Update("""
      UPDATE board_post
         SET is_deleted = TRUE, updated_at = NOW()
       WHERE post_id = #{postId}
         AND board_id = 2
         AND (is_deleted = FALSE OR is_deleted IS NULL)
    """)
    int adminDeleteFree(BoardDTO dto);
    
    // 자유게시판 게시글 총 개수
    @Select("""
    	    SELECT COUNT(*)
    	    FROM board_post p
    	    WHERE p.board_id = 2 AND p.is_deleted = FALSE
    	  """)
    	  int totalCnt();
    
    // 조회수(자유)
    @Options(flushCache = Options.FlushCachePolicy.TRUE, useCache = false)
    @Update("UPDATE board_post SET view_count = view_count + 1 WHERE post_id = #{postId}")
    int increaseViewCount(BoardDTO dto);
    
    // 최초 1회 조회 기록(자유)
    @Insert("""
      INSERT IGNORE INTO board_post_view(post_id, employee_id, viewed_at)
      VALUES (#{postId}, #{employeeId}, NOW())
    """)
    int recordView(BoardDTO dto);

    
    // 좋아요 카운트 증감
    @Update("UPDATE board_post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    int incrementLikeCount(BoardLikeDTO dto);

    // 좋아요: 실제 테이블 기준 동기화만 사용
    @Update("""
      UPDATE board_post p
         SET like_count = (SELECT COUNT(*) FROM board_like l WHERE l.post_id = p.post_id)
       WHERE p.post_id = #{postId}
    """)
    int syncLikeCount(BoardLikeDTO dto);
     
}
