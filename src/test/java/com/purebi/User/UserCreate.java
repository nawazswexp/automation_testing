package com.purebi.User;

import com.purebi.homepage.BaseTest;
import com.purebi.homepage.BackToHomeScreen;
import com.purebi.utils.ScreenshotUtil;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;

public class UserCreate extends BaseTest {

    @Test
    public void createSiteUserFromExcel() {
        // Skip if login failed
        try {
            Boolean loginPassed = com.purebi.utils.TestExecutionControl.getLoginPassed();
            if (loginPassed != null && loginPassed.equals(Boolean.FALSE)) {
                Reporter.log("Skipping UserCreate because login failed.", true);
                throw new SkipException("Login failed");
            }
        } catch (Exception ignored) {
        }

        // Optionally skip if a shared UserTest flag exists and is false
        try {
            Object res = null;
            try {
                res = Class.forName("com.purebi.utils.TestExecutionControl")
                        .getMethod("getUserTestPassed")
                        .invoke(null);
            } catch (NoSuchMethodException ignored) {
            }
            if (res instanceof Boolean && ((Boolean) res).equals(Boolean.FALSE)) {
                Reporter.log("Skipping UserCreate because UserTest failed.", true);
                throw new SkipException("UserTest failed");
            }
        } catch (SkipException se) {
            throw se;
        } catch (Exception ignored) {
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        InputStream fis = null;
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

            // Close dropdown overlay
            actions.sendKeys(Keys.ESCAPE).perform();
            Reporter.log("Pressed ESC to close dropdown.", true);

            // Small stabilization wait
            Thread.sleep(1000);

            String excelPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "User" + File.separator + "DataUser.xlsx";
            File f = new File(excelPath);
            if (f.exists()) {
                fis = getClass().getClassLoader().getResourceAsStream("User/DataUser.xlsx");
                if (fis == null) {
                    fis = new FileInputStream(f);
                }
            }
            if (fis == null) {
                Reporter.log("Excel file not found: " + excelPath, true);
                Assert.fail("Excel file not found: " + excelPath);
            }
            wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(1); // A2 is row index 1
            DataFormatter df = new DataFormatter();

            String firstName = df.formatCellValue(row.getCell(0));
            String lastName = df.formatCellValue(row.getCell(1));
            String userName = df.formatCellValue(row.getCell(2));
            String email = df.formatCellValue(row.getCell(3));
            String password = df.formatCellValue(row.getCell(4));
            String role = df.formatCellValue(row.getCell(5));

            // Wait for overlay to be fully closed
            Thread.sleep(1000);

            // Click + Add Site User button with stable handling
            WebElement addUser = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space()='+ Add Site User']")
                )
            );

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                addUser
            );

            wait.until(ExpectedConditions.elementToBeClickable(addUser));

            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", addUser
            );
            Reporter.log("Clicked + Add Site User.", true);

            // Fill form
                wait.until(ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.FIRST_NAME_INPUT))
                    .sendKeys(firstName);
                driver.findElement(com.purebi.utils.Locators.LAST_NAME_INPUT).sendKeys(lastName);
                driver.findElement(com.purebi.utils.Locators.USER_NAME_INPUT).sendKeys(userName);
                driver.findElement(com.purebi.utils.Locators.EMAIL_INPUT).sendKeys(email);
                driver.findElement(com.purebi.utils.Locators.PASSWORD_INPUT).sendKeys(password);

            // Select role using visible text from Excel
                Select selectRole = new Select(driver.findElement(com.purebi.utils.Locators.ROLE_SELECT));
            selectRole.selectByVisibleText(role);

            // Click Save
                driver.findElement(com.purebi.utils.Locators.SAVE_BUTTON).click();

            // Verify toast message
                String toastTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(com.purebi.utils.Locators.TOAST_DETAIL)).getText().trim();
            Reporter.log("Toast message: " + toastTxt, true);

            if (toastTxt.contains("User added successfully.")) {
                Reporter.log("✅ User added successfully.", true);
                try { com.purebi.utils.TestExecutionControl.setUserCreatePassed(true); } catch (Exception ignored) {}
                Assert.assertTrue(true);
            } else {
                try { com.purebi.utils.TestExecutionControl.setUserCreatePassed(false); } catch (Exception ignored) {}
                String shot = ScreenshotUtil.takeScreenshot(driver, "user_create_toast_unexpected");
                Reporter.log("Screenshot saved: " + shot, true);
                Assert.fail("Unexpected toast message: " + toastTxt);
            }

        } catch (Exception e) {
            try { com.purebi.utils.TestExecutionControl.setUserCreatePassed(false); } catch (Exception ignored) {}
            Reporter.log("Exception in UserCreate: " + e.getMessage(), true);
            String shot = ScreenshotUtil.takeScreenshot(driver, "user_create_exception");
            Reporter.log("Screenshot saved: " + shot, true);
            Assert.fail("Exception creating user: " + e.getMessage());
        } finally {
            // Always attempt to close the sidebar
            try {
                By close = com.purebi.utils.Locators.SIDEBAR_CLOSE_SPAN;
                try {
                    WebElement closeEl = wait.until(ExpectedConditions.elementToBeClickable(close));
                    closeEl.click();
                    Reporter.log("Closed Add Site User sidebar.", true);
                } catch (Exception clickEx) {
                    try {
                        WebElement el = driver.findElement(close);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                        Reporter.log("Closed Add Site User sidebar via JS click.", true);
                    } catch (Exception jsEx) {
                        Reporter.log("Failed to close sidebar (both click and JS): " + jsEx.getMessage(), true);
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                if (wb != null) wb.close();
                if (fis != null) fis.close();
            } catch (Exception ignored) {
            }
        }
        }

    @AfterMethod
    public void ensureSidebarClosed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            By close = By.xpath("//span[contains(@class,'p-sidebar-close-icon') and contains(@class,'pi-times')]");
            try {
                WebElement closeEl = wait.until(ExpectedConditions.elementToBeClickable(close));
                closeEl.click();
                Reporter.log("Closed Add Site User sidebar (AfterMethod).", true);
            } catch (Exception clickEx) {
                try {
                    WebElement el = driver.findElement(close);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                    Reporter.log("Closed Add Site User sidebar via JS click (AfterMethod).", true);
                } catch (Exception jsEx) {
                    Reporter.log("Failed to close sidebar in AfterMethod (both click and JS): " + jsEx.getMessage(), true);
                }
            }
        } catch (Exception e) {
            Reporter.log("Failed to close sidebar in AfterMethod: " + e.getMessage(), true);
        }
    }

}

