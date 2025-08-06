package com.kdt.KDT_PJT.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private Long authorId;

    private LocalDateTime createdAt;
    private int viewCount;
    private int likeCount;

    // ✅ JPA용 연관관계: board_id → BoardType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardType boardType;

    // ✅ MyBatis용 필드: 단순 board_id 값만 전달
    @Transient
    private Long boardId;
    
    @Transient
    private String authorName;

}
