package com.purebi.homepage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Utility to return to the application's home/dashboard screen.
 * This class is intentionally NOT a TestNG test class and contains
 * no TestNG annotations or lifecycle methods.
 */
public class BackToHomeScreen {

    private BackToHomeScreen() {
        // prevent instantiation
    }

    /**
     * Clicks the site logo to navigate to the home screen.
     * Call explicitly from tests when needed.
     *
     * @param driver active WebDriver instance
     */
    public static void navigateToHomeScreen(WebDriver driver) {
        if (driver == null) return;
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            By logo = By.xpath("//img[@class='logo']");
            WebElement logoEl = wait.until(ExpectedConditions.elementToBeClickable(logo));
            logoEl.click();
        } catch (Exception ignored) {
            // Let calling test handle failures (optionally take screenshot).
        }
    }

}
