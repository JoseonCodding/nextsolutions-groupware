package com.kdt.KDT_PJT.boards.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDTO {
	Long commentId;
    Long postId;
    String employeeId;
    String empNm;
    Long parentCommentId;
    String content;
    LocalDateTime createdAt;
    Boolean isDeleted;
    Boolean hasChild;
    Integer commentCount;
}
