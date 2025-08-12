package com.kdt.KDT_PJT.boards.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.HandlerMapping;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;

import jakarta.servlet.http.HttpServletRequest; // ※ Spring Boot 2.x 사용 시 javax.servlet 로 변경
import lombok.RequiredArgsConstructor;

@ControllerAdvice(basePackages = "com.kdt.KDT_PJT")
@RequiredArgsConstructor
public class BoardTabsAdvice {

    private final BoardMapper boardMapper;

    /** 상단 탭에 뿌릴 전체 활성 게시판(공지/자유/커스텀) */
    @ModelAttribute("allBoards")
    public List<BoardDTO> allBoards() {
        return boardMapper.selectAllBoardsForTabs();
    }

    /**
     * 현재 요청의 URI나 path variable을 보고 활성 탭(boardId)을 자동 결정.
     * - /board/{boardId} → pathVar 사용
     * - /board/notice, /board/free → 타입으로 board_id 조회
     * - 기타 화면 → null(하이라이트 없음)
     */
    @ModelAttribute("activeBoardId")
    public Integer activeBoardId(HttpServletRequest req) {
        // 1) /board/{boardId} 형태: path variable 우선
        @SuppressWarnings("unchecked")
        Map<String, String> uriVars = (Map<String, String>)
                req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (uriVars != null && uriVars.containsKey("boardId")) {
            try {
                return Integer.valueOf(uriVars.get("boardId"));
            } catch (NumberFormatException ignore) {
                // 무시하고 URI로 판별
            }
        }

        // 2) 고정 라우트: /board/notice, /board/free
        String uri = req.getRequestURI().toLowerCase(Locale.ROOT);
        if (uri.startsWith("/board/notice")) {
            return boardMapper.findBoardIdByType("notice");
        }
        if (uri.startsWith("/board/free")) {
            return boardMapper.findBoardIdByType("free");
        }

        // 3) 그 외(탭 하이라이트 없음)
        return null;
    }
}
