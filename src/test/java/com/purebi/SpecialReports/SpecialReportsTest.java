package com.purebi.SpecialReports;

import com.purebi.homepage.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class SpecialReportsTest extends BaseTest {

    public String captureScreenshotBase64() {

        try {

            // Scroll to middle before capture
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight/2);");

            TakesScreenshot ts = (TakesScreenshot) driver;
            String base64 = ts.getScreenshotAs(OutputType.BASE64);

            return base64;

        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void lifecycleAllReports() {
        SpecialReportsPage page = new SpecialReportsPage(driver);
        SoftAssert sa = new SoftAssert();

        List<String> types = Arrays.asList(
                "Bar", "Line", "Dot", "Bar Label", "Line Label",
                "Table", "Numeric", "Gauge", "Pie", "Donut", "Box", "Text"
        );

        boolean overallFailure = false;

        for (String type : types) {

            System.out.println("Report Type - " + type);
            Reporter.log("<br><b>Report Type - " + type + "</b><br>");

            // ================= CREATION =================
            try {

                createReport(page, type);
                Reporter.log("Creation PASS: " + type + "<br>");

            } catch (Exception e) {

                overallFailure = true;

                // 🔴 Capture screenshot immediately BEFORE any UI action
                String base64 = captureScreenshotBase64();

                Reporter.log("<span style='color:red;'><b>Creation FAIL: "
                        + type + "</b></span><br>");

                Reporter.log("<b>Reason:</b> " + e.getMessage() + "<br>");

                if (base64 != null) {
                    Reporter.log("<img src='data:image/png;base64," + base64
                            + "' height='400' width='700'/><br>");
                }

                // After screenshot, now clear notifications (if needed)
                try {
                    driver.findElement(org.openqa.selenium.By.xpath("//button[@title='Clear all notifications']")).click();
                } catch (Exception ignore) {}

                Reporter.log("Skipping View & Delete<br><hr>");

                continue;
            }

            // ================= VIEW =================
            try {

                viewReport(page, type);
                Reporter.log("View PASS: " + type + "<br>");

            } catch (Exception e) {

                overallFailure = true;

                String base64 = captureScreenshotBase64();

                Reporter.log("<span style='color:red;'><b>View FAIL: "
                        + type + "</b></span><br>");

                Reporter.log("<b>Reason:</b> " + e.getMessage() + "<br>");

                if (base64 != null) {
                    Reporter.log("<img src='data:image/png;base64," + base64
                            + "' height='400' width='700'/><br>");
                }

                Reporter.log("Skipping Delete<br><hr>");

                continue;
            }

            // ================= DELETE =================
            try {

                deleteReport(page, type);
                Reporter.log("Delete PASS: " + type + "<br><hr>");

            } catch (Exception e) {

                overallFailure = true;

                String base64 = captureScreenshotBase64();

                Reporter.log("<span style='color:red;'><b>Delete FAIL: "
                        + type + "</b></span><br>");

                Reporter.log("<b>Reason:</b> " + e.getMessage() + "<br>");

                if (base64 != null) {
                    Reporter.log("<img src='data:image/png;base64," + base64
                            + "' height='400' width='700'/><br>");
                }

                Reporter.log("<hr>");
            }
        }

        if (overallFailure) {
            org.testng.Assert.fail("One or more report types failed. Check emailable-report.html for details.");
        }

        sa.assertAll();
    }

    // Test-local helpers that reuse existing page flows (do not change page methods)
    private void createReport(SpecialReportsPage page, String type) throws Exception {
        clickSpecialReports();
        page.clickAddReport();
        page.setDashboardName(type);
        page.selectReportCategoryProcess();
        page.setDescription("This is the test Report for " + type);
        page.selectSingleLayout();
        page.expandReportSection1();
        page.setReportTitle(type);

        if (!"Text".equals(type)) {
            page.selectReportSectionType(type);
            page.selectParameters();
        } else {
            page.selectReportSectionType(type);
        }

        if (!type.equalsIgnoreCase("Text")) {
            page.addSection();
            page.saveDashboard();
        }

        String reportType = type;
        if (reportType.equalsIgnoreCase("Text")) {
            page.enterTextReportContent("Testing the text report type");
            page.addSection();
            page.saveDashboard();
        }

        boolean notif = page.waitForCreationNotification(type, 5);
        if (!notif) throw new RuntimeException("Creation notification not found for " + type);
    }

    private void viewReport(SpecialReportsPage page, String type) {
        boolean ok = page.viewReportUIValidation(type);
        if (!ok) throw new RuntimeException("UI validation failed for " + type);
    }

    private void deleteReport(SpecialReportsPage page, String type) {
        page.deleteReportByName(type);
    }

}
