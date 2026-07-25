package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Top-level response wrapper for the Open-Meteo Geocoding API.
 * Example endpoint:
 * https://geocoding-api.open-meteo.com/v1/search?name={CITY}&count=1
 *
 * When a city is not found, Open-Meteo returns a JSON body with no
 * "results" field at all, so {@link #getResults()} may be null - callers
 * must check for that case (see WeatherService).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoResponse {

    private List<GeoResult> results;

    public List<GeoResult> getResults() {
        return results;
    }

    public void setResults(List<GeoResult> results) {
        this.results = results;
    }
}
