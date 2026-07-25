package com.example.weather.controller;

import com.example.weather.exception.CityNotFoundException;
import com.example.weather.exception.WeatherServiceException;
import com.example.weather.model.WeatherViewModel;
import com.example.weather.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles all HTTP routes for the weather UI. Kept intentionally thin:
 * it only reads input, calls {@link WeatherService}, and chooses which
 * model attributes to expose to the Thymeleaf view. All API calls and
 * data transformation live in the service layer.
 */
@Controller
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /** Renders the empty search form on first load. */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("history", weatherService.getSearchHistory());
        return "index";
    }

    /**
     * Handles the search form submission. Any failure is translated into
     * a friendly "error" model attribute instead of propagating as a
     * 500 error page, per the "handle invalid city names gracefully"
     * requirement.
     */
    @PostMapping("/search")
    public String search(@RequestParam("city") String city, Model model) {
        model.addAttribute("history", weatherService.getSearchHistory());

        if (city == null || city.isBlank()) {
            model.addAttribute("error", "Please enter a city name.");
            return "index";
        }

        try {
            WeatherViewModel weather = weatherService.getWeatherForCity(city);
            model.addAttribute("weather", weather);
            model.addAttribute("searchedCity", city);
            // refresh history after a successful search so it includes the new entry
            model.addAttribute("history", weatherService.getSearchHistory());
        } catch (CityNotFoundException e) {
            log.info("City not found: {}", city);
            model.addAttribute("error", "City not found: \"" + city + "\". Please check the spelling and try again.");
        } catch (WeatherServiceException e) {
            log.error("Weather service failure while searching for '{}'", city, e);
            model.addAttribute("error", "Weather service is temporarily unavailable. Please try again in a moment.");
        }

        return "index";
    }
}
