package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runner Cucumber untuk web test berbasis Gherkin.
 * Dijalankan oleh TestNG melalui web-testng.xml.
 *
 * Tags yang tersedia:
 *   @web     — semua web test
 *   @positive — hanya skenario positif
 *   @negative — hanya skenario negatif
 *   @auth    — fitur autentikasi
 *   @ticket  — fitur tiket
 *   @history — fitur riwayat
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
