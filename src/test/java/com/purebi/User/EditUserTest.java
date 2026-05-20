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
import org.openqa.selenium.WebElement;
import java.util.List;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;

public class EditUserTest extends BaseTest {

    @Test
    public void editUserFromExcel() {
        // Skip if login failed
        try {
            Boolean loginPassed = com.purebi.utils.TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping EditUserTest because login failed.", true);
                throw new SkipException("Login failed");
            }
        } catch (Exception ignored) {
        }

        // Skip if UserCreate did not pass
        try {
            Boolean userCreatePassed = com.purebi.utils.TestExecutionControl.getUserCreatePassed();
            if (userCreatePassed == null || userCreatePassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping EditUserTest because UserCreate did not pass.", true);
                throw new SkipException("UserCreate did not pass");
            }
        } catch (SkipException se) {
            throw se; // rethrow skip so TestNG marks it as skipped
        } catch (Exception ignored) {
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        FileInputStream fisUser = null;
        Workbook wbUser = null;
        FileInputStream fisEdit = null;
        Workbook wbEdit = null;

        try {
            // Ensure home
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

            // Read user email from DataUser.xlsx (D2)
            String dataUserPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "User" + File.separator + "DataUser.xlsx";
            File fu = new File(dataUserPath);
            if (!fu.exists()) {
                Reporter.log("Missing DataUser.xlsx: " + dataUserPath, true);
                Assert.fail("Missing DataUser.xlsx");
            }
            fisUser = new FileInputStream(fu);
            wbUser = new XSSFWorkbook(fisUser);
            Sheet sUser = wbUser.getSheetAt(0);
            Row rUser = sUser.getRow(1);
            DataFormatter df = new DataFormatter();
            String targetEmail = df.formatCellValue(rUser.getCell(3));

            // Click edit button for the row matching the email (try multiple locators)
            boolean clickedEdit = false;
            String[] editXPaths = new String[]{
                    "//tr[td[normalize-space()='" + targetEmail + "']]//button[.//i[contains(@class,'fa-edit')]]",
                    "//tr[td[normalize-space()='" + targetEmail + "']]//i[contains(@class,'fa-edit')]/ancestor::button",
                    "//tr[td[normalize-space()='" + targetEmail + "']]//button[1]",
                    "//tr[td[normalize-space()='" + targetEmail + "']]//a[contains(@class,'edit') or contains(@title,'Edit')]",
                    "//tr[td[normalize-space()='" + targetEmail + "']]//i[contains(@class,'fa-pencil')]/ancestor::button"
            };

            for (String xp : editXPaths) {
                try {
                    By editBtn = By.xpath(xp);
                    WebElement eb = wait.until(ExpectedConditions.elementToBeClickable(editBtn));
                    eb.click();
                    Reporter.log("Clicked edit for user using xpath: " + xp, true);
                    clickedEdit = true;
                    break;
                } catch (Exception ignore) {
                    Reporter.log("Edit locator failed: " + xp + " -> " + ignore.getMessage(), true);
                }
            }

            if (!clickedEdit) {
                Reporter.log("Failed to click edit for user: " + targetEmail, true);
                Assert.fail("Could not find clickable edit button for user: " + targetEmail);
            }

            // Wait for edit sidebar/form to appear (be defensive about placeholder variations)
            try {
                WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                longWait.until(ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.FIRST_NAME_VARIANTS));
                Reporter.log("Edit form appeared for user: " + targetEmail, true);
            } catch (Exception waitEx) {
                Reporter.log("Edit form did not appear after clicking edit: " + waitEx.getMessage(), true);
                String shot = ScreenshotUtil.takeScreenshot(driver, "edit_form_not_visible");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Edit form did not appear for user: " + targetEmail);
            }

            // Read edit data from DataEdit.xlsx
            String dataEditPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "User" + File.separator + "DataEdit.xlsx";
            File fe = new File(dataEditPath);
            if (!fe.exists()) {
                Reporter.log("Missing DataEdit.xlsx: " + dataEditPath, true);
                Assert.fail("Missing DataEdit.xlsx");
            }
            fisEdit = new FileInputStream(fe);
            wbEdit = new XSSFWorkbook(fisEdit);
            Sheet sEdit = wbEdit.getSheetAt(0);
            Row rEdit = sEdit.getRow(1);

            String firstName = df.formatCellValue(rEdit.getCell(0));
            String lastName = df.formatCellValue(rEdit.getCell(1));
            String userName = df.formatCellValue(rEdit.getCell(2));
            String email = df.formatCellValue(rEdit.getCell(3));
            String password = df.formatCellValue(rEdit.getCell(4));
            String role = df.formatCellValue(rEdit.getCell(5));

            // Fill edit form
            wait.until(ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.FIRST_NAME_EXACT)).clear();
            driver.findElement(com.purebi.utils.Locators.FIRST_NAME_EXACT).sendKeys(firstName);
            driver.findElement(com.purebi.utils.Locators.LAST_NAME_EXACT).clear();
            driver.findElement(com.purebi.utils.Locators.LAST_NAME_EXACT).sendKeys(lastName);
            driver.findElement(com.purebi.utils.Locators.USER_NAME_EXACT).clear();
            driver.findElement(com.purebi.utils.Locators.USER_NAME_EXACT).sendKeys(userName);
            // Intentionally left blank: do not populate Email field from Excel per request
            driver.findElement(By.xpath("//input[@placeholder='Password']")).clear();
            driver.findElement(By.xpath("//input[@placeholder='Password']")).sendKeys(password);

