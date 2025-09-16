package com.hejubian.common.service;

import com.hejubian.common.model.User;

public interface UserService {
    /**
     * 获取用户
     * @param user
     * @return
     */
    User gerUser(User user);

    default short getNumber(){
        return 1;
    }
}
