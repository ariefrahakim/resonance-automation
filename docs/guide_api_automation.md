# Building API Automation from Scratch

This guide walks through building a REST API test framework using Java, RestAssured, TestNG, and Allure — exactly as implemented in the Resonance project. Follow each step in order; someone starting with zero files can reach a running test suite by the end.

---

## 1. Project Setup — Gradle and Dependencies

Create a `build.gradle` file at the project root. This single file configures all dependencies, the Allure plugin, and the custom Gradle tasks that separate the API suite from the web suite.

```groovy
plugins {
    id 'java'
    id 'io.qameta.allure' version '2.11.2'
}

group = 'org.resonance'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

def allureVersion = '2.27.0'

dependencies {
    // TestNG (API tests)
    testImplementation 'org.testng:testng:7.10.2'
    testImplementation 'org.hamcrest:hamcrest:2.2'
    testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.18.0'
    testImplementation 'io.rest-assured:rest-assured:5.3.1'

    // Selenium + WebDriverManager (Web tests)
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.23.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.9.1'

    // Cucumber (Gherkin BDD for web tests)
    testImplementation 'io.cucumber:cucumber-java:7.18.0'
    testImplementation 'io.cucumber:cucumber-testng:7.18.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.18.0'

    // Allure reporting
    testImplementation "io.qameta.allure:allure-testng:${allureVersion}"
    testImplementation "io.qameta.allure:allure-rest-assured:${allureVersion}"
    testImplementation "io.qameta.allure:allure-cucumber7-jvm:${allureVersion}"

    // Shared
    implementation 'org.json:json:20230227'
}

allure {
    version = allureVersion
    autoconfigure = true
}

// API-only suite (TestNG)
task apiTest(type: Test) {
    useTestNG {
        suites 'src/test/java/runner/api-testng.xml'
    }
    testLogging {
        showStandardStreams = true
        events 'passed', 'skipped', 'failed'
    }
    reports.html.outputLocation = file('build/reports/tests/api')
}

// Web suite via Cucumber runner (TestNG)
task webTest(type: Test) {
    useTestNG {
        suites 'src/test/java/runner/web-testng.xml'
    }
    testLogging {
        showStandardStreams = true
        events 'passed', 'skipped', 'failed'
    }
    reports.html.outputLocation = file('build/reports/tests/web')
}

// Default task — runs both API and Web suites
test {
    useTestNG {
        suites 'src/test/java/runner/api-testng.xml',
               'src/test/java/runner/web-testng.xml'
    }
    testLogging {
        showStandardStreams = true
        events 'passed', 'skipped', 'failed'
    }
    systemProperty 'allure.results.directory', 'build/allure-results'
}
```

**Why this matters:** The `apiTest` and `webTest` custom tasks let you run one suite without triggering the other — critical in CI where you want separate jobs for API and browser tests.

---

## 2. config.properties — Centralised Configuration

Create `src/main/resources/config.properties`. All environment-specific values live here so tests never contain hardcoded URLs or credentials.

```properties
baseUrlResonance=https://resonance.dibimbing.id
usernameOrEmailResonance=user1
passwordResonance=password
webUrl=https://resonance.dibimbing.id
browser=chrome
headless=true
```

**Why this matters:** Changing the target environment (staging vs production) requires editing exactly one file, not hunting through test code.

---

## 3. Creating ConfigReader.java

Create `src/main/java/utils/ConfigReader.java`. This class loads `config.properties` once at startup and exposes two methods: a simple key lookup and a placeholder resolver used by web tests.

```java
package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for reading configuration properties from config.properties.
 *
 * All test configuration (base URL, credentials, browser settings) is centralized
 * in src/main/resources/config.properties so tests never have hardcoded values.
 *
 * Available properties:
 *   baseUrlResonance         — API base URL (e.g., https://resonance.dibimbing.id)
 *   usernameOrEmailResonance — login username or email
 *   passwordResonance        — login password
 *   webUrl                   — browser base URL for Selenium tests
 *   browser                  — browser type: chrome, firefox, edge
 *   headless                 — true/false for headless browser mode (used in CI)
 */
public class ConfigReader {

    /** Loaded once at class initialization; shared across all tests. */
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    /**
     * Returns the value of a property by key.
     *
     * @param key the property name (e.g., "baseUrlResonance")
     * @return the property value, or null if the key does not exist
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Resolves a value that may contain a ${key} config placeholder.
     *
     * Used in Cucumber step definitions so feature files can reference
     * config.properties values without hardcoding them:
     *
     *   Feature file:   Given I am logged in as "${usernameOrEmailResonance}"
     *   Step def:       loginPage.login(ConfigReader.resolve(username), ...)
     *   Resolved value: "user1"  (read from config.properties at runtime)
     *
     * If the value does not match the ${...} pattern it is returned unchanged,
     * so literal strings like "wrongpassword" are passed through as-is.
     *
     * @param value raw string (e.g., "${passwordResonance}" or "wrongpassword")
     * @return resolved value from config, or the original value unchanged
     */
    public static String resolve(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String key = value.substring(2, value.length() - 1);
            String resolved = properties.getProperty(key);
            if (resolved == null) {
                throw new RuntimeException("Config key not found: " + key);
            }
            return resolved;
        }
        return value;
    }
}
```

