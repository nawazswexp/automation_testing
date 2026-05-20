package com.purebi.PlantSettings;

import com.purebi.homepage.BaseTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

public class RevertPlantSettingsTest extends BaseTest {

    @Test(dependsOnGroups = "plant-settings-setup")
    public void revertPlantSettings() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Keep a small buffer between PlantSettingsTest and this test.
            Thread.sleep(10000);

            System.out.println("STEP 1: Navigate");
            jsClick(wait, By.xpath("//img[@class='logo']"));
            jsClick(wait, By.xpath("//a[contains(normalize-space(),'PLANT SETTINGS')]") );
            jsClick(wait, By.xpath("//a[normalize-space()='PLANT CUSTOMIZATION']"));
            Thread.sleep(60000);

            System.out.println("STEP 2: Read Excel");
            String expectedSaved;
            String revertName;
            String revertAddress;
            String revertContact;
            String revertDescription;

            try (InputStream fis = getClass().getClassLoader().getResourceAsStream("PlantSettings.xlsx")) {
                if (fis == null) {
                    throw new RuntimeException("Excel file not found in resources folder");
                }

                try (Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheet("Data");
                if (sheet == null) {
                    throw new RuntimeException("Sheet 'Data' not found in Plant Settings.xlsx");
                }

                Row row2 = sheet.getRow(1); // B2..E2
                Row row3 = sheet.getRow(2); // B3
                if (row2 == null || row3 == null) {
                    throw new RuntimeException("Required rows (2/3) not found in Data sheet");
                }

                expectedSaved = row3.getCell(1).getStringCellValue();
                revertName = row2.getCell(1).getStringCellValue();
                revertAddress = row2.getCell(2).getStringCellValue();
                revertContact = String.valueOf((long) row2.getCell(3).getNumericCellValue());
                revertDescription = row2.getCell(4).getStringCellValue();
                }
            }

