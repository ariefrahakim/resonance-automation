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

            // Attach to Allure/Cucumber report
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
