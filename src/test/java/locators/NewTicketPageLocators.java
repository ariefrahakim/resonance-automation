package locators;

import org.openqa.selenium.By;

/**
 * Centralised locators for the new ticket creation page (/new).
 * Public/Private controls are clickable divs, not native checkbox inputs.
 */
public class NewTicketPageLocators {
    // Form fields
    public static final By TITLE_INPUT          = By.id("input-ticket-title");
    public static final By DESCRIPTION_INPUT    = By.id("textarea-ticket-description");

    // Public/Private toggles (Chakra UI custom div — not a native <input>)
    public static final By PUBLIC_TOGGLE        = By.id("checkbox-ticket-public");
    public static final By PRIVATE_TOGGLE       = By.id("checkbox-ticket-private");

    // File upload
    public static final By FILE_UPLOAD_BTN      = By.id("btn-upload-file");
    public static final By FILE_UPLOAD_INPUT    = By.id("file-upload");

    // Buttons
    public static final By SUBMIT_BUTTON        = By.id("btn-submit-ticket");
    public static final By BACK_BUTTON          = By.id("btn-back-dashboard");

    // Success/error toast
    public static final By SUCCESS_TOAST        = By.cssSelector("li.chakra-toast .chakra-alert[data-status='success']");
    public static final By ERROR_TOAST          = By.cssSelector("li.chakra-toast .chakra-alert[data-status='error']");
}
