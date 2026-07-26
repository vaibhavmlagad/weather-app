// ==========================================================================
// Weather Information App - client-side behavior
// (No frameworks; plain JS kept small and dependency-free.)
// ==========================================================================

(function () {
    const THEME_KEY = "weather-app-theme";
    const html = document.documentElement;
    const themeToggle = document.getElementById("themeToggle");
    const themeIcon = document.getElementById("themeIcon");

    /** Applies the given theme ("light" | "dark") to the document and icon. */
    function applyTheme(theme) {
        html.setAttribute("data-theme", theme);
        if (themeIcon) {
            themeIcon.className = theme === "dark" ? "bi bi-sun-fill" : "bi bi-moon-stars-fill";
        }
    }

    // Restore saved preference, defaulting to the user's OS preference.
    const savedTheme = window.localStorage.getItem(THEME_KEY);
    const prefersDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
    applyTheme(savedTheme || (prefersDark ? "dark" : "light"));

    if (themeToggle) {
        themeToggle.addEventListener("click", function () {
            const current = html.getAttribute("data-theme");
            const next = current === "dark" ? "light" : "dark";
            applyTheme(next);
            window.localStorage.setItem(THEME_KEY, next);
        });
    }

    // ---------------- Search form: spinner + Enter-to-search ----------------

    const form = document.getElementById("searchForm");
    const cityInput = document.getElementById("city");
    const searchBtn = document.getElementById("searchBtn");
    const searchBtnText = document.getElementById("searchBtnText");
    const searchSpinner = document.getElementById("searchSpinner");

    if (form) {
        form.addEventListener("submit", function () {
            // Basic client-side guard: don't submit a blank city.
            if (!cityInput.value || !cityInput.value.trim()) {
                return;
            }
            searchBtn.disabled = true;
            searchBtnText.classList.add("u-hidden");
            searchSpinner.classList.remove("u-hidden");
        });
    }

    // Enter key triggers search (native form submission already handles this
    // for a single-input form, but this makes the behavior explicit and
    // resilient if more fields are added later).
    if (cityInput) {
        cityInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                form.requestSubmit();
            }
        });
        // Auto-focus the search box on load for fast keyboard-first use.
        cityInput.focus();
    }

    function fillCity(cityName) {
        if (cityInput) {
            cityInput.value = cityName;
            cityInput.focus();
        }
    }

    // ---------------- Recent-search chips ----------------
    // Chips carry their city name in a data-city attribute (set server-side
    // via th:attr) rather than an inline onclick handler, since Thymeleaf
    // disallows string variables inside on-event attributes. Wired up here
    // instead via plain event delegation on the container.
    const historyContainer = document.querySelector(".history-chips");
    if (historyContainer) {
        historyContainer.addEventListener("click", function (event) {
            const chip = event.target.closest(".history-chip");
            if (chip && chip.dataset.city) {
                fillCity(chip.dataset.city);
            }
        });
    }
})();
