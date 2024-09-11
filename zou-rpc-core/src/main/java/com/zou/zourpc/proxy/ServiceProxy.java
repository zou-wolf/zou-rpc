package com.zou.zourpc.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.constant.RpcConstant;
import com.zou.zourpc.fault.retry.RetryStrategy;
import com.zou.zourpc.fault.retry.RetryStrategyFactory;
import com.zou.zourpc.fault.tolerant.TolerantStrategy;
import com.zou.zourpc.fault.tolerant.TolerantStrategyFactory;
import com.zou.zourpc.loadbalancer.LoadBalancer;
import com.zou.zourpc.loadbalancer.LoadBalancerFactory;
import com.zou.zourpc.model.RpcRequest;
import com.zou.zourpc.model.RpcResponse;
import com.zou.zourpc.model.ServiceMetaInfo;
import com.zou.zourpc.protocol.*;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.serializer.JdkSerializer;
import com.zou.zourpc.serializer.Serializer;
import com.zou.zourpc.serializer.SerializerFactory;
import com.zou.zourpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {



    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
//        Serializer serializer = new JdkSerializer();
//        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();

        RpcRequest rpcRequest1 = new RpcRequest();


        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
//        try {

// 序列化
//            byte[] bodyBytes = serializer.serialize(rpcRequest);

// 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
// 暂时先取第一个
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //将调用的方法名（请求路径）作为负载均衡参数
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("methodName",rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams,serviceMetaInfoList);

            // 发送 TCP 请求
//            Vertx vertx = Vertx.vertx();优化2
//            NetClient netClient = vertx.createNetClient();
//            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
//            netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(),
//                    result -> {
//                        if (result.succeeded()) {
//                            System.out.println("Connected to TCP server");
//                            io.vertx.core.net.NetSocket socket = result.result();
//                            // 发送数据
//                            // 构造消息
//                            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//                            ProtocolMessage.Header header = new ProtocolMessage.Header();
//                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
//                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                            header.setRequestId(IdUtil.getSnowflakeNextId());
//                            protocolMessage.setHeader(header);
//                            protocolMessage.setBody(rpcRequest);
//                            // 编码请求
//                            try {
//                                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
//                                socket.write(encodeBuffer);
//                            } catch (IOException e) {
//                                throw new RuntimeException("协议消息编码错误");
//                            }
//
//                            // 接收响应
//                            socket.handler(buffer -> {
//                                try {
//                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
//                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
//                                } catch (IOException e) {
//                                    throw new RuntimeException("协议消息解码错误");
//                                }
//                            });
//                        } else {
//                            System.err.println("Failed to connect to TCP server");
//                        }
//                    });
//
//
//            RpcResponse rpcResponse = responseFuture.get();
//            // 记得关闭连接
//            netClient.close();
//            return rpcResponse.getData();
            //rpc请求
            // 使用重试机制
            // 使用重试机制
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo)
                        );
            //最终简洁优化

// 发送请求
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())//第一次
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }



//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//            // 发送请求
//            // todo 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）
//            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
        } catch (IOException e) {
            //容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null,e);
        }
        return rpcResponse.getData();

    }
}
