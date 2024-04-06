package com.jingwei.rpc.core.cluster;

import com.jingwei.rpc.core.api.LoadBalancer;
import com.jingwei.rpc.core.util.ToolUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRibonLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger random = new AtomicInteger();
    @Override
    public T choose(List<T> providers) {
        if (ToolUtils.isEmpty(providers)) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }

        return providers.get( (random.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
