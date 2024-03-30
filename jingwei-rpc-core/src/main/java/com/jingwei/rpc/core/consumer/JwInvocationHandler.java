package com.jingwei.rpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import com.jingwei.rpc.core.util.HttpUtils;
import com.jingwei.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class JwInvocationHandler implements InvocationHandler {

    Class<?> service;

    public JwInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(service.getCanonicalName());
        request.setMethodSign(method.getName());
        request.setArgs(args);
        RpcResponse<Object> rpcResponse = post(request);

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

    private RpcResponse<Object> post(RpcRequest request) {
        log.info("reqJson={}", JSON.toJSONString(request));
        String url = "http://localhost:8080/";
        String result = HttpUtils.post(url, JSON.toJSONString(request));
        log.info("respJson={}", result);

        // 反序列化化成RpcResponse
        RpcResponse<Object> response = JSON.parseObject(result, new TypeReference<>(RpcResponse.class) {
        });

        return response;
    }


}
