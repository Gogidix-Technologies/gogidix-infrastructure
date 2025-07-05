package com.exalt.warehousing.management.config;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Feign clients
 */
@Configuration
@EnableFeignClients(basePackages = "com.ecosystem.warehousing.management.client")
public class FeignClientConfig {

    /**
     * Configure Feign request interceptor to propagate authentication headers
     *
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                // Propagate Authorization header
                String authorizationHeader = requestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader != null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
                }
                
                // Propagate correlation ID for tracing
                String correlationId = requestAttributes.getRequest().getHeader("X-Correlation-ID");
                if (correlationId != null) {
                    requestTemplate.header("X-Correlation-ID", correlationId);
                }
            }
        };
    }

    /**
     * Configure Feign retry strategy
     *
     * @return the retryer configuration
     */
    @Bean
    public Retryer retryer() {
        // Retry 3 times, starting with a 100ms backoff, multiplying by 1.5 each time
        // with a maximum backoff of 2 seconds between retries
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(2), 3);
    }

    /**
     * Configure Feign error handling
     *
     * @return the error decoder
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            String message = String.format("Error calling Feign client - status: %s, method: %s", status, methodKey);
            
            if (status == HttpStatus.NOT_FOUND.value()) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
            } else if (status == HttpStatus.BAD_REQUEST.value()) {
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            } else if (status == HttpStatus.UNAUTHORIZED.value()) {
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
            } else if (status == HttpStatus.FORBIDDEN.value()) {
                return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
            } else {
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        };
    }
} 
