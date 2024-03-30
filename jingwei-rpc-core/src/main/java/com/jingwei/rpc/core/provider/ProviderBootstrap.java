package com.jingwei.rpc.core.provider;

import com.jingwei.rpc.core.annotation.JwProvider;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * provider 的启动类
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    @Autowired
    ApplicationContext applicationContext;

    private final Map<String, Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(JwProvider.class);
        providers.forEach((x, y) -> log.info("x={}, y={}", x, y));
//        skeleton.putAll(providers);
        providers.values().forEach(
                this::getInterface
        );
    }

    private void getInterface(Object x) {
        Class<?> anInterface = x.getClass().getInterfaces()[0];
        skeleton.put(anInterface.getCanonicalName(), x);
    }


    public RpcResponse<Object> invoke(RpcRequest request) {
        Object bean = skeleton.get(request.getService());

        try {
            Method method = findMethod(bean.getClass(), request.getMethodSign());
            assert method != null;
            Object invoke = method.invoke(bean, request.getArgs());

            return RpcResponse.success(invoke);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (Objects.equals(method.getName(), methodName)) {
                return method;
            }
        }
        return null;
    }

}
