package cn.sau.mapper;

import cn.sau.domain.Club;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ClubMapper {
    Club findById(Long id);
    Club findByName(String name);
    List<Club> findAll();
    List<Club> findByPresidentId(Long presidentId);
    void insert(Club club);
    void update(Club club);
    void delete(Long id);
}
