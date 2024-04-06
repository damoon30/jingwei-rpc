package com.jingwei.rpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jingwei.rpc.core.api.*;
import com.jingwei.rpc.core.util.HttpUtils;
import com.jingwei.rpc.core.util.MethodUtils;
import com.jingwei.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class JwInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext rpcContext;
    List<String> providers;
    public JwInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method.getName())) {
            return method.invoke(this, args);
        }

        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(MethodUtils.methodSign(method));
        request.setArgs(args);
        List<String> urls = rpcContext.getRouter().route(this.providers);
        String url = (String)rpcContext.getLoadBalancer().choose(urls);
        log.info("loadBalancer choose(urls) ==> {}", url);
        RpcResponse<Object> rpcResponse = post(request, url);

        if(rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            if(data instanceof JSONObject jsonResult) {
                return jsonResult.toJavaObject(method.getReturnType());
            } else {
                return TypeUtils.cast(data, method.getReturnType());
            }
        }else {
            Exception ex = rpcResponse.getEx();
            //ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private RpcResponse<Object> post(RpcRequest request, String url) {
        log.info("reqJson={}", JSON.toJSONString(request));
//        String url = "http://localhost:8080/";
        String result = HttpUtils.post(url, JSON.toJSONString(request));
        log.info("respJson={}", result);

        // 反序列化化成RpcResponse
        RpcResponse<Object> response = JSON.parseObject(result, new TypeReference<>(RpcResponse.class) {
        });

        return response;
    }


}
