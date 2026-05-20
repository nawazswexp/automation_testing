package com.purebi.homepage;

import com.purebi.homepage.BackToHomeScreen;
import com.purebi.utils.ScreenshotUtil;
import com.purebi.utils.TestExecutionControl;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.Duration;

public class UserTest extends BaseTest {

    @Test(description = "Verify Site User Settings page is accessible", alwaysRun = true)
    public void userSettingsPresence() {
        // Run only if login succeeded
        Boolean loginPassed = TestExecutionControl.getLoginPassed();
        if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
            Reporter.log("Skipping UserTest because login failed.", true);
            throw new SkipException("Login failed");
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            Actions actions = new Actions(driver);

            // Locate PLANT SETTINGS
            WebElement plantSettings = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[normalize-space()='PLANT SETTINGS']")
                )
            );

            // Scroll to element
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                plantSettings
            );

            // KEEP HOVER ACTIVE
            actions.moveToElement(plantSettings).pause(Duration.ofSeconds(2)).perform();
            Reporter.log("Hovered on PLANT SETTINGS.", true);

            // Now locate USER while hover is ACTIVE
            WebElement userMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space()='USER']")
                )
            );

            // Move directly from PLANT SETTINGS -> USER without losing hover
            actions.moveToElement(userMenu)
                   .pause(Duration.ofMillis(500))
                   .click()
                   .perform();
            Reporter.log("Clicked USER menu.", true);

            // Close dropdown overlay
            actions.sendKeys(Keys.ESCAPE).perform();
            Reporter.log("Pressed ESC to close dropdown.", true);

            // Small stabilization wait
            Thread.sleep(1000);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='Site User Settings']")));
            Reporter.log("Site User Settings header visible", true);

        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "user_settings_missing");
            Reporter.log("Screenshot saved: " + shot, true);
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
            Assert.fail("Site User Settings header not found: " + e.getMessage());
        }
    }
}
