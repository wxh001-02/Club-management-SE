package cn.sau.mapper;

import cn.sau.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MemberMapper {
    Member findById(Long id);
    Member findByClubIdAndUserId(@Param("clubId") Long clubId, @Param("userId") Long userId);
    List<Member> findByClubId(Long clubId);
    List<Member> findByUserId(Long userId);
    List<Member> findByStatus(Integer status);
    void insert(Member member);
    void update(Member member);
    void delete(Long id);
    void deleteByClubIdAndUserId(@Param("clubId") Long clubId, @Param("userId") Long userId);
}
