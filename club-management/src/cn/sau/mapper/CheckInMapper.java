package cn.sau.mapper;

import cn.sau.domain.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CheckInMapper {
    CheckIn findById(Long id);
    CheckIn findByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
    List<CheckIn> findByActivityId(Long activityId);
    List<CheckIn> findByUserId(Long userId);
    void insert(CheckIn checkIn);
    int countByActivityId(Long activityId);
}
