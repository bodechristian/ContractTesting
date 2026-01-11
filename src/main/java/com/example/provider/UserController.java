package com.example.provider;

import com.example.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    public User getUserById(Long id) {
        //users.put(id, new User(id, "a", "b@c.de"));
        return users.get(id);
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void clearUsers() {
        users.clear();
    }
}
