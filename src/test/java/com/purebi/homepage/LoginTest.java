package com.purebi.homepage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.purebi.utils.ScreenshotUtil;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = "login")
    public void loginTest() {

    driver.findElement(By.id("login")).sendKeys("zld");
    driver.findElement(By.id("password")).sendKeys("Bbpuram@&zsrnf12");
    driver.findElement(com.purebi.utils.Locators.LOGIN_BUTTON).click();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    try {
        // SUCCESS CASE
        WebElement plantSettings = wait.until(
            ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.PLANT_SETTINGS));

        Reporter.log("✅ Login successful. PLANT SETTINGS is visible.", true);
        Assert.assertTrue(plantSettings.isDisplayed());


    } catch (Exception e) {
        // FAILURE CASE
        try {
                if (driver.findElements(com.purebi.utils.Locators.ERROR_MESSAGE).size() > 0) {

                String errorMsg = driver.findElement(
                    com.purebi.utils.Locators.ERROR_MESSAGE
                ).getText();

                String shot = ScreenshotUtil.takeScreenshot(driver, "login_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                try {
                    driver.quit();
                } catch (Exception ignore) {
                } finally {
                    driver = null;
                }

                Reporter.log("❌ Login failed with error message: <b>" + errorMsg + "</b>", true);
                Assert.fail("Login failed: " + errorMsg);

            } else {
                String shot = ScreenshotUtil.takeScreenshot(driver, "login_failed_no_message");
                Reporter.log("Screenshot saved: " + shot, true);
                try {
                    driver.quit();
                } catch (Exception ignore) {
                } finally {
                    driver = null;
                }

                Reporter.log("❌ Login failed: PLANT SETTINGS not found and no error message displayed", true);
                Assert.fail("Unknown login failure");
            }
        } catch (Exception inner) {
            Reporter.log("Exception while handling login failure: " + inner.getMessage(), true);
            Assert.fail("Login failure handling error: " + inner.getMessage());
        }
    }
}
}