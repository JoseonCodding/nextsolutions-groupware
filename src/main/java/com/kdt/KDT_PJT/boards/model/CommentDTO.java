package com.kdt.KDT_PJT.boards.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDTO {
	Long commentId;
    Long postId;
    String authorId;
    Long parentCommentId;
    String content;
    LocalDateTime createdAt;
    Boolean isDeleted;
}
