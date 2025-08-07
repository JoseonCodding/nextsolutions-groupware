package mapper.pjt_mng;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PjtMngMapper {

    List<Map<String, Object>> searchProjects(@Param("keyword") String keyword,
                                             @Param("sortType") String sortType,
                                             @Param("order") String order,
                                             @Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    int countProjects(@Param("keyword") String keyword);
}
