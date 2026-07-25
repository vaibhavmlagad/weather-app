package com.example.weather.exception;

/**
 * Thrown when a downstream Open-Meteo API call fails for reasons other
 * than "city not found" - e.g. the service is unreachable, times out,
 * or returns an unexpected response. Wraps the root cause for logging
 * while giving the controller a single, generic type to catch.
 */
public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherServiceException(String message) {
        super(message);
    }
}
