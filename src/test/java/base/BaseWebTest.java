package base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import pages.*;
import utils.ConfigReader;
import utils.DriverManager;

public class BaseWebTest {
    protected LoginPage loginPage;
    protected HomePage homePage;
    protected NewTicketPage newTicketPage;
    protected TicketDetailPage ticketDetailPage;
    protected HistoryPage historyPage;

    @BeforeClass
    public void setup() {
        DriverManager.initDriver();
        loginPage = new LoginPage(DriverManager.getDriver());
        homePage = new HomePage(DriverManager.getDriver());
        newTicketPage = new NewTicketPage(DriverManager.getDriver());
        ticketDetailPage = new TicketDetailPage(DriverManager.getDriver());
        historyPage = new HistoryPage(DriverManager.getDriver());
    }

    protected void doLogin() {
        String username = ConfigReader.getProperty("usernameOrEmailResonance");
        String password = ConfigReader.getProperty("passwordResonance");
        loginPage.login(username, password);
    }

    @AfterClass
    public void teardown() {
        DriverManager.quitDriver();
    }
}
