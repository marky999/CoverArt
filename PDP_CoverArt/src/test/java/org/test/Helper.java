package org.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import static org.test.PDPCoverArtTest.test;
import static org.testng.Assert.assertNotNull;

public class Helper {
    private final AppiumDriver driver;
    private WebDriverWait wait;
    public final String shareSheet_cancelButton =  "//*[@name=\"IconButton,ShareSheet_CancelButton\"]" ;
    public final String cancelButton = "//XCUIElementTypeButton[@name=\"\u200ECancel\"]";
    public final String weFound_xpath = "//XCUIElementTypeStaticText[@name=\"We spent \"]";


    public Helper(AppiumDriver driver) throws MalformedURLException {
        this.driver = driver;

    }

    public void blindTap(int x, int y){
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y)); // Coordinates (x=500, y=500)
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(List.of(tap));
    }
    public void pinchOut(WebElement element) {
        int centerX = element.getRect().getX() + element.getSize().getWidth() / 2;
        int centerY = element.getRect().getY() + element.getSize().getHeight() / 2;

        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence finger1Action = new Sequence(finger1, 0)
                .addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY - 50))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), centerX, centerY - 350))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence finger2Action = new Sequence(finger2, 0)
                .addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY + 50))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), centerX, centerY + 350))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(finger1Action, finger2Action));
    }
    public void scrollUp(){
        Map<String, Object> params = new HashMap<>();
        params.put("fromX", 200); // Starting X coordinate (center horizontally)
        params.put("fromY", 500); // Starting Y coordinate
        params.put("toX", 200);   // Ending X coordinate (same as start)
        params.put("toY", 488);   // Slightly above the starting Y
        params.put("duration", 0.1); // Swipe duration in seconds
        driver.executeScript("mobile: dragFromToForDuration", params);
        test.info("Scroll up");
    }

    public void scrollRightToLeft(WebElement element) {
        // Get the location and size of the element
        int startX = element.getLocation().getX() + element.getSize().getWidth() - 1; // Right edge
        int endX = element.getLocation().getX() + 1; // Left edge
        int y = element.getLocation().getY() + (element.getSize().getHeight() / 2); // Center vertically

        // Define parameters for horizontal scroll
        Map<String, Object> params = new HashMap<>();
        params.put("fromX", startX); // Starting X coordinate (right edge of the element)
        params.put("fromY", y);      // Starting Y coordinate (center vertically)
        params.put("toX", endX);     // Ending X coordinate (left edge of the element)
        params.put("toY", y);        // Y coordinate remains the same (center vertically)
        params.put("duration", 0.1); // Swipe duration in seconds

        // Execute the scroll action (right to left)
        driver.executeScript("mobile: dragFromToForDuration", params);
        test.info("Scroll right to left");
    }

    public void scrollRightToLeft(Map<String, Object> params) {
        // Execute the scroll action (right to left)
        driver.executeScript("mobile: dragFromToForDuration", params);
        test.info("Scroll right to left");
    }

    public void scrollLeftToRight(WebElement element) {
        // Get the location and size of the element
        int startX = element.getLocation().getX() + 1; // Left edge
        int endX = element.getLocation().getX() + element.getSize().getWidth() - 1; // Right edge

        int y = element.getLocation().getY() + (element.getSize().getHeight() / 2); // Center vertically

        // Define parameters for horizontal scroll
        Map<String, Object> params = new HashMap<>();
        params.put("fromX", startX); // Starting X coordinate (right edge of the element)
        params.put("fromY", y);      // Starting Y coordinate (center vertically)
        params.put("toX", endX);     // Ending X coordinate (left edge of the element)
        params.put("toY", y);        // Y coordinate remains the same (center vertically)
        params.put("duration", 0.1); // Swipe duration in seconds

        // Execute the scroll action (right to left)
        driver.executeScript("mobile: dragFromToForDuration", params);
        test.info("Scroll right to left");
    }

    public boolean isImageSame( File img1, File img2) throws IOException {
        BufferedImage image1 = ImageIO.read(img1);
        BufferedImage image2 = ImageIO.read(img2);
        return isImageSame(image1, image2);
     //   ImageDiffer imgDiff = new ImageDiffer();  // Use a library like AShot
     //   ImageDiff diff = imgDiff.makeDiff(image1, image2);

     //   return !diff.hasDiff();
    }

    public boolean isImageSame( BufferedImage img1, BufferedImage img2) throws IOException {
        ImageDiffer imgDiff = new ImageDiffer();  // Use a library like AShot
        ImageDiff diff = imgDiff.makeDiff(img1, img2);

        return !diff.hasDiff();
    }

    public boolean isElementFound(String xpath, int waitDuration ) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitDuration));
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            assertNotNull(element);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isShareButtonExist() {
        try {
            WebElement actionButton = driver.findElement(By.id("ActionButton_View"));
            String buttonLabel = actionButton.getText();
            if ("share".equals(buttonLabel)) {
                return true;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Share button not found");
            return false;
        }
        return false;
    }
    public int getRandomNumber(){
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        return rand.nextInt();
    }

    public String getRandomNumber(int num){
        StringBuilder randomNumberString = new StringBuilder(num);
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            int digit = random.nextInt(10); // Generates a random number between 0 and 9
            randomNumberString.append(digit);
        }
        return String.valueOf(randomNumberString);
    }

    public WebElement getButtonElement(String str){
        return driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"" + str + "\"]"));
    }

    public WebElement getStaticTextElementWithLabel(String str) {
       return  driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@label, '" + str + "')]"));
    }
    public WebElement getTextFieldElement(String str){
        return driver.findElement(By.xpath("//XCUIElementTypeTextField[@name=\"" + str + "\"]"));
    }

    public WebElement getStaticTextElement(String str){
        if(PDPCoverArtTest.Android){
            try{
                driver.findElement(By.xpath("//android.widget.TextView[@text= \"" + str + "\"]"));
                return driver.findElement(By.xpath("//android.widget.TextView[@text= \"" + str + "\"]"));
            }catch(Exception e){
                try{
                    driver.findElement(By.xpath("//android.widget.Button[@text= \"" + str + "\"]"));
                    return driver.findElement(By.xpath("//android.widget.Button[@text= \"" + str + "\"]"));

                }catch (Exception e1){
                    return null;
                }
            }
        }else
        {
            return driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@name,\"" + str + "\")]"));
        }
    }

    public WebElement getTypeOtherElement(String str){
        return driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"" + str + "\"]"));
    }

    public WebElement getTypeSearchFieldElement(String str){
        return driver.findElement(By.xpath("//XCUIElementTypeSearchField[contains[@name,\"" + str + "\"]"));
    }


    public boolean isElementExist(By xpath) {
        return !driver.findElements(xpath).isEmpty();
    }

    public String formatWithCommas(String numberString) {
        try {
            // Parse the string to a number
            Number number = NumberFormat.getInstance().parse(numberString);

            // Format the number with commas
            return NumberFormat.getNumberInstance().format(number);
        } catch (ParseException e) {
            e.printStackTrace();
            return numberString; // Return original if parsing fails
        }
    }

}
