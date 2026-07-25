package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the "current" object returned by the Open-Meteo Forecast API,
 * i.e. the current weather conditions for a given latitude/longitude.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {

    private String time;

    @JsonProperty("temperature_2m")
    private double temperature2m;

    @JsonProperty("relative_humidity_2m")
    private double relativeHumidity2m;

    @JsonProperty("apparent_temperature")
    private double apparentTemperature;

    /** 1 = day, 0 = night */
    @JsonProperty("is_day")
    private int isDay;

    private double precipitation;

    private double rain;

    @JsonProperty("weather_code")
    private int weatherCode;

    @JsonProperty("cloud_cover")
    private double cloudCover;

    @JsonProperty("surface_pressure")
    private double surfacePressure;

    @JsonProperty("wind_speed_10m")
    private double windSpeed10m;

    @JsonProperty("wind_direction_10m")
    private double windDirection10m;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTemperature2m() {
        return temperature2m;
    }

    public void setTemperature2m(double temperature2m) {
        this.temperature2m = temperature2m;
    }

    public double getRelativeHumidity2m() {
        return relativeHumidity2m;
    }

    public void setRelativeHumidity2m(double relativeHumidity2m) {
        this.relativeHumidity2m = relativeHumidity2m;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public int getIsDay() {
        return isDay;
    }

    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public double getRain() {
        return rain;
    }

    public void setRain(double rain) {
        this.rain = rain;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getSurfacePressure() {
        return surfacePressure;
    }

    public void setSurfacePressure(double surfacePressure) {
        this.surfacePressure = surfacePressure;
    }

    public double getWindSpeed10m() {
        return windSpeed10m;
    }

    public void setWindSpeed10m(double windSpeed10m) {
        this.windSpeed10m = windSpeed10m;
    }

    public double getWindDirection10m() {
        return windDirection10m;
    }

    public void setWindDirection10m(double windDirection10m) {
        this.windDirection10m = windDirection10m;
    }
}
