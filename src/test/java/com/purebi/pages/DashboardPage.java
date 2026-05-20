package com.purebi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    private final By plantSettings = By.xpath("//a[normalize-space()='PLANT SETTINGS']");
    private final By userIcon = By.cssSelector("img.avatar[alt='User avatar']");
    private final By logoutBtn = By.cssSelector("a.sub-dropdown-lgn");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean waitForPlantSettings() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(plantSettings));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getPlantSettingsElement() {
        return driver.findElement(plantSettings);
    }

    public void clickUserIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(userIcon)).click();
    }

    public void clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }
}
