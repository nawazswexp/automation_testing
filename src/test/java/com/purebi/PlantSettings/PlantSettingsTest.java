package com.purebi.PlantSettings;

import com.purebi.homepage.BaseTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

public class PlantSettingsTest extends BaseTest {

	private static final By PLANT_SETTINGS_LINK = By.xpath("//a[contains(normalize-space(),'PLANT SETTINGS')]");
	private static final By PLANT_CUSTOMIZATION_LINK = By.xpath("//a[normalize-space()='PLANT CUSTOMIZATION']");
	private static final By HOME_LOGO = By.xpath("//img[contains(@class,'logo')]");
	private static final By SITE_NAME_FIELD = By.id("siteName");
	private static final By SITE_ADDRESS_FIELD = By.id("siteAddress");
	private static final By CONTACT_NUMBER_FIELD = By.id("contactNumber");
	private static final By SITE_DESCRIPTION_FIELD = By.id("siteDescription");
	private static final By NEXT_BUTTON = By.xpath("//span[normalize-space()='Next']");
	private static final By CHANNELS_INPUT = By.xpath("//input[@formcontrolname='channels']");
	private static final By GRID_CELL_4A = By.xpath("//span[text()='4A']");
	private static final By MODULES_FAB_BUTTON = By.xpath("//button[contains(@class,'fab-btn')]");
	private static final By DAF_CHECK = By.xpath("//span[normalize-space()='DAF']");
	private static final By DAF_DRAG_SOURCE = By.xpath("//span[normalize-space()='DAF']/ancestor::div[contains(@class,'dummy-module')]");
	private static final By DROP_TARGET_4A = By.xpath("//span[text()='4A']/ancestor::div[contains(@class,'droppable-cell')]");
	private static final By CONFIG_DIALOG_HEADER = By.xpath("//div[contains(@class,'dialog-header')]");
	private static final By ADD_PARAMETER_TITLE = By.xpath("//h3[normalize-space()='Add Parameter']");
	private static final By RANGE_SELECTED_ROWS = By.xpath("//tr[contains(@class,'range-selected')]");
	private static final By ADD_PARAMETER_BUTTON = By.xpath("//button[.//span[normalize-space()='ADD Parameter']]");
	private static final By SAVE_SPAN = By.xpath("//span[normalize-space()='Save']");

