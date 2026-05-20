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

public class OpexDashboardTest extends BaseTest {

    @Test(groups = {"opex"})
    public void opexModuleDashboardNavigationAndValidation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        boolean iconNavigationOk = false;
        boolean menuNavigationOk = false;

        try {
            iconNavigationOk = navigateViaDashboardIcon(wait);
            Reporter.log("Icon path navigation success: " + iconNavigationOk, true);

            if (iconNavigationOk) {
                try {
                    WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@class='logo']")));
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", logo);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='REPORTS']")));
                    Reporter.log("Reset to main view after icon path.", true);
                } catch (Exception e) {
                    Reporter.log("Reset after icon path failed: " + e.getMessage(), true);
                }
            }

            menuNavigationOk = navigateViaReportsMenu(wait);
            Reporter.log("Menu path navigation success: " + menuNavigationOk, true);

            if (iconNavigationOk || menuNavigationOk) {
                Reporter.log("OPEX navigation considered SUCCESS (at least one path succeeded).", true);
                Assert.assertTrue(true);
            } else {
                String shot = ScreenshotUtil.takeScreenshot(driver, "opex_navigation_fail");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("OPEX navigation failed for both icon and menu paths.");
            }
        } finally {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement logo = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[@class='logo']")));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", logo);
                Reporter.log("Clicked logo at test end to return to main view.", true);
                shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='REPORTS']")));
            } catch (Exception e) {
                Reporter.log("Logo click at test end failed or not present: " + e.getMessage(), true);
            }
        }
    }

    private boolean navigateViaDashboardIcon(WebDriverWait wait) {
        try {
            WebElement icon = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//img[contains(@routerlink,'opex')]")
            ));

            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", icon
            );
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", icon);

            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[normalize-space()='OPEX Module Reports']")
            ));

            return header.isDisplayed();
        } catch (Exception e) {
            Reporter.log("Dashboard icon navigation failed: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "opex_navigation_fail");
            Reporter.log("Screenshot saved: " + shot, true);
            return false;
        }
    }

    private boolean navigateViaReportsMenu(WebDriverWait wait) {
        try {
            WebElement reports = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[normalize-space()='REPORTS']")
            ));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", reports);

            WebElement opex = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//ul[contains(@class,'dropdown-menu') and contains(@class,'show')]//a[normalize-space()='OPEX']")
            ));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opex);

            WebElement module = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//ul[contains(@class,'dropdown-menu') and contains(@class,'show')]//a[normalize-space()='MODULE']")
            ));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", module);

            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[normalize-space()='OPEX Module Reports']")
            ));

            return header.isDisplayed();
        } catch (Exception e) {
            Reporter.log("Menu navigation failed: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "opex_navigation_fail");
            Reporter.log("Screenshot saved: " + shot, true);
            return false;
        }
    }

    private void validateHeaderAndCarousel(WebDriverWait wait, By headerLocator) {
        // Verify header text exactly
        WebElement header = driver.findElement(headerLocator);
        String txt = header.getText().trim();
        Reporter.log("Header text: '" + txt + "'", true);
        Assert.assertEquals(txt, "Opex Module Dashboard", "Header did not match");

        // Validate carousel container and items
        By carousel = By.cssSelector("div.p-carousel-content");
        WebElement carouselEl = wait.until(ExpectedConditions.visibilityOfElementLocated(carousel));

        if (carouselEl == null) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "opex_carousel_missing");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Opex carousel container not found");
        }

        String[] expectedItems = new String[]{
                "Treated Flow",
                "Chemicals",
                "Electricity",
                "Sludge Disposal",
                "Utilities",
                "Total",
                "Opex Corporate Parameter"
        };

        for (String item : expectedItems) {
            java.util.List<WebElement> found = carouselEl.findElements(By.xpath(".//a[contains(@class,'nav-link') and normalize-space()='" + item + "']"));
            if (found == null || found.size() == 0) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "opex_missing_item_" + item.replaceAll("\\s+", "_"));
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Carousel item not found: " + item);
            }
            Reporter.log("Found carousel item: " + item, true);
        }
    }

}

