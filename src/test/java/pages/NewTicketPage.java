package pages;

import locators.NewTicketPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the new ticket creation page (/new).
 */
public class NewTicketPage extends BasePage {

    /** Initialises the NewTicketPage with the given WebDriver instance. */
    public NewTicketPage(WebDriver driver) {
        super(driver);
    }

    /** Navigates the browser to the new-ticket page. */
    public void navigate() {
        navigateTo("/new");
    }

    /** Types the given title into the ticket title input. */
    public void enterTitle(String title) {
        type(NewTicketPageLocators.TITLE_INPUT, title);
    }

    /** Types the given description into the ticket description textarea. */
    public void enterDescription(String description) {
        type(NewTicketPageLocators.DESCRIPTION_INPUT, description);
    }

    /** Selects the Public visibility option by clicking div#checkbox-ticket-public (not a native input). */
    public void selectPublic() {
        click(NewTicketPageLocators.PUBLIC_TOGGLE);
    }

    /** Selects the Private visibility option by clicking div#checkbox-ticket-private. */
    public void selectPrivate() {
        click(NewTicketPageLocators.PRIVATE_TOGGLE);
    }

    /** Clicks the submit button via JS to ensure the event fires in both local and CI headless Chrome. */
    public void clickSubmit() {
        jsClick(NewTicketPageLocators.SUBMIT_BUTTON);
    }

    /**
     * Returns {@code true} if a success toast or URL change (redirect away from /new) is detected.
     * Uses a short 5s window because Chakra UI toasts are transient — a 15s wait overshoots
     * and the toast will have already disappeared by then.
     */
    public boolean isSuccessToastDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.SUCCESS_TOAST));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns {@code true} if an error toast is visible after ticket submission (5s window). */
    public boolean isErrorToastDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.ERROR_TOAST));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns {@code true} if the current URL corresponds to the new-ticket page. */
    public boolean isOnNewPage() {
        return driver.getCurrentUrl().contains("/new");
    }
}
