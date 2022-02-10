package service;

import domain.User;

import java.util.List;

public interface UserService {
    void add(User user);
    List<User> getAll();
    void deleteAll();
    void update(User user);
    void upgradeLevels();
}
