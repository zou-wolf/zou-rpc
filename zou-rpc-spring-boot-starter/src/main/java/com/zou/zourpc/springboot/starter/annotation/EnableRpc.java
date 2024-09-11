package com.zou.zourpc.springboot.starter.annotation;


import com.zou.zourpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.zou.zourpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.zou.zourpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用RPC 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default  true;
}
