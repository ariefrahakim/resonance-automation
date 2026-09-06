package pages;

import locators.NewTicketPageLocators;
import org.openqa.selenium.WebDriver;

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

    public void togglePublic() {
        click(NewTicketPageLocators.IS_PUBLIC_TOGGLE);
    }

    public void submitTicket() {
        click(NewTicketPageLocators.SUBMIT_BUTTON);
    }

    public void createTicket(String title, String description) {
        navigate();
        enterTitle(title);
        enterDescription(description);
        submitTicket();
    }

    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(NewTicketPageLocators.SUCCESS_MESSAGE);
    }
}
