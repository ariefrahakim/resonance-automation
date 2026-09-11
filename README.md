# Resonance Automation

API (REST Assured + TestNG) and Web (Selenium + Cucumber/Gherkin) automation framework for [Resonance](https://resonance.dibimbing.id).

**Stack:** Java 17 · Gradle · TestNG · REST Assured · Selenium 4 · Cucumber 7 · Allure 2

---

## Project Structure

```
resonance-automation/
├── .github/workflows/tests.yml
├── docs/
│   └── test_cases.xlsx
├── postman/
│   └── Resonance_API.postman_collection.json
└── src/
    ├── main/java/
    │   ├── body/               # Request body builders (auth, ticket, comment, progression, vote)
    │   └── utils/
    │       ├── ConfigReader.java       # Reads config.properties; resolve("${key}") expands placeholders
    │       ├── JsonFileManager.java    # Reads/writes JSON state files (token, IDs)
    │       └── Utils.java             # Random data generators
    ├── main/resources/
    │   ├── config.properties
    │   └── json/               # token.json, ticket_id.json, comment_id.json, progression_id.json
    └── test/java/
        ├── base/BaseApiTest.java       # authRequest() + baseRequest()
        ├── data/                       # TestNG @DataProvider classes
        ├── locators/                   # Centralized Selenium locators
        ├── pages/                      # Page Object Model
        ├── runner/                     # api-testng.xml, web-testng.xml, CucumberRunner.java
        ├── steps/                      # Cucumber step definitions + Hooks
        ├── tests/api/                  # API test cases (auth, ticket, comment, progression, vote)
        └── utils/TestCaseDocGenerator.java
    test/resources/features/           # login, create_ticket, view_ticket, history
```

---

## Configuration

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

## Running Tests

```bash
./gradlew clean apiTest         # API only
./gradlew clean webTest         # Web only
./gradlew clean test            # Both
./gradlew allureServe           # View Allure report
./gradlew generateTestCaseDoc   # Generate docs/test_cases.xlsx
```

---

## Authentication

1. `LoginApiTest` calls `POST /api/rest/login` → token saved to `token.json`
2. All authenticated tests call `authRequest()` which reads the token:
   ```java
   protected RequestSpecification authRequest() {
       String token = JsonFileManager.readValue(TOKEN_FILE, "token");
       return given().contentType(ContentType.JSON)
                     .header("Authorization", "Bearer " + token);
   }
   ```

---

## Data-Driven Testing

**API — TestNG `@DataProvider`:** Returns `Object[][]` (each inner array = one test run). `Object` is used because rows hold mixed types (String, boolean).

```java
@Test(dataProvider = "createTicketData", dataProviderClass = TicketDataProvider.class)
public void testCreateTicket(String title, String description, boolean isPublic, String scenario) { ... }
```

**Web — Cucumber `Scenario Outline` + `Examples`:** Credentials use `${configKey}` placeholders resolved at runtime via `ConfigReader.resolve()` — never hardcoded in feature files.

```gherkin
Scenario Outline: Login fails with invalid credentials
  When I enter username "<username>"
  Examples:
    | username                    | password      |
    | ${usernameOrEmailResonance} | wrongpassword |
```

---

## Web Architecture (BDD)

```
Feature File → Step Definitions → Page Objects → Locators → BasePage
```

**Locator priority:** `By.id()` > `By.name()` > `By.cssSelector()` > `By.xpath()` — never use Chakra UI hash classes (`css-n0wfye`).

**Screenshot on failure:** `Hooks.java` attaches screenshot to Allure report and saves to `build/screenshots/`.

**CI reliability:** `WebDriverWait` (up to 15s) instead of `Thread.sleep` — CI runners are slower than local machines.

---

## Test Coverage

| Suite | Positive | Negative | Total |
|-------|----------|---------|-------|
| API — Auth | 2 | 8 | 10 |
| API — Ticket | 9 | 6 | 15 |
| API — Comment | 4 | 3 | 7 |
| API — Progression | 4 | 3 | 7 |
| API — Vote | 3 | 4 | 7 |
| Web — Login | 2 | 5 | 7 |
| Web — Create Ticket | 2 | 2 | 4 |
| Web — View Ticket | 4 | 1 | 5 |
| Web — History | 3 | 1 | 4 |

---

## Postman Collection

Import `postman/Resonance_API.postman_collection.json`:

- Collection-level Bearer auth (auto-filled from login)
- Pre-request scripts generate unique random data
- Auto-saves `token`, `ticketId`, `commentId`, `progressionId` as variables

Run order: **Auth → Tickets → Comments → Progressions → Votes**

---

## CI/CD

Triggered by: PR to `main` or manual dispatch (`api` / `web` / `both`).

- `api-tests` — runs `./gradlew apiTest`, uploads TestNG + Allure results
- `web-tests` — runs `./gradlew webTest`, uploads screenshots + Cucumber + Allure results
- `allure-report` — merges results and uploads combined HTML report artifact
