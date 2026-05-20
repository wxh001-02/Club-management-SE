package cn.sau.service.impl;

import cn.sau.domain.Activity;
import cn.sau.domain.ActivityRegistration;
import cn.sau.domain.CheckIn;
import cn.sau.mapper.ActivityMapper;
import cn.sau.mapper.ActivityRegistrationMapper;
import cn.sau.mapper.CheckInMapper;
import cn.sau.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRegistrationMapper registrationMapper;

    @Override
    @Transactional
    public CheckIn checkIn(Long activityId, Long userId, String code) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getCheckInCode() == null || !activity.getCheckInCode().equals(code)) {
            throw new RuntimeException("签到码错误");
        }

        ActivityRegistration registration = registrationMapper.findByActivityIdAndUserId(activityId, userId);
        if (registration == null) {
            throw new RuntimeException("您未报名该活动，无法签到");
        }

        CheckIn existCheckIn = checkInMapper.findByActivityIdAndUserId(activityId, userId);
        if (existCheckIn != null) {
            throw new RuntimeException("您已签到，请勿重复签到");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setActivityId(activityId);
        checkIn.setUserId(userId);
        checkInMapper.insert(checkIn);
        return checkIn;
    }

    @Override
    public List<CheckIn> findByActivityId(Long activityId) {
        return checkInMapper.findByActivityId(activityId);
    }

    @Override
    public List<CheckIn> findByUserId(Long userId) {
        return checkInMapper.findByUserId(userId);
    }

    @Override
    public int getCheckInCount(Long activityId) {
        return checkInMapper.countByActivityId(activityId);
    }

    @Override
    public boolean hasCheckedIn(Long activityId, Long userId) {
        return checkInMapper.findByActivityIdAndUserId(activityId, userId) != null;
    }
}
