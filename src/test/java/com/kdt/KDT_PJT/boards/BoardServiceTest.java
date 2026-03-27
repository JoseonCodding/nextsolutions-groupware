package com.kdt.KDT_PJT.boards;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kdt.KDT_PJT.boards.mapper.BoardLikeMapper;
import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.mapper.CommentMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.model.BoardLikeDTO;
import com.kdt.KDT_PJT.boards.service.BoardService;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock BoardMapper boardMapper;
    @Mock CommentMapper commentMapper;
    @Mock BoardLikeMapper likeMapper;

    @InjectMocks
    BoardService boardService;

    @Test
    @DisplayName("좋아요 없을 때 toggleLike → insert 호출")
    void toggleLike_insert() {
        BoardLikeDTO dto = new BoardLikeDTO();
        when(likeMapper.exists(dto)).thenReturn(false);

        boardService.toggleLike(dto);

        verify(likeMapper).insert(dto);
        verify(likeMapper, never()).delete(dto);
        verify(boardMapper).syncLikeCount(dto);
    }

    @Test
    @DisplayName("좋아요 있을 때 toggleLike → delete 호출")
    void toggleLike_delete() {
        BoardLikeDTO dto = new BoardLikeDTO();
        when(likeMapper.exists(dto)).thenReturn(true);

        boardService.toggleLike(dto);

        verify(likeMapper).delete(dto);
        verify(likeMapper, never()).insert(dto);
        verify(boardMapper).syncLikeCount(dto);
    }

    @Test
    @DisplayName("게시글 삭제 후 결과 반환")
    void deletePost_returnsCount() {
        BoardDTO dto = new BoardDTO();
        when(boardMapper.delete(dto)).thenReturn(1);

        int result = boardService.deletePost(dto);

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("increaseViewCount → upsertBoardDailyView 함께 호출")
    void increaseViewCount_alsoCalls_upsertDailyView() {
        BoardDTO dto = new BoardDTO();
        dto.setBoardId(1);

        boardService.increaseViewCount(dto);

        verify(boardMapper).increaseViewCount(dto);
        verify(boardMapper).upsertBoardDailyView(1);
    }

    @Test
    @DisplayName("댓글 목록 조회")
    void getComments() {
        when(commentMapper.selectCommentsByPostId(1L)).thenReturn(List.of());

        var result = boardService.getComments(1L);

        assertThat(result).isNotNull();
    }
}
