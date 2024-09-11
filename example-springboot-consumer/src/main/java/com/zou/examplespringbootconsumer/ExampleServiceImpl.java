package com.zou.examplespringbootconsumer;


import com.zou.example.common.model.User;
import com.zou.example.common.service.UserService;
import com.zou.zourpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("zou");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
