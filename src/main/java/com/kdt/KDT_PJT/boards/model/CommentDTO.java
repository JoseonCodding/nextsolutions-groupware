package com.kdt.KDT_PJT.boards.model;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDTO {
	Long    commentId;
    Long    postId;
    String  authorId;
    Long    parentCommentId;
    String  content;
    Date    createdAt;
    Boolean isDeleted;
}
