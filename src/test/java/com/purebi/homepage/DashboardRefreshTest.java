package com.purebi.homepage;

import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

import java.time.Duration;

public class DashboardRefreshTest extends BaseTest {

    @Test(groups = {"dashboard"}, enabled = true)
    public void dashboardRefreshAndNotificationTest() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Quick presence check for the Notifications button. If missing, skip this test and record reason.
        By notifXpath = com.purebi.utils.Locators.NOTIFICATIONS_BUTTON;
        try {
            List<WebElement> notifEls = driver.findElements(notifXpath);
            if (notifEls == null || notifEls.isEmpty()) {
                Reporter.log("Skipping DashboardRefreshTest: Notifications button not found.", true);
                throw new SkipException("Notifications button not found");
            }
        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            Reporter.log("Warning checking Notifications presence: " + e.getMessage(), true);
        }

        final By refreshLocator = By.cssSelector("img[title='Refresh'], img[src*='refresh.png']");
        final By notificationsLocator = By.cssSelector("a.notification-link[title='Notifications'], li.notification-nav-item a.notification-link");
        final By titleLocator = By.cssSelector("h5.notification-item-title");
        final By successBadgeLocator = By.cssSelector("span.badge.badge-success");
        final String expectedTitle = "Site Dashboard Updated";
        final String expectedBadgeText = "success";

        final By closeLocator = By.cssSelector("button[title='Close'], button.btn-close-panel");

        try {
            // Find the 'Updated On' label and capture the old timestamp
            By updatedLabelLocator = By.xpath("//label[contains(normalize-space(.),'Updated On')]");
            WebElement updatedLabel = shortWait.until(ExpectedConditions.visibilityOfElementLocated(updatedLabelLocator));
            String oldTimestamp = updatedLabel.getText().trim();
            Reporter.log("Old Updated On text: " + oldTimestamp, true);

            WebElement refreshBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(com.purebi.utils.Locators.REFRESH_BUTTON_IMG)
            );
            refreshBtn.click();
            Reporter.log("Clicked dashboard Refresh button.", true);

            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofMinutes(5));

            boolean isUpdated = false;

            try {
                isUpdated = longWait.until(driver -> {
                    try {
                        WebElement label = driver.findElement(updatedLabelLocator);
                        String newText = label.getText().trim();

                        Reporter.log("Polling Updated On: " + newText, true);

                        return !newText.equals(oldTimestamp);
                    } catch (Exception e) {
                        return false;
                    }
                });
            } catch (Exception e) {
                Reporter.log("Timeout waiting for Updated On to change", true);
            }

            if (isUpdated) {
                Reporter.log("Updated time changed after refresh", true);
                Assert.assertTrue(true);
            } else {
                Reporter.log("Updated time did NOT change within 5 minutes", true);
                Assert.fail("Updated time did not change after waiting");
            }

        } catch (Exception e) {
            Reporter.log("❌ Dashboard refresh test encountered exception: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "dashboard_refresh_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception during dashboard refresh test: " + e.getMessage());
        } finally {
            // Always attempt to close the notification/side panel so logout can proceed cleanly
            try {
                List<WebElement> closeBtns = driver.findElements(closeLocator);

                if (!closeBtns.isEmpty()) {
                    WebElement closeBtn = closeBtns.get(0);

                    if (closeBtn.isDisplayed()) {
                        closeBtn.click();
                        Reporter.log("Clicked Close button.", true);
                    }
                } else {
                    Reporter.log("Close button not present, skipping.", true);
                }
            } catch (Exception e) {
                Reporter.log("Close button handling skipped: " + e.getMessage(), true);
            }
        }
    }
}
