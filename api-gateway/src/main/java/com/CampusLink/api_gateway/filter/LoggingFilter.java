package com.CampusLink.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("Gateway Request : {} {} from {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(),
                exchange.getRequest().getRemoteAddress()
        );

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Gateway Response: {} - Status: {}",
                    exchange.getRequest().getURI(),
                    exchange.getResponse().getStatusCode());
        }));
    }
}
