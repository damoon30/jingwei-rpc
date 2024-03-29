package com.jingwei.rpc.core.provider;

import com.jingwei.rpc.core.annotation.JwProvider;
import com.jingwei.rpc.core.api.RegistryCenter;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import com.jingwei.rpc.core.meta.ProviderMeta;
import com.jingwei.rpc.core.util.MethodUtils;
import com.jingwei.rpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private String instance;

    @Value("${server.port}")
    private String port;

    @PostConstruct
    @SneakyThrows
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(JwProvider.class);
        providers.forEach((x,y) -> System.out.println(x));
        providers.values().forEach(x-> getInterface(x));
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop(){
        skeleton.keySet().forEach(this::unRegisterService);
    }

    private void registerService(String service){
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(service, instance);
    }
    private void unRegisterService(String service){
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.unregister(service, instance);
    }


    public RpcResponse invoke(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetaList = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetaList, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        }catch (InvocationTargetException ex){
            rpcResponse.setEx(new RuntimeException(ex.getTargetException().getMessage()));
        } catch (IllegalAccessException ex) {
            rpcResponse.setEx(new RuntimeException(ex.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes){
        if(args == null || args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetaList, String methodSign) {
        Optional<ProviderMeta> optional = providerMetaList.stream().filter(x
                -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }


    private void getInterface(Object x) {
        Arrays.stream(x.getClass().getInterfaces()).forEach(
                iter -> {
                    Method[] methods = iter.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(iter, x, method);
                    }
                }
        );
    }

    private void createProvider(Class<?> iter, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(x);
        meta.setMethodSign(MethodUtils.methodSign(method));
        skeleton.add(iter.getCanonicalName(), meta);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
