package com.example.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Weather Information Web Application.
 *
 * This is a self-contained Spring Boot MVC application that talks to the
 * free Open-Meteo APIs (no API key required, no database required) to
 * resolve a city name to coordinates and then fetch current weather
 * conditions for those coordinates.
 */
@SpringBootApplication
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
