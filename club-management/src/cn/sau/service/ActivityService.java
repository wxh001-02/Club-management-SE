package cn.sau.service;

import cn.sau.domain.Activity;
import cn.sau.domain.ActivityRegistration;
import java.util.List;

public interface ActivityService {
    Activity findById(Long id);
    List<Activity> findAll();
    List<Activity> findByClubId(Long clubId);
    Activity create(Activity activity);
    void update(Activity activity);
    void delete(Long id);

    ActivityRegistration register(Long activityId, Long userId);
    void cancelRegistration(Long activityId, Long userId);
    List<ActivityRegistration> findRegistrationsByActivityId(Long activityId);
    List<ActivityRegistration> findRegistrationsByUserId(Long userId);
}
