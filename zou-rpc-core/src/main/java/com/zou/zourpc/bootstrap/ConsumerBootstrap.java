package com.zou.zourpc.bootstrap;

import com.zou.zourpc.RpcApplication;

/**
 * 服务消费者启动类
 */
public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init(){
        //rpc 框架初始化
        RpcApplication.init();
    }
}
