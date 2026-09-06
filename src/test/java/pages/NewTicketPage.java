package pages;

import locators.NewTicketPageLocators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NewTicketPage extends BasePage {

    public NewTicketPage(WebDriver driver) {
        super(driver);
    }

    public void navigate() {
        navigateTo("/new");
    }

    public void enterTitle(String title) {
        type(NewTicketPageLocators.TITLE_INPUT, title);
    }

    public void enterDescription(String description) {
        type(NewTicketPageLocators.DESCRIPTION_INPUT, description);
    }

    /** Tiket publik: klik div#checkbox-ticket-public (bukan native input). */
    public void selectPublic() {
        click(NewTicketPageLocators.PUBLIC_TOGGLE);
    }

    /** Tiket private: klik div#checkbox-ticket-private. */
    public void selectPrivate() {
        click(NewTicketPageLocators.PRIVATE_TOGGLE);
    }

    public void clickSubmit() {
        click(NewTicketPageLocators.SUBMIT_BUTTON);
    }

    public void clickBack() {
        click(NewTicketPageLocators.BACK_BUTTON);
    }

    public void createTicket(String title, String description) {
        navigate();
        enterTitle(title);
        enterDescription(description);
        clickSubmit();
    }

    public boolean isSuccessToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.SUCCESS_TOAST));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorToastDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(NewTicketPageLocators.ERROR_TOAST));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnNewPage() {
        return driver.getCurrentUrl().contains("/new");
    }
}
