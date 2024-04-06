package com.jingwei.rpc.core.cluster;

import com.jingwei.rpc.core.api.LoadBalancer;
import com.jingwei.rpc.core.util.ToolUtils;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (ToolUtils.isEmpty(providers)) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }

        return providers.get(random.nextInt(providers.size()));
    }
}
