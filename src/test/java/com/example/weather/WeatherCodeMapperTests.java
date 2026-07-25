package com.example.weather;

import com.example.weather.service.WeatherCodeMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for WeatherCodeMapper - verifies a sample of known
 * Open-Meteo weather codes map to the expected description/theme.
 */
class WeatherCodeMapperTests {

    @Test
    void clearSkyDuringDayIsSunny() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(0, true);
        assertEquals("Clear Sky", info.description());
        assertEquals("sunny", info.theme());
    }

    @Test
    void clearSkyAtNightIsNightTheme() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(0, false);
        assertEquals("night", info.theme());
    }

    @Test
    void overcastIsCloudyTheme() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(3, true);
        assertEquals("Overcast", info.description());
        assertEquals("cloudy", info.theme());
    }

    @Test
    void heavyRainIsRainTheme() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(65, true);
        assertEquals("Heavy Rain", info.description());
        assertEquals("rain", info.theme());
    }

    @Test
    void heavySnowIsSnowTheme() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(75, true);
        assertEquals("Heavy Snow", info.description());
        assertEquals("snow", info.theme());
    }

    @Test
    void thunderstormIsRainTheme() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(95, true);
        assertEquals("Thunderstorm", info.description());
        assertEquals("rain", info.theme());
    }

    @Test
    void unknownCodeFallsBackGracefully() {
        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(9999, true);
        assertEquals("Unknown Conditions", info.description());
    }
}
