package com.purebi.homepage;

import com.purebi.utils.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import org.testng.ITestResult;
import org.testng.SkipException;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import com.purebi.pages.DashboardPage;
import com.purebi.pages.LoginPage;
import com.purebi.utils.TestExecutionControl;
import com.purebi.homepage.LoginTest;

public class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final Set<WebDriver> ACTIVE_DRIVERS = ConcurrentHashMap.newKeySet();

    protected WebDriver driver;

    public static WebDriver getCurrentDriver() {
        return DRIVER.get();
    }

    @BeforeSuite(alwaysRun = true)
    public void resetDriverState() {
        ACTIVE_DRIVERS.clear();
        DRIVER.remove();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(java.lang.reflect.Method method) {
        String className = method.getDeclaringClass().getName();
        boolean loginTest = className.equals(LoginTest.class.getName());

        Boolean loginPassed = waitForLoginDecision(loginTest);
        if (!loginTest && !Boolean.TRUE.equals(loginPassed)) {
            throw new SkipException("Skipping because login failed");
        }

        WebDriver currentDriver = DRIVER.get();
        if (currentDriver != null) {
            try {
                currentDriver.getWindowHandles();
                driver = currentDriver;
                return;
            } catch (Exception ignored) {
                try {
                    currentDriver.quit();
                } catch (Exception ignoredAgain) {
                }
                ACTIVE_DRIVERS.remove(currentDriver);
                DRIVER.remove();
            }
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", System.getenv().getOrDefault("CI", "false"))
        );

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        WebDriver newDriver = new ChromeDriver(options);
        newDriver.manage().window().maximize();

        DRIVER.set(newDriver);
        ACTIVE_DRIVERS.add(newDriver);
        driver = newDriver;

        String baseUrl = Config.getBaseUrl();
        System.out.println("Running tests on: " + baseUrl + " | headless=" + headless);
        try {
            newDriver.get(baseUrl + "/login");

            if (!loginTest) {
                authenticateBrowser(newDriver, baseUrl);
            }
        } catch (RuntimeException | Error ex) {
            try {
                newDriver.quit();
            } catch (Exception ignored) {
            } finally {
                ACTIVE_DRIVERS.remove(newDriver);
                DRIVER.remove();
                driver = null;
            }
            throw ex;
        }
    }

    private Boolean waitForLoginDecision(boolean loginTest) {
        if (loginTest) {
            return TestExecutionControl.getLoginPassed();
        }

        long timeoutMs = Long.parseLong(System.getProperty("loginGateTimeoutMs", "180000"));
        long start = System.currentTimeMillis();
        Boolean loginPassed = TestExecutionControl.getLoginPassed();

        while (loginPassed == null && (System.currentTimeMillis() - start) < timeoutMs) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
            loginPassed = TestExecutionControl.getLoginPassed();
        }

        return loginPassed;
    }

    @AfterTest(alwaysRun = true)
    public void quitDriver() {
        WebDriver currentDriver = DRIVER.get();
        if (currentDriver == null) {
            return;
        }

        try {
            try {
                WebDriverWait wait = new WebDriverWait(currentDriver, Duration.ofSeconds(5));
                boolean hasPlantSettings = currentDriver.findElements(By.xpath("//a[normalize-space()='PLANT SETTINGS']")).size() > 0;
                if (hasPlantSettings) {
                    DashboardPage dashboard = new DashboardPage(currentDriver);
                    try {
                        dashboard.clickUserIcon();
                        dashboard.clickLogout();
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }

            currentDriver.quit();
        } catch (Exception ignored) {
        } finally {
            ACTIVE_DRIVERS.remove(currentDriver);
            DRIVER.remove();
            driver = null;
        }
    }

    @AfterSuite(alwaysRun = true)
    public void quitAllDrivers() {
        for (WebDriver activeDriver : new LinkedHashSet<>(ACTIVE_DRIVERS)) {
            try {
                activeDriver.quit();
            } catch (Exception ignored) {
            }
        }
        ACTIVE_DRIVERS.clear();
        DRIVER.remove();
    }

    private void authenticateBrowser(WebDriver currentDriver, String baseUrl) {
        LoginPage loginPage = new LoginPage(currentDriver);
        loginPage.waitForLoginPageReady();
        loginPage.login("zld", "Bbpuram@&zsrnf12");
        loginPage.waitForDashboardReady();

        if (currentDriver.findElements(By.xpath("//a[normalize-space()='PLANT SETTINGS']")).isEmpty()
                && currentDriver.findElements(By.xpath("//img[@class='logo']")).isEmpty()) {
            throw new SkipException("Browser login did not reach the dashboard");
        }

        System.out.println("Authenticated browser ready for tests on: " + baseUrl);
    }

    public WebElement waitForSpecialReportsLink() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[normalize-space()='SPECIAL REPORTS']")
        ));
    }

    public void clickSpecialReports() {
        WebElement element = waitForSpecialReportsLink();
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}