            // Select role
            try {
                Select sel = new Select(driver.findElement(com.purebi.utils.Locators.ROLE_SELECT));
                sel.selectByVisibleText(role);
            } catch (Exception rex) {
                Reporter.log("Role select failed: " + rex.getMessage(), true);
            }

            // Upload image
            try {
                String imagePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "com" + File.separator + "purebi" + File.separator + "User" + File.separator + "pp.jpg";
                WebElement imageUploadInput = wait.until(ExpectedConditions.presenceOfElementLocated(com.purebi.utils.Locators.IMAGE_UPLOAD_INPUT));
                imageUploadInput.sendKeys(imagePath);
                Reporter.log("Image path sent: " + imagePath, true);
            } catch (Exception imgEx) {
                Reporter.log("Image upload failed: " + imgEx.getMessage(), true);
            }

            // Click Update
            driver.findElement(com.purebi.utils.Locators.UPDATE_BUTTON).click();

            // Quick screenshot within ~1s after clicking Update if toast not yet present
            By toast = com.purebi.utils.Locators.UPDATED_TOAST;
            try {
                WebDriverWait tiny = new WebDriverWait(driver, Duration.ofSeconds(1));
                tiny.until(ExpectedConditions.visibilityOfElementLocated(toast));
                // toast appeared very quickly; proceed
            } catch (Exception quickEx) {
                try {
                    String quickShot = ScreenshotUtil.takeScreenshot(driver, "edit_after_update_quick");
                    Reporter.log("Quick screenshot saved after clicking Update: " + quickShot, true);
                } catch (Exception ssEx) {
                    Reporter.log("Failed to take quick screenshot: " + ssEx.getMessage(), true);
                }
            }

            // Verify toast (normal wait)
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(toast));
                Reporter.log("✅ User updated successfully.", true);
                try { com.purebi.utils.TestExecutionControl.setUserEditPassed(true); } catch (Exception ignored) {}
                Assert.assertTrue(true);
            } catch (Exception toEx) {
                try { com.purebi.utils.TestExecutionControl.setUserEditPassed(false); } catch (Exception ignored) {}
                String shot = ScreenshotUtil.takeScreenshot(driver, "edit_user_no_toast");
                Reporter.log("Screenshot saved: " + shot, true);
                // Try to close the sidebar when edit fails, then fail the test
                try {
                    By close = com.purebi.utils.Locators.SIDEBAR_CLOSE_SPAN;
                    try {
                        WebElement closeEl = new WebDriverWait(driver, Duration.ofSeconds(5))
                                .until(ExpectedConditions.elementToBeClickable(close));
                        closeEl.click();
                        Reporter.log("Closed edit sidebar after failure.", true);
                    } catch (Exception clickEx) {
                        try {
                            WebElement el = driver.findElement(close);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                            Reporter.log("Closed edit sidebar via JS after failure.", true);
                        } catch (Exception jsEx) {
                            Reporter.log("Failed to close edit sidebar after failure: " + jsEx.getMessage(), true);
                        }
                    }
                } catch (Exception ignore) {}
                Assert.fail("Update confirmation not found");
            }

        } catch (Exception e) {
            Reporter.log("Exception in EditUserTest: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "edit_user_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception editing user: " + e.getMessage());
        } finally {
            try { if (wbUser != null) wbUser.close(); if (fisUser != null) fisUser.close(); } catch (Exception ignored) {}
            try { if (wbEdit != null) wbEdit.close(); if (fisEdit != null) fisEdit.close(); } catch (Exception ignored) {}
        }
    }

    @AfterMethod
    public void ensureReturnHome() {
        try { BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}
    }

}
