package com.kdt.KDT_PJT.boards.di;

import org.springframework.stereotype.Component;

import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Component("insert") // Dispatcher에서 "insert"로 구분
public class BoardInsert implements MyAction {

    @Resource
    BoardMapper mapper;

    public Object execute(PageInfo pInfo, BoardDTO dto, HttpServletRequest request) {
        // mapper의 insert 메서드 호출
        int result = mapper.insert(dto);

        // 결과나 메시지를 request에 담아도 되고
        // 이후 redirect 등을 controller에서 처리 가능
        return "redirect:/board/list";
    }
}
