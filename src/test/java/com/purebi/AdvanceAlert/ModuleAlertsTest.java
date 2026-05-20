package com.purebi.AdvanceAlert;

import com.purebi.homepage.BackToHomeScreen;
import com.purebi.homepage.BaseTest;
import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class ModuleAlertsTest extends BaseTest {

    @Test(groups = "module-alerts")
    public void moduleAlertFullFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Ensure home
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}

            Reporter.log("STEP 1 — Navigation: Clicking Home logo", true);
            By logo = By.xpath("//img[@class='logo']");
            clickWithFallback(wait, logo, "Home logo");

            // Hover and click Plant Settings
            By plantSettings = By.xpath("//span[normalize-space()='PLANT SETTINGS']");
            WebElement ps = wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            Actions actions = new Actions(driver);
            actions.moveToElement(ps).pause(Duration.ofSeconds(1)).perform();
            Reporter.log("Hovered on PLANT SETTINGS.", true);

            By advancedAlerts = By.xpath("//a[normalize-space()='ADVANCED ALERTS']");
            
            // Hover again before clicking submenu
            actions.moveToElement(ps)
                   .pause(Duration.ofSeconds(2))
                   .perform();

            Reporter.log("Hovered on PLANT SETTINGS again.", true);

            // Wait for submenu visibility instead of clickable
            WebElement adv = wait.until(
                ExpectedConditions.visibilityOfElementLocated(advancedAlerts)
            );

            // JS click because submenu disappears quickly
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", adv);

            Reporter.log("Clicked ADVANCED ALERTS.", true);

            // Wait for page load
            By pageLoaded = By.xpath("//div[contains(@class,'d-flex') and contains(@class,'justify-content-between') and contains(@class,'w-100')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageLoaded));
            Reporter.log("Advanced Alerts page loaded.", true);

            // STEP 2 — Create Module Alert
            Reporter.log("STEP 2 — Create Module Alert: Opening create modal", true);
            By createBtn = By.xpath("//button[contains(.,'CREATE NEW ALERT')]");
            clickWithFallback(wait, createBtn, "CREATE NEW ALERT");

            By modal = By.xpath("//div[contains(@class,'card-header') and contains(.,'CREATE NEW ALERT')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
            Reporter.log("Create modal visible.", true);

            // STEP 3 — Select Alert Type -> Module Alert
            By alertType = By.xpath("//select[@formcontrolname='alerttype']");
            selectAlertType(wait, alertType);

            // STEP 4 — Alert Name
            By alertName = By.xpath("//input[@formcontrolname='alertname']");
            wait.until(ExpectedConditions.elementToBeClickable(alertName)).sendKeys("Module Alert Test");
            Reporter.log("Entered alert name: Module Alert Test", true);

            // STEP 5 — Module
            By module = By.xpath("//select[@formcontrolname='module']");
            selectByVisibleText(wait, module, "RO-3");

            // STEP 6 — Condition
            By condition = By.xpath("//select[@formcontrolname='conditions']");
            selectByVisibleText(wait, condition, "Greater (>)");

            // STEP 7 — Assign To
            By assignTo = By.xpath("//select[@formcontrolname='assignto']");
            selectByVisibleText(wait, assignTo, "Site Admin");

            // STEP 8 — Score
            By score = By.xpath("//input[@formcontrolname='score']");
            wait.until(ExpectedConditions.elementToBeClickable(score)).sendKeys("70");
            Reporter.log("Entered score: 70", true);

            // STEP 9 — Take screenshot before saving
            String preShot = ScreenshotUtil.takeScreenshot(driver, "module_alert_before_save");
            Reporter.log("Screenshot saved: " + preShot, true);

            // Click Save
            By saveBtn = By.xpath("//button[normalize-space()='Save']");
            clickWithFallback(wait, saveBtn, "Save alert");
            Reporter.log("Clicked Save.", true);

            // STEP 10 — Validate Creation Toast
            By addedToast = By.xpath("//*[contains(text(),'Added successfully')]");
            try {
                WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                toastWait.until(ExpectedConditions.visibilityOfElementLocated(addedToast));
                Reporter.log("Toast: Added successfully visible.", true);
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_toast_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Creation toast 'Added successfully' not visible.");
            }

            // STEP 11 — Validate Alert Exists
            Reporter.log("STEP 11 — Validate Alert Exists: Reopening Advanced Alerts.", true);
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
            // Re-navigate
            ps = wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            actions.moveToElement(ps).pause(Duration.ofSeconds(1)).perform();
            adv = wait.until(ExpectedConditions.elementToBeClickable(advancedAlerts));
            try { adv.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", adv); }

            // Click Module Alert tab/filter
            By moduleTab = By.xpath("//a[contains(normalize-space(),'Module Alert')]");
            clickWithFallback(wait, moduleTab, "Module Alert tab");

            // Find AG Grid row
            String rowXpath = "//div[@role='row' and contains(.,'Module Alert Test')]";
            boolean found = false;
            for (int i = 0; i < 8; i++) {
                if (driver.findElements(By.xpath(rowXpath)).size() > 0) { found = true; break; }
                try {
                    By gridViewport = By.xpath("//div[contains(@class,'ag-body-vertical-scroll-viewport')]");
                    WebElement viewport = wait.until(ExpectedConditions.visibilityOfElementLocated(gridViewport));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop += arguments[0].clientHeight;", viewport);
                    Thread.sleep(300);
                } catch (Exception ignored) {}
            }

            if (!found) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_row_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Created Module Alert row not found in AG Grid.");
            }

            Reporter.log("Module Alert row found in AG Grid.", true);

            // STEP 12 — Delete Alert
            Reporter.log("STEP 12 — Delete Alert: Clicking delete icon on row.", true);
            By deleteIcon = By.xpath(rowXpath + "//i[@data-action='delete']");
            clickWithFallback(wait, deleteIcon, "Delete icon");

            // STEP 13 — Delete Confirmation Popup
            By deleteConfirmationTitle = By.xpath("//*[contains(normalize-space(),'DELETE CONFIRMATION')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteConfirmationTitle));

            By popupDeleteBtn = By.xpath("//button[normalize-space()='DELETE']");
            clickWithFallback(wait, popupDeleteBtn, "Popup DELETE button");

            // STEP 14 — Validate Delete Toast
            By deletedToast = By.xpath("//*[contains(text(),'Deleted successfully')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deletedToast));

            // STEP 15 — Validate Deletion
            Reporter.log("STEP 15 — Validate Deletion: Ensuring row no longer exists.", true);
            Thread.sleep(2000);
            if (driver.findElements(By.xpath(rowXpath)).size() > 0) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_delete_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Module Alert row still exists after deletion.");
            }

            Reporter.log("Module Alert flow completed successfully.", true);

        } catch (AssertionError ae) {
            Reporter.log("Assertion failed: " + ae.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_assertion_failure");
            Reporter.log("Screenshot saved: " + shot, true);
            throw ae;
        } catch (Exception e) {
            Reporter.log("Exception in ModuleAlertsTest: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception during Module Alert flow: " + e.getMessage());
        }
    }

    // Helper: click with JS fallback
    private void clickWithFallback(WebDriverWait wait, By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            try { el.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
            Reporter.log("Clicked: " + label, true);
        } catch (Exception e) {
            Reporter.log("Failed to click: " + label + " -> " + e.getMessage(), true);
            throw e;
        }
    }

    // Helper: select by visible text
    private void selectByVisibleText(WebDriverWait wait, By locator, String visibleText) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Select sel = new Select(el);
            sel.selectByVisibleText(visibleText);
            Reporter.log("Selected '" + visibleText + "' on " + locator.toString(), true);
        } catch (Exception e) {
            Reporter.log("Failed to select '" + visibleText + "' on " + locator.toString() + " -> " + e.getMessage(), true);
            throw e;
        }
    }

    // Helper: select Module Alert by visible text and log trimmed options
    private void selectAlertType(WebDriverWait wait, By locator) {
        try {
            Reporter.log("Selecting Module Alert type...", true);

            wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            // Re-fetch immediately before creating Select to avoid stale/dynamic binding issues.
            WebElement freshDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            );

            Select alertTypeSelect = new Select(freshDropdown);

            for (WebElement option : alertTypeSelect.getOptions()) {
                Reporter.log(
                        "Alert Type Option: '" + option.getText().trim() + "'",
                        true
                );
            }

            alertTypeSelect.selectByVisibleText("Module Alert");

            String selectedText = alertTypeSelect.getFirstSelectedOption().getText().trim();
            Assert.assertEquals(selectedText, "Module Alert", "Selected alert type mismatch.");

            Reporter.log("Selected Alert Type: Module Alert", true);
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "module_alert_type_selection_failed");
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select Module Alert type: " + e.getMessage(), true);
            throw e;
        }
    }

}
