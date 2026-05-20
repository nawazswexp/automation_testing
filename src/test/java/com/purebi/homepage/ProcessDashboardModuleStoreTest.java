package com.purebi.homepage;

import com.purebi.utils.TestExecutionControl;
import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.Duration;

public class ProcessDashboardModuleStoreTest extends BaseTest {

    @Test(alwaysRun = true, description = "Click process dashboard icon, check module store row, then return home")
    public void processDashboardModuleStoreCheck() {
        // Ensure this runs only when login succeeded; the LoginGatekeeperListener will also enforce skipping
        Boolean loginPassed = TestExecutionControl.getLoginPassed();
        if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
            Reporter.log("Skipping ProcessDashboardModuleStoreTest because login failed.", true);
            throw new SkipException("Login failed");
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By clickIcon = By.xpath("//img[@routerlink='/dashboard/processdashboard']");
        By locator = By.xpath("//div[@class='row mt-1 ng-star-inserted']");

        try {
            // Click the process dashboard icon to navigate
            try {
                WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(clickIcon));
                icon.click();
                Reporter.log("Clicked process dashboard icon: " + clickIcon.toString(), true);
            } catch (Exception e) {
                Reporter.log("Failed to click process dashboard icon: " + e.getMessage(), true);
                String shot = ScreenshotUtil.takeScreenshot(driver, "process_dashboard_icon_click_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                // Continue to attempt to find the element even if click failed
            }

            // Now validate presence of the required element
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Reporter.log("Found element: " + locator.toString(), true);
            Assert.assertTrue(el.isDisplayed(), "Module store row is not displayed");

        } catch (Exception e) {
            Reporter.log("Element not found after navigation: " + locator.toString() + " -> " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "process_dashboard_module_store_missing");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Required element not found: " + locator.toString());
        } finally {
            // Post-test action: always return to home screen by clicking the logo
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
                By logo = By.xpath("//img[@class='logo']");
                WebElement logoEl = shortWait.until(ExpectedConditions.elementToBeClickable(logo));
                logoEl.click();
                Reporter.log("Clicked home logo after ProcessDashboardModuleStoreTest.", true);
            } catch (Exception ex) {
                Reporter.log("Failed to click home logo after ProcessDashboardModuleStoreTest: " + ex.getMessage(), true);
            }
        }
    }
}
