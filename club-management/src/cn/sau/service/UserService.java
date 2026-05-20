package cn.sau.service;

import cn.sau.domain.User;
import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    User register(User user);
    User login(String username, String password);
    void update(User user);
    void delete(Long id);
}
