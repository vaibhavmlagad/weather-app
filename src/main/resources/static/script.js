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
        html.setAttribute("data-bs-theme", theme);
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
            const current = html.getAttribute("data-bs-theme");
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
            searchBtnText.classList.add("d-none");
            searchSpinner.classList.remove("d-none");
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

    // Exposed globally so the inline th:onclick on history chips can call it.
    window.fillCity = function (cityName) {
        if (cityInput) {
            cityInput.value = cityName;
            cityInput.focus();
        }
    };
})();
