package com.kdt.KDT_PJT.boards.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.KDT_PJT.boards.mapper.BoardLikeMapper;
import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.mapper.CommentMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;
import com.kdt.KDT_PJT.boards.model.CommentDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;
    private final BoardLikeMapper likeMapper;

    // ===== 게시판 메타 =====

    public List<BoardDTO> getActiveBoards(Integer companyId) {
        return boardMapper.selectActiveBoards(companyId);
    }

    public List<BoardDTO> getAllBoards(Integer companyId) {
        return boardMapper.selectBoards(companyId);
    }

    public int countBoards(BoardDTO dto) {
        return boardMapper.countBoards(dto);
    }

    public List<BoardDTO> getAllBoardsForTabs(Integer companyId) {
        return boardMapper.selectAllBoardsForTabs(companyId);
    }

    public BoardDTO getBoardById(Integer boardId) {
        return boardMapper.selectBoardById(boardId);
    }

    public String getBoardType(Integer boardId) {
        return boardMapper.findBoardTypeById(boardId);
    }

    public Integer findBoardIdByType(String boardType, Integer companyId) {
        return boardMapper.findBoardIdByType(boardType, companyId);
    }

    @Transactional
    public void createBoard(BoardDTO dto) {
        boardMapper.insertBoard(dto);
    }

    @Transactional
    public void updateBoard(BoardDTO dto) {
        boardMapper.updateBoard(dto);
    }

    @Transactional
    public void setBoardActive(Integer boardId, Integer active) {
        boardMapper.updateBoardActive(boardId, active);
    }

    @Transactional
    public void deleteBoard(Integer boardId) {
        boardMapper.softDeleteBoard(boardId);
    }

    // ===== 공지 게시판 =====

    public List<BoardDTO> getNoticePosts(BoardDTO dto) {
        return boardMapper.selectNoticePosts(dto);
    }

    public int getNoticeTotalCount(BoardDTO dto) {
        return boardMapper.noticeTotalCnt(dto);
    }

    public BoardDTO getNoticeDetail(BoardDTO dto) {
        return boardMapper.findNoticeApprovedById(dto);
    }

    @Transactional
    public int saveNoticeDraft(BoardDTO dto) {
        return boardMapper.insertNoticeDraft(dto);
    }

    @Transactional
    public int approveNotice(BoardDTO dto) {
        return boardMapper.approveNotice(dto);
    }

    @Transactional
    public int rejectNotice(BoardDTO dto) {
        return boardMapper.rejectNotice(dto);
    }

    @Transactional
    public void increaseNoticeView(BoardDTO dto, Integer noticeBoardId) {
        boardMapper.increaseNoticeView(dto);
        if (noticeBoardId != null) boardMapper.upsertBoardDailyView(noticeBoardId);
    }

    @Transactional
    public void deleteNotice(BoardDTO dto) {
        boardMapper.adminDeleteNotice(dto);
    }

    // ===== 자유 게시판 =====

    public List<BoardDTO> getFreePosts(BoardDTO dto) {
        return boardMapper.selectFreePosts(dto);
    }

    public int getFreeTotalCount(BoardDTO dto) {
        return boardMapper.freeTotalCnt(dto);
    }

    public BoardDTO getFreeDetail(BoardDTO dto) {
        return boardMapper.detail(dto);
    }

    // ===== 커스텀 게시판 =====

    public List<BoardDTO> getCustomPosts(BoardDTO dto) {
        return boardMapper.selectCustomPosts(dto);
    }

    public int getCustomTotalCount(BoardDTO dto) {
        return boardMapper.customTotalCnt(dto);
    }

    public BoardDTO getCustomPostById(Integer postId) {
        return boardMapper.selectPostById(postId);
    }

    // ===== 게시글 CRUD (자유/커스텀 공통) =====

    @Transactional
    public void savePost(BoardDTO dto) {
        boardMapper.insert(dto);
    }

    @Transactional
    public int modifyPost(BoardDTO dto) {
        return boardMapper.modify(dto);
    }

    @Transactional
    public int deletePost(BoardDTO dto) {
        return boardMapper.delete(dto);
    }

    @Transactional
    public void adminDeletePost(Integer postId) {
        boardMapper.adminDelete(postId);
    }

    // ===== 조회수 =====

    @Transactional
    public int recordView(BoardDTO dto) {
        return boardMapper.recordView(dto);
    }

    @Transactional
    public void increaseViewCount(BoardDTO dto) {
        boardMapper.increaseViewCount(dto);
        boardMapper.upsertBoardDailyView(dto.getBoardId());
    }

    // ===== 좋아요 =====

    public int getLikeCount(BoardLikeDTO dto) {
        return likeMapper.countByPostId(dto);
    }

    public boolean isLikedByMe(BoardLikeDTO dto) {
        return likeMapper.exists(dto);
    }

    @Transactional
    public void syncLikeCount(BoardLikeDTO dto) {
        boardMapper.syncLikeCount(dto);
    }

    // ===== 댓글 =====

    public List<CommentDTO> getComments(Long postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    public int getCommentCount(Long postId) {
        return commentMapper.countAliveByPostId(postId);
    }

    @Transactional
    public void insertComment(CommentDTO dto) {
        commentMapper.insertComment(dto);
    }

    @Transactional
    public int deleteComment(Long commentId, String employeeId) {
        return commentMapper.deleteByOwner(commentId, employeeId);
    }

    // ===== 좋아요 토글 (insert/delete + sync) =====

    @Transactional
    public void toggleLike(BoardLikeDTO dto) {
        if (likeMapper.exists(dto)) {
            likeMapper.delete(dto);
        } else {
            likeMapper.insert(dto);
        }
        boardMapper.syncLikeCount(dto);
    }

    // ===== 통계 (관리자) =====

    public long getTodayViews(Integer boardId) {
        return boardMapper.selectTodayViews(boardId);
    }

    public long getTodayLikes(Integer boardId) {
        return boardMapper.selectTodayLikes(boardId);
    }

    public long getTodayPosts(Integer boardId) {
        return boardMapper.selectTodayPosts(boardId);
    }
}
