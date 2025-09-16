package com.hejubian.provider;

import com.hejubian.common.model.User;
import com.hejubian.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User gerUser(User user) {
        System.out.println("username: " + user.getUsername());
        return user;
    }
}
