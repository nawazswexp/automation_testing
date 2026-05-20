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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PMAlertsTest extends BaseTest {

    @Test(groups = "pm-alerts")
    public void pmAlertFullFlow() {
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

            // STEP 2 — Create PM Alert
            Reporter.log("STEP 2 — Create PM Alert: Opening create modal", true);
            By createBtn = By.xpath("//button[normalize-space()='CREATE NEW ALERT']");
            clickWithFallback(wait, createBtn, "CREATE NEW ALERT");

            By modal = By.xpath("//div[contains(@class,'card-header') and contains(.,'CREATE NEW ALERT')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
            Reporter.log("Create modal visible.", true);

            // Select Alert Type -> PM Alert
            By alertType = By.xpath("//select[@formcontrolname='alerttype']");
            selectAlertType(wait, alertType);

            // Alert Name
            By alertName = By.xpath("//input[@formcontrolname='alertname']");
            wait.until(ExpectedConditions.elementToBeClickable(alertName)).sendKeys("PM Alert Test");
            Reporter.log("Entered alert name: PM Alert Test", true);

            // Equipment
            By equipment = By.xpath("//select[@formcontrolname='equipment']");
            selectEquipment(wait, equipment, "PI-9019AN");

            // Interval
            By interval = By.xpath("//select[@formcontrolname='interval']");
            selectByVisibleText(wait, interval, "Daily");

            // Assign To
            By assignTo = By.xpath("//select[@formcontrolname='assignto']");
            selectByVisibleText(wait, assignTo, "Site Admin");

            // Date Time field: set to now minus 24 hours
            By dateTime = By.xpath("//input[contains(@class,'p-datepicker-input')]");
            setDateTimeWithCalendar(wait, dateTime);

            // Take a screenshot before saving so the final field state is captured if validation fails
            String dateShot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_before_save");
            Reporter.log("Screenshot saved: " + dateShot, true);

            // Click Save
            By saveBtn = By.xpath("//button[@type='submit' and normalize-space()='Save']");
            clickWithFallback(wait, saveBtn, "Save alert");
            Reporter.log("Clicked Save.", true);

            // STEP 3 — Validate Creation Toast
            By addedToast = By.xpath("//div[contains(@class,'ajs-message') and contains(.,'Added successfully')]");
            try {
                WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                toastWait.until(ExpectedConditions.visibilityOfElementLocated(addedToast));
                Reporter.log("Toast: Added successfully visible.", true);
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_toast_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Creation toast 'Added successfully' not visible.");
            }

            // STEP 4 — Validate Alert Exists
            Reporter.log("STEP 4 — Validate Alert Exists: Reopening Advanced Alerts.", true);
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
            // Re-navigate
            ps = wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            actions.moveToElement(ps).pause(Duration.ofSeconds(1)).perform();
            adv = wait.until(ExpectedConditions.elementToBeClickable(advancedAlerts));
            try { adv.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", adv); }

            // Click PM Alert tab
            By pmTab = By.xpath("//a[normalize-space()='PM Alert']");
            clickWithFallback(wait, pmTab, "PM Alert tab");

            // Scroll AG Grid until row found
            By gridViewport = By.xpath("//div[contains(@class,'ag-body-vertical-scroll-viewport')]");
            WebElement viewport = wait.until(ExpectedConditions.visibilityOfElementLocated(gridViewport));

            // Row XPath
            String rowXpath = "//div[@role='row'][.//div[@col-id='alertname' and normalize-space()='PM Alert Test'] and .//div[@col-id='type' and normalize-space()='PM Alert']]";
            // Try to find row (scrolling loop, small attempts)
            boolean found = false;
            for (int i = 0; i < 8; i++) {
                if (driver.findElements(By.xpath(rowXpath)).size() > 0) { found = true; break; }
                // scroll viewport down
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop += arguments[0].clientHeight;", viewport);
                    Thread.sleep(300);
                } catch (Exception ignored) {}
            }

            if (!found) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_row_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Created PM Alert row not found in AG Grid.");
            }

            Reporter.log("PM Alert row found in AG Grid.", true);
            // Validate trigger contains PI-9019AN, Daily
            WebElement row = driver.findElement(By.xpath(rowXpath));
            String rowText = row.getText();
            Assert.assertTrue(rowText.contains("PI-9019AN"), "Row does not contain equipment PI-9019AN");
            Assert.assertTrue(rowText.contains("Daily"), "Row does not contain interval Daily");

            // STEP 5 — Delete Alert
            Reporter.log("STEP 5 — Delete Alert: Clicking delete icon on row.", true);
            By deleteIcon = By.xpath(rowXpath + "//i[@data-action='delete']");
            clickWithFallback(wait, deleteIcon, "Delete icon");

            // Wait for DELETE CONFIRMATION popup, then click popup DELETE button
            By deleteConfirmationTitle = By.xpath("//*[contains(normalize-space(),'DELETE CONFIRMATION')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteConfirmationTitle));

            By popupDeleteBtn = By.xpath("//button[normalize-space()='DELETE']");
            clickWithFallback(wait, popupDeleteBtn, "Popup DELETE button");

            // Wait for delete success toast
            By deletedToast = By.xpath("//*[contains(text(),'Deleted successfully')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deletedToast));

            // STEP 6 — Validate Deletion
            Reporter.log("STEP 6 — Validate Deletion: Ensuring row no longer exists.", true);
            Thread.sleep(2000);
            if (driver.findElements(By.xpath(rowXpath)).size() > 0) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_delete_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("PM Alert row still exists after deletion.");
            }

            Reporter.log("PM Alert flow completed successfully.", true);

        } catch (AssertionError ae) {
            Reporter.log("Assertion failed: " + ae.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_assertion_failure");
            Reporter.log("Screenshot saved: " + shot, true);
            throw ae;
        } catch (Exception e) {
            Reporter.log("Exception in PMAlertsTest: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception during PM Alert flow: " + e.getMessage());
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

    // Helper: select PM Alert by stable value and log trimmed options
    private void selectAlertType(WebDriverWait wait, By locator) {
        try {
            Reporter.log("Selecting PM Alert type...", true);

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

            alertTypeSelect.selectByVisibleText("PM Alert");

            String selectedText = alertTypeSelect.getFirstSelectedOption().getText().trim();
            Assert.assertEquals(selectedText, "PM Alert", "Selected alert type mismatch.");

            Reporter.log("Selected Alert Type: PM Alert", true);
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_type_selection_failed");
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select PM Alert type: " + e.getMessage(), true);
            throw e;
        }
    }

    // Helper: select equipment after dynamically loaded options are available
    private void selectEquipment(WebDriverWait wait, By locator, String expectedText) {
        try {
            Reporter.log("Waiting for Equipment dropdown options to load...", true);

            WebElement equipmentDropdown = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );

            Select equipmentSelect = new Select(equipmentDropdown);

            wait.until(driver -> {
                Select s = new Select(driver.findElement(locator));
                return s.getOptions().size() > 1;
            });

            boolean optionFound = false;

            for (WebElement option : equipmentSelect.getOptions()) {
                String text = option.getText().trim();
                Reporter.log("Equipment option found: " + text, true);

                if (text.equalsIgnoreCase(expectedText)) {
                    equipmentSelect.selectByVisibleText(text);
                    Reporter.log("Selected Equipment: " + text, true);
                    optionFound = true;
                    break;
                }
            }

            if (!optionFound) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_equipment_missing");
                Reporter.log("Screenshot saved: " + shot, true);
            }

            Assert.assertTrue(
                    optionFound,
                    "Equipment option '" + expectedText + "' not found in dropdown."
            );
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "pm_alert_equipment_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select equipment '" + expectedText + "': " + e.getMessage(), true);
            throw e;
        }
    }

    // Helper: set date/time using PrimeNG calendar UI controls
    private void setDateTimeWithCalendar(WebDriverWait wait, By locator) {
        try {
            Reporter.log("Setting Start Date & Time...", true);

            LocalDateTime targetDateTime = LocalDateTime.now().minusHours(24);
            int targetDay = targetDateTime.getDayOfMonth();
            int targetMonthZeroBased = targetDateTime.getMonthValue() - 1;

            Reporter.log("Target datetime: " + targetDateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")), true);

            WebElement dateField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            By calendarButton = By.xpath("//button[@aria-label='Choose Date']");
            By calendarPopup = By.xpath("//div[contains(@class,'p-datepicker-panel')]");

            boolean popupOpen = !driver.findElements(calendarPopup).isEmpty()
                    && driver.findElement(calendarPopup).isDisplayed();

            if (!popupOpen) {
                wait.until(ExpectedConditions.elementToBeClickable(calendarButton)).click();
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(calendarPopup));
            Reporter.log("Calendar popup opened", true);

            String targetDateXpath = "//span[@data-date='" + targetDateTime.getYear() + "-" + targetMonthZeroBased + "-" + targetDay + "']";
            WebElement targetDate = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(targetDateXpath)));
            targetDate.click();
            Reporter.log("Target day selected", true);

            String actualValue = String.valueOf(dateField.getAttribute("value")).trim();
            Assert.assertFalse(actualValue.isEmpty(), "Start Date & Time field value is empty after calendar selection.");

            Reporter.log("Selected datetime value: " + actualValue, true);
            Reporter.log("Datetime field populated", true);
        } catch (Exception e) {
            Reporter.log("Failed to set date/time -> " + e.getMessage(), true);
            throw e;
        }
    }

}
