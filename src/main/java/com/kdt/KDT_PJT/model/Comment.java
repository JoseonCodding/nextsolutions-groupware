package com.kdt.KDT_PJT.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long authorId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdAt;
}
