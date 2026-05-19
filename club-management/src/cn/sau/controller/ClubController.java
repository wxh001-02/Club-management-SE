package cn.sau.controller;

import cn.sau.common.Result;
import cn.sau.domain.Club;
import cn.sau.domain.User;
import cn.sau.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping
    public Result<List<Club>> getAllClubs() {
        return Result.success(clubService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Club> getClubById(@PathVariable Long id) {
        Club club = clubService.findById(id);
        if (club == null) {
            return Result.error("社团不存在");
        }
        return Result.success(club);
    }

    @GetMapping("/my")
    public Result<List<Club>> getMyClubs(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(clubService.findByPresidentId(currentUser.getId()));
    }

    @PostMapping
    public Result<Club> createClub(@RequestBody Club club, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Club created = clubService.create(club, currentUser.getId());
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Void> updateClub(@PathVariable Long id, @RequestBody Club club, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        club.setId(id);
        clubService.update(club, currentUser.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteClub(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        clubService.delete(id, currentUser.getId());
        return Result.success();
    }
}
