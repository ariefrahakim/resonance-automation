package locators;

import org.openqa.selenium.By;

/**
 * Locator terpusat untuk halaman Login (/login).
 * Menggunakan ID atribut nyata dari halaman Resonance.
 */
public class LoginPageLocators {
    // Input fields
    public static final By USERNAME_INPUT    = By.id("input-email-login");
    public static final By PASSWORD_INPUT    = By.id("input-password-login");

    // Buttons
    public static final By LOGIN_BUTTON      = By.id("btn-login");

    // Error toast - Chakra UI toast muncul di chakra-toast-manager-top
    public static final By ERROR_TOAST       = By.cssSelector("li.chakra-toast .chakra-alert[data-status='error']");
    public static final By ANY_TOAST         = By.cssSelector("li.chakra-toast .chakra-alert");

    // Navigation links
    public static final By REGISTER_LINK     = By.id("link-register");
    public static final By FORGOT_PWD_LINK   = By.id("link-reset-password");
}
