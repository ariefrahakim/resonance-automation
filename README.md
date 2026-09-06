# Resonance Automation

Framework otomasi testing gabungan **API** (RestAssured) dan **Web** (Selenium + Cucumber/Gherkin) untuk aplikasi [Resonance](https://resonance.dibimbing.id).

Tech stack: Java 17 · Gradle · TestNG · RestAssured · Selenium 4 · Cucumber 7 · Allure 2

---

## Struktur Proyek

```
resonance-automation/
├── .github/workflows/tests.yml         # CI/CD — 3 job: API, Web, Allure Report
├── docs/
│   └── test_cases.xlsx                 # Dokumen test case (auto-generated)
├── postman/
│   └── Resonance_API.postman_collection.json
├── src/
│   ├── main/java/
│   │   ├── body/                       # Request body builders (API)
│   │   │   ├── auth/LoginBody.java
│   │   │   ├── comment/
│   │   │   ├── progression/
│   │   │   ├── ticket/
│   │   │   └── vote/
│   │   └── utils/                      # Utilities (ConfigReader, Utils, JsonFileManager)
│   ├── main/resources/
│   │   ├── config.properties           # Konfigurasi URL + kredensial
│   │   └── json/                       # State sharing antar test (token, ID)
│   └── test/java/
│       ├── base/BaseApiTest.java        # Setup RestAssured
│       ├── data/                        # DataProvider untuk data-driven API tests
│       │   ├── LoginDataProvider.java
│       │   ├── TicketDataProvider.java
│       │   ├── CommentDataProvider.java
│       │   └── ProgressionDataProvider.java
│       ├── locators/                    # Locator terpusat (By.id(), By.cssSelector())
│       │   ├── LoginPageLocators.java
│       │   ├── HomePageLocators.java
│       │   ├── NewTicketPageLocators.java
│       │   ├── TicketDetailPageLocators.java
│       │   └── HistoryPageLocators.java
│       ├── pages/                       # Page Object Model
│       │   ├── BasePage.java
│       │   ├── LoginPage.java
│       │   ├── HomePage.java
│       │   ├── NewTicketPage.java
│       │   ├── TicketDetailPage.java
│       │   └── HistoryPage.java
│       ├── runner/                      # Test suites
│       │   ├── testng.xml              # Full suite (API only — web pakai Cucumber)
│       │   ├── api-testng.xml          # API suite
│       │   ├── web-testng.xml          # Web suite (runs CucumberRunner)
│       │   └── CucumberRunner.java     # Cucumber/TestNG runner
│       ├── steps/                       # Cucumber Step Definitions
│       │   ├── Hooks.java              # Before/After per scenario (WebDriver init/quit)
│       │   ├── LoginSteps.java
│       │   ├── TicketSteps.java
│       │   ├── HistorySteps.java
│       │   └── CommonSteps.java
│       ├── tests/api/                   # API Test Cases (TestNG)
│       │   ├── auth/LoginApiTest.java
│       │   ├── ticket/  (Create, Get, UpdateStatus, Delete, Negative)
│       │   ├── comment/ (Create, Update, Delete, Negative)
│       │   ├── progression/ (Create, Update, Delete, Negative)
│       │   └── vote/    (Vote, Negative)
│       └── utils/TestCaseDocGenerator.java  # Generator docs/test_cases.xlsx
└── test/resources/features/            # Gherkin Feature Files
    ├── login.feature
    ├── create_ticket.feature
    ├── view_ticket.feature
    └── history.feature
```

---

## Konfigurasi

Edit `src/main/resources/config.properties`:

```properties
baseUrlResonance=https://resonance.dibimbing.id
usernameOrEmailResonance=user1
passwordResonance=password
webUrl=https://resonance.dibimbing.id
browser=chrome
headless=true
```

---

## Prasyarat

- Java 17+
- Gradle 8+
- Google Chrome (web tests)
- Allure CLI (opsional, untuk lihat report lokal)

---

## Menjalankan Test

```bash
# Semua API test
./gradlew clean test -Dsuite=api-testng

# Semua Web test (Cucumber/Gherkin)
./gradlew clean test -Dsuite=web-testng

# Full suite
./gradlew clean test

# Lihat Allure report
./gradlew allureServe
```

---

## Arsitektur Web Tests (Gherkin BDD)

```
Feature File (.feature)
    └── Step Definitions (steps/)
            └── Page Objects (pages/)
                    └── Locators (locators/)   ← terpusat, stabil
                            └── BasePage (pages/BasePage.java)
```

### Prinsip Pemilihan Locator

| Prioritas | Strategi | Contoh |
|-----------|----------|--------|
| 1 (paling stabil) | `By.id()` | `By.id("btn-login")` |
| 2 | `By.name()` | `By.name("username")` |
| 3 | `By.cssSelector()` berdasarkan atribut struktural | `By.cssSelector("a[href*='/ticket/']")` |
| 4 | `By.xpath()` untuk relasi parent/child atau teks | `By.xpath("//a[contains(@href,'/ticket/')]//span[1]")` |
| ❌ Hindari | Class hash Chakra UI | `div.css-n0wfye`, `span.css-1gu0mm2` |

---

## Feature Files

| Feature | Skenario | Positif | Negatif |
|---------|----------|---------|---------|
| Login | 7 | 2 | 5 |
| Create Ticket | 4 | 2 | 2 |
| View Ticket | 5 | 4 | 1 |
| History | 4 | 3 | 1 |

---

## API Test Coverage

| Modul | Positif | Negatif | Total |
|-------|---------|---------|-------|
| Auth | 2 | 6 | 8 |
| Ticket | 9 | 6 | 15 |
| Comment | 4 | 4 | 8 |
| Progression | 4 | 4 | 8 |
| Vote | 3 | 3 | 6 |
| **Total** | **22** | **23** | **45** |

---

## Data-Driven Testing

Test API menggunakan `@DataProvider` TestNG:

| DataProvider | Skenario |
|---|---|
| `loginValidData` | Login sukses dengan username |
| `loginInvalidData` | 8 kombinasi data tidak valid (email salah, password salah, kosong, terlalu pendek/panjang) |
| `createTicketData` | Tiket publik, private, bug report |
| `activeTicketOrderData` | Order: VOTE, NEWEST, SOLVE |
| `createCommentData` | 3 variasi isi komentar |
| `createProgressionData` | 3 fase progres (investigasi, pengembangan, QA) |

---

## Allure Report

Allure terintegrasi untuk TestNG (API) dan Cucumber (Web).

```bash
# Generate & buka report lokal
./gradlew allureServe

# Generate report (HTML saja)
./gradlew allureReport
```

Pada CI/CD, Allure HTML report di-upload sebagai artifact (`allure-html-report-{run_number}`).

---

## Postman Collection

Import `postman/Resonance_API.postman_collection.json`:

- Token otomatis disimpan setelah login
- ID tiket/komentar/progres tersimpan otomatis ke collection variable
- Setiap request memiliki test assertion
- Endpoint: Auth, Tickets, Comments, Progressions, Votes, Utils

---

## Test Case Document

Generate dokumen Excel:

```bash
./gradlew generateTestCaseDoc
# Output: docs/test_cases.xlsx
```

File berisi 2 sheet:
- **API Test Cases** — 40 test case dengan kolom TC ID, Modul, Step, Test Data, Expected Result
- **Web Test Cases (Gherkin)** — 19 test case dengan format Given/When/Then
