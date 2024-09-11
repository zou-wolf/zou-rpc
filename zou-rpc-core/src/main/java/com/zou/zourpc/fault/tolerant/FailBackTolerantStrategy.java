package com.zou.zourpc.fault.tolerant;

import com.zou.zourpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级容错
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //todo 可自行扩展，获取降级的服务并调用
        return null;
    }
}
