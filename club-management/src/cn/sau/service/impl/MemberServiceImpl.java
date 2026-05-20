package cn.sau.service.impl;

import cn.sau.domain.Club;
import cn.sau.domain.Member;
import cn.sau.mapper.ClubMapper;
import cn.sau.mapper.MemberMapper;
import cn.sau.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ClubMapper clubMapper;

    @Override
    public Member findById(Long id) {
        return memberMapper.findById(id);
    }

    @Override
    public Member findByClubIdAndUserId(Long clubId, Long userId) {
        return memberMapper.findByClubIdAndUserId(clubId, userId);
    }

    @Override
    public List<Member> findByClubId(Long clubId) {
        return memberMapper.findByClubId(clubId);
    }

    @Override
    public List<Member> findByUserId(Long userId) {
        return memberMapper.findByUserId(userId);
    }

    @Override
    public List<Member> findPendingApplications() {
        return memberMapper.findByStatus(0);
    }

    @Override
    public Member apply(Long clubId, Long userId) {
        Club club = clubMapper.findById(clubId);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }
        Member existMember = memberMapper.findByClubIdAndUserId(clubId, userId);
        if (existMember != null) {
            throw new RuntimeException("您已申请或已是该社团成员");
        }
        Member member = new Member();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setStatus(0);
        member.setJoinDate(LocalDate.now());
        memberMapper.insert(member);
        return member;
    }

    @Override
    public void approve(Long memberId) {
        Member member = memberMapper.findById(memberId);
        if (member == null) {
            throw new RuntimeException("申请记录不存在");
        }
        member.setStatus(1);
        memberMapper.update(member);
    }

    @Override
    public void reject(Long memberId) {
        Member member = memberMapper.findById(memberId);
        if (member == null) {
            throw new RuntimeException("申请记录不存在");
        }
        memberMapper.delete(memberId);
    }

    @Override
    public void remove(Long memberId) {
        memberMapper.delete(memberId);
    }

    @Override
    public void quit(Long clubId, Long userId) {
        Member member = memberMapper.findByClubIdAndUserId(clubId, userId);
        if (member == null) {
            throw new RuntimeException("您不是该社团成员");
        }
        memberMapper.deleteByClubIdAndUserId(clubId, userId);
    }
}
