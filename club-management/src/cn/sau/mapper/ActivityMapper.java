package cn.sau.mapper;

import cn.sau.domain.Activity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ActivityMapper {
    Activity findById(Long id);
    List<Activity> findAll();
    List<Activity> findByClubId(Long clubId);
    void insert(Activity activity);
    void update(Activity activity);
    void delete(Long id);
}
