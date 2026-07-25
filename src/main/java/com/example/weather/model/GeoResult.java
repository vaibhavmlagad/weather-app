package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single location match returned by the Open-Meteo Geocoding API.
 * Unknown JSON fields (elevation, timezone, population, etc.) are ignored
 * since they are not needed by this application.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoResult {

    private String name;

    private double latitude;

    private double longitude;

    private String country;

    @JsonProperty("country_code")
    private String countryCode;

    /** State / region, e.g. "England", "California". Not always present. */
    private String admin1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAdmin1() {
        return admin1;
    }

    public void setAdmin1(String admin1) {
        this.admin1 = admin1;
    }
}
