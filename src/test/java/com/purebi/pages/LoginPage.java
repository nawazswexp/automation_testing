package com.purebi.pages;

import com.purebi.homepage.BaseTest;
import com.purebi.utils.Config;
import com.purebi.utils.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class LoginPage extends BasePage {

    private final By username = By.id("login");
    private final By password = By.id("password");
    private final By loginButton = Locators.LOGIN_BUTTON;
    private final By errorMessage = Locators.ERROR_MESSAGE;
    private final By plantSettings = Locators.PLANT_SETTINGS;
    private final By homeLogo = Locators.LOGO;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * No-arg compatibility constructor used by older tests that instantiate LoginPage without a driver.
     * It uses the current thread's BaseTest driver instance.
     */
    public LoginPage() {
        super(BaseTest.getCurrentDriver());
    }

    public void open() {
        String baseUrl = Config.getBaseUrl();
        driver.get(baseUrl + "/login");
    }

    public void waitForLoginPageReady() {
        waitForLoginPageReady(false);
    }

    private void waitForLoginPageReady(boolean retried) {
        WebDriverWait loginReadyWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            loginReadyWait.until(d -> "complete".equals(
                    ((JavascriptExecutor) d).executeScript("return document.readyState")
            ));
            loginReadyWait.until(ExpectedConditions.visibilityOfElementLocated(username));
            loginReadyWait.until(ExpectedConditions.visibilityOfElementLocated(password));
            loginReadyWait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
        } catch (Exception e) {
            if (retried) {
                throw e;
            }

            driver.navigate().refresh();
            waitForLoginPageReady(true);
        }
    }

    public void enterUsername(String user) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        el.clear();
        el.sendKeys(user);
    }

    public void enterPassword(String pass) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(password));
        el.clear();
        el.sendKeys(pass);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public void login(String user, String pass) {
        waitForLoginPageReady();
        enterUsername(user);
        enterPassword(pass);
        clickLogin();
    }

    public void waitForDashboardReady() {
        WebDriverWait dashboardWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        dashboardWait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(plantSettings),
                ExpectedConditions.visibilityOfElementLocated(homeLogo)
        ));
    }

    public boolean isErrorDisplayed() {
        return driver.findElements(errorMessage).size() > 0;
    }

    public String getErrorText() {
        if (isErrorDisplayed()) return driver.findElement(errorMessage).getText();
        return null;
    }

    // Compatibility aliases for older test code
    public boolean isLoggedIn(int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement el = shortWait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            return el != null && el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return getErrorText();
    }
}