**Why this matters:** The static initializer runs once; every subsequent call to `getProperty` is just a map lookup, so there is no I/O overhead per test.

---

## 4. Creating JsonFileManager.java

Create `src/main/java/utils/JsonFileManager.java`. API tests are stateful — a login test saves the Bearer token, and every test that follows reads it. This class handles that file-based state sharing.

Also create the JSON files that will be populated during test runs:

```
src/main/resources/json/token.json
src/main/resources/json/ticket_id.json
src/main/resources/json/comment_id.json
src/main/resources/json/progression_id.json
```

Initialise each with `{}` so reads before the first write do not crash.

```java
package utils;

import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for reading from and writing to JSON files.
 *
 * Used for sharing state between test classes (e.g., saving a Bearer token
 * after login so subsequent tests can use it without logging in again).
 *
 * Files are stored under src/main/resources/json/:
 *   token.json          — Bearer token from login response
 *   ticket_id.json      — ticket ID created during test run
 *   comment_id.json     — comment ID for update/delete tests
 *   progression_id.json — progression ID for update/delete tests
 */
public class JsonFileManager {

    /**
     * Reads a string value from a JSON file by key.
     *
     * @param filePath path to the JSON file
     * @param key      the JSON key to look up (e.g., "token")
     * @return the string value associated with the key
     */
    public static String readValue(String filePath, String key) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content).getString(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read '" + key + "' from " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Writes a single key-value pair to a JSON file, overwriting existing content.
     *
     * Example output: { "token": "eyJhbGci..." }
     *
     * @param filePath path to the JSON file to write
     * @param key      the JSON key (e.g., "ticketId")
     * @param value    the string value to store
     */
    public static void writeValue(String filePath, String key, String value) {
        try (FileWriter fw = new FileWriter(filePath)) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            fw.write(json.toString(2));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write '" + key + "' to " + filePath + ": " + e.getMessage());
        }
    }
}
```

**Why this matters:** Without shared state, every test class would need to call `/login` independently, multiplying network calls and coupling tests to auth logic they should not own.

---

## 5. Creating Utils.java

Create `src/main/java/utils/Utils.java` for random data generation and timestamp helpers. Unique test data prevents tests from interfering with each other across runs.

```java
package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * General-purpose utility methods used across API and Web tests.
 *
 * Provides:
 *   - Random string / email generation for unique test data
 *   - ISO-8601 timestamp formatting for date-based API parameters
 */
public class Utils {

    private static final Random random = new Random();

    /** ISO-8601 format expected by the Resonance API for datetime fields. */
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Generates a random alphanumeric string of the specified length.
     * Used to make test data unique across runs (e.g., ticket titles, comment bodies).
     */
    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a random email address for use in registration or negative login tests.
     * Format: test_<6 random chars>@resonance.test
     */
    public static String generateRandomEmail() {
        return "test_" + generateRandomString(6) + "@resonance.test";
    }

    /**
     * Returns the current local date-time formatted as ISO-8601.
     * Used as the 'date' query parameter in activeTickets and as 'solvedAt'.
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(ISO_FMT);
    }

    /**
     * Returns a future date-time offset by the given number of days from now.
     * Useful for setting 'expectedSolveAt' fields in ticket creation tests.
     */
    public static String getDateTimeAfterDays(int days) {
        return LocalDateTime.now().plusDays(days).format(ISO_FMT);
    }
}
```

**Why this matters:** `generateRandomString` appended to a ticket title ensures each test run creates a genuinely new object rather than potentially colliding with leftover data from a previous run.

---

## 6. Creating Request Body Builders

Create one builder class per endpoint under `src/main/java/body/<feature>/`. Each builder is a static factory — just call `build()` with parameters and receive a `JSONObject` ready to serialise.

### Pattern

```
src/main/java/body/
    auth/
        LoginBody.java
    ticket/
        CreateTicketBody.java
    comment/
        CreateCommentBody.java
```

### LoginBody.java (example)

```java
package body.auth;

import org.json.JSONObject;

public class LoginBody {
    public static JSONObject build(String usernameOrEmail, String password) {
        JSONObject body = new JSONObject();
        body.put("usernameOrEmail", usernameOrEmail);
        body.put("password", password);
        return body;
    }
}
```

### CreateTicketBody.java (another example)

```java
package body.ticket;

import org.json.JSONObject;

public class CreateTicketBody {
    public static JSONObject build(String title, String description, boolean isPublic) {
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("description", description);
        body.put("attachment", JSONObject.NULL);
        body.put("isPublic", isPublic);
        return body;
    }
}
```

**Why this matters:** Centralising body construction in one place means you update field names or add required fields in a single file rather than across every test class that calls the endpoint.

---

## 7. Creating BaseApiTest.java

Create `src/test/java/base/BaseApiTest.java`. Every test class extends this. It configures RestAssured and exposes two request specifications: one with a Bearer token, one without.

```java
package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.JsonFileManager;

import static io.restassured.RestAssured.given;

/**
 * Base class for all API tests.
 *
 * Sets up RestAssured with the base URL from config.properties and provides
 * two pre-configured request specs:
 *
 *   authRequest()  — includes Authorization: Bearer <token> header
 *                    (token saved by LoginApiTest to token.json)
 *   baseRequest()  — no authentication, used for public endpoints
 *                    and negative auth tests
 *
 * Authentication flow:
 *   1. LoginApiTest.testLoginSuccess() calls POST /api/rest/login
 *   2. The response token is saved to src/main/resources/json/token.json
 *   3. All subsequent @Test methods that call authRequest() read and use
 *      that token as a Bearer token in the Authorization header
 *
 * Shared state files (src/main/resources/json/):
 *   token.json          — Bearer token from login
 *   ticket_id.json      — ticket ID created in CreateTicketTest
 *   comment_id.json     — comment ID created in CreateCommentTest
 *   progression_id.json — progression ID created in CreateProgressionTest
 */
public class BaseApiTest {

    protected static final String TOKEN_FILE            = "src/main/resources/json/token.json";
    protected static final String TICKET_ID_FILE        = "src/main/resources/json/ticket_id.json";
    protected static final String COMMENT_ID_FILE       = "src/main/resources/json/comment_id.json";
    protected static final String PROGRESSION_ID_FILE   = "src/main/resources/json/progression_id.json";

    /**
     * Initializes RestAssured base URI before any test in this class runs.
     * Reads the base URL from config.properties so it never needs to be hardcoded.
     */
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrlResonance");
    }

    /**
     * Returns a RequestSpecification pre-configured with:
     *   - Content-Type: application/json
     *   - Authorization: Bearer <token>  (token read from token.json)
     *
     * Use this for any endpoint that requires a logged-in user.
     *
     * @return authenticated request specification
     */
    protected RequestSpecification authRequest() {
        String token = JsonFileManager.readValue(TOKEN_FILE, "token");
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token);
    }

    /**
     * Returns a RequestSpecification with only Content-Type set.
     * No authentication header is included.
     *
     * Use this for:
     *   - Public endpoints (e.g., login itself)
     *   - Negative tests verifying that unauthenticated requests are rejected
     *
     * @return unauthenticated request specification
     */
    protected RequestSpecification baseRequest() {
        return given().contentType(ContentType.JSON);
    }
}
```

### authRequest vs baseRequest

| Method | Header included | When to use |
|---|---|---|
| `authRequest()` | `Authorization: Bearer <token>` | Any protected endpoint |
| `baseRequest()` | None | Login endpoint, negative auth tests |

**Why this matters:** By reading the token from a file rather than passing it as a parameter, every test class gets authentication for free — just call `authRequest()`.

---

## 8. Creating DataProvider Classes

Create `src/test/java/data/LoginDataProvider.java`. TestNG DataProviders supply multiple input rows to a single `@Test` method, turning one method into many independent test executions.

### Why `Object[][]`?

The return type is `Object[][]` because a single test scenario can mix types — a username (`String`), a password (`String`), a flag (`boolean`), an expected status code (`int`). Java arrays require a single element type, so `Object` is used as the common supertype that can hold all of them. TestNG maps each column to the corresponding `@Test` method parameter by position.

```java
package data;

import org.testng.annotations.DataProvider;
import utils.ConfigReader;
import utils.Utils;

/**
 * TestNG DataProvider for login endpoint test scenarios.
 *
 * Demonstrates data-driven testing: supplying multiple input rows
 * to a single @Test method. TestNG calls the method once per row.
 *
 * Valid credentials are read from config.properties via ConfigReader so
 * they are never hardcoded in test code.
 */
public class LoginDataProvider {

    /**
     * Positive login scenarios — all combinations that should return HTTP 200.
     * Columns: [usernameOrEmail, password, scenarioDescription]
     */
    @DataProvider(name = "loginValidData")
    public static Object[][] loginValidData() {
        String validUser = ConfigReader.getProperty("usernameOrEmailResonance");
        String validPass = ConfigReader.getProperty("passwordResonance");

        return new Object[][] {
            { validUser, validPass, "Login with valid username from config.properties" },
        };
    }

    /**
     * Negative login scenarios — combinations that should NOT return HTTP 200.
     * Columns: [usernameOrEmail, password, scenarioDescription]
     *
     * Covers API validation rules from the OpenAPI spec:
     *   - password minLength: 4
     *   - password maxLength: 12
     *   - usernameOrEmail: required (non-empty)
     */
    @DataProvider(name = "loginInvalidData")
    public static Object[][] loginInvalidData() {
        String validUser = ConfigReader.getProperty("usernameOrEmailResonance");

        return new Object[][] {
            { Utils.generateRandomEmail(),   "password",         "Unregistered email (random generated)" },
            { validUser,                     "wrongpassword",    "Wrong password for existing user" },
            { "",                            "password",         "Empty username (required field)" },
            { validUser,                     "",                 "Empty password (required field)" },
            { validUser,                     "ab",               "Password too short (< 4 chars, minLength violation)" },
            { validUser,                     "passwordyang13ch", "Password too long (> 12 chars, maxLength violation)" },
            { "",                            "",                 "Both username and password empty" },
            { "notanemailformat",            "password",         "Username without @ symbol (invalid format)" },
            { "test",                        "password",         "Unregistered username 'test' (valid format, not registered)" },
        };
    }
}
```

**Why this matters:** Without a DataProvider you would write nine separate `@Test` methods with near-identical logic. A DataProvider keeps the logic in one place and makes adding new scenarios trivial — just add a row.

---

## 9. Creating Test Classes

Create `src/test/java/tests/api/auth/LoginApiTest.java`. This is where RestAssured calls happen and assertions are made.

```java
package tests.api.auth;

import base.BaseApiTest;
import body.auth.LoginBody;
import data.LoginDataProvider;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.JsonFileManager;

/**
 * Test class for the Resonance authentication endpoint.
 * Covers successful and failed login scenarios.
 */
public class LoginApiTest extends BaseApiTest {

    /**
     * TC-AUTH-001: Verifies a successful login with valid credentials.
     * The returned JWT token is saved for use by subsequent tests.
     */
    @Test(priority = 1, description = "Successful login with valid credentials")
    public void testLoginSuccess() {
        String username = ConfigReader.getProperty("usernameOrEmailResonance");
        String password = ConfigReader.getProperty("passwordResonance");

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code must be 200 for a successful login");

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Bearer token must be returned after a successful login");

        // Save token to file — used as Authorization: Bearer <token> in subsequent tests
        JsonFileManager.writeValue(TOKEN_FILE, "token", token);
        System.out.println("[PASS] Bearer token saved: " + token.substring(0, Math.min(20, token.length())) + "...");
    }

    /**
     * TC-AUTH-002: Data-driven — valid credential combinations.
     */
    @Test(priority = 2,
          dataProvider = "loginValidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login with various valid credential formats")
    public void testLoginWithValidData(String username, String password, String scenario) {
        System.out.println("[INFO] Scenario: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Login must succeed for scenario: " + scenario);
        Assert.assertNotNull(response.jsonPath().getString("token"),
                "Token must be returned for scenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Login successful");
    }

    /**
     * TC-AUTH-003: Data-driven — each combination must return a status code other than 200.
     */
    @Test(priority = 3,
          dataProvider = "loginInvalidData",
          dataProviderClass = LoginDataProvider.class,
          description = "Login fails with various invalid data combinations")
    public void testLoginWithInvalidData(String username, String password, String scenario) {
        System.out.println("[INFO] Negative scenario: " + scenario);

        Response response = baseRequest()
                .body(LoginBody.build(username, password).toString())
                .post("/api/rest/login");

        Assert.assertNotEquals(response.getStatusCode(), 200,
                "Login should fail for scenario: " + scenario);

        System.out.println("[PASS] " + scenario + " - Status: " + response.getStatusCode());
    }
}
```

### @Test + DataProvider Wiring

```
@Test(
    dataProvider = "loginInvalidData",       // name of the @DataProvider method
    dataProviderClass = LoginDataProvider.class  // class that owns the provider
)
public void testLoginWithInvalidData(String username, String password, String scenario) {
```

TestNG matches the `@DataProvider` name to the `dataProvider` attribute. The method parameters (`String username`, `String password`, `String scenario`) receive the columns from each `Object[]` row in order.

**Why this matters:** The `priority = 1` on `testLoginSuccess()` guarantees it runs before the data-driven tests, so `token.json` exists when other test classes call `authRequest()`.

---

## 10. Creating api-testng.xml

Create `src/test/java/runner/api-testng.xml`. This file controls test execution order across classes within the suite.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Resonance API Test Suite" verbose="1" parallel="false">

    <test name="Auth Tests">
        <classes>
            <class name="tests.api.auth.LoginApiTest"/>
        </classes>
    </test>

    <test name="Ticket Positive Tests">
        <classes>
            <class name="tests.api.ticket.CreateTicketTest"/>
            <class name="tests.api.ticket.GetTicketTest"/>
            <class name="tests.api.ticket.UpdateTicketStatusTest"/>
        </classes>
    </test>

    <test name="Ticket Negative Tests">
        <classes>
            <class name="tests.api.ticket.NegativeTicketTest"/>
        </classes>
    </test>

    <test name="Comment Positive Tests">
        <classes>
            <class name="tests.api.comment.CreateCommentTest"/>
            <class name="tests.api.comment.UpdateCommentTest"/>
            <class name="tests.api.comment.DeleteCommentTest"/>
        </classes>
    </test>

    <test name="Comment Negative Tests">
        <classes>
            <class name="tests.api.comment.NegativeCommentTest"/>
        </classes>
    </test>

    <test name="Progression Positive Tests">
        <classes>
            <class name="tests.api.progression.CreateProgressionTest"/>
            <class name="tests.api.progression.UpdateProgressionTest"/>
            <class name="tests.api.progression.DeleteProgressionTest"/>
        </classes>
    </test>

    <test name="Progression Negative Tests">
        <classes>
            <class name="tests.api.progression.NegativeProgressionTest"/>
        </classes>
    </test>

    <test name="Vote Positive Tests">
        <classes>
            <class name="tests.api.vote.VoteTicketTest"/>
        </classes>
    </test>

    <test name="Vote Negative Tests">
        <classes>
            <class name="tests.api.vote.NegativeVoteTest"/>
        </classes>
    </test>

    <test name="Delete Ticket Tests">
        <classes>
            <class name="tests.api.ticket.DeleteTicketTest"/>
        </classes>
    </test>

</suite>
```

### Execution Order and State Dependencies

The XML order matters:

1. **Auth Tests** runs first — saves `token.json`
2. **Ticket Positive Tests** runs next — creates a ticket, saves `ticket_id.json`
3. **Comment/Progression Tests** follow — read `ticket_id.json` to add comments and progressions on the created ticket
4. **Delete Ticket Tests** runs last — so it doesn't destroy the ticket before other tests use it

**Why this matters:** `parallel="false"` ensures sequential execution. If tests ran in parallel, `token.json` might not exist when a ticket test starts reading it.

---

## 11. Running the API Tests

```bash
# Run only the API suite
./gradlew apiTest

# Run with more output
./gradlew apiTest --info

# Run both API and web suites
./gradlew test

# Clean previous results before running
./gradlew clean apiTest
```

Output appears in the terminal as each test passes or fails. The TestNG HTML report is written to `build/reports/tests/api/`.

---

## 12. Viewing the Allure Report

Allure raw results are written to `build/allure-results/` during the run. To convert them into an HTML report:

```bash
# Install Allure CLI (macOS via Homebrew)
brew install allure

# Generate and open the report
allure serve build/allure-results
```

The report opens in your browser automatically. It shows pass/fail per test, request/response bodies (captured by the `allure-rest-assured` integration), timeline, and categories of failures.

In CI (GitHub Actions), the workflow uploads `build/allure-results/` as an artifact. A separate job downloads and merges API and web results, then generates a combined HTML report available from the Actions run page.

---

## Directory Structure Summary

```
src/
  main/
    java/
      body/
        auth/LoginBody.java
        ticket/CreateTicketBody.java
      utils/
        ConfigReader.java
        JsonFileManager.java
        Utils.java
    resources/
      config.properties
      json/
        token.json
        ticket_id.json
        comment_id.json
        progression_id.json
  test/
    java/
      base/BaseApiTest.java
      data/LoginDataProvider.java
      data/TicketDataProvider.java
      runner/api-testng.xml
      tests/api/auth/LoginApiTest.java
      tests/api/ticket/CreateTicketTest.java
```
