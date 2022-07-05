package com.vote.auth.filter;

import com.vote.auth.content.ReactiveRequestContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * ReactiveRequestContextFilter
 *
 *
 */
public class ReactiveRequestContextFilter implements WebFilter{

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange).subscriberContext(ctx -> ReactiveRequestContextHolder.put(ctx, exchange));
	}

}