package com.example.weather.service;

import com.example.weather.exception.CityNotFoundException;
import com.example.weather.exception.WeatherServiceException;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.GeoResponse;
import com.example.weather.model.GeoResult;
import com.example.weather.model.WeatherResponse;
import com.example.weather.model.WeatherViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Contains all business logic for resolving a city name into current
 * weather conditions:
 *
 *   1. Geocode the city name into latitude/longitude via Open-Meteo's
 *      Geocoding API.
 *   2. Fetch current conditions for those coordinates via Open-Meteo's
 *      Forecast API.
 *   3. Combine both results into a single, display-ready
 *      {@link WeatherViewModel}.
 *
 * Also keeps a small, in-memory (non-persistent) search history since
 * the application intentionally has no database.
 */
@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation," +
                    "rain,weather_code,cloud_cover,surface_pressure,wind_speed_10m,wind_direction_10m";

    private static final int MAX_HISTORY_SIZE = 10;

    private final WebClient webClient;

    /**
     * Most-recent-first, in-memory search history. A thread-safe deque is
     * used since Spring beans are singletons shared across concurrent
     * requests. This is intentionally NOT persisted - it resets whenever
     * the application restarts, per the "no database" requirement.
     */
    private final Deque<String> searchHistory = new ConcurrentLinkedDeque<>();

    public WeatherService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Main entry point used by the controller: resolves the given city
     * name to full current-weather details.
     *
     * @param cityName free-text city name entered by the user
     * @return a ready-to-render view model
     * @throws CityNotFoundException  if the city could not be geocoded
     * @throws WeatherServiceException if either downstream API call fails
     */
    public WeatherViewModel getWeatherForCity(String cityName) {
        GeoResult location = geocodeCity(cityName.trim());
        CurrentWeather current = fetchCurrentWeather(location.getLatitude(), location.getLongitude());

        WeatherViewModel view = buildViewModel(location, current);
        recordSearch(location.getName());
        return view;
    }

    /** @return an unmodifiable snapshot of the recent search history, most recent first. */
    public List<String> getSearchHistory() {
        return Collections.unmodifiableList(List.copyOf(searchHistory));
    }

    // ------------------------------------------------------------------
    // Step 1: Geocoding
    // ------------------------------------------------------------------

    private GeoResult geocodeCity(String cityName) {
        String url = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                .queryParam("name", cityName)
                .queryParam("count", 1)
                .toUriString();

        GeoResponse response;
        try {
            response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(GeoResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Geocoding API returned an error status for '{}': {}", cityName, e.getStatusCode());
            throw new WeatherServiceException("Geocoding service returned an error response.", e);
        } catch (WebClientRequestException e) {
            log.error("Unable to reach the Geocoding API for '{}'", cityName, e);
            throw new WeatherServiceException("Unable to reach the geocoding service. Please try again later.", e);
        }

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            throw new CityNotFoundException(cityName);
        }

        return response.getResults().get(0);
    }

    // ------------------------------------------------------------------
    // Step 2: Current weather
    // ------------------------------------------------------------------

    private CurrentWeather fetchCurrentWeather(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromHttpUrl(FORECAST_URL)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", CURRENT_FIELDS)
                .toUriString();

        WeatherResponse response;
        try {
            response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Forecast API returned an error status for ({}, {}): {}", latitude, longitude, e.getStatusCode());
            throw new WeatherServiceException("Weather service returned an error response.", e);
        } catch (WebClientRequestException e) {
            log.error("Unable to reach the Forecast API for ({}, {})", latitude, longitude, e);
            throw new WeatherServiceException("Unable to reach the weather service. Please try again later.", e);
        }

        if (response == null || response.getCurrent() == null) {
            throw new WeatherServiceException("Weather service returned an empty response.");
        }

        return response.getCurrent();
    }

    // ------------------------------------------------------------------
    // Step 3: Combine into a view model
    // ------------------------------------------------------------------

    private WeatherViewModel buildViewModel(GeoResult location, CurrentWeather current) {
        WeatherViewModel view = new WeatherViewModel();

        view.setCity(location.getName());
        view.setCountry(location.getCountry());
        view.setState(location.getAdmin1());
        view.setLatitude(location.getLatitude());
        view.setLongitude(location.getLongitude());

        view.setTemperature(current.getTemperature2m());
        view.setFeelsLike(current.getApparentTemperature());
        view.setHumidity(current.getRelativeHumidity2m());
        view.setWindSpeed(current.getWindSpeed10m());
        view.setWindDirection(current.getWindDirection10m());
        view.setCloudCover(current.getCloudCover());
        view.setRain(current.getRain());
        view.setPrecipitation(current.getPrecipitation());
        view.setSurfacePressure(current.getSurfacePressure());
        view.setWeatherCode(current.getWeatherCode());

        boolean isDay = current.getIsDay() == 1;
        view.setDay(isDay);

        WeatherCodeMapper.WeatherInfo info = WeatherCodeMapper.resolve(current.getWeatherCode(), isDay);
        view.setDescription(info.description());
        view.setIconClass(info.iconClass());
        view.setBackgroundTheme(info.theme());

        view.setLastUpdated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")));

        return view;
    }

    private void recordSearch(String cityName) {
        // Avoid duplicate back-to-back entries and cap the history size.
        searchHistory.remove(cityName);
        searchHistory.addFirst(cityName);
        while (searchHistory.size() > MAX_HISTORY_SIZE) {
            searchHistory.removeLast();
        }
    }
}
