package cn.sau.service.impl;

import cn.sau.domain.Activity;
import cn.sau.domain.ActivityRegistration;
import cn.sau.mapper.ActivityMapper;
import cn.sau.mapper.ActivityRegistrationMapper;
import cn.sau.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRegistrationMapper registrationMapper;

    @Override
    public Activity findById(Long id) {
        return activityMapper.findById(id);
    }

    @Override
    public List<Activity> findAll() {
        return activityMapper.findAll();
    }

    @Override
    public List<Activity> findByClubId(Long clubId) {
        return activityMapper.findByClubId(clubId);
    }

    private String generateCheckInCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    @Override
    @Transactional
    public Activity create(Activity activity) {
        activity.setCheckInCode(generateCheckInCode());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public void update(Activity activity) {
        activityMapper.update(activity);
    }

    @Override
    public void delete(Long id) {
        activityMapper.delete(id);
    }

    @Override
    @Transactional
    public ActivityRegistration register(Long activityId, Long userId) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        ActivityRegistration existRegistration = registrationMapper.findByActivityIdAndUserId(activityId, userId);
        if (existRegistration != null) {
            throw new RuntimeException("您已报名该活动");
        }

        Integer currentCount = registrationMapper.countByActivityId(activityId);
        if (activity.getMaxParticipants() != null && currentCount >= activity.getMaxParticipants()) {
            throw new RuntimeException("报名人数已满");
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setRegisterTime(LocalDateTime.now());

        registrationMapper.insert(registration);
        return registration;
    }

    @Override
    public void cancelRegistration(Long activityId, Long userId) {
        registrationMapper.deleteByActivityIdAndUserId(activityId, userId);
    }

    @Override
    public List<ActivityRegistration> findRegistrationsByActivityId(Long activityId) {
        return registrationMapper.findByActivityId(activityId);
    }

    @Override
    public List<ActivityRegistration> findRegistrationsByUserId(Long userId) {
        return registrationMapper.findByUserId(userId);
    }
}
