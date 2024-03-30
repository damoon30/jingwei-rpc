package com.jingwei.rpc.core.provider;

import com.jingwei.rpc.core.annotation.JwProvider;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import com.jingwei.rpc.core.meta.ProviderMeta;
import com.jingwei.rpc.core.util.MethodUtils;
import com.jingwei.rpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    private final MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(JwProvider.class);
        providers.forEach((x, y) -> log.info("x={}, y={}", x, y));
//        skeleton.putAll(providers);
        providers.values().forEach(
                this::getInterface
        );
    }

    private void getInterface(Object x) {
        Arrays.stream(x.getClass().getInterfaces()).forEach(
            anInterface -> {
                Method[] methods = anInterface.getMethods();
                for (Method method : methods) {
                    if (MethodUtils.checkLocalMethod(method)) {
                        continue;
                    }
                    createProvider(anInterface, x, method);
                }
            }
        );
    }

    private void createProvider(Class<?> anInterface, Object x, Method method) {
        ProviderMeta providerMeta = new ProviderMeta();
        providerMeta.setMethod(method);
        providerMeta.setMethodSign(MethodUtils.methodSign(method));
        providerMeta.setServiceImpl(x);
        log.info("create a provider : {}", providerMeta);
        skeleton.add(anInterface.getCanonicalName(), providerMeta);
    }


    public RpcResponse<Object> invoke(RpcRequest request) {
        List<ProviderMeta> beanList = skeleton.get(request.getService());
        String methodSign = request.getMethodSign();

        try {
            ProviderMeta meta = findProviderMeta(beanList, methodSign);
            Method method = meta.getMethod();
            assert method != null;
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());

            Object invoke = method.invoke(meta.getServiceImpl(), args);
            return RpcResponse.success(invoke);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return RpcResponse.failed(e.getMessage());
        }
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {return args;}
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            objects[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }

        return objects;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> beanList, String methodSign) {
        return beanList.stream().filter(it -> Objects.equals(methodSign, it.getMethodSign()))
                .findFirst().orElse(null);
    }

}
