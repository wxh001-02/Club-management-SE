package cn.sau.service;

import cn.sau.domain.Club;
import java.util.List;

public interface ClubService {
    Club findById(Long id);
    List<Club> findAll();
    List<Club> findByPresidentId(Long presidentId);
    Club create(Club club, Long presidentId);
    void update(Club club, Long userId);
    void delete(Long id, Long userId);
}
