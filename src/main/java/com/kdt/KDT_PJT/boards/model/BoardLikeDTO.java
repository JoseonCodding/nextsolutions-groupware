package com.kdt.KDT_PJT.boards.model;

import lombok.Data;

@Data
public class BoardLikeDTO {
	Long likeId;      // PK
    Long postId;      // FK (board_post.post_id)
    String employeeId;    // FK (employee.employeeId)
    //private Date createdAt;   // 생성일 (자동 매핑)
}
