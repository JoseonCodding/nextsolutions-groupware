package com.kdt.KDT_PJT.boards.di;

import com.kdt.KDT_PJT.boards.model.PageInfo;
import com.kdt.KDT_PJT.boards.model.BoardDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface MyAction {
    Object execute(PageInfo pInfo, BoardDTO dto, HttpServletRequest request);
}
