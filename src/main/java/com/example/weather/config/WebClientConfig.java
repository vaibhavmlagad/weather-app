package com.example.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Central configuration for the reactive {@link WebClient} used to call
 * the Open-Meteo Geocoding and Forecast APIs. No base URL is set here
 * since the two APIs live on different hosts; the service layer supplies
 * the full URL per call.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Bounded connect/response timeouts so a slow/unreachable API
        // fails fast instead of hanging the request thread.
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(8));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
