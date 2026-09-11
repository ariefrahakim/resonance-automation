package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;

/**
 * Abstract base class for all page objects, providing shared WebDriver helpers and a 15-second explicit wait.
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String baseUrl = ConfigReader.getProperty("webUrl");

    /** Initialises the page object with the given WebDriver instance. */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** Waits until the element identified by {@code locator} is visible and returns it. */
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element identified by {@code locator} is clickable and returns it. */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Waits for the element to be clickable and clicks it. */
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    /** Scrolls the element into view then clicks via JavaScript — bypasses animation overlays. */
    protected void jsClick(By locator) {
        WebElement el = waitForElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", el);
    }

    /** Clears the element identified by {@code locator} and types the given text. */
    protected void type(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Returns the visible text of the element identified by {@code locator}. */
    protected String getText(By locator) {
        return waitForElement(locator).getText();
    }

    /** Returns {@code true} if the element is present and displayed, {@code false} otherwise. */
    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Navigates the browser to {@code baseUrl + path}. */
    protected void navigateTo(String path) {
        driver.get(baseUrl + path);
    }
}
