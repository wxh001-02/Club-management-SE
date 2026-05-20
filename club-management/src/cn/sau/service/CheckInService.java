package cn.sau.service;

import cn.sau.domain.CheckIn;
import java.util.List;

public interface CheckInService {
    CheckIn checkIn(Long activityId, Long userId, String code);
    List<CheckIn> findByActivityId(Long activityId);
    List<CheckIn> findByUserId(Long userId);
    int getCheckInCount(Long activityId);
    boolean hasCheckedIn(Long activityId, Long userId);
}
