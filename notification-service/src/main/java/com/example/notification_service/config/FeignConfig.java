package com.example.notification_service.config;



import com.example.notification_service.client.decoder.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public CustomErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
