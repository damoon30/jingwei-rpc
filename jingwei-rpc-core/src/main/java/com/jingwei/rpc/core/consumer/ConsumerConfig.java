package com.jingwei.rpc.core.consumer;

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
   @Order(Integer.MIN_VALUE)
   public ApplicationRunner consumerRun(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("ConsumerBootstrap starting");
            consumerBootstrap.start();
            log.info("ConsumerBootstrap started");
        };
   }
}
