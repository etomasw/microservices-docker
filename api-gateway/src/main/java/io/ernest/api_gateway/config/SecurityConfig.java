package io.ernest.api_gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeExchange(a -> a.anyExchange().authenticated())
                .oauth2Login();
        return http.build();
    }
}
