package com.example.gataway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> tokens = headers.get("token");

        if (!validateToken(tokens)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            response.setComplete();//结束
            DataBuffer wrap = exchange.getResponse().bufferFactory().wrap("not auth".getBytes());
            return exchange.getResponse().writeWith(Mono.just(wrap));
        }

        return chain.filter(exchange);
    }

    //验证token是否合法
    private boolean validateToken(List<String> tokens) {
        if (null != tokens && tokens.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
