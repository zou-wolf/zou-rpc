package com.zou.zourpc.registry;

import com.zou.zourpc.spi.SpiLoader;

/**
    * 注册中心工厂(用于获取注册中心对象)
    * @author zou
    */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class,key);
    }

}
