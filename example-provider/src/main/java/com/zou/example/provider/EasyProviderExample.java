package com.zou.example.provider;


import com.zou.example.common.service.UserService;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.registry.LocalRegistry;
import com.zou.zourpc.server.HttpServer;
import com.zou.zourpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        //rpc框架初始化
        RpcApplication.init();
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
//        // 注册服务
//        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
//
//        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(8080);
    }
}
