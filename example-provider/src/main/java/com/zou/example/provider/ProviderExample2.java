package com.zou.example.provider;


import com.zou.example.common.service.UserService;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.config.RegistryConfig;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.model.ServiceMetaInfo;
import com.zou.zourpc.registry.LocalRegistry;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.server.tcp.VertxTcpServer;

/**
 * 服务提供者示例

 */
public class ProviderExample2 {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(8084);
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        // 启动 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8084);
    }
}
