package com.jingwei.rpc.consumer;

import com.jingwei.rpc.api.Order;
import com.jingwei.rpc.api.OrderService;
import com.jingwei.rpc.api.User;
import com.jingwei.rpc.api.UserService;
import com.jingwei.rpc.core.annotation.JwConsumer;
import com.jingwei.rpc.core.consumer.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import({ConsumerConfig.class})
public class JingweiRpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JingweiRpcConsumerApplication.class, args);
    }

    @JwConsumer
    private UserService userService;
    @JwConsumer
    private OrderService orderService;


    @Bean
    ApplicationRunner providerRun(){
        return x-> {
            User user = userService.findById(1);
            log.info("user={}",user);

            Order order = orderService.findById(1);
            log.info("order={}",order);
        };
    }
}
