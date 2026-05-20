package cn.sau.service.impl;

import cn.sau.domain.Club;
import cn.sau.mapper.ClubMapper;
import cn.sau.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubServiceImpl implements ClubService {

    @Autowired
    private ClubMapper clubMapper;

    @Override
    public Club findById(Long id) {
        return clubMapper.findById(id);
    }

    @Override
    public List<Club> findAll() {
        return clubMapper.findAll();
    }

    @Override
    public List<Club> findByPresidentId(Long presidentId) {
        return clubMapper.findByPresidentId(presidentId);
    }

    @Override
    public Club create(Club club, Long presidentId) {
        Club existClub = clubMapper.findByName(club.getName());
        if (existClub != null) {
            throw new RuntimeException("社团名称已存在");
        }
        club.setPresidentId(presidentId);
        club.setStatus(1);
        club.setCreatedDate(java.time.LocalDate.now());
        clubMapper.insert(club);
        return club;
    }

    @Override
    public void update(Club club, Long userId) {
        Club existClub = clubMapper.findById(club.getId());
        if (existClub == null) {
            throw new RuntimeException("社团不存在");
        }
        if (!existClub.getPresidentId().equals(userId)) {
            throw new RuntimeException("无权限修改该社团信息");
        }
        clubMapper.update(club);
    }

    @Override
    public void delete(Long id, Long userId) {
        Club existClub = clubMapper.findById(id);
        if (existClub == null) {
            throw new RuntimeException("社团不存在");
        }
        if (!existClub.getPresidentId().equals(userId)) {
            throw new RuntimeException("无权限删除该社团");
        }
        clubMapper.delete(id);
    }
}
