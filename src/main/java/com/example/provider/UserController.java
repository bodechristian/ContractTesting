package com.example.provider;

import com.example.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    public User getUserById(Long id) {
        return users.get(id);
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void clearUsers() {
        users.clear();
    }
}
