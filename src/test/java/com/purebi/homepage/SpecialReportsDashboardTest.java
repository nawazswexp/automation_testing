package com.purebi.homepage;

import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.Duration;

public class SpecialReportsDashboardTest extends BaseTest {

    @Test
    public void specialReportsDashboardCheck() {
        // Skip if login failed
        try {
            Boolean loginPassed = com.purebi.utils.TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping SpecialReportsDashboardTest because login failed.", true);
                throw new SkipException("Login failed");
            }
        } catch (Exception ignored) {
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By dashboardList = By.xpath("//h4[normalize-space()='Dashboard Report list']");

        try {
            try {
                clickSpecialReports();
                Reporter.log("Clicked SPECIAL REPORTS link.", true);
            } catch (Exception clickEx) {
                Reporter.log("Failed to click SPECIAL REPORTS link: " + clickEx.getMessage(), true);
                String shot = ScreenshotUtil.takeScreenshot(driver, "special_reports_click_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                // continue to attempt to find the dashboard list
            }

            // Verify the dashboard report list header
            wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardList));
            Reporter.log("✅ Dashboard Report list present. Test passed.", true);

        } catch (Exception e) {
            Reporter.log("❌ Dashboard Report list not found or other error: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "special_reports_missing");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Dashboard Report list not found after clicking SPECIAL REPORTS");
        } finally {
            // Always attempt to go back to home so LogoutTest can run cleanly
            try {
                com.purebi.homepage.BackToHomeScreen.navigateToHomeScreen(driver);
                Reporter.log("Returned to Home screen after SpecialReportsDashboardTest.", true);
            } catch (Exception ex) {
                Reporter.log("Failed to navigate back home: " + ex.getMessage(), true);
            }
        }
    }
}
