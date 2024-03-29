package com.jingwei.rpc.core.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Value("${}")
    String servers;



}
