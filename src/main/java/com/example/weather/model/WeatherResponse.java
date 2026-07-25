package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Top-level response wrapper for the Open-Meteo Forecast API.
 * Example endpoint:
 * https://api.open-meteo.com/v1/forecast?latitude=..&longitude=..&current=...
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private double latitude;

    private double longitude;

    private String timezone;

    private CurrentWeather current;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public CurrentWeather getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeather current) {
        this.current = current;
    }
}
