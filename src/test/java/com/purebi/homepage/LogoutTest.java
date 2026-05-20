package com.purebi.homepage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.purebi.pages.DashboardPage;

public class LogoutTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void waitBeforeLogoutTest() {
        try {
            Reporter.log("Waiting 20 seconds before executing LogoutTest...", true);
            Thread.sleep(20000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting before LogoutTest", ie);
        }
    }

    @Test(alwaysRun = true)
    public void logoutTest() {
        // Ensure we are on the home/dashboard screen first (works even if previous tests failed)
        try { com.purebi.homepage.BackToHomeScreen.navigateToHomeScreen(driver); } catch (Exception ignored) {}

        DashboardPage dashboard = new DashboardPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            Reporter.log("Waiting for user menu to be ready before logout...", true);
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("img.avatar[alt='User avatar']")));

            // Use page object methods (they include waits) instead of brittle sleeps/selectors
            dashboard.clickUserIcon();
            dashboard.clickLogout();

            // Verify logout by waiting for the login input to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
            Reporter.log("✅ Logout successful; login screen visible.", true);
        } catch (Exception e) {
            Reporter.log("❌ Logout failed: " + e.getMessage(), true);
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public void tearDown() {
        // Driver will be quit by BaseTest.@AfterSuite; do not quit here to avoid closing browser prematurely.
    }
}