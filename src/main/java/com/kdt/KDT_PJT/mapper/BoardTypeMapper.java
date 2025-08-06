package com.kdt.KDT_PJT.mapper;

import com.kdt.KDT_PJT.model.BoardType;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BoardTypeMapper {
    @Select("SELECT * FROM board_type ORDER BY id")
    List<BoardType> findAll();

    @Select("SELECT * FROM board_type WHERE id = #{id}")
    BoardType findById(Long id);

    @Insert("""
      INSERT INTO board_type(
        name,
        roles_allowed,
        use_comments,
        use_likes,
        is_active
      )
      VALUES(
        #{name},
        #{rolesAllowed},
        #{useComments},
        #{useLikes},
        #{isActive}
      )
      """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(BoardType bt);

    @Update("""
      UPDATE board_type
      SET name = #{name},
          roles_allowed = #{rolesAllowed},
          use_comments = #{useComments},
          use_likes = #{useLikes},
          is_active = #{isActive}
      WHERE id = #{id}
      """)
    void update(BoardType bt);

    @Delete("DELETE FROM board_type WHERE id = #{id}")
    void delete(Long id);
}
