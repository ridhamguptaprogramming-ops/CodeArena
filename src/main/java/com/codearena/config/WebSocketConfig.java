package com.codearena.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration @EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) { registry.enableSimpleBroker("/topic","/queue"); registry.setApplicationDestinationPrefixes("/app"); registry.setUserDestinationPrefix("/user"); }
    @Override public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) { registry.addEndpoint("/ws").setAllowedOriginPatterns("http://localhost:3000").withSockJS(); }
}
