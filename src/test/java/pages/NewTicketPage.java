package pages;

import locators.NewTicketPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    /** Clicks the submit button to create the ticket. */
    public void clickSubmit() {
        click(NewTicketPageLocators.SUBMIT_BUTTON);
    }

    /** Clicks the back button to return to the dashboard. */
    public void clickBack() {
        click(NewTicketPageLocators.BACK_BUTTON);
    }

    /** Navigates to the new-ticket page, fills in the form, and submits it. */
    public void createTicket(String title, String description) {
        navigate();
        enterTitle(title);
        enterDescription(description);
        clickSubmit();
    }

    /** Returns {@code true} if a success toast is visible after ticket submission. */
    public boolean isSuccessToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.SUCCESS_TOAST));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns {@code true} if an error toast is visible after ticket submission. */
    public boolean isErrorToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.ERROR_TOAST));
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
