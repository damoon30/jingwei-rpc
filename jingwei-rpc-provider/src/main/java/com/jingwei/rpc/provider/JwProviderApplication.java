package com.jingwei.rpc.provider;

import com.jingwei.rpc.core.provider.ProviderConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class JwProviderApplication {

    private static void main(String[] args){
        SpringApplication.run(JwProviderApplication.class, args);
    }

}
