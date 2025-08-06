package com.kdt.KDT_PJT.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_type")
@Data
@NoArgsConstructor
public class BoardType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "roles_allowed")
    private String rolesAllowed;

    @Column(name = "use_comments")
    private boolean useComments;

    @Column(name = "use_likes")
    private boolean useLikes;

    @OneToMany(mappedBy = "boardType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();
}
