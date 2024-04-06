package com.jingwei.rpc.core.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RpcContext {
    List<Filter> filterList = new ArrayList<>();

    Router router;
    LoadBalancer loadBalancer;

}
