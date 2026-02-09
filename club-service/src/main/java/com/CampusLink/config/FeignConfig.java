package com.CampusLink.config;

import com.CampusLink.client.decoder.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public CustomErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
