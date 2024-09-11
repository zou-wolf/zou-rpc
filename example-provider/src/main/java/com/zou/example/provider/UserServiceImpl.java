package com.zou.example.provider;


import com.zou.example.common.model.User;
import com.zou.example.common.service.UserService;


/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        user.setName("邹");
        return user;
    }
}
