package org.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
public class YIRShareOpecCloseTest {
    private WebDriverWait wait;
    private Helper helper;
    public AppiumDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void methodSetup() throws Exception {

        XCUITestOptions options = new XCUITestOptions()
                .setPlatformVersion("18.0.1")
                .setDeviceName("iPhone_13 Pro")
                .setUdid("00008110-001C45D60CC0401E")
                .setApp("com.amazon.mp3.CloudPlayer") // or the app path
                .setAutomationName("XCUITest")
                .setShowXcodeLog(true);

        driver = new IOSDriver(new URL("http://127.0.0.1:4723"), options);
        Thread.sleep(1000);
        //----------------  DeepLink ---------------------//
        driver.get("https://music.amazon.com/recap/delivered/2024");
        //------------------------------------------------//
        //----------------  Library Ingress ---------------------//
      //  driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"AMMyMusicNavigationTabIconAccessibilityIdentifier\"]")).click();
      //  driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"ExpandedInfoView_PrimaryLabel\" and @label=\"2024 Delivered\"]")).click();
        //-------------------------------------------------------//
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        helper = new Helper(driver, wait);
    }

//    @Test(priority = 1)
//    public void testOpenCloseShareCardFromDecade() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 1: Share Card from Decades'");
//
//        helper.goNextFrame(2);
//        helper.openShareCard("testOpenCloseShareCardFromDecade");
//    }
//
//    @Test(priority = 2)
//    public void testOpenCloseShareCardFromGenre() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 2: Share Card from Genres'");
//
//        helper.goNextFrame(3);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        List<String> topGenresFromJSon = JasonExtraction.getTopGenres();
//        String firstGenre = topGenresFromJSon.get(0);
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(String.format("//*[@name='%s']", firstGenre))));
//        helper.openShareCard("testOpenCloseShareCardFromGenre");
//    }
//
//    @Test(priority = 3)
//    public void testOpenCloseShareCardLaterYouWere() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 3:Open Close Share Card  from Later You Were");
//
//        helper.goNextFrame(4);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//
//        helper.openShareCard("testOpenCloseShareCardLaterYouWere");
//    }
//
//    @Test(priority = 4)
//    public void testOpenCloseShareCardYourTopSong() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 4:Open Close Share Card  from Your Top Song");
//
//        helper.goNextFrame(5);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardYourTopSong");
//    }

//    @Test(priority = 5)
//    public void testOpenCloseShareCardBigFanTopSong() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 5:Open Close Share Card  from Big Fan Top Song Rare card");
//
//  //      helper.goNextFrame(6);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardBigFanTopSong");
//    }
//
//    @Test(priority = 6)
//    public void testOpenCloseShareCardTopPlayList() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 6:Open Close Share Card  from Top Play Lists");
//
//   //     helper.goNextFrame(8);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardTopPlayList");
//    }
//
//    @Test(priority = 7)
//    public void testOpenCloseShareCardYourTopArtist() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 7:Open Close Share Card  from Your Top Artist");
//
// //       helper.goNextFrame(9);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardYourTopArtist");
//    }
//
//    @Test(priority = 8)
//    public void testOpenCloseShareCardTopArtists() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 8:Open Close Share Card  from Top Artists");
//
//  //      helper.goNextFrame(11);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        if(Objects.equals(elem.getAttribute("label"), "share")){
//            helper.openShareCard("testOpenCloseShareCardTopArtists");
//        }else{
//            System.out.println("Share button not found and wait for next frame");
//            Thread.sleep(3000);
//            helper.openShareCard("testOpenCloseShareCardTopArtists");
//        }
//    }
//
//    @Test(priority = 9)
//    public void testOpenCloseShareCardEarlyListener() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 9:Open Close Share Card  from Early Listener");
//
// //       helper.goNextFrame(13);
//        Thread.sleep(1000);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardEarlyListener");
//    }
//
//    @Test(priority = 10)
//    public void testOpenCloseShareCardTopDiscovery() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 10:Open Close Share Card  from Top Discovery");
//
////        helper.goNextFrame(14);
//        Thread.sleep(1000);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardTopDiscovery");
//    }
//
//    @Test(priority = 11)
//    public void testOpenCloseShareCardAlexa() throws InterruptedException {
//        System.out.println("\n====================================================================================");
//        System.out.println("\nCase 11:Open Close Share Card  from Alexa");
//
//  //      helper.goNextFrame(15);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
//        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
//        helper.openShareCard("testOpenCloseShareCardAlexa");
//    }
//
    @Test(priority = 12)
    public void testOpenCloseShareCardFromTheRest() throws InterruptedException {
        System.out.println("\n====================================================================================");
        System.out.println("\nCase 12:Open Close Share Card  from the rest");
 //       helper.goNextFrame(16);
        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40)); // 10 seconds max wait
        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ActionButton_View")));
        ValidateEachFrame validateEachFrame =  new ValidateEachFrame(driver, helper, wait);
        validateEachFrame.openSharePageOtherwiseGotoNextPage();
    }

    @AfterMethod(alwaysRun = true)
    public void methodTearDown() {
        if (driver != null) {
            driver.quit(); // Close the browser
        }
    }

}
