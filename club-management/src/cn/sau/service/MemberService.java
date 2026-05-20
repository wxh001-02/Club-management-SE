package cn.sau.service;

import cn.sau.domain.Member;
import java.util.List;

public interface MemberService {
    Member findById(Long id);
    Member findByClubIdAndUserId(Long clubId, Long userId);
    List<Member> findByClubId(Long clubId);
    List<Member> findByUserId(Long userId);
    List<Member> findPendingApplications();
    Member apply(Long clubId, Long userId);
    void approve(Long memberId);
    void reject(Long memberId);
    void remove(Long memberId);
    void quit(Long clubId, Long userId);
}
