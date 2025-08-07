package com.kdt.KDT_PJT.boards.model;

import lombok.Data;

@Data
public class BoardLikeDTO {
	private Long likeId;      // PK
    private Long postId;      // FK (board_post.post_id)
    private String userId;    // FK (employee.employeeId)
    //private Date createdAt;   // 생성일 (자동 매핑)
}
