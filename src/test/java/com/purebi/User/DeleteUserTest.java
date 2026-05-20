package com.purebi.User;

import com.purebi.homepage.BaseTest;
import com.purebi.homepage.BackToHomeScreen;
import com.purebi.utils.ScreenshotUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;

public class DeleteUserTest extends BaseTest {

    @Test
    public void deleteUserByEmail() {
        // Skip if login failed
        try {
            Boolean loginPassed = com.purebi.utils.TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping DeleteUserTest because login failed.", true);
                throw new SkipException("Login failed");
            }
        } catch (Exception ignored) {
        }

        // Only run if UserCreate passed. Delete should run regardless of EditUser outcome.
        try {
            Boolean userCreatePassed = com.purebi.utils.TestExecutionControl.getUserCreatePassed();
            if (userCreatePassed == null || userCreatePassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping DeleteUserTest because UserCreate did not pass.", true);
                throw new SkipException("UserCreate did not pass");
            }
        } catch (SkipException se) {
            throw se;
        } catch (Exception ignored) {
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        FileInputStream fis = null;
        Workbook wb = null;

        try {
            // Ensure home screen
            try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}

            Actions actions = new Actions(driver);

            // Locate PLANT SETTINGS
            WebElement plantSettings = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[normalize-space()='PLANT SETTINGS']")
                )
            );

            // Scroll to element
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                plantSettings
            );

            // KEEP HOVER ACTIVE
            actions.moveToElement(plantSettings).pause(Duration.ofSeconds(2)).perform();
            Reporter.log("Hovered on PLANT SETTINGS.", true);

            // Now locate USER while hover is ACTIVE
            WebElement userMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space()='USER']")
                )
            );

            // Move directly from PLANT SETTINGS -> USER without losing hover
            actions.moveToElement(userMenu)
                   .pause(Duration.ofMillis(500))
                   .click()
                   .perform();
            Reporter.log("Clicked USER menu.", true);

            // IMPORTANT: close dropdown overlay
            actions.sendKeys(Keys.ESCAPE).perform();
            Reporter.log("Pressed ESC to close dropdown.", true);

            // wait for overlay to disappear
            Thread.sleep(1000);

            // Open dropdown and select 100 (try span trigger then div trigger; handle options inside p-overlay)
            By dropdownSpan = com.purebi.utils.Locators.DROPDOWN_SPAN;
            boolean clickedTrigger = false;
            try {
                try {
                    WebElement span = wait.until(ExpectedConditions.elementToBeClickable(dropdownSpan));
                    span.click();
                    Reporter.log("Clicked dropdown span trigger.", true);
                    clickedTrigger = true;
                } catch (Exception spanEx) {
                    Reporter.log("Dropdown span click failed: " + spanEx.getMessage(), true);
                    // fallback to div trigger
                    By dropdown = com.purebi.utils.Locators.DROPDOWN_DIV;
                    try {
                        WebElement d = wait.until(ExpectedConditions.elementToBeClickable(dropdown));
                        d.click();
                        Reporter.log("Clicked dropdown div trigger.", true);
                        clickedTrigger = true;
                    } catch (Exception divEx) {
                        Reporter.log("Dropdown div click failed: " + divEx.getMessage(), true);
                        // try JS click on span
                        try {
                            WebElement span2 = driver.findElement(dropdownSpan);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", span2);
                            Reporter.log("Clicked dropdown span via JS fallback.", true);
                            clickedTrigger = true;
                        } catch (Exception jsEx) {
                            Reporter.log("Dropdown JS fallback also failed: " + jsEx.getMessage(), true);
                            // extra JS fallback: click nearest button or parent element to ensure dropdown opens
                            try {
                                ((JavascriptExecutor) driver).executeScript("var s=document.querySelector(\"span.p-dropdown-trigger-icon.pi.pi-chevron-down\"); if(s){var btn=s.closest('button')||s.parentElement; if(btn) btn.click();}");
                                Reporter.log("Triggered dropdown via nearest button JS fallback.", true);
                                clickedTrigger = true;
                            } catch (Exception jsBtnEx) {
                                Reporter.log("Nearest-button JS fallback failed: " + jsBtnEx.getMessage(), true);
                            }
                        }
                    }
                }

                if (!clickedTrigger) {
                    Reporter.log("Failed to open dropdown trigger, continuing to next steps.", true);
                }

                // Wait for overlay containing options to appear and select '100' inside it
                By overlay = com.purebi.utils.Locators.P_OVERLAY;
                try {
                    WebDriverWait overlayWait = new WebDriverWait(driver, Duration.ofSeconds(8));
                    overlayWait.until(ExpectedConditions.visibilityOfElementLocated(overlay));
                    // Find all visible overlays and attempt to click the '100' option inside the correct one
                    List<WebElement> overlays = driver.findElements(com.purebi.utils.Locators.P_OVERLAY);
                    boolean selected = false;
                    for (WebElement ov : overlays) {
                        try {
                            WebElement opt = ov.findElement(By.xpath(".//*[normalize-space()='100']"));
                            if (opt != null && opt.isDisplayed()) {
                                try {
                                    overlayWait.until(ExpectedConditions.elementToBeClickable(opt));
                                    opt.click();
                                    Reporter.log("Selected '100' from overlay (direct click).", true);
                                } catch (Exception clickEx) {
                                    try {
                                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                                        Reporter.log("Selected '100' from overlay (JS click).", true);
                                    } catch (Exception jsEx) {
                                        Reporter.log("Failed to click overlay option via JS: " + jsEx.getMessage(), true);
                                        continue;
                                    }
                                }
                                selected = true;
                                break;
                            }
                        } catch (Exception ignoreOverlay) {
                            // not in this overlay
                        }
                    }
                    if (!selected) {
                        Reporter.log("Could not find '100' option in any visible overlay.", true);
                    }
                } catch (Exception ovEx) {
                    Reporter.log("Overlay not visible after clicking dropdown: " + ovEx.getMessage(), true);
                }

            } catch (Exception ex) {
                Reporter.log("Dropdown click handling encountered an unexpected error: " + ex.getMessage(), true);
            }

            // Read email from DataUser.xlsx (D2)
            String dataUserPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "User" + File.separator + "DataUser.xlsx";
            File f = new File(dataUserPath);
            if (!f.exists()) {
                Reporter.log("Missing DataUser.xlsx: " + dataUserPath, true);
                Assert.fail("Missing DataUser.xlsx");
            }
            fis = new FileInputStream(f);
            wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(1);
            DataFormatter df = new DataFormatter();
            String email = df.formatCellValue(row.getCell(3));

            // Click delete icon for the row matching the email
            By deleteIcon = By.xpath("//tr[td[normalize-space()='" + email + "']]//i[contains(@class,'fa-trash')]");
            wait.until(ExpectedConditions.elementToBeClickable(deleteIcon)).click();
            Reporter.log("Clicked delete icon for user: " + email, true);

            // Click confirmation 'Yes' button
            try {
                WebElement y = wait.until(ExpectedConditions.elementToBeClickable(com.purebi.utils.Locators.YES_BUTTON));
                y.click();
                Reporter.log("Clicked confirmation Yes button.", true);
            } catch (Exception confEx) {
                Reporter.log("Confirmation 'Yes' button click failed: " + confEx.getMessage(), true);
                String shot = ScreenshotUtil.takeScreenshot(driver, "delete_confirm_missing");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Confirmation 'Yes' button not found or not clickable");
            }

            // Verify deletion toast
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.DELETED_TOAST));
                Reporter.log("✅ User deleted successfully toast found.", true);
            } catch (Exception toastEx) {
                Reporter.log("User deleted toast not found: " + toastEx.getMessage(), true);
                String shot = ScreenshotUtil.takeScreenshot(driver, "delete_no_toast");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("User deleted confirmation not found");
            }

        } catch (Exception e) {
            Reporter.log("Exception in DeleteUserTest: " + e.getMessage(), true);
            try { String shot = ScreenshotUtil.takeScreenshot(driver, "delete_user_exception"); Reporter.log("Screenshot saved: " + shot, true); } catch (Exception ignore) {}
            Assert.fail("Exception deleting user: " + e.getMessage());
        } finally {
            try { if (wb != null) wb.close(); if (fis != null) fis.close(); } catch (Exception ignored) {}
        }
    }

    @AfterMethod
    public void ensureReturnHome() {
        try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
    }

}
