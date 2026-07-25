package com.example.weather.service;

import java.util.Map;

/**
 * Translates Open-Meteo's numeric WMO weather codes into:
 *  - a human readable description
 *  - a Bootstrap Icons class name
 *  - a "theme" bucket used to pick the card's background gradient
 *    (sunny / cloudy / rain / snow / night)
 *
 * Reference: https://open-meteo.com/en/docs (WMO Weather interpretation codes)
 */
public final class WeatherCodeMapper {

    /** Small immutable holder for the three derived display values. */
    public record WeatherInfo(String description, String iconClass, String theme) {
    }

    private static final Map<Integer, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry(0, "Clear Sky"),
            Map.entry(1, "Mainly Clear"),
            Map.entry(2, "Partly Cloudy"),
            Map.entry(3, "Overcast"),
            Map.entry(45, "Fog"),
            Map.entry(48, "Depositing Rime Fog"),
            Map.entry(51, "Light Drizzle"),
            Map.entry(53, "Moderate Drizzle"),
            Map.entry(55, "Dense Drizzle"),
            Map.entry(61, "Slight Rain"),
            Map.entry(63, "Moderate Rain"),
            Map.entry(65, "Heavy Rain"),
            Map.entry(71, "Slight Snow"),
            Map.entry(73, "Moderate Snow"),
            Map.entry(75, "Heavy Snow"),
            Map.entry(80, "Rain Showers"),
            Map.entry(95, "Thunderstorm")
    );

    private WeatherCodeMapper() {
        // utility class, no instances
    }

    /**
     * Resolves description/icon/theme for a given weather code.
     *
     * @param code  the Open-Meteo weather_code value
     * @param isDay true if it is currently daytime at the location
     */
    public static WeatherInfo resolve(int code, boolean isDay) {
        String description = DESCRIPTIONS.getOrDefault(code, "Unknown Conditions");

        // Night takes visual priority over the weather condition itself,
        // except when there is active precipitation - a rainy night should
        // still look like rain, not just "night".
        if (!isDay && isCalm(code)) {
            return new WeatherInfo(description, "bi-moon-stars-fill", "night");
        }

        if (isThunder(code)) {
            return new WeatherInfo(description, "bi-cloud-lightning-rain-fill", "rain");
        }
        if (isSnow(code)) {
            return new WeatherInfo(description, "bi-snow", "snow");
        }
        if (isRain(code)) {
            return new WeatherInfo(description, "bi-cloud-rain-heavy-fill", "rain");
        }
        if (isFog(code)) {
            return new WeatherInfo(description, "bi-cloud-haze2-fill", "cloudy");
        }
        if (isOvercast(code)) {
            return new WeatherInfo(description, "bi-clouds-fill", "cloudy");
        }
        if (isPartlyCloudy(code)) {
            return new WeatherInfo(description, isDay ? "bi-cloud-sun-fill" : "bi-cloud-moon-fill", "cloudy");
        }
        // Clear sky / mainly clear
        return new WeatherInfo(description, isDay ? "bi-sun-fill" : "bi-moon-stars-fill", isDay ? "sunny" : "night");
    }

    private static boolean isCalm(int code) {
        return code == 0 || code == 1 || code == 2;
    }

    private static boolean isPartlyCloudy(int code) {
        return code == 1 || code == 2;
    }

    private static boolean isOvercast(int code) {
        return code == 3;
    }

    private static boolean isFog(int code) {
        return code == 45 || code == 48;
    }

    private static boolean isRain(int code) {
        return (code >= 51 && code <= 67) || (code >= 80 && code <= 82);
    }

    private static boolean isSnow(int code) {
        return (code >= 71 && code <= 77) || code == 85 || code == 86;
    }

    private static boolean isThunder(int code) {
        return code == 95 || code == 96 || code == 99;
    }
}
