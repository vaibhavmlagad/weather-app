# Weather Information Web Application

A Spring Boot 3 + Thymeleaf + Bootstrap 5 web app that shows live current
weather for any city, using only the free [Open-Meteo](https://open-meteo.com)
APIs. **No database. No API key.**

## Stack

- Java 17, Spring Boot 3
- Spring Web (MVC) + Thymeleaf
- Spring WebFlux's `WebClient` for HTTP calls (app itself stays a classic MVC app)
- Jackson for JSON mapping
- Self-contained custom CSS (no external CSS framework) + Bootstrap Icons webfont (via CDN, icons only)
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

## Deploying to an EC2 instance

**Manual:**
```bash
ssh -i your-key.pem ec2-user@<EC2_PUBLIC_IP>
docker pull <your-dockerhub-username>/weather-app:latest
docker run -d --name weather-app --restart unless-stopped -p 8080:8080 <your-dockerhub-username>/weather-app:latest
```
Make sure the instance's security group allows inbound traffic on port 8080 (or whichever port you map).

**Automated (CI/CD):** the `deploy-to-ec2` job in `.github/workflows/build-deploy.yml`
SSHs into your instance after every successful push to `main`, pulls the
newly published image, stops/removes the old container, and starts the
new one. It requires these additional repository secrets:

| Secret | Value |
|---|---|
| `EC2_HOST` | Public IP or DNS name of your EC2 instance |
| `EC2_USERNAME` | SSH user (`ec2-user` for Amazon Linux, `ubuntu` for Ubuntu AMIs) |
| `EC2_SSH_KEY` | Full contents of the private key (`.pem` file) used to SSH in |
| `EC2_SSH_PORT` | *(optional)* only needed if SSH runs on a non-default port |

See the "Adding GitHub Secrets" section below for how to add these.

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

`.github/workflows/build-deploy.yml` runs four jobs in sequence on every
push to `main`:

1. **build** — compiles with Maven, runs unit tests, packages
   `weather-app.jar`, uploads it as a workflow artifact.
2. **docker** — builds the Docker image and boots a container as a
   smoke test (curls `/` until it responds) before anything is published.
3. **publish** — logs in to Docker Hub and pushes the image, tagged both
   `:latest` and `:<commit-sha>`.
4. **deploy-to-ec2** — SSHs into your EC2 instance, pulls the freshly
   published image, and restarts the `weather-app` container.

On pull requests, only step 1 and 2 run (build + smoke test) - no
credentials are needed, so PRs from forks build and test cleanly without
ever touching your secrets.

## Adding GitHub Secrets

Go to your repo → **Settings** → **Secrets and variables** → **Actions**
→ **New repository secret**, and add each of the following:

| Secret | Used by | Value |
|---|---|---|
| `DOCKERHUB_USERNAME` | `publish`, `deploy-to-ec2` | Your Docker Hub username |
| `DOCKERHUB_TOKEN` | `publish` | A Docker Hub **access token** (Account Settings → Security → New Access Token, Read & Write scope) - not your account password |
| `EC2_HOST` | `deploy-to-ec2` | Public IP or public DNS of your EC2 instance |
| `EC2_USERNAME` | `deploy-to-ec2` | SSH user - `ec2-user` (Amazon Linux) or `ubuntu` (Ubuntu AMIs) |
| `EC2_SSH_KEY` | `deploy-to-ec2` | Full contents of the `.pem` private key used to SSH in (`cat your-key.pem`, paste everything including the `BEGIN`/`END` lines) |
| `EC2_SSH_PORT` | `deploy-to-ec2` | *(optional)* only add if SSH runs on a non-default port |

Notes:
- Secrets are encrypted at rest and masked in workflow logs automatically.
- Secrets are **not** exposed to workflows triggered from forked PRs, which is why the `publish` and `deploy-to-ec2` jobs are gated with
  `if: github.ref == 'refs/heads/main' && github.event_name == 'push'`.
- Your EC2 security group needs inbound access on port 22 (SSH, for the deploy job) and port 8080 (for the running app itself).
