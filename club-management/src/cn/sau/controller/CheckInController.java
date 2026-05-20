package cn.sau.controller;

import cn.sau.common.Result;
import cn.sau.domain.CheckIn;
import cn.sau.domain.User;
import cn.sau.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping("/{activityId}")
    public Result<CheckIn> checkIn(@PathVariable Long activityId, @RequestBody Map<String, String> body, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        String code = body.get("code");
        CheckIn checkIn = checkInService.checkIn(activityId, currentUser.getId(), code);
        return Result.success(checkIn);
    }

    @GetMapping("/activity/{activityId}")
    public Result<List<CheckIn>> listByActivity(@PathVariable Long activityId) {
        return Result.success(checkInService.findByActivityId(activityId));
    }

    @GetMapping("/my")
    public Result<List<CheckIn>> myCheckIns(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(checkInService.findByUserId(currentUser.getId()));
    }

    @GetMapping("/activity/{activityId}/count")
    public Result<Map<String, Integer>> getCount(@PathVariable Long activityId) {
        int count = checkInService.getCheckInCount(activityId);
        Map<String, Integer> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    @GetMapping("/activity/{activityId}/status")
    public Result<Map<String, Boolean>> getStatus(@PathVariable Long activityId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        boolean checkedIn = checkInService.hasCheckedIn(activityId, currentUser.getId());
        Map<String, Boolean> data = new HashMap<>();
        data.put("checkedIn", checkedIn);
        return Result.success(data);
    }
}
