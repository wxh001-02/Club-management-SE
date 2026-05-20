package cn.sau.controller;

import cn.sau.common.Result;
import cn.sau.domain.Club;
import cn.sau.domain.Member;
import cn.sau.domain.User;
import cn.sau.service.ClubService;
import cn.sau.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ClubService clubService;

    @GetMapping("/club/{clubId}/user/{userId}")
    public Result<Member> getMembership(@PathVariable Long clubId, @PathVariable Long userId) {
        Member member = memberService.findByClubIdAndUserId(clubId, userId);
        if (member == null) {
            return Result.error("未找到该成员关系");
        }
        return Result.success(member);
    }

    @GetMapping("/club/{clubId}")
    public Result<List<Member>> getMembersByClub(@PathVariable Long clubId) {
        return Result.success(memberService.findByClubId(clubId));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Member>> getMyClubs(@PathVariable Long userId) {
        return Result.success(memberService.findByUserId(userId));
    }

    @GetMapping("/pending")
    public Result<List<Member>> getPendingApplications(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        if (!"PRESIDENT".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限查看申请列表");
        }
        return Result.success(memberService.findPendingApplications());
    }

    @PostMapping("/apply/{clubId}")
    public Result<Void> applyToJoin(@PathVariable Long clubId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        memberService.apply(clubId, currentUser.getId());
        return Result.success();
    }

    @PutMapping("/approve/{memberId}")
    public Result<Void> approveApplication(@PathVariable Long memberId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Member member = memberService.findById(memberId);
        if (member == null) {
            return Result.error("申请记录不存在");
        }
        Club club = clubService.findById(member.getClubId());
        if (club == null) {
            return Result.error("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限审核该申请");
        }
        memberService.approve(memberId);
        return Result.success();
    }

    @PutMapping("/reject/{memberId}")
    public Result<Void> rejectApplication(@PathVariable Long memberId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Member member = memberService.findById(memberId);
        if (member == null) {
            return Result.error("申请记录不存在");
        }
        Club club = clubService.findById(member.getClubId());
        if (club == null) {
            return Result.error("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限审核该申请");
        }
        memberService.reject(memberId);
        return Result.success();
    }

    @DeleteMapping("/quit/{clubId}")
    public Result<Void> quitClub(@PathVariable Long clubId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        memberService.quit(clubId, currentUser.getId());
        return Result.success();
    }

    @DeleteMapping("/remove/{memberId}")
    public Result<Void> removeMember(@PathVariable Long memberId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        Member member = memberService.findById(memberId);
        if (member == null) {
            return Result.error("成员记录不存在");
        }
        Club club = clubService.findById(member.getClubId());
        if (club == null) {
            return Result.error("社团不存在");
        }
        if (!club.getPresidentId().equals(currentUser.getId()) && !"ADMIN".equals(currentUser.getRole())) {
            return Result.error(403, "无权限移除该成员");
        }
        memberService.remove(memberId);
        return Result.success();
    }
}
