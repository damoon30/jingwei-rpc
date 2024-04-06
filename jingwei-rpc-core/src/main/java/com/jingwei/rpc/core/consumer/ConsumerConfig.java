package com.jingwei.rpc.core.consumer;

import com.jingwei.rpc.core.api.LoadBalancer;
import com.jingwei.rpc.core.api.Router;
import com.jingwei.rpc.core.cluster.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
public class ConsumerConfig {

    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
       return new ConsumerBootstrap();
   }
    @Bean
    Router createRouter() {
       return Router.Default;
   }
    @Bean
    LoadBalancer createLoadBalancer() {
       return new RandomLoadBalancer();
   }


   @Bean
   @Order(Integer.MIN_VALUE)
   public ApplicationRunner consumerRun(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("ConsumerBootstrap starting");
            consumerBootstrap.start();
            log.info("ConsumerBootstrap started");
        };
   }
}
