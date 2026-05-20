package cn.sau.mapper;

import cn.sau.domain.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ActivityRegistrationMapper {
    ActivityRegistration findById(Long id);
    ActivityRegistration findByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
    List<ActivityRegistration> findByActivityId(Long activityId);
    List<ActivityRegistration> findByUserId(Long userId);
    void insert(ActivityRegistration registration);
    void deleteByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
    int countByActivityId(Long activityId);
}
