package com.kdt.KDT_PJT.api_p;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.boards.mapper.BoardMapper;
import com.kdt.KDT_PJT.boards.model.BoardDTO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
    BoardMapper boardMapper;

	
	@GetMapping("logInfo")
	ResponseEntity<?> get(HttpSession sesson) {
		
		System.out.println("/api/logInfo 진입");
		//return sesson.getAttribute("loginUser").toString();
		
		return ResponseEntity.ok(sesson.getAttribute("loginUser"));
	}
	
    /**
     * 🔷 메인페이지용: 최신 공지 목록
     * - 기본 5개, 최대 20개까지 제한
     * - Mapper는 그대로 사용: selectNoticePosts() (이미 최신순 정렬)
     */
    @GetMapping("/mainnotices")
    
    public ResponseEntity<List<BoardDTO>> getLatestNotices(
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        // 안전장치
        if (limit < 1) limit = 1;
        if (limit > 20) limit = 20;

        List<BoardDTO> all = boardMapper.selectNoticePosts(); // 최신순 정렬되어 있음
        if (all == null || all.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        int end = Math.min(limit, all.size());
        List<BoardDTO> sliced = all.subList(0, end);
        return ResponseEntity.ok(sliced);
    }
}

