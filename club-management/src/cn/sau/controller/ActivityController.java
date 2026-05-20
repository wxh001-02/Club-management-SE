package cn.sau.controller;

import cn.sau.common.Result;
import cn.sau.domain.Activity;
import cn.sau.domain.ActivityRegistration;
import cn.sau.domain.Club;
import cn.sau.domain.User;
import cn.sau.service.ActivityService;
import cn.sau.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClubService clubService;

    @GetMapping
    public Result<List<Activity>> getAllActivities() {
        return Result.success(activityService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    @GetMapping("/club/{clubId}")
    public Result<List<Activity>> getActivitiesByClub(@PathVariable Long clubId) {
        return Result.success(activityService.findByClubId(clubId));
    }

    @GetMapping("/{id}/check-in-code")
    public Result<Map<String, String>> getCheckInCode(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        Club club = clubService.findById(activity.getClubId());
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限查看签到码");
        }
        Map<String, String> data = new HashMap<>();
        data.put("checkInCode", activity.getCheckInCode());
        return Result.success(data);
    }

    @GetMapping("/{id}/registrations")
    public Result<List<ActivityRegistration>> getRegistrations(@PathVariable Long id) {
        return Result.success(activityService.findRegistrationsByActivityId(id));
    }

    @GetMapping("/my-registrations")
    public Result<List<ActivityRegistration>> getMyRegistrations(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(activityService.findRegistrationsByUserId(currentUser.getId()));
    }

    @PostMapping
    public Result<Activity> createActivity(@RequestBody Activity activity, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Club club = clubService.findById(activity.getClubId());
        if (club == null) {
            return Result.error("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限为该社团发布活动");
        }
        Activity created = activityService.create(activity);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Void> updateActivity(@PathVariable Long id, @RequestBody Activity activity, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Activity existingActivity = activityService.findById(id);
        if (existingActivity == null) {
            return Result.error("活动不存在");
        }
        Club club = clubService.findById(existingActivity.getClubId());
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限修改该活动");
        }
        activity.setId(id);
        activity.setClubId(existingActivity.getClubId());
        activityService.update(activity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Activity existingActivity = activityService.findById(id);
        if (existingActivity == null) {
            return Result.error("活动不存在");
        }
        Club club = clubService.findById(existingActivity.getClubId());
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限删除该活动");
        }
        activityService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/register")
    public Result<Void> registerForActivity(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        activityService.register(id, currentUser.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}/register")
    public Result<Void> cancelRegistration(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        activityService.cancelRegistration(id, currentUser.getId());
        return Result.success();
    }
}
