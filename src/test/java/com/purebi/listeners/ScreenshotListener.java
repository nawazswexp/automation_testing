package com.purebi.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.purebi.homepage.BaseTest;
import com.purebi.utils.ScreenshotUtil;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) { }

    @Override
    public void onTestSuccess(ITestResult result) { }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String testName = result.getName();
            String path = ScreenshotUtil.takeScreenshot(BaseTest.getCurrentDriver(), testName);
            if (path != null) {
                // path is returned as a report-relative path like "screenshots/NAME.png"
                String rel = path.replaceAll("\\\\", "/");
                // show a small embedded thumbnail that links to the full image
                Reporter.log("<a href='" + rel + "'><img src='" + rel + "' height='200' alt='screenshot'/></a>", true);
                Reporter.log("<br/><a href='" + rel + "'>Full screenshot: " + testName + "</a>", true);
            } else {
                Reporter.log("Screenshot not available", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) { }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }

    @Override
    public void onStart(ITestContext context) { }

    @Override
    public void onFinish(ITestContext context) { }
}
