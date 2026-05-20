package cn.sau.mapper;

import cn.sau.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    void insert(User user);
    void update(User user);
    void delete(Long id);
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}
