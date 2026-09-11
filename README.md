# Resonance Automation

Combined **API** (REST Assured + TestNG) and **Web** (Selenium + Cucumber/Gherkin) automation framework for [Resonance](https://resonance.dibimbing.id).

Tech stack: Java 17 · Gradle · TestNG · REST Assured · Selenium 4 · Cucumber 7 · Allure 2

---

## Project Structure

```
resonance-automation/
├── .github/workflows/tests.yml         # CI/CD — trigger: PR + manual (no schedule)
├── docs/
│   └── test_cases.xlsx                                # Generated test case document (API + Web)
├── postman/
│   └── Resonance_API.postman_collection.json          # Full Postman collection with Bearer auth
├── src/
│   ├── main/java/
│   │   ├── body/                       # Request body builders (API)
│   │   │   ├── auth/LoginBody.java
│   │   │   ├── comment/
│   │   │   ├── progression/
│   │   │   ├── ticket/
│   │   │   └── vote/
│   │   └── utils/
│   │       ├── ConfigReader.java       # Reads config.properties (never hardcode credentials)
│   │       ├── JsonFileManager.java    # Reads/writes JSON state files (token, IDs)
│   │       └── Utils.java             # Random data generators + timestamp formatters
│   ├── main/resources/
│   │   ├── config.properties           # All test configuration — edit here, not in code
│   │   └── json/                       # State sharing between test classes
│   │       ├── token.json              # Bearer token from POST /api/rest/login
│   │       ├── ticket_id.json          # Ticket ID from POST /api/rest/createTicket
│   │       ├── comment_id.json         # Comment ID from POST /api/rest/createComment
│   │       └── progression_id.json     # Progression ID from POST /api/rest/createProgression
│   └── test/java/
│       ├── base/BaseApiTest.java        # RestAssured setup + authRequest() + baseRequest()
│       ├── data/                        # TestNG @DataProvider classes (data binding)
│       │   ├── LoginDataProvider.java   # loginValidData / loginInvalidData
│       │   ├── TicketDataProvider.java  # createTicketData / activeTicketOrderData / ...
│       │   ├── CommentDataProvider.java # createCommentData / createCommentInvalidData
│       │   └── ProgressionDataProvider.java # createProgressionData
│       ├── locators/                    # Centralized Selenium locators
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
│       ├── runner/
│       │   ├── api-testng.xml          # API test suite (TestNG)
│       │   ├── web-testng.xml          # Web suite (runs CucumberRunner)
│       │   └── CucumberRunner.java     # Cucumber/TestNG runner
│       ├── steps/                       # Cucumber Step Definitions
│       │   ├── Hooks.java              # Before/After per scenario — screenshot on failure
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
│       └── utils/TestCaseDocGenerator.java
└── test/resources/features/            # Gherkin Feature Files
    ├── login.feature
    ├── create_ticket.feature
    ├── view_ticket.feature
    └── history.feature
```

---

## Configuration

Edit `src/main/resources/config.properties` — never hardcode values in test code:

```properties
baseUrlResonance=https://resonance.dibimbing.id
usernameOrEmailResonance=user1
passwordResonance=password
webUrl=https://resonance.dibimbing.id
browser=chrome
headless=true
```

---

## Prerequisites

- Java 17+
- Gradle 8+
- Google Chrome (web tests)
- Allure CLI (optional, for local reports)

---

## Running Tests

```bash
# API tests only (REST Assured + TestNG)
./gradlew clean apiTest

# Web tests only (Cucumber/Gherkin + Selenium)
./gradlew clean webTest

# Both suites (default test task)
./gradlew clean test

# View Allure report locally
./gradlew allureServe

# Generate Excel test case document
./gradlew generateTestCaseDoc
# Output: docs/test_cases.xlsx
```

---

## Authentication — Bearer Token

The API uses **Bearer token authentication** (`Authorization: Bearer <token>`).

**Flow:**
1. `LoginApiTest.testLoginSuccess()` calls `POST /api/rest/login`
2. Response: `{ "ok": true, "token": "eyJ...", "user": { ... } }`
3. Token saved to `src/main/resources/json/token.json` via `JsonFileManager`
4. All authenticated tests call `authRequest()` which reads the token and sets:
   ```
   Authorization: Bearer eyJ...
   ```

**Code:**
```java
// BaseApiTest.java
protected RequestSpecification authRequest() {
    String token = JsonFileManager.readValue(TOKEN_FILE, "token");
    return given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token);
}
```

---

## Data Binding / Data-Driven Testing

Data-driven testing runs the **same test logic** with **different input values** — automatically.
Test data never lives inside test methods; it comes from DataProvider classes or Examples tables.

### API: TestNG @DataProvider

DataProvider classes in `src/test/java/data/` supply rows to `@Test` methods.
Each row is one test execution. Parameters are position-matched.

```java
// data/TicketDataProvider.java
@DataProvider(name = "createTicketData")
public static Object[][] createTicketData() {
    return new Object[][] {
        { "Public Ticket Automation",  "Description...", true,  "Create public ticket" },
        { "Private Ticket Automation", "Description...", false, "Create private ticket" },
        { "Bug: Login page error",     "Error 500...",   true,  "Bug report ticket" },
    };
}

// tests/api/ticket/CreateTicketTest.java
@Test(dataProvider = "createTicketData", dataProviderClass = TicketDataProvider.class)
public void testCreateTicketWithDataProvider(
        String title, String description, boolean isPublic, String scenario) {
    // TestNG calls this method once per row — 3 times total
    Response response = authRequest()
            .body(CreateTicketBody.build(title, description, isPublic).toString())
            .post("/api/rest/createTicket");
    Assert.assertEquals(response.getStatusCode(), 200);
}
```

**Valid credentials come from ConfigReader, not hardcoded:**
```java
// data/LoginDataProvider.java
String validUser = ConfigReader.getProperty("usernameOrEmailResonance");
String validPass = ConfigReader.getProperty("passwordResonance");
```

**Random data uses Utils:**
```java
// data/LoginDataProvider.java (negative scenarios)
{ Utils.generateRandomEmail(), "password", "Unregistered random email" }
```

### DataProvider classes overview

| Class | DataProvider(s) | Scenarios |
|---|---|---|
| `LoginDataProvider` | `loginValidData`, `loginInvalidData` | 1 valid + 8 invalid login combos |
| `TicketDataProvider` | `createTicketData`, `createTicketInvalidData`, `activeTicketOrderData`, `getTicketInvalidIdData` | 3 valid tickets, 1 invalid, 3 orders, 3 invalid IDs |
| `CommentDataProvider` | `createCommentData`, `createCommentInvalidData` | 3 valid comments, 2 invalid |
| `ProgressionDataProvider` | `createProgressionData` | 3 progression phases |

### Web: Cucumber Scenario Outline

For UI tests, data binding uses the `Scenario Outline` + `Examples` pattern:

```gherkin
# src/test/resources/features/login.feature
@negative
Scenario Outline: Login fails with invalid credentials
  When  I enter username "<username>"
  And   I enter password "<password>"
  And   I click the Login button
  Then  I should see an error or stay on the login page

  Examples:
    | username                  | password      |
    | unknown_user@fake.com     | password      |
    | user1                     | wrongpassword |
    | user1                     | ab            |
```

Cucumber runs the scenario once per row — step definitions stay the same; only the data changes.

---

## Web Test Architecture (BDD)

```
Feature File (.feature)
    └── Step Definitions (steps/)
            └── Page Objects (pages/)
                    └── Locators (locators/)   ← centralized, stable
                            └── BasePage (pages/BasePage.java)
```

### Screenshot on Failure

`Hooks.java` captures a screenshot on every failed scenario:
- **Attached to Allure/Cucumber report** for inline viewing
- **Saved to `build/screenshots/`** for CI artifact upload

```java
// steps/Hooks.java
@After
public void tearDown(Scenario scenario) {
    if (scenario.isFailed()) {
        byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "Screenshot - " + scenario.getName());
        // Also saved to build/screenshots/<timestamp>_<scenarioName>.png
    }
    DriverManager.quitDriver();
}
```

### Locator Priority

| Priority | Strategy | Example |
|----------|----------|---------|
| 1 (most stable) | `By.id()` | `By.id("btn-login")` |
| 2 | `By.name()` | `By.name("username")` |
| 3 | `By.cssSelector()` on structural attributes | `By.cssSelector("a[href*='/ticket/']")` |
| 4 | `By.xpath()` for parent/child or text | `By.xpath("//a[contains(@href,'/ticket/')]//span[1]")` |
| ❌ Avoid | Chakra UI hash classes | `div.css-n0wfye`, `span.css-1gu0mm2` |

---

## Feature Files Coverage

| Feature | Scenarios | Positive | Negative |
|---------|-----------|----------|---------|
| Login | 7 | 2 | 5 |
| Create Ticket | 4 | 2 | 2 |
| View Ticket | 5 | 4 | 1 |
| History | 4 | 3 | 1 |

---

## API Test Coverage

| Module | Positive | Negative | Total |
|--------|----------|---------|-------|
| Auth | 2 | 8 | 10 |
| Ticket | 9 | 6 | 15 |
| Comment | 4 | 3 | 7 |
| Progression | 4 | 3 | 7 |
| Vote | 3 | 4 | 7 |
| **Total** | **22** | **24** | **46** |

---

## Allure Report

```bash
# Generate & open locally
./gradlew allureServe

# Generate HTML only
./gradlew allureReport
```

In CI, the Allure HTML report is uploaded as artifact `allure-html-report-{run_number}`.

---

## Postman Collection

Import `postman/Resonance_API.postman_collection.json`:

- **Collection-level Bearer auth** — set automatically from token variable
- **Pre-request scripts** — generate unique random data (title, comment) before each request
- **Test scripts** — assert status, field existence, and value correctness per request
- **Auto-save IDs** — Login saves `token`, createTicket saves `ticketId`, createComment saves `commentId`, createProgression saves `progressionId`
- **Collection variables** (match Java config):
  - `baseUrl` = `https://resonance.dibimbing.id`
  - `username` = `user1`
  - `password` = `password`
  - `token`, `ticketId`, `commentId`, `progressionId` = auto-filled

Recommended execution order: **Auth → Tickets → Comments → Progressions → Votes → Utils**

---

## CI/CD

Triggered by: **Pull Request** to `main` or **manual dispatch** (no schedule).

- **api-tests job** — runs `./gradlew apiTest`, uploads TestNG report + Allure results
- **web-tests job** — runs `./gradlew webTest`, uploads failure screenshots + Cucumber report + Allure results
- **allure-report job** — merges API + Web Allure results, generates HTML report artifact
