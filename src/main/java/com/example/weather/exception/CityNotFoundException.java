package com.example.weather.exception;

/**
 * Thrown when the Open-Meteo Geocoding API returns no results for the
 * city name the user entered. Handled by the controller to show a
 * friendly "City not found" message instead of a stack trace.
 */
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String city) {
        super("City not found: " + city);
    }
}
