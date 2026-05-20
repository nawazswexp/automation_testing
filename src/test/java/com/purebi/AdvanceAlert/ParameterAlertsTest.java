package com.purebi.AdvanceAlert;

import com.purebi.homepage.BackToHomeScreen;
import com.purebi.homepage.BaseTest;
import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class ParameterAlertsTest extends BaseTest {

    @Test(groups = "parameter-alerts")
    public void parameterAlertFullFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            try {
                BackToHomeScreen.navigateToHomeScreen(driver);
            } catch (Exception ignored) {
            }

            Reporter.log("STEP 1 — Navigation: Clicking Home logo", true);
            By logo = By.xpath("//img[@class='logo']");
            clickWithFallback(wait, logo, "Home logo");

            By plantSettings = By.xpath("//span[normalize-space()='PLANT SETTINGS']");
            WebElement ps = wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            Actions actions = new Actions(driver);
            actions.moveToElement(ps).pause(Duration.ofSeconds(1)).perform();
            Reporter.log("Hovered on PLANT SETTINGS.", true);

            By advancedAlerts = By.xpath("//a[normalize-space()='ADVANCED ALERTS']");
            actions.moveToElement(ps).pause(Duration.ofSeconds(2)).perform();
            Reporter.log("Hovered on PLANT SETTINGS again.", true);

            WebElement adv = wait.until(ExpectedConditions.visibilityOfElementLocated(advancedAlerts));
            clickElement(wait, adv, "ADVANCED ALERTS");
            Reporter.log("Clicked ADVANCED ALERTS.", true);

            By pageLoaded = By.xpath("//div[contains(@class,'d-flex') and contains(@class,'justify-content-between') and contains(@class,'w-100')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageLoaded));
            Reporter.log("Advanced Alerts page loaded.", true);

            Reporter.log("STEP 2 — Open Create Alert Modal: Opening create modal", true);
            By createBtn = By.xpath("//button[contains(.,'CREATE NEW ALERT')]");
            clickWithFallback(wait, createBtn, "CREATE NEW ALERT");

            By modal = By.xpath("//div[contains(@class,'card-header') and contains(.,'CREATE NEW ALERT')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
            Reporter.log("Create modal visible.", true);

            Reporter.log("STEP 3 — Select Alert Type", true);
            By alertType = By.xpath("//select[@formcontrolname='alerttype']");
            selectAlertType(wait, alertType, "Parameter Alert");

            Reporter.log("STEP 4 — Enter Alert Name", true);
            By alertName = By.xpath("//input[@formcontrolname='alertname']");
            wait.until(ExpectedConditions.elementToBeClickable(alertName)).sendKeys("Parameter Alert Test");
            Reporter.log("Entered alert name: Parameter Alert Test", true);

            Reporter.log("STEP 5 — Select Module", true);
            By module = By.xpath("//select[@formcontrolname='moduleparam']");
            selectByVisibleText(wait, module, "RO-3");
            validateSelectedText(wait, module, "RO-3", "Module");
            Thread.sleep(2000);

            Reporter.log("STEP 6 — Select Parameter", true);
            By parameter = By.xpath("//select[@formcontrolname='parameter']");
            selectFirstDynamicOption(wait, parameter, "Select Parameter*", "parameter_alert_parameter_missing", "Parameter");

            Reporter.log("STEP 7 — Select Condition", true);
            By condition = By.xpath("//select[@formcontrolname='conditions']");
            selectAnyValidCondition(wait, condition);

            Reporter.log("STEP 8 — Assign To", true);
            By assignTo = By.xpath("//select[@formcontrolname='assignto']");
            selectByVisibleText(wait, assignTo, "Site Admin");

            Reporter.log("STEP 9 — Enter Score", true);
            By score = By.xpath("//input[@formcontrolname='score']");
            wait.until(ExpectedConditions.elementToBeClickable(score)).sendKeys("70");
            Reporter.log("Entered score: 70", true);

            Reporter.log("STEP 10 — Screenshot Before Save", true);
            String preShot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_before_save");
            Reporter.log("Screenshot saved: " + preShot, true);

            Reporter.log("STEP 11 — Save Alert", true);
            By saveBtn = By.xpath("//button[normalize-space()='Save']");
            clickWithFallback(wait, saveBtn, "Save alert");
            Reporter.log("Clicked Save.", true);

            Reporter.log("STEP 12 — Validate Creation Toast", true);
            By addedToast = By.xpath("//*[contains(text(),'Added successfully')]");
            try {
                WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                toastWait.until(ExpectedConditions.visibilityOfElementLocated(addedToast));
                Reporter.log("Toast: Added successfully visible.", true);
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_toast_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Creation toast 'Added successfully' not visible.");
            }

            Reporter.log("STEP 13 — Reopen Advanced Alerts Page", true);
            try {
                BackToHomeScreen.navigateToHomeScreen(driver);
            } catch (Exception ignored) {
            }

            ps = wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            actions.moveToElement(ps).pause(Duration.ofSeconds(1)).perform();
            Reporter.log("Hovered on PLANT SETTINGS.", true);
            actions.moveToElement(ps).pause(Duration.ofSeconds(2)).perform();
            Reporter.log("Hovered on PLANT SETTINGS again.", true);
            adv = wait.until(ExpectedConditions.visibilityOfElementLocated(advancedAlerts));
            clickElement(wait, adv, "ADVANCED ALERTS");
            Reporter.log("Clicked ADVANCED ALERTS.", true);
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageLoaded));
            Reporter.log("Advanced Alerts page loaded.", true);

            Reporter.log("STEP 14 — Click Parameter Alert Filter", true);
            By parameterTab = By.xpath("//a[contains(normalize-space(),'Parameter Alert')]");
            clickWithFallback(wait, parameterTab, "Parameter Alert tab");

                Reporter.log("STEP 15 — Validate Row Exists In AG Grid", true);

                String alertNameStr = "Parameter Alert Test";

                By targetRow = By.xpath(
                    "//div[@role='row' and .//div[@col-id='alertname' and normalize-space()='" + alertNameStr + "'] ]"
                );

                WebElement targetRowElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(targetRow)
                );

                Reporter.log("Parameter Alert row found in AG Grid.", true);

                Reporter.log("STEP 16 — Delete Alert", true);

                WebElement deleteIcon = targetRowElement.findElement(
                    By.xpath(".//i[@data-action='delete']")
                );

                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    deleteIcon
                );

                Thread.sleep(1000);

                wait.until(ExpectedConditions.visibilityOf(deleteIcon));

                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    deleteIcon
                );

                Thread.sleep(1000);

                Reporter.log("Clicked delete icon for Parameter Alert Test row.", true);

            Reporter.log("STEP 17 — Delete Confirmation Popup", true);
            By deleteConfirmationTitle = By.xpath("//*[contains(normalize-space(),'DELETE CONFIRMATION')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteConfirmationTitle));
            By popupDeleteBtn = By.xpath("//button[normalize-space()='DELETE']");
            clickWithFallback(wait, popupDeleteBtn, "Popup DELETE button");

            Reporter.log("STEP 18 — Validate Delete Toast", true);
            By deletedToast = By.xpath("//*[contains(text(),'Deleted successfully')]");
            wait.until(ExpectedConditions.visibilityOfElementLocated(deletedToast));

            Reporter.log("STEP 19 — Final Delete Validation", true);
            Thread.sleep(4000);

            By deletedRow = By.xpath(
                    "//div[@role='row' and .//*[contains(normalize-space(),'Parameter Alert Test')]]"
            );

            boolean visibleRowExists = false;

            for (WebElement row : driver.findElements(deletedRow)) {
                try {
                    if (row.isDisplayed()) {
                        visibleRowExists = true;
                        Reporter.log("Visible stale row text: " + row.getText(), true);
                    }
                } catch (Exception ignored) {
                }
            }

            if (visibleRowExists) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_delete_failed");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Parameter Alert row still visible after deletion.");
            }

            Reporter.log("Parameter Alert row deleted successfully", true);

        } catch (AssertionError ae) {
            Reporter.log("Assertion failed: " + ae.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_assertion_failure");
            Reporter.log("Screenshot saved: " + shot, true);
            throw ae;
        } catch (Exception e) {
            Reporter.log("Exception in ParameterAlertsTest: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception during Parameter Alert flow: " + e.getMessage());
        }
    }

    private void clickWithFallback(WebDriverWait wait, By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            Reporter.log("Clicked: " + label, true);
        } catch (Exception e) {
            Reporter.log("Failed to click: " + label + " -> " + e.getMessage(), true);
            throw e;
        }
    }

    private void clickElement(WebDriverWait wait, WebElement element, String label) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            Reporter.log("Clicked: " + label, true);
        } catch (Exception e) {
            Reporter.log("Failed to click: " + label + " -> " + e.getMessage(), true);
            throw e;
        }
    }

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

    private void validateSelectedText(WebDriverWait wait, By locator, String expectedText, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Select sel = new Select(el);
            String selectedText = sel.getFirstSelectedOption().getText().trim();
            Assert.assertFalse(selectedText.isEmpty(), label + " selection is empty.");
            Assert.assertEquals(selectedText, expectedText, label + " selection mismatch.");
            Reporter.log("Validated selected " + label + ": " + selectedText, true);
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_" + label.toLowerCase().replace(" ", "_") + "_validation_failed");
            Reporter.log("Screenshot saved: " + shot, true);
            throw e;
        }
    }

    private void selectAlertType(WebDriverWait wait, By locator, String expectedText) {
        try {
            Reporter.log("Selecting Parameter Alert type...", true);

            wait.until(ExpectedConditions.elementToBeClickable(locator));
            WebElement freshDropdown = wait.until(ExpectedConditions.elementToBeClickable(locator));
            Select alertTypeSelect = new Select(freshDropdown);

            for (WebElement option : alertTypeSelect.getOptions()) {
                Reporter.log("Alert Type Option: '" + option.getText().trim() + "'", true);
            }

            alertTypeSelect.selectByVisibleText(expectedText);
            String selectedText = alertTypeSelect.getFirstSelectedOption().getText().trim();
            Assert.assertEquals(selectedText, expectedText, "Selected alert type mismatch.");

            Reporter.log("Selected Alert Type: " + expectedText, true);
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_type_selection_failed");
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select Parameter Alert type: " + e.getMessage(), true);
            throw e;
        }
    }

    private void selectFirstDynamicOption(WebDriverWait wait, By locator, String excludedText, String screenshotName, String label) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(driver -> {
                Select s = new Select(driver.findElement(locator));
                return s.getOptions().size() > 1;
            });

            Select select = new Select(dropdown);
            String selectedText = null;

            for (WebElement option : select.getOptions()) {
                String text = option.getText().trim();
                Reporter.log(label + " option found: " + text, true);
                if (!text.isEmpty() && !text.equalsIgnoreCase(excludedText)) {
                    select.selectByVisibleText(text);
                    selectedText = select.getFirstSelectedOption().getText().trim();
                    Reporter.log("Selected " + label + ": " + selectedText, true);
                    break;
                }
            }

            if (selectedText == null || selectedText.isEmpty()) {
                String shot = ScreenshotUtil.takeScreenshot(driver, screenshotName);
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail(label + " dropdown did not contain a valid selectable option.");
            }

            Assert.assertFalse(selectedText.isEmpty(), label + " selection is empty.");
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, screenshotName);
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select " + label + ": " + e.getMessage(), true);
            throw e;
        }
    }

    private void selectAnyValidCondition(WebDriverWait wait, By locator) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(driver -> {
                Select s = new Select(driver.findElement(locator));
                return s.getOptions().size() > 1;
            });

            Select select = new Select(dropdown);
            String selectedText = null;

            for (WebElement option : select.getOptions()) {
                String text = option.getText().trim();
                Reporter.log("Condition option found: " + text, true);
                if (!text.isEmpty()
                        && !text.equalsIgnoreCase("Conditions*")
                        && !text.toLowerCase().contains("condition")) {
                    select.selectByVisibleText(text);
                    selectedText = select.getFirstSelectedOption().getText().trim();
                    Reporter.log("Selected Condition: " + selectedText, true);
                    break;
                }
            }

            if (selectedText == null || selectedText.isEmpty()) {
                String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_condition_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Condition dropdown did not contain a valid selectable option.");
            }
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(driver, "parameter_alert_condition_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Reporter.log("Failed to select condition: " + e.getMessage(), true);
            throw e;
        }
    }
}
