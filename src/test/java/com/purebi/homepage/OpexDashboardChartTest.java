package com.purebi.homepage;

import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class OpexDashboardChartTest extends BaseTest {

    @Test(groups = {"chart"})
    public void opexDashboardChartDataCheck() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            By chartContainer = By.id("echart");
            wait.until(ExpectedConditions.visibilityOfElementLocated(chartContainer));



                // New requirement: test passes if a list item with text 'Chemicals' exists on the page
                try {
                    By chem = By.xpath("//span[contains(@class,'listText') and normalize-space()='Chemicals']");
                    WebElement found = wait.until(ExpectedConditions.visibilityOfElementLocated(chem));
                    Reporter.log("Found list item: " + found.getText(), true);
                    Assert.assertTrue(true);
                } catch (Exception e) {
                    String shot = ScreenshotUtil.takeScreenshot(driver, "opex_no_chemicals");
                    Reporter.log("Screenshot saved: " + shot, true);
                    Assert.fail("Required list item 'Chemicals' not found on page");
                }

        } catch (Exception e) {
            Reporter.log("Exception during chart check: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "opex_chart_error");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Error extracting chart data: " + e.getMessage());
        }
    }
}
