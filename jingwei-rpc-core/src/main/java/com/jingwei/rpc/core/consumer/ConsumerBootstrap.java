package com.jingwei.rpc.core.consumer;

import com.jingwei.rpc.core.annotation.JwConsumer;
import com.jingwei.rpc.core.api.LoadBalancer;
import com.jingwei.rpc.core.api.RegistryCenter;
import com.jingwei.rpc.core.api.Router;
import com.jingwei.rpc.core.api.RpcContext;
import com.jingwei.rpc.core.util.ToolUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    /**
     * springboot 启动中就会将上下文写入
     */
    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();


    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);

        RpcContext rpcContext = new RpcContext();
        rpcContext.setRouter(router);
        rpcContext.setLoadBalancer(loadBalancer);

        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);


        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);

            if (!name.contains("jingweiRpcConsumerApplication")) {
                continue;
            }

            String packageName = bean.getClass().getPackageName();
            List<Field> fields = findAnnotatedField(bean.getClass());
            if (packageName.startsWith("org.springframework") ||
                    packageName.startsWith("java.") ||
                    packageName.startsWith("javax.") ||
                    packageName.startsWith("jdk.") ||
                    packageName.startsWith("com.fasterxml.") ||
                    packageName.startsWith("com.sun.") ||
                    packageName.startsWith("jakarta.") ||
                    packageName.startsWith("org.apache") ) {
                continue;  // 这段逻辑可以降低一半启动速度 300ms->160ms
            }
            fields.forEach(f -> {
                Class<?> service = f.getType();
                String canonicalName = service.getCanonicalName();
                Object consumer = stub.get(canonicalName);

                if (consumer == null) {
                    consumer = createConsumerFromRegister(service, rpcContext, rc);
                }
                f.setAccessible(true);
                try {
                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                stub.put(canonicalName, consumer);
            });
            log.info("init stub success = {}", stub);
        }
    }

    private Object createConsumerFromRegister(Class<?> service, RpcContext rpcContext, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        List<String> providers = rc.fetchAll(serviceName);
        return createConsumer(service, rpcContext, providers);
    }

    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new JwInvocationHandler(service, rpcContext, providers));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();

        while (aClass != null) {
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(JwConsumer.class)){
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }


}
