# Weather Information Web Application

A Spring Boot 3 + Thymeleaf + Bootstrap 5 web app that shows live current
weather for any city, using only the free [Open-Meteo](https://open-meteo.com)
APIs. **No database. No API key.**

## Stack

- Java 17, Spring Boot 3
- Spring Web (MVC) + Thymeleaf
- Spring WebFlux's `WebClient` for HTTP calls (app itself stays a classic MVC app)
- Jackson for JSON mapping
- Bootstrap 5 + Bootstrap Icons (via CDN)
- Maven

## How it works

1. User submits a city name via the search form.
2. `WeatherService` calls the **Open-Meteo Geocoding API** to resolve the
   city to latitude/longitude/country/state.
3. `WeatherService` calls the **Open-Meteo Forecast API** with those
   coordinates to get current conditions.
4. Everything is combined into a `WeatherViewModel` and rendered inside a
   Bootstrap card, with a background gradient and icon chosen from the
   WMO `weather_code` (sunny / cloudy / rain / snow / night).

## Run locally

```bash
mvn spring-boot:run
```

Then open http://localhost:8080

## Run the packaged jar

```bash
mvn clean package
java -jar target/weather-app.jar
```

## Run tests

```bash
mvn test
```

## Run with Docker

The `Dockerfile` is a multi-stage build: stage 1 builds the jar with Maven,
stage 2 copies just the jar into a minimal `eclipse-temurin:17-jre-alpine`
image, so the final image doesn't contain Maven or source code.

**Build and run manually:**
```bash
docker build -t weather-app .
docker run -d --name weather-app -p 8080:8080 weather-app
```
Then open http://localhost:8080

**Or with Docker Compose (one command):**
```bash
docker compose up --build
```
Stop it with `docker compose down`.

No environment variables, secrets, or database containers are required -
the app only talks outbound to the public Open-Meteo APIs.

## Project structure

```
Dockerfile                          # Multi-stage build: Maven -> minimal JRE image
docker-compose.yml                  # One-command build + run
.dockerignore

src/main/java/com/example/weather/
├── WeatherApplication.java        # Spring Boot entry point
├── controller/
│   └── WeatherController.java     # Thin MVC controller (routes only)
├── service/
│   ├── WeatherService.java        # All business logic + Open-Meteo calls
│   └── WeatherCodeMapper.java     # WMO weather_code -> description/icon/theme
├── model/
│   ├── GeoResponse.java           # Geocoding API response wrapper
│   ├── GeoResult.java             # Single geocoding match
│   ├── WeatherResponse.java       # Forecast API response wrapper
│   ├── CurrentWeather.java        # "current" block of the Forecast API
│   └── WeatherViewModel.java      # Flat, display-ready model for the view
├── config/
│   └── WebClientConfig.java       # Shared WebClient bean (timeouts, etc.)
└── exception/
    ├── CityNotFoundException.java
    └── WeatherServiceException.java

src/main/resources/
├── application.properties
├── templates/index.html           # Single-page UI (search + result card)
└── static/
    ├── style.css                  # Theme, gradients, dark/light mode
    └── script.js                  # Theme toggle, spinner, enter-to-search
```

## Features

- Graceful "City not found" message for invalid input.
- Graceful fallback message if Open-Meteo is unreachable/slow.
- Loading spinner on the search button while the request is in flight.
- In-memory (non-persistent) recent search history, shown as clickable chips.
- Dark / light mode toggle (persisted in the browser via `localStorage`).
- Press **Enter** to search.
- Fully responsive layout, down to small mobile screens.
- "Last updated" timestamp on every result.
- Weather code -> human-readable description mapping.

## CI/CD

`.github/workflows/build-deploy.yml` builds the project with Maven, runs
tests, packages `weather-app.jar`, does a smoke-test boot of the jar, and
uploads it as a workflow artifact. Since no secrets/database are needed,
the workflow runs with zero configuration on any fork.
