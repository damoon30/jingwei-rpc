package com.jingwei.rpc.provider;

import com.jingwei.rpc.core.annotation.JwProvider;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class JingweiRpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(JingweiRpcProviderApplication.class, args);
    }


    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request){
        return invokeRequest(request);
    }

    private RpcResponse<Object> invokeRequest(RpcRequest request) {
        Object bean = skeleton.get(request.getService());

        try {
            Method method = bean.getClass().getMethod(request.getMethod());
            Object invoke = method.invoke(bean, request.getArgs());

            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    ApplicationContext context;

    private final Map<String, Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = context.getBeansWithAnnotation(JwProvider.class);
        providers.forEach((x, y) -> log.info("x={}, y={}", x, y));
//        skeleton.putAll(providers);
        providers.values().forEach(
                x-> getInterface(x)
        );
    }

    private void getInterface(Object x) {
        Class<?> anInterface = x.getClass().getInterfaces()[0];
        skeleton.put(anInterface.getCanonicalName(), x);
    }
}
