package com.kdt.KDT_PJT.boards.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_post")
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "author_id", nullable = false, length = 50)
    private String authorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // 🔽 MyBatis나 뷰 전용 필드
    @Transient
    private String authorName;
}