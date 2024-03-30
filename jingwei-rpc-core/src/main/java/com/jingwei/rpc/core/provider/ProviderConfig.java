package com.jingwei.rpc.core.provider;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap(){
        return new ProviderBootstrap();
    }

}
