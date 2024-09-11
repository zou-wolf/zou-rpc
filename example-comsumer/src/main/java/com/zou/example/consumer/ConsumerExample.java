package com.zou.example.consumer;

import com.zou.example.common.model.User;
import com.zou.example.common.service.UserService;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.bootstrap.ConsumerBootstrap;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.constant.RpcConstant;
import com.zou.zourpc.proxy.ServiceProxy;
import com.zou.zourpc.proxy.ServiceProxyFactory;
import com.zou.zourpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {

        //服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("zou");
        for (int i = 0; i < 10; i++) {
            User newUser = userService.getUser(user);
            // 可以对 newUser 进行处理，例如打印输出或存储
            System.out.println("Call " + (i + 1) + ": " + newUser);

            if (newUser != null) {
                System.out.println("你好" + newUser.getName());
            } else {
                System.out.println("user == null");
            }
        }


    }
}
