package com.kdt.KDT_PJT.api_p;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.model.ScheduleMapper;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiBoardController {
	
	@Autowired
    BoardMapper boardMapper;


	
	@GetMapping("notices")
	Object schedules(HttpSession sesson) {
		BoardDTO dto = new BoardDTO();
		int size = (dto.getSize()!=null && dto.getSize()>0) ? dto.getSize() : 5;
        int page = (dto.getPage()!=null && dto.getPage()>0) ? dto.getPage() : 1;
        dto.setLimit(size);
        dto.setOffset((page-1)*size);
        dto.setSort(dto.sortOrDefault());

        List<BoardDTO> res = boardMapper.selectNoticePosts(dto);
		
		System.out.println("/api/notices 진입 : "+ res);
		return res;
		
		
	}
	
}