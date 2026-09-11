# Building Web Automation from Scratch

This guide walks through building a Selenium + Cucumber/Gherkin BDD test framework using Java and TestNG — exactly as implemented in the Resonance project. Each step builds on the previous one; someone starting with zero files can reach a running browser test suite by the end.

---

## 1. Project Setup — Gradle, Cucumber, and Selenium

The same `build.gradle` serves both API and web tests. The key web-specific dependencies are:

```groovy
plugins {
    id 'java'
    id 'io.qameta.allure' version '2.11.2'
}

repositories {
    mavenCentral()
}

def allureVersion = '2.27.0'

dependencies {
    // Selenium + WebDriverManager (Web tests)
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.23.0'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.9.1'

    // Cucumber (Gherkin BDD for web tests)
    testImplementation 'io.cucumber:cucumber-java:7.18.0'
    testImplementation 'io.cucumber:cucumber-testng:7.18.0'
    testImplementation 'io.cucumber:cucumber-picocontainer:7.18.0'

    // Allure reporting for Cucumber
    testImplementation "io.qameta.allure:allure-cucumber7-jvm:${allureVersion}"

    // Shared utilities
    implementation 'org.json:json:20230227'
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
```

**Why this matters:** `cucumber-picocontainer` enables dependency injection between step definition classes — without it, sharing the `WebDriver` instance across `LoginSteps`, `Hooks`, and `CommonSteps` would require manual wiring. `webdrivermanager` automatically downloads the correct ChromeDriver binary, so CI runners do not need a pre-installed driver.

### Browser config in config.properties

```properties
webUrl=https://resonance.dibimbing.id
browser=chrome
headless=true
```

Set `headless=false` for local debugging, `headless=true` for CI.

---

## 2. Creating DriverManager.java

Create `src/test/java/utils/DriverManager.java`. This class owns the `WebDriver` lifecycle — init, access, and quit. It uses `ThreadLocal` to support future parallel execution safely.

```java
package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOpts = new FirefoxOptions();
                if (headless) firefoxOpts.addArguments("--headless");
                driver.set(new FirefoxDriver(firefoxOpts));
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOpts = new ChromeOptions();
                if (headless) {
                    chromeOpts.addArguments("--headless=new", "--no-sandbox",
                            "--disable-dev-shm-usage", "--window-size=1920,1080");
                }
                driver.set(new ChromeDriver(chromeOpts));
                break;
        }
        getDriver().manage().window().maximize();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

### Key flags for CI headless mode

| Flag | Purpose |
|---|---|
| `--headless=new` | Modern headless mode (Chrome 112+) |
| `--no-sandbox` | Required in Linux Docker/CI environments |
| `--disable-dev-shm-usage` | Prevents crashes from limited `/dev/shm` in containers |
| `--window-size=1920,1080` | Sets a consistent viewport so element coordinates are predictable |

**Why this matters:** `ThreadLocal<WebDriver>` ensures each thread gets its own driver instance. Even if you run scenarios sequentially today, the design supports parallel execution without code changes.

---

## 3. Creating BasePage.java

Create `src/test/java/pages/BasePage.java`. Every page object extends this abstract class, which provides explicit waits and common helpers so individual page classes stay focused on business actions.

```java
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;

/**
 * Abstract base class for all page objects.
 * Provides shared WebDriver helpers and a 15-second explicit wait.
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String baseUrl = ConfigReader.getProperty("webUrl");

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** Waits until the element is visible and returns it. */
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element is clickable and returns it. */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Waits for the element to be clickable and clicks it. */
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    /** Clears the element and types the given text. */
    protected void type(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Returns the visible text of the element. */
    protected String getText(By locator) {
        return waitForElement(locator).getText();
    }

    /** Returns true if the element is present and displayed. */
    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Navigates the browser to baseUrl + path. */
    protected void navigateTo(String path) {
        driver.get(baseUrl + path);
    }
}
```

**Why this matters:** The 15-second `WebDriverWait` in every interaction means tests wait for elements to be ready rather than assuming a fixed load time. This is the single most important change you can make to eliminate flaky tests.

---

## 4. Locator Strategy — Priority and Rules

Locators determine how Selenium finds elements on the page. Use the highest-priority strategy available for each element.

| Priority | Strategy | Example | Use when |
|---|---|---|---|
| 1 (best) | `By.id()` | `By.id("btn-login")` | Element has a stable `id` attribute |
| 2 | `By.name()` | `By.name("email")` | Form fields with a `name` attribute |
| 3 | `By.cssSelector()` | `By.cssSelector("input[type='email']")` | Structural selectors, attribute-based |
| 4 (last resort) | `By.xpath()` | `By.xpath("//button[text()='Login']")` | Dynamic content, text-based matching |

### Rules for React / Chakra UI apps

- **Never target generated hash classes** like `css-1ab2cd3`. Chakra UI regenerates these on every build — your locator breaks with every deployment.
- Prefer `id` attributes added specifically for testing. If the app does not have them, request them from the dev team.
- For Chakra UI toast messages, use attribute selectors: `By.cssSelector(".chakra-alert[data-status='error']")` — the `data-status` attribute is stable.

---

## 5. Creating LoginPageLocators.java

Create `src/test/java/locators/LoginPageLocators.java`. Separating locators from page actions means when a locator changes you update one file, not every method that uses it.

```java
package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the Login page (/login).
 * Uses real ID attributes from the Resonance application.
 */
public class LoginPageLocators {

    // Input fields
    public static final By USERNAME_INPUT = By.id("input-email-login");
    public static final By PASSWORD_INPUT = By.id("input-password-login");

    // Buttons
    public static final By LOGIN_BUTTON   = By.id("btn-login");

    // Error toast — Chakra UI toasts render inside chakra-toast-manager-top
    // data-status='error' is stable; the CSS hash classes are not
    public static final By ERROR_TOAST    = By.cssSelector("li.chakra-toast .chakra-alert[data-status='error']");
    public static final By ANY_TOAST      = By.cssSelector("li.chakra-toast .chakra-alert");

    // Navigation links
    public static final By REGISTER_LINK    = By.id("link-register");
    public static final By FORGOT_PWD_LINK  = By.id("link-reset-password");
}
```

**Why this matters:** All four locators above use `id` or stable attribute selectors. If the Chakra UI version changes and regenerates class hashes, these locators still work.

---

## 6. Creating LoginPage.java

Create `src/test/java/pages/LoginPage.java`. Each method represents one user action on the page. Tests call `loginPage.login(user, pass)` rather than manipulating DOM elements directly.

```java
package pages;

import locators.LoginPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Login page (/login).
 */
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the login page. */
    public void navigate() {
        navigateTo("/login");
    }

    /** Types the given username into the email/username field. */
    public void enterUsername(String username) {
        type(LoginPageLocators.USERNAME_INPUT, username);
    }

    /** Types the given password into the password field. */
    public void enterPassword(String password) {
        type(LoginPageLocators.PASSWORD_INPUT, password);
    }

    /** Clicks the login submit button. */
    public void clickLoginButton() {
        click(LoginPageLocators.LOGIN_BUTTON);
    }

    /** Navigates to the login page and submits the given credentials in one call. */
    public void login(String username, String password) {
        navigate();
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Waits up to 15 seconds for the browser to navigate away from /login.
     * Use this after calling login() — CI runners are slower than local
     * machines and a fixed Thread.sleep causes flaky failures.
     */
    public void waitForLoginRedirect() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/login"));
    }

    /** Waits for a Chakra UI error toast to appear and returns its text. */
    public String getErrorToastText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageLocators.ERROR_TOAST));
            return driver.findElement(LoginPageLocators.ERROR_TOAST).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Returns true if an error toast is currently visible. */
    public boolean isErrorToastDisplayed() {
        return !getErrorToastText().isEmpty();
    }

    /** Returns true if the current URL contains "/login". */
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    /** Clicks the register navigation link. */
    public void clickRegisterLink() {
        click(LoginPageLocators.REGISTER_LINK);
    }

    /** Clicks the forgot-password navigation link. */
    public void clickForgotPasswordLink() {
        click(LoginPageLocators.FORGOT_PWD_LINK);
    }
}
```

**Why this matters:** Step definitions call page methods by intent (`loginPage.clickLoginButton()`), not by implementation (`driver.findElement(By.id("btn-login")).click()`). If the button's locator changes, only `LoginPageLocators` needs updating.

---

## 7. Writing Feature Files in Gherkin

Create `src/test/resources/features/login.feature`. Feature files describe behaviour in plain English using the Given/When/Then vocabulary.

```gherkin
@web @auth
Feature: Login
  As a Resonance user
  I want to log in to the application
  So that I can access the available features

  # ${usernameOrEmailResonance} and ${passwordResonance} are resolved at runtime
  # from config.properties via ConfigReader.resolve() — credentials are never hardcoded here.

  Background:
    Given I am on the login page

  @positive
  Scenario: Login succeeds with valid credentials
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password "${passwordResonance}"
    And  I click the Login button
    Then I should be redirected away from the login page
    And  I should be on the dashboard page

  @negative
  Scenario: Login fails with unregistered email
    When I enter username "unregistered_user@fake.com"
    And  I enter password "password"
    And  I click the Login button
    Then I should see an error or stay on the login page

  @negative
  Scenario: Login fails with wrong password
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password "wrongpassword123"
    And  I click the Login button
    Then I should see an error or stay on the login page

  @negative
  Scenario: Login fails when username is empty
    When I enter username ""
    And  I enter password "password"
    And  I click the Login button
    Then I should stay on the login page

  @negative
  Scenario: Login fails when password is empty
    When I enter username "${usernameOrEmailResonance}"
    And  I enter password ""
    And  I click the Login button
    Then I should stay on the login page

  @negative
  Scenario Outline: Login fails with various invalid credential combinations
    When I enter username "<username>"
    And  I enter password "<password>"
    And  I click the Login button
    Then I should see an error or stay on the login page

    Examples:
      | username                    | password      |
      | unknown_user@fake.com       | password      |
      | ${usernameOrEmailResonance} | wrongpassword |
      | ${usernameOrEmailResonance} | ab            |

  @positive
  Scenario: Login page displays Register and Forgot Password links
    Then I should see a link to the register page
    And  I should see a forgot password link
```

### Scenario Outline + Examples for Data-Driven Testing

`Scenario Outline` is Gherkin's equivalent of a DataProvider. The `<placeholder>` syntax binds to columns in the `Examples` table. Cucumber executes the scenario once per row — exactly like TestNG's `Object[][]` pattern.

```gherkin
Scenario Outline: Login fails with various invalid credential combinations
  When I enter username "<username>"        # <-- bound to column
  And  I enter password "<password>"        # <-- bound to column
  ...
  Examples:
    | username              | password      |
    | unknown_user@fake.com | password      |   # row 1 execution
    | user1                 | wrongpassword |   # row 2 execution
```

**Why this matters:** Without `Scenario Outline`, you would copy-paste the same three Given/When/Then steps for each credential combination. The Examples table keeps all variations visible in one place.

---

## 8. The ${configKey} Placeholder Pattern

Feature files use `${configKey}` placeholders so credentials are never hardcoded in version-controlled files. The resolution happens in step definitions via `ConfigReader.resolve()`.

### How it works

```
Feature file:    When I enter username "${usernameOrEmailResonance}"
                                        ↓
Step definition: String resolved = ConfigReader.resolve(username);
                                        ↓
ConfigReader:    detects ${ } pattern → looks up "usernameOrEmailResonance" in config.properties
                                        ↓
Runtime value:   "user1"
```

### ConfigReader.resolve() implementation

```java
public static String resolve(String value) {
    if (value != null && value.startsWith("${") && value.endsWith("}")) {
        String key = value.substring(2, value.length() - 1);
        String resolved = properties.getProperty(key);
        if (resolved == null) {
            throw new RuntimeException("Config key not found: " + key);
        }
        return resolved;
    }
    return value;  // literal strings like "wrongpassword" pass through unchanged
}
```

Literal strings like `"wrongpassword123"` do not match `${...}` and pass through unchanged — so negative test data stays readable in the feature file without any special escaping.

**Why this matters:** If you ever rotate the test account password, you change exactly one line in `config.properties`. Every feature file that references `${passwordResonance}` picks up the new value automatically.

---

## 9. Creating Step Definitions

### LoginSteps.java

Create `src/test/java/steps/LoginSteps.java`. Each `@Given`, `@When`, `@Then` method maps to one step in the feature file. Cucumber matches by the string pattern.

```java
package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.LoginPage;
import utils.ConfigReader;
import utils.DriverManager;

/**
 * Step definitions for the login feature.
 *
 * All string parameters from the feature file are passed through
 * ConfigReader.resolve() before use. This allows feature files to reference
 * config.properties values via ${key} placeholders instead of hardcoding
 * credentials.
 *
 * Examples:
 *   Feature file: When I enter username "${usernameOrEmailResonance}"
 *   Step def:     loginPage.enterUsername(ConfigReader.resolve(username))
 *   Runtime:      loginPage.enterUsername("user1")
 */
public class LoginSteps {

    private final LoginPage loginPage = new LoginPage(DriverManager.getDriver());

    @Given("I am on the login page")
    public void iAmOnLoginPage() {
        loginPage.navigate();
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected to be on the login page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @When("I enter username {string}")
    public void iEnterUsername(String username) {
        String resolved = ConfigReader.resolve(username);
        if (!resolved.isEmpty()) {
            loginPage.enterUsername(resolved);
        }
    }

    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        String resolved = ConfigReader.resolve(password);
        if (!resolved.isEmpty()) {
            loginPage.enterPassword(resolved);
        }
    }

    @When("I click the Login button")
    public void iClickLoginButton() {
        loginPage.clickLoginButton();
        sleep(2000);
    }

    @Then("I should be redirected away from the login page")
    public void iShouldBeRedirectedAwayFromLogin() {
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Expected to leave the login page after successful login. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should be on the dashboard page")
    public void iShouldBeOnDashboard() {
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Expected to be on the dashboard. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should see an error or stay on the login page")
    public void iShouldSeeErrorOrStayOnLogin() {
        boolean onLoginPage = loginPage.isOnLoginPage();
        boolean hasError    = loginPage.isErrorToastDisplayed();
        Assert.assertTrue(onLoginPage || hasError,
                "Expected to stay on login page or see an error. URL: "
                        + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should stay on the login page")
    public void iShouldStayOnLoginPage() {
        sleep(1500);
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Expected to remain on the login page. URL: " + DriverManager.getDriver().getCurrentUrl());
    }

    @Then("I should see a link to the register page")
    public void iSeeRegisterLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.REGISTER_LINK),
                "Register link should be visible on the login page");
    }

    @Then("I should see a forgot password link")
    public void iSeeForgotPasswordLink() {
        Assert.assertTrue(loginPage.isDisplayed(locators.LoginPageLocators.FORGOT_PWD_LINK),
                "Forgot password link should be visible on the login page");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```

**Why this matters:** The `{string}` capture group in `@When("I enter username {string}")` is Cucumber Expression syntax. It captures the text between quotes in the feature file step and passes it as the method parameter. This is how `"${usernameOrEmailResonance}"` becomes the `username` argument.

### Hooks.java

Create `src/test/java/steps/Hooks.java`. `@Before` starts the browser before each scenario; `@After` quits the browser and captures a screenshot on failure.

```java
package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.DriverManager;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @Before
    public void setUp() {
        DriverManager.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            // Attach to Allure/Cucumber report — appears inline on the failed step
            scenario.attach(screenshot, "image/png", "Screenshot - " + scenario.getName());

            // Save to build/screenshots/ for CI artifact upload
            try {
                File dir = new File("build/screenshots");
                dir.mkdirs();
                String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
                File file = new File(dir, ts + "_" + safeName + ".png");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(screenshot);
                }
                System.out.println("[SCREENSHOT] Saved: " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[SCREENSHOT] Failed to save: " + e.getMessage());
            }
        }
        DriverManager.quitDriver();
    }
}
```

**Why this matters:** `scenario.isFailed()` means screenshots are only taken on failure, keeping build artifacts clean on green runs. The dual output — `scenario.attach()` for in-report viewing and file write for CI artifact download — means you can debug failures without re-running locally.

---

## 10. CommonSteps.java — WebDriverWait vs Thread.sleep

Create `src/test/java/steps/CommonSteps.java` for reusable steps shared across features (navigation, waits).

```java
package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverManager;

import java.time.Duration;

public class CommonSteps {

    @Given("I navigate to {string}")
    public void iNavigateTo(String path) {
        DriverManager.getDriver().get(path);
    }

    @When("I wait for the page to load")
    public void iWaitForPageLoad() {
        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(15))
                .until(d -> d.executeScript("return document.readyState").equals("complete"));
    }
}
```

### WebDriverWait vs Thread.sleep

| Approach | Behaviour | CI reliability |
|---|---|---|
| `Thread.sleep(2000)` | Always waits 2 seconds, even if element is ready in 200ms | Flaky — CI machines are slower; 2s is sometimes not enough |
| `WebDriverWait` | Polls until condition is true, up to a timeout | Reliable — passes as soon as the condition is met, fails fast if it isn't |

Use `WebDriverWait` for all production step definitions. `Thread.sleep` in `LoginSteps` is a pragmatic exception for a post-click animation delay where no specific DOM condition signals completion — but it should be replaced with a URL-condition wait whenever possible.

**Why this matters:** A test that passes locally with `sleep(2000)` and fails in CI 30% of the time is worse than a failing test — it erodes trust in the entire suite. Explicit waits eliminate this category of flakiness.

---

## 11. Creating CucumberRunner.java

Create `src/test/java/runner/CucumberRunner.java`. This class connects Cucumber to TestNG and configures where features, step definitions, and reports are located.

```java
package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runner connecting Cucumber/Gherkin to TestNG.
 * Executed by TestNG via web-testng.xml.
 *
 * Available tags:
 *   @web      — all web tests
 *   @positive — positive scenarios only
 *   @negative — negative scenarios only
 *   @auth     — authentication feature
 *   @ticket   — ticket feature
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue     = "steps",
    tags     = "@web",
    plugin   = {
        "pretty",
        "html:build/reports/cucumber/report.html",
        "json:build/reports/cucumber/report.json",
        "junit:build/reports/cucumber/report.xml"
    },
    monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
```

### @CucumberOptions explained

| Attribute | Value | Purpose |
|---|---|---|
| `features` | `src/test/resources/features` | Where Cucumber scans for `.feature` files |
| `glue` | `steps` | Package containing `@Given/@When/@Then` step def classes |
| `tags` | `@web` | Only runs scenarios tagged `@web`; change to `@positive` to run a subset |
| `plugin` | `pretty`, `html`, `json`, `junit` | Output formats — html for humans, json/junit for CI integrations |
| `monochrome` | `true` | Removes ANSI colour codes from console output (cleaner CI logs) |

**Why this matters:** The `tags` attribute is your primary filter mechanism. Running `tags = "@web and @positive"` executes only positive web scenarios — useful when debugging a specific subset without waiting for the full suite.

---

## 12. Creating web-testng.xml

Create `src/test/java/runner/web-testng.xml`. This file tells TestNG to run the `CucumberRunner` class, which in turn drives Cucumber.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Resonance Web Test Suite (Cucumber/Gherkin)" verbose="1" parallel="false">

    <test name="Web BDD Tests">
        <classes>
            <class name="runner.CucumberRunner"/>
        </classes>
    </test>

</suite>
```

**Why this matters:** The TestNG XML is the entry point that `build.gradle`'s `webTest` task references. Keeping a separate `web-testng.xml` means you can run web tests independently of API tests with `./gradlew webTest`.

---

## 13. Running the Web Tests

```bash
# Run only the web suite (headless by default per config.properties)
./gradlew webTest

# Run with verbose output
./gradlew webTest --info

# Clean previous results first
./gradlew clean webTest

# Run only positive scenarios (edit tags in CucumberRunner.java first, or pass via system property)
./gradlew webTest -Dcucumber.filter.tags="@web and @positive"
```

To run headed locally (see the browser), change `config.properties`:

```properties
headless=false
```

---

## 14. Screenshot on Failure

`Hooks.java` automatically captures a screenshot when a scenario fails. The screenshot is handled in two ways:

### 1. Attached to the Allure/Cucumber report

```java
scenario.attach(screenshot, "image/png", "Screenshot - " + scenario.getName());
```

When you open the Cucumber HTML report or Allure report, failed scenarios show an embedded screenshot inline on the failed step. No manual download needed.

### 2. Saved to build/screenshots/

```java
File dir = new File("build/screenshots");
dir.mkdirs();
String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
File file = new File(dir, ts + "_" + safeName + ".png");
```

Example output filename: `20260912_143022_Login_fails_with_wrong_password.png`

In CI (GitHub Actions), this folder is uploaded as an artifact:

```yaml
- name: Upload failure screenshots
  uses: actions/upload-artifact@v4
  if: failure()
  with:
    name: web-failure-screenshots-${{ github.run_number }}
    path: build/screenshots/
    retention-days: 30
```

`if: failure()` means screenshots are only uploaded when at least one scenario fails — reducing artifact noise on passing runs.

**Why this matters:** Without screenshots, diagnosing a CI failure means guessing what the browser showed at the moment of failure. With screenshots, you open the artifact, see the exact state, and know within seconds whether it is a broken locator, a missing element, or a timing issue.

---

## 15. Viewing the Allure Report

The `allure-cucumber7-jvm` dependency automatically writes Allure results during the test run.

```bash
# Install Allure CLI (macOS)
brew install allure

# Generate and open report in browser
allure serve build/allure-results
```

The report shows:
- Pass/fail per scenario
- Step-level breakdown (which Given/When/Then step failed)
- Screenshot attachments on failed scenarios
- Duration timeline per scenario

### CI Combined Report

The GitHub Actions workflow merges API and web Allure results into a single report:

```yaml
allure-report:
  name: Publish Allure Report
  needs: [ api-tests, web-tests ]
  if: always()
  steps:
    - name: Merge Allure results
      run: |
        mkdir -p allure-results/merged
        cp -r allure-results/api/. allure-results/merged/ 2>/dev/null || true
        cp -r allure-results/web/. allure-results/merged/ 2>/dev/null || true

    - name: Generate Allure HTML report
      run: allure generate allure-results/merged -o allure-report --clean

    - name: Upload Allure HTML report
      uses: actions/upload-artifact@v4
      with:
        name: allure-html-report-${{ github.run_number }}
        path: allure-report/
        retention-days: 30
```

Download the `allure-html-report-N` artifact from the GitHub Actions run page, extract the zip, and open `index.html` in any browser.

---

## Directory Structure Summary

```
src/
  main/
    java/
      utils/
        ConfigReader.java
        JsonFileManager.java
        Utils.java
    resources/
      config.properties
  test/
    java/
      locators/
        LoginPageLocators.java
      pages/
        BasePage.java
        LoginPage.java
      runner/
        CucumberRunner.java
        web-testng.xml
      steps/
        Hooks.java
        LoginSteps.java
        CommonSteps.java
      utils/
        DriverManager.java
    resources/
      features/
        login.feature
.github/
  workflows/
    tests.yml
```

## Complete Flow Recap

```
./gradlew webTest
    ↓
web-testng.xml → CucumberRunner
    ↓
Cucumber scans src/test/resources/features/*.feature
    ↓
Hooks.@Before → DriverManager.initDriver() → Chrome starts (headless in CI)
    ↓
Feature file scenario runs → each step matched to LoginSteps method
    ↓
"${usernameOrEmailResonance}" → ConfigReader.resolve() → "user1"
    ↓
LoginPage.login("user1", "password") → Selenium actions
    ↓
Assert checks URL / toast visibility
    ↓
Hooks.@After → screenshot on failure → DriverManager.quitDriver()
    ↓
Allure results written to build/allure-results/
    ↓
allure serve build/allure-results → HTML report in browser
```
