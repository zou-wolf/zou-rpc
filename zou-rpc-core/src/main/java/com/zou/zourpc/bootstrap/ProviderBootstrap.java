package com.zou.zourpc.bootstrap;

import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.config.RegistryConfig;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.model.ServiceMetaInfo;
import com.zou.zourpc.model.ServiceRegisterInfo;
import com.zou.zourpc.registry.LocalRegistry;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.server.tcp.VertxTcpServer;
import io.vertx.grpc.VertxServer;

import java.util.List;

/**
 * 服务提供者优化
 */
public class ProviderBootstrap {

    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        //RPC 框架初始化(配置和注册中心)
        RpcApplication.init();
        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList){
            String serviceName  = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName +"服务注册失败" ,e);
            }

        }
            //启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
