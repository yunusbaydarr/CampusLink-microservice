package com.CampusLink.api_gateway.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        log.info("CONFIGURATION GATEWAY ROUTES");

        return builder.routes()
                .route(id -> id.path("/user/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Request", "API Gateway")
                                .addResponseHeader("X-Gateway-Response", "API Gateway"))
                        .uri("lb://user-service"))

                .route(id -> id.path("/club/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Request", "API Gateway")
                                .addResponseHeader("X-Gateway-Response", "API Gateway"))
                        .uri("lb://club-service"))

                .route(id -> id.path("/event/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Request", "API Gateway")
                                .addResponseHeader("X-Gateway-Response", "API Gateway"))
                        .uri("lb://event-service"))

                .route(id -> id.path("/invitation/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Request", "API Gateway")
                                .addResponseHeader("X-Gateway-Response", "API Gateway"))
                        .uri("lb://invitation-service"))

                .route(id -> id.path("/api/v1/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Request", "API Gateway")
                                .addResponseHeader("X-Gateway-Response", "API Gateway"))
                        .uri("lb://user-service"))

                .build();
    }
}
