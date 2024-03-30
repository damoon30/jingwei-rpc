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

import java.util.ArrayList;
import java.util.List;

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
            List<Integer> lists = new ArrayList<>(){{add(1);add(2);}};
            List<Integer> listStr = userService.getIds(lists);
            log.info("userId2={}", listStr);

//            int id2 = userService.getId(new User(2, "12"));
//            log.info("userId2={}", id2);

//            User user2 = userService.findById(2, "12");
//            log.info("user={}",user2);
//
//            User user = userService.findById(1);
//            log.info("user={}",user);
//
//            Order order = orderService.findById(1);
//            log.info("order={}",order);
        };
    }
}
