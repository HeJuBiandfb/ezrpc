package com.hejubian.consumer;

import com.hejubian.common.model.User;
import com.hejubian.common.service.UserService;
import com.hejubian.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setUsername("乔鲁诺·乔巴拿");
        // 调用
        User newUser = userService.gerUser(user);
        if (newUser != null) {
            System.out.println(newUser.getUsername());
        } else {
            System.out.println("User is null");
        }
    }
}
