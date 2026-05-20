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

public class SpecialReportPage extends BaseTest {

    @Test(description = "Run special reports navigation check; only when login succeeded")
    public void specialReportPageTest() {
        // Skip if login failed
        try {
            Boolean loginPassed = com.purebi.utils.TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping SpecialReportPage because login failed.", true);
                throw new SkipException("Login failed");
            }
        } catch (SkipException se) {
            throw se;
        } catch (Exception ignored) {
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            clickSpecialReports();
            Reporter.log("Clicked SPECIAL REPORTS.", true);

            By header = By.xpath("//h4[text()='Dashboard Report list']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(header));
            Reporter.log("✅ Dashboard Report list found.", true);
            Assert.assertTrue(true, "Dashboard Report list found");
        } catch (Exception e) {
            Reporter.log("❌ SpecialReportPage test failed: " + e.getMessage(), true);
            try {
                String shot = ScreenshotUtil.takeScreenshot(driver, "special_report_page_failed");
                Reporter.log("Screenshot saved: " + shot, true);
            } catch (Exception ignored) {}
            Assert.fail("Dashboard Report list not found: " + e.getMessage());
        } finally {
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
        }
    }
}
