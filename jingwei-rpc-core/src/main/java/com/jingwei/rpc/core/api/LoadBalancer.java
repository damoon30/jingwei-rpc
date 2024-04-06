package com.jingwei.rpc.core.api;

import com.jingwei.rpc.core.util.ToolUtils;

import java.util.List;

/**
 * 从某个机房选出一个机器
 */
public interface LoadBalancer<T> {


    T choose(List<T> providers);

   LoadBalancer<?> Default = providers -> ToolUtils.isEmpty(providers) ? null : providers.get(0);
}