	@Test(groups = "plant-settings-setup", alwaysRun = true)
	public void plantSettingsTest() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		try {
			System.out.println("STEP 0: Go to Home");
			clickLocatorWithJs(wait, HOME_LOGO, "Home Logo");

			System.out.println("STEP 1: Navigate to Plant Customization");
			clickLocatorWithJs(wait, PLANT_SETTINGS_LINK, "PLANT SETTINGS");
			clickLocatorWithJs(wait, PLANT_CUSTOMIZATION_LINK, "PLANT CUSTOMIZATION");

			System.out.println("STEP 2: Wait 60 seconds");
			sleepWithMessage(60_000, "Waiting 60 seconds");

			System.out.println("STEP 3: Read Excel data");
			PlantSettingsData data = loadExcelData();

			System.out.println("STEP 4: Fill form");
			enterText(wait, SITE_NAME_FIELD, data.siteName);
			enterText(wait, SITE_ADDRESS_FIELD, data.siteAddress);
			enterText(wait, CONTACT_NUMBER_FIELD, data.contactNumber);
			enterText(wait, SITE_DESCRIPTION_FIELD, data.siteDescription);

			System.out.println("STEP 5: Click Next");
			clickLocatorWithJs(wait, NEXT_BUTTON, "Next");
			System.out.println("Wait 10 seconds");
			sleepWithMessage(10_000, "Waiting 10 seconds");

			System.out.println("STEP 6: Increment channels");
			incrementChannelNumber(wait);

			System.out.println("STEP 7: Wait for grid cell 4A");
			wait.until(ExpectedConditions.visibilityOfElementLocated(GRID_CELL_4A));

			System.out.println("STEP 8-10: Safe Modules open and JS drag-drop");
			dragModuleToCell(wait, "DAF", "4A");

			System.out.println("STEP 11: Close Modules panel");
			WebElement body = driver.findElement(By.tagName("body"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", body);
			new Actions(driver).sendKeys(Keys.ESCAPE).perform();
			Thread.sleep(1000);

			System.out.println("STEP 12: Close overlay before CONFIG click");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//mat-dialog-container")
			));
			Thread.sleep(1000);

			System.out.println("STEP 12: Click CONFIG button for DAF");
			clickConfigButtonForDAF();

			System.out.println("STEP 13: Wait config dialog");
			wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[contains(@class,'cdk-overlay-pane')]//*[contains(text(),'Configure')]")
			));
			System.out.println("Configure popup opened successfully");

			System.out.println("STEP 14: Enter aliasName");
			enterText(wait, By.xpath("//input[@name='aliasName']"), "Test Module");

			System.out.println("STEP 15: Select dropdowns");
			selectMatSelectByIndex(wait, 1, null);
			selectMatSelectByIndex(wait, 2, "2");

			System.out.println("STEP 16: Click ADD Parameter");
			clickAddParameter(wait);

			System.out.println("STEP 17: Wait Add Parameter screen");
			wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_PARAMETER_TITLE));

			System.out.println("STEP 18: Select first checkbox");
			WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("(//tr[.//td[normalize-space()='Inlet Flow rate']]//input[@type='checkbox'])[1]")
			));

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", checkbox);
			Thread.sleep(500);

			((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

			wait.until(driver -> checkbox.isSelected());

			System.out.println("Checkbox clicked successfully");

			System.out.println("STEP 19: Click FINAL Add Parameter button");

			wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//h3[normalize-space()='Add Parameter']")
			));

			List<WebElement> buttons = driver.findElements(
				By.xpath("//button[@class='mdc-button mdc-button--unelevated mat-mdc-unelevated-button mat-accent mat-mdc-button-base']")
			);

			for (int i = buttons.size() - 1; i >= 0; i--) {
				WebElement btn = buttons.get(i);
				if (btn.isDisplayed()) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
					System.out.println("Clicked correct Add Parameter button");
					break;
				}
			}

			JavascriptExecutor js = (JavascriptExecutor) driver;

			System.out.println("STEP 20: Wait for next page");
			wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//tr[.//td[normalize-space()='Inlet Flow rate']]")
			));

			System.out.println("STEP 21: Click checkbox again");
			WebElement checkboxAfterRedirect = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("(//tr[.//td[normalize-space()='Inlet Flow rate']]//input[@type='checkbox'])[1]")
			));

			js.executeScript("arguments[0].scrollIntoView({block:'center'});", checkboxAfterRedirect);
			Thread.sleep(500);
			js.executeScript("arguments[0].click();", checkboxAfterRedirect);

			wait.until(driver -> checkboxAfterRedirect.isSelected());

			System.out.println("STEP 22: Click Edit button");
			WebElement editBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//span[@title='Edit']")
			));

			js.executeScript("arguments[0].click();", editBtn);

			System.out.println("STEP 23: Enter value");
			WebElement inputBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//td[contains(@class,'ng-star-inserted')]//input[@type='number']")
			));

			inputBox.clear();
			inputBox.sendKeys("100");

			System.out.println("STEP 24: Save range");
			WebElement rangeSaveBtn = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//tr[contains(@class,'range-selected')]//button[.//span[normalize-space()='Save']]")
			));

			js.executeScript("arguments[0].click();", rangeSaveBtn);

			System.out.println("STEP 25: Final Save");
			WebElement finalSaveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//div[contains(@class,'dialog-actions')]//button[.//span[normalize-space()='Save']]")
			));

			js.executeScript("arguments[0].scrollIntoView({block:'center'});", finalSaveBtn);
			Thread.sleep(1000);

			js.executeScript("arguments[0].click();", finalSaveBtn);

			new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//span[@class='fab-btn-text' and normalize-space()='Modules']")
				));

			driver.findElement(By.xpath("//span[@class='fab-btn-text' and normalize-space()='Modules']")).click();

			System.out.println("Final Save clicked successfully");

			System.out.println("Parameter configured and saved successfully");

			System.out.println("STEP 26: WAIT AFTER FIRST SAVE");

			wait.until(ExpectedConditions.invisibilityOfElementLocated(
				By.xpath("//div[contains(@class,'cdk-overlay-pane')]//mat-dialog-container")
			));

			Thread.sleep(2000);

			System.out.println("STEP 27: CLICK CONFIG BUTTON AGAIN");

			Thread.sleep(2000);

			List<WebElement> configButtons = driver.findElements(
				By.xpath("//button[contains(@class,'module-config-btn')]")
			);

			if (configButtons.isEmpty()) {
				throw new RuntimeException("No CONFIG buttons found");
			}

			WebElement configBtn = configButtons.get(configButtons.size() - 1);

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", configBtn);
			Thread.sleep(1000);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", configBtn);

			System.out.println("Clicked CONFIG again successfully");

			System.out.println("STEP 29: WAIT FOR CONFIG DIALOG");
			wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//div[contains(@class,'cdk-overlay-pane')]//mat-dialog-container")
			));

			System.out.println("STEP 30: FINAL SAVE AGAIN");

			WebElement finalSaveAgain = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//div[contains(@class,'cdk-overlay-pane')]//button[.//span[normalize-space()='Save']]")
			));

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", finalSaveAgain);
			Thread.sleep(1000);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalSaveAgain);

			System.out.println("Second Save flow completed");

			System.out.println("STEP 31: Click Next button");
			WebElement nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//button[@class='mdc-button mat-mdc-button mat-unthemed mat-mdc-button-base ng-star-inserted']//span[@class='mat-mdc-button-touch-target']")
			));

			((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({block:'center'});", nextBtn
			);

			Thread.sleep(1000);

			wait.until(ExpectedConditions.visibilityOf(nextBtn));

			((JavascriptExecutor) driver).executeScript(
				"arguments[0].click();", nextBtn
			);

			System.out.println("STEP 32: Click Create New Group");
			WebElement createBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//button[.//span[normalize-space()='Create New Group']]")
			));

			((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({block:'center'});", createBtn
			);

			Thread.sleep(1000);

			wait.until(ExpectedConditions.elementToBeClickable(createBtn));

			((JavascriptExecutor) driver).executeScript(
				"arguments[0].click();", createBtn
			);

			System.out.println("STEP 33: Enter Group Name");
			driver.findElement(By.xpath("//input[contains(@class,'custom-input')]")).sendKeys("Test Group");

			System.out.println("STEP 34: Select checkbox for Collection tank");
			driver.findElement(By.xpath("//tr[.//td[contains(normalize-space(),'Collection tank')]]//input[@type='checkbox']")).click();

			System.out.println("STEP 35: Enter value 100");
			driver.findElement(By.xpath("//tr[.//td[contains(normalize-space(),'Collection tank')]]//input[@type='number']")).sendKeys("100");

			System.out.println("STEP 36: Select dropdown value = 1");
			new Select(driver.findElement(By.xpath("//tr[.//td[contains(normalize-space(),'Collection tank')]]//select"))).selectByIndex(1);

			System.out.println("STEP 37: Click Save");
			driver.findElement(By.xpath("//button[@type='submit' and .//span[normalize-space()='Save']]")).click();

			System.out.println("STEP 38: Click Next Arrow");
			driver.findElement(By.xpath("//div[contains(@class,'mat-horizontal-stepper-content-current')]//button[.//mat-icon[normalize-space()='arrow_forward']]")).click();

			System.out.println("STEP 39: Click Submit button (Step 4)");
			WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//div[@id='cdk-stepper-0-content-3']//button[@type='submit']")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

			System.out.println("STEP 40: Select Manpower cost checkbox");
			WebElement checkboxWrapper = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//span[normalize-space()='Manpower cost']/preceding::mat-checkbox[1]")
			));

			WebElement clickable = checkboxWrapper.findElement(
				By.xpath(".//div[contains(@class,'mdc-checkbox')]")
			);

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", clickable);
			Thread.sleep(1000);

			wait.until(ExpectedConditions.elementToBeClickable(clickable));

			try {
				clickable.click();
			} catch (Exception e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
			}

			Thread.sleep(1000);

			String ariaChecked = checkboxWrapper.getAttribute("aria-checked");
			String checkboxClass = checkboxWrapper.getAttribute("class");
			boolean selected = (ariaChecked != null && ariaChecked.equals("true")) || (checkboxClass != null && checkboxClass.toLowerCase().contains("checked"));
			if (!selected) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
				Thread.sleep(1000);
				ariaChecked = checkboxWrapper.getAttribute("aria-checked");
				checkboxClass = checkboxWrapper.getAttribute("class");
				selected = (ariaChecked != null && ariaChecked.equals("true")) || (checkboxClass != null && checkboxClass.toLowerCase().contains("checked"));
				if (!selected) {
					throw new AssertionError("Checkbox UI not responding");
				}
			}

			wait.until(webDriver -> {
				List<WebElement> rows = webDriver.findElements(By.xpath("//table//tr"));
				return rows.size() > 0;
			});

			Thread.sleep(2000);

			WebElement manpower = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//div[contains(@class,'opex-sidebar')]//span[normalize-space()='Manpower cost']")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", manpower);

			((JavascriptExecutor) driver).executeScript("arguments[0].click();", manpower);
			Thread.sleep(2000);

			wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//table")
			));

			wait.until(webDriver -> {
				List<WebElement> rows = webDriver.findElements(By.xpath("//table//tr"));
				return rows.size() > 0;
			});

			Thread.sleep(2000);

			List<WebElement> internalEmployeeRows = driver.findElements(
				By.xpath("//td[contains(normalize-space(),'Internal employee')]")
			);
			if (internalEmployeeRows.isEmpty()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", manpower);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", manpower);
				Thread.sleep(2000);
				internalEmployeeRows = driver.findElements(By.xpath("//td[contains(normalize-space(),'Internal employee')]") );
			}
			if (internalEmployeeRows.isEmpty()) {
				throw new RuntimeException("Internal employee row was not found after Manpower cost actions");
			}

			wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//td[contains(normalize-space(),'Internal employee')]")
			));

			System.out.println("STEP 42: Select Internal employee checkbox");
			WebElement internalEmployeeCheckbox = driver.findElement(
				By.xpath("//td[normalize-space()='Internal employee']/preceding-sibling::td//input[@type='checkbox']")
			);

			if (!internalEmployeeCheckbox.isSelected()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", internalEmployeeCheckbox);
				Thread.sleep(500);

				try {
					internalEmployeeCheckbox.click();
				} catch (Exception e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", internalEmployeeCheckbox);
				}

				Thread.sleep(1000);
			}

			if (!internalEmployeeCheckbox.isSelected()) {
				throw new AssertionError("Internal employee checkbox not selected");
			}

			System.out.println("STEP 43: Click edit button");
			WebElement editBtnInternal = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//tr[.//td[normalize-space()='Internal employee']]//button[.//mat-icon[normalize-space()='edit']]")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", editBtnInternal);
			Thread.sleep(1000);
			try {
				wait.until(ExpectedConditions.elementToBeClickable(editBtnInternal)).click();
			} catch (Exception e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtnInternal);
			}

			System.out.println("STEP 44: Fill details");
			WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//tr[.//input[@ng-reflect-model='Internal employee']]//td[3]//input")
			));
			nameField.clear();
			nameField.sendKeys("Test Details");

			WebElement currencyField = driver.findElement(
				By.xpath("//tr[.//input[@ng-reflect-model='Internal employee']]//td[4]//input")
			);
			currencyField.clear();
			currencyField.sendKeys("\u20B9INR");

			WebElement valueField = driver.findElement(
				By.xpath("//tr[.//input[@ng-reflect-model='Internal employee']]//input[@type='number']")
			);
			valueField.clear();
			valueField.sendKeys("69");

			System.out.println("STEP 45: Save row");
			WebElement saveRow = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//tr[.//input[@ng-reflect-model='Internal employee']]//mat-icon[normalize-space()='check']")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", saveRow);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveRow);

			System.out.println("STEP 46: Next step");
			WebElement nextBtn2 = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//div[contains(@class,'mat-horizontal-stepper-content-current')]//button[contains(@class,'mat-stepper-next')]")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", nextBtn2);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextBtn2);

			System.out.println("STEP 47: Final Save");
			WebElement finalSave = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//button[.//span[normalize-space()='Save']]")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", finalSave);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalSave);

			System.out.println("STEP 48: Wait for Home button and click");
			WebElement homeBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//mat-dialog-actions//button[.//span[normalize-space()='Home']]")
			));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", homeBtn);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", homeBtn);

			WebDriverWait longWait = new WebDriverWait(driver, Duration.ofMinutes(5));

			// STEP 1: Go to Home
			driver.findElement(By.xpath("//img[@class='logo']")).click();

			// STEP 2: Open Notification Panel
			WebElement bell = longWait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//i[contains(@class,'notification-icon')]")
			));
			bell.click();

			// STEP 3: WAIT + POLLING for SUCCESS NOTIFICATION
			boolean isSuccess = false;

			long endTime = System.currentTimeMillis() + (5 * 60 * 1000);

			while (System.currentTimeMillis() < endTime) {

				try {
					List<WebElement> notifications = driver.findElements(By.xpath(
						"//div[contains(@class,'notification-item')]" +
						"[.//h5[normalize-space()='Site Configuration Saved']" +
						" and .//span[contains(@class,'badge') and normalize-space()='success']]"
					));

					if (!notifications.isEmpty()) {
						isSuccess = true;
						break;
					}

				} catch (Exception ignored) {
				}

				Thread.sleep(3000);
			}

			// STEP 4: RESULT
			if (isSuccess) {
				System.out.println("TEST PASS \u2705 - Site Configuration Saved");
			} else {
				throw new AssertionError("TEST FAIL \u274C - Site Configuration not saved");
			}

			// STEP 4.5: CLICK CLEAR ALL (optional, non-failing)
			try {
				WebElement clearAll = longWait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[normalize-space()='Clear all']")
				));
				clearAll.click();
			} catch (Exception e) {
				System.out.println("Clear all button not found or not clickable");
			}

			// STEP 5: CLOSE PANEL
			driver.findElement(By.xpath("//button[@title='Close']")).click();

		} catch (Exception e) {
			Assert.fail("Step failed: " + e.getMessage(), e);
		}
	}

	public void jsClick(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	private PlantSettingsData loadExcelData() {
		try {
			InputStream is = null;
			try {
				is = getClass().getClassLoader().getResourceAsStream("PlantSettings.xlsx");
			} catch (Exception ignored) {
				// fall through to filesystem lookup below
			}

			if (is == null) {
				String fallbackPath = System.getProperty("user.dir") + java.io.File.separator + "src" + java.io.File.separator + "test" + java.io.File.separator + "resources" + java.io.File.separator + "PlantSettings.xlsx";
				java.io.File fallbackFile = new java.io.File(fallbackPath);
				if (fallbackFile.exists()) {
					is = new FileInputStream(fallbackFile);
				}
			}

			if (is == null) {
				throw new RuntimeException("Excel file not found. Checked classpath PlantSettings.xlsx and src/test/resources/PlantSettings.xlsx");
			}

			try (InputStream managed = is; Workbook workbook = new XSSFWorkbook(managed)) {
				Sheet sheet = workbook.getSheet("Data");
				if (sheet == null) {
					throw new RuntimeException("Sheet 'Data' not found in PlantSettings.xlsx");
				}

				Row row = sheet.getRow(2);
				if (row == null) {
					throw new RuntimeException("Row 3 not found in sheet 'Data' for PlantSettings.xlsx");
				}

				String siteName = row.getCell(1).getStringCellValue();
				String siteAddress = row.getCell(2).getStringCellValue();
				String contactNumber = String.valueOf((long) row.getCell(3).getNumericCellValue());
				String siteDescription = row.getCell(4).getStringCellValue();

				return new PlantSettingsData(siteName, siteAddress, contactNumber, siteDescription);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to load PlantSettings.xlsx: " + e.getMessage(), e);
		}
	}

	private void clickVisibleTextLink(WebDriverWait wait, By locator, String label) {
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click " + label + ": " + e.getMessage(), e);
		}
	}

	private void clickVisibleTextElement(WebDriverWait wait, By locator, String label) {
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click " + label + ": " + e.getMessage(), e);
		}
	}

	private void enterText(WebDriverWait wait, By field, String value) {
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
			scrollIntoView(element);
			element.clear();
			element.sendKeys(value);
		} catch (Exception e) {
			throw new RuntimeException("Unable to enter text for " + field + ": " + e.getMessage(), e);
		}
	}

	private void incrementChannelNumber(WebDriverWait wait) {
		try {
			WebElement channelInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CHANNELS_INPUT));
			scrollIntoView(channelInput);
			String currentValue = channelInput.getAttribute("value");
			int currentNumber = Integer.parseInt(currentValue == null || currentValue.trim().isEmpty() ? "0" : currentValue.trim());
			int updatedNumber = currentNumber + 1;

			channelInput.clear();
			channelInput.sendKeys(String.valueOf(updatedNumber));
		} catch (Exception e) {
			throw new RuntimeException("Unable to update channel number: " + e.getMessage(), e);
		}
	}

	private void clickModulesButton(WebDriverWait wait) {
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(MODULES_FAB_BUTTON));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to open Modules: " + e.getMessage(), e);
		}
	}

	private void clickConfigButtonForDAF() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

			// STEP 1: Wait for CONFIG button using global XPath
			WebElement configBtn = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//div[contains(@class,'droppable-cell')][.//span[normalize-space()='DAF']]//button[contains(@class,'module-config-btn')]")
			));

			// STEP 3: Scroll into view
			((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({block:'center'});", configBtn
			);

			Thread.sleep(500);

			// STEP 4: JS click (important)
			((JavascriptExecutor) driver).executeScript(
				"arguments[0].click();", configBtn
			);

			System.out.println("Clicked CONFIGURE for DAF");

		} catch (Exception e) {
			throw new RuntimeException("Unable to click CONFIG button for DAF: " + e.getMessage(), e);
		}
	}

	private void clickAddParameter(WebDriverWait wait) {
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(ADD_PARAMETER_BUTTON));
			scrollIntoView(element);
			Thread.sleep(1500);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click ADD Parameter: " + e.getMessage(), e);
		}
	}

	private void clickCheckboxByIndex(WebDriverWait wait, int index) {
		By locator = By.xpath("(//input[@type='checkbox'])[" + index + "]");
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click checkbox at index " + index + ": " + e.getMessage(), e);
		}
	}

	private void selectMatSelectByIndex(WebDriverWait wait, int index, String optionText) {
		By triggerLocator = By.xpath("(//div[contains(@class,'mat-mdc-select-trigger')])[" + index + "]");
		try {
			WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(triggerLocator));
			scrollIntoView(trigger);
			jsClick(trigger);

			By optionLocator;
			if (optionText == null) {
				optionLocator = By.xpath("(//mat-option)[1]");
			} else {
				optionLocator = By.xpath("//mat-option[.//span[normalize-space()='" + optionText + "'] or normalize-space()='" + optionText + "']");
			}

			WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));
			jsClick(option);
		} catch (Exception e) {
			throw new RuntimeException("Unable to select dropdown " + index + ": " + e.getMessage(), e);
		}
	}

	private void clickButtonWithVisibleText(WebDriverWait wait, String text) {
		By locator = By.xpath("//button[.//span[normalize-space()='" + text + "']]");
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click button with text '" + text + "': " + e.getMessage(), e);
		}
	}

	private void clickLocatorWithJs(WebDriverWait wait, By locator, String label) {
		try {
			WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			scrollIntoView(element);
			jsClick(element);
		} catch (Exception e) {
			throw new RuntimeException("Unable to click " + label + ": " + e.getMessage(), e);
		}
	}

	public void dragModuleToCell(WebDriverWait wait, String module, String cell) {
		try {
			// ---------------------------------------
			// STEP 1: FORCE SCROLL + CLICK
			// ---------------------------------------

			By modulesBtn = By.xpath("//button[contains(@class,'fab-btn')]");

			WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(modulesBtn));

			// Scroll into view
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
			Thread.sleep(1000);

			// Use Actions (REAL user click)
			Actions actions = new Actions(driver);
			actions.moveToElement(btn).pause(Duration.ofMillis(300)).click().perform();

			System.out.println("Clicked Modules button using Actions");

			Thread.sleep(2000);

			// ---------------------------------------
			// STEP 2: FORCE PANEL OPEN (RETRY LOGIC)
			// ---------------------------------------

			By panelLocator = By.xpath("//div[contains(@class,'fab-popover')]");

			boolean isPanelVisible = false;

			for (int i = 0; i < 3; i++) {
				List<WebElement> panels = driver.findElements(panelLocator);

				if (!panels.isEmpty() && panels.get(0).isDisplayed()) {
					isPanelVisible = true;
					break;
				}

				// Retry click
				actions.moveToElement(btn).click().perform();
				Thread.sleep(1500);
			}

			if (!isPanelVisible) {
				throw new RuntimeException("Module panel did NOT open");
			}

			System.out.println("Module panel is visible");

			// ---------------------------------------
			// STEP 3: WAIT FOR DAF (NOW SAFE)
			// ---------------------------------------

			By dafLocator = By.xpath("//div[contains(@class,'fab-popover')]//span[normalize-space()='DAF']");

			WebElement daf = wait.until(ExpectedConditions.visibilityOfElementLocated(dafLocator));

			System.out.println("DAF found");

			// Close module panel overlay before drag so drop zone is not blocked.
			driver.findElement(By.xpath("//body")).click();
			Thread.sleep(1000);

			// ---------------------------------------
			// STEP 4: TARGET CELL
			// ---------------------------------------

			By targetCell = By.xpath("//span[text()='4A']/ancestor::div[contains(@class,'droppable-cell')]");

			WebElement target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetCell));

			// ---------------------------------------
			// STEP 5: REAL DRAG (IMPORTANT)
			// ---------------------------------------
			WebElement grid = driver.findElement(By.xpath("//div[contains(@class,'grid')]"));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = 0;", grid);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", grid);
			((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({block:'center', inline:'center'});",
				target
			);
			Thread.sleep(1000);

			Actions dragActions = new Actions(driver);

			dragActions.clickAndHold(daf)
				.pause(Duration.ofMillis(500))
				.moveToElement(target, 10, 10)
				.pause(Duration.ofMillis(500))
				.release()
				.perform();

			System.out.println("Dragged DAF to 4A using safe target offset");
			Thread.sleep(2000);

			List<WebElement> configCheck = target.findElements(By.xpath(".//button"));

			if (configCheck.isEmpty()) {
				throw new RuntimeException("Drag FAILED — module not dropped in 4A");
			}

			System.out.println("Module successfully dropped in 4A");
		} catch (Exception e) {
			throw new RuntimeException("Unable to drag module " + module + " to cell " + cell + ": " + e.getMessage(), e);
		}
	}

	private void scrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
	}

	private void sleepWithMessage(long millis, String message) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting: " + message, ie);
		}
	}

	private static final class PlantSettingsData {
		private final String siteName;
		private final String siteAddress;
		private final String contactNumber;
		private final String siteDescription;

		private PlantSettingsData(String siteName, String siteAddress, String contactNumber, String siteDescription) {
			this.siteName = siteName;
			this.siteAddress = siteAddress;
			this.contactNumber = contactNumber;
			this.siteDescription = siteDescription;
		}
	}
}
