package com.purebi.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtil {

    public static String takeScreenshot(WebDriver driver, String testName) {
        try {
            if (driver == null) return null;
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path screenshotsDir = Path.of(System.getProperty("user.dir"), "target", "surefire-reports", "screenshots");
            String fileName = testName + "_" + time + ".png";
            File destFile = screenshotsDir.resolve(fileName).toFile();
            if (destFile.getParentFile() != null) {
                destFile.getParentFile().mkdirs();
            }
            Files.copy(src.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());
            // Return a path relative to the report (used inside emailable-report.html)
            return "screenshots/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