            System.out.println("STEP 3: Validation");
            WebElement siteNameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='siteName']")
            ));
            String currentSiteName = siteNameEl.getAttribute("value");
            if (!expectedSaved.equals(currentSiteName)) {
                Assert.fail("Saved site name validation failed. Expected B3='" + expectedSaved + "' but found '" + currentSiteName + "'");
            }

            System.out.println("STEP 4: Revert Data");
            clearAndType(wait, By.xpath("//input[@id='siteName']"), revertName);
            clearAndType(wait, By.xpath("//input[@id='siteAddress']"), revertAddress);
            clearAndType(wait, By.xpath("//input[@id='contactNumber']"), revertContact);
            clearAndType(wait, By.xpath("//textarea[@id='siteDescription']"), revertDescription);

            System.out.println("STEP 5: Next");
            jsClick(wait, By.xpath("//span[normalize-space()='Next']"));
            Thread.sleep(10000);

            System.out.println("STEP 6: Delete Module");
            jsClick(wait, By.xpath("//div[contains(@class,'droppable-cell')][.//span[normalize-space()='Test Module']]//button[@title='Delete module']"));

            System.out.println("STEP 7: Decrease Channel");
            WebElement channelEl = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='channels']")
            ));
            scrollIntoView(channelEl);
            int currentChannels = Integer.parseInt(channelEl.getAttribute("value").trim());
            int updatedChannels = Math.max(0, currentChannels - 1);
            channelEl.clear();
            channelEl.sendKeys(String.valueOf(updatedChannels));
            jsClick(wait, By.xpath("//span[normalize-space()='Next']"));

            System.out.println("STEP 8: Delete Group");
            jsClick(wait, By.xpath("//div[contains(@class,'group-summary-container')][.//div[normalize-space()='Test Group']]/button[@aria-label='Delete Group']"));
            jsClick(wait, By.xpath("//mat-dialog-container//button[.//span[normalize-space()='Delete']]"));

            System.out.println("STEP 9: Click Next (TOP button)");
            WebElement topNextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'mat-horizontal-stepper-content-current')]//button[.//span[normalize-space()='Next']]")
            ));
            scrollIntoView(topNextBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", topNextBtn);
            Thread.sleep(4000);

            System.out.println("STEP 10: Click arrow forward (Site Score -> Open Opex)");
            WebElement forwardBtn1 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'mat-horizontal-stepper-content-current')]//button[.//mat-icon[normalize-space()='arrow_forward']]")
            ));
            scrollIntoView(forwardBtn1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forwardBtn1);
            Thread.sleep(5000);

            System.out.println("STEP 11: Click arrow forward again (enter Open Opex page)");
            WebElement forwardBtn2 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'mat-horizontal-stepper-content-current')]//button[.//mat-icon[normalize-space()='arrow_forward']]")
            ));
            scrollIntoView(forwardBtn2);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forwardBtn2);
            Thread.sleep(6000);

            System.out.println("STEP 12: Verify Open Opex page");
            List<WebElement> opexSidebar = driver.findElements(By.xpath("//div[contains(@class,'opex-sidebar')]") );
            if (opexSidebar.isEmpty()) {
                Assert.fail("Open Opex page not loaded: opex-sidebar not found");
            }

            System.out.println("STEP 13: Manpower Cost");
            jsClick(wait, By.xpath("//div[contains(@class,'opex-sidebar')]//span[normalize-space()='Manpower cost']"));

            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[.//td[normalize-space()='Internal employee']]")
            ));

            System.out.println("STEP 14: Click Internal employee edit");
            WebElement internalEditBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[.//td[normalize-space()='Internal employee']]//button")
            ));
            scrollIntoView(internalEditBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", internalEditBtn);

            System.out.println("STEP 15: Update Fields");
            By internalEmployeeEditRow = By.xpath(
                "//tr[.//input[contains(@ng-reflect-model,'Internal employee')]]" +
                " | //tr[.//td[normalize-space()='Internal employee']]"
            );
            wait.until(ExpectedConditions.presenceOfElementLocated(internalEmployeeEditRow));
            wait.until(ExpectedConditions.visibilityOfElementLocated(internalEmployeeEditRow));

            WebElement r = driver.findElement(internalEmployeeEditRow);
            List<WebElement> editableInputs = r.findElements(By.xpath(".//input"));
            if (editableInputs.size() < 3) {
                Assert.fail("Internal employee editable inputs not found in row");
            }

            WebElement d = editableInputs.get(1);
            scrollIntoView(d);
            d.clear();
            d.sendKeys("Internal employee Details");

            WebElement c = editableInputs.get(2);
            scrollIntoView(c);
            c.clear();
            c.sendKeys("Currency");

            WebElement numberEl = r.findElement(By.xpath(".//input[@type='number']"));
            scrollIntoView(numberEl);
            numberEl.clear();

            WebElement save = r.findElement(By.xpath(".//mat-icon[normalize-space()='check']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", save);

            System.out.println("STEP 12: Checkbox Handling");
            clickWithFallback(wait, By.xpath("//td[contains(normalize-space(),'Internal employee')]/preceding-sibling::td//input"));
            clickWithFallback(wait, By.xpath("//span[normalize-space()='Manpower cost']/preceding::mat-checkbox[1]"));

            System.out.println("STEP 13: Final Save");
            jsClick(wait, By.xpath("//button[contains(@class,'mat-stepper-next')]"));
            jsClick(wait, By.xpath("//button[.//span[normalize-space()='Save']]"));

            System.out.println("STEP 14: Home Button");
            jsClick(wait, By.xpath("//mat-dialog-actions//button[.//span[normalize-space()='Home']]"));

            System.out.println("STEP 15: Notification Validation");
            jsClick(wait, By.xpath("//img[@class='logo']"));
            jsClick(wait, By.xpath("//i[contains(@class,'notification-icon')]"));

            boolean success = false;
            long endTime = System.currentTimeMillis() + Duration.ofMinutes(5).toMillis();
            while (System.currentTimeMillis() < endTime) {
                try {
                    List<WebElement> notifications = driver.findElements(By.xpath(
                        "//div[contains(@class,'notification-item')]" +
                        "[.//h5[normalize-space()='Site Configuration Saved']" +
                        " and .//span[contains(@class,'badge') and normalize-space()='success']]"
                    ));
                    if (!notifications.isEmpty()) {
                        success = true;
                        break;
                    }
                } catch (StaleElementReferenceException ignored) {
                }

                Thread.sleep(3000);
            }

            if (success) {
                System.out.println("TEST PASS - Revert Successful");
            } else {
                Assert.fail("TEST FAIL - Revert failed (success notification not found within 5 minutes)");
            }

            System.out.println("STEP 15.5: Clear Notifications");
            try {
                WebElement clearAll = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Clear all']")
                ));
                clearAll.click();
            } catch (Exception e) {
                System.out.println("Clear all button not found or not clickable");
            }

            System.out.println("STEP 16: Close Notification");
            jsClick(wait, By.xpath("//button[@title='Close']"));

        } catch (Exception e) {
            Assert.fail("Step failed: " + e.getMessage(), e);
        }
    }

    private void jsClick(WebDriverWait wait, By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void clearAndType(WebDriverWait wait, By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        scrollIntoView(element);
        element.clear();
        element.sendKeys(value);
    }

    private void clickWithFallback(WebDriverWait wait, By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}
