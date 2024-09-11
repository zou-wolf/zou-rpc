package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{
    /**
     * 一直性哈希负载均衡器
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        //构建虚拟节点环
        for(ServiceMetaInfo serviceMetaInfo:serviceMetaInfoList){
            for(int i=0;i<VIRTUAL_NODE_NUM;i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() +"#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
        //处理调用请求的hash值
        int hash = getHash(requestParams);

        //选择最接近且大于等于调用请求 Hash 值的虚拟节点
        Map.Entry<Integer,ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry ==null){
            //如果没有大于等于调用请求的hash的虚拟节点，否则返回环首部的节点
            entry=virtualNodes.firstEntry();
        }
        return entry.getValue();

    }
    //哈希算法 可自行实现
    private int getHash(Object key){
        return key.hashCode();
    }
}
