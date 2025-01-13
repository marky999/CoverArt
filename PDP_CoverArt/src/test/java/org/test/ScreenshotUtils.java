package org.test;

import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String dateName = new SimpleDateFormat("MMddhhmmss").format(new Date());
        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destination = "screenshots/" + screenshotName + dateName + ".png";
        File finalDestination = new File(destination);

        try {
            FileHandler.copy(source, finalDestination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;  // Return the path to the screenshot
    }


    public static File captureElementScreenShot(AppiumDriver driver, String xpath, String imageName) throws IOException {
        WebElement element = driver.findElement(By.xpath(xpath));
        File screenshotFile = element.getScreenshotAs(OutputType.FILE);
        File destinationFile = new File("./screenshots/"+imageName);

        // Copy the screenshot to the specified location
        FileUtils.copyFile(screenshotFile, destinationFile);
        return screenshotFile;
    }
}
