package com.jingwei.rpc.provider;

import com.alibaba.fastjson.JSON;
import com.jingwei.rpc.api.User;
import com.jingwei.rpc.core.annotation.JwProvider;
import com.jingwei.rpc.core.api.RpcRequest;
import com.jingwei.rpc.core.api.RpcResponse;
import com.jingwei.rpc.core.provider.ProviderBootstrap;
import com.jingwei.rpc.core.provider.ProviderConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@SpringBootApplication
@Import({ProviderConfig.class})
public class JingweiRpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(JingweiRpcProviderApplication.class, args);
    }

    @Autowired
    private ProviderBootstrap providerBootstrap;

    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request){
        return providerBootstrap.invoke(request);
    }


    @Bean
    ApplicationRunner providerRun(){
        return x-> {
            RpcRequest request = new RpcRequest();
            request.setService("com.jingwei.rpc.api.UserService");
            request.setMethodSign("getId@1_float");
            request.setArgs(new Object[]{1.11});
            RpcResponse<Object> invoke = invoke(request);
            log.info(JSON.toJSONString(invoke));

//            RpcRequest request = new RpcRequest();
//            request.setService("com.jingwei.rpc.api.UserService");
//            request.setMethodSign("getId@1_com.jingwei.rpc.api.User");
//            request.setArgs(new Object[]{new User(100, "1234")});
//            RpcResponse<Object> invoke = invoke(request);
//            log.info(JSON.toJSONString(invoke));

//            RpcRequest request = new RpcRequest();
//            request.setService("com.jingwei.rpc.api.UserService");
//            request.setMethodSign("findById@1_int");
//            request.setArgs(new Object[]{100});
//            RpcResponse<Object> invoke = invoke(request);
//            log.info(JSON.toJSONString(invoke));

//            RpcRequest request2 = new RpcRequest();
//            request2.setService("com.jingwei.rpc.api.UserService");
//            request2.setMethodSign("findById@2_int_java.lang.String");
//            request2.setArgs(new Object[]{100, "test"});
//            RpcResponse<Object> invoke2 = invoke(request2);
//            log.info(JSON.toJSONString(invoke2));
        };
    }
}
