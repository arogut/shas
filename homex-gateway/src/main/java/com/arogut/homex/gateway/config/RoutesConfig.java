package com.arogut.homex.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("devices-message", r -> r.path("/devices/message")
                        .uri("http://localhost:8082"))
                .route("devices-register", r -> r.path("/devices/register")
                        .uri("no://op"))
                .build();
    }
}
