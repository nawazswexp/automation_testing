package com.purebi.homepage;

import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class GaugeTest extends BaseTest {

    @Test(groups = {"visual"}, alwaysRun = true)
    public void gaugeEchartCanvasPresenceAfterOpex() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By canvasXpath = By.xpath("//div[@class='col-md-7']//div[@id='echart']//div//canvas");
        try {
            WebElement canvas = wait.until(ExpectedConditions.visibilityOfElementLocated(canvasXpath));
            Reporter.log("Found echart canvas using: " + canvasXpath.toString(), true);
            Assert.assertTrue(canvas.isDisplayed(), "Echart canvas is not displayed");
        } catch (Exception e) {
            Reporter.log("❌ Echart canvas not found: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "gauge_echart_canvas_missing_after_opex");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Echart canvas not found: " + e.getMessage());
        }
    }
}
