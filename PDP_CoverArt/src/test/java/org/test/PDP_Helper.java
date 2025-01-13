package org.test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.test.PDPCoverArtTest.test;
import static org.test.PDPCoverArtTest.testPlayList;

public class PDP_Helper {
    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final Helper helper;
    //============================================================//
    static String  uploadButton = "(//XCUIElementTypeOther[@name=\"Box\"])[4]/XCUIElementTypeOther[3]";
    static String currentCoverArt = "(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]";
    static String default2x2Image = "(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]";
    final  String maxChars = "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890";

    final String emoji = "\uD83D\uDC4C\uD83D\uDE4F\uD83D\uDEB5\u200D♂\uFE0F\uD83C\uDF97" + "@#$%^";
    //============================================================//
 //   public WebElement showMore;
    public PDP_Helper(AppiumDriver driver, WebDriverWait wait, Helper helper){
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
    }

    public void clickUpload() throws InterruptedException {

        try{
            helper.getTypeOtherElement("PickImage_SelectedImage").click();
            //driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"PickImage_SelectedImage\"]")).click();
        }catch(Exception e)
        {
            driver.findElement(By.xpath(uploadButton)).click();
        }




        sleep(1000);
    }

    public void clickRandomImageFromLibrary() throws InterruptedException {

        List<WebElement> elems =  driver.findElements(By.xpath("//XCUIElementTypeOther[@name=\"PXGGridLayout-Group\"]/*"));
        System.out.println(elems.size());
        Random random = new Random();
        int digit = random.nextInt(elems.size()); // Generates a random number between 0 and 9

        elems.get(digit).click();
        test.info(digit + "th image clicked");
        sleep(1000);
    }

    public void clickChoose() throws InterruptedException {
        clickButton("Choose");
        test.info("Click Choose");
        sleep(1000);
    }

    public void clickButton(String str) throws InterruptedException {
        try {
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"" + str + "\"]")).click();
        }catch(Exception e){
            try {
                driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"" +str  + "\"]")).click();
            }catch(Exception ex){
                test.info("Failed to locate and click the button with name: " + str);
                System.out.println("Executed #3");
                throw ex; // Re-throw exception if necessary
            }
        }finally{
            test.info("Click " + str);
        }

        sleep(1000);
    }

    public void clickLibraryFromActionSheet() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@label, 'Launch Library')]")).click();
        sleep(1000);
        test.info("Library menu clicked from Action sheet");
    }

    public void clickCameraFromActionSheet() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@label, 'Launch Camera')]")).click();
        sleep(3000);
        test.info("click Camera");
    }

    public boolean isTextDisplayed(String str)
    {
        try{
            return driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@label, \"" + str + "\")]")).isDisplayed();
        } catch (Exception e) {
           // return driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"ExpandedInfoView_PrimaryLabel\" and @label=\"Empty desc\"]")).isDisplayed();
            return false;
        }
    }

    public WebElement showMore(){
        return driver.findElement(By.xpath("//XCUIElementTypeLink[@name=\"TextBlock_InlineLink\"]"));
    }

    public WebElement showLess(){
        return driver.findElement(By.xpath("//XCUIElementTypeLink[@name=\"Show less\"]"));
    }

    public void clickShowMore(){
        showMore().click();
        test.info("click Show more");
    }

    public void clickShowLess(){
        showLess().click();
        test.info("click Show less");
    }

    public void clickDone() throws InterruptedException {
        clickButton("BauhausTextButton,TopAppBar_Done");
        sleep(3000);//DO NOT REDUCE
        test.info("click Done");
        sleep(1000);
    }

    public void clickCancel() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"BauhausTextButton,TopAppBar_Cancel\"]")).click();
        sleep(1000);//DO NOT REDUCE
        test.info("click Cancel");
    }

    public void clearDescriptionTextField(String description) throws InterruptedException {
        String currentDescription = getLabelFromDescriptionField();
        WebElement textFieldsElement = driver.findElements(By.xpath("//XCUIElementTypeStaticText[@name=\"TextArea_text\"]")).get(1);
        textFieldsElement.click();


        if(currentDescription.contains("Add a description")){
            //Do nothing
            System.out.println("Already desc field is blank");
        }
        else
        {
            sleep(500);
            driver.findElement(By.xpath("//XCUIElementTypeTextView[@name=\"TextArea\"]")).clear();
            System.out.println("Clear the field");
            test.info("Description text now cleared");
        }
    }

    public String getLabelFromDescriptionField(){
        try{
            List<WebElement> textFieldsElements = driver.findElements(By.xpath("//XCUIElementTypeStaticText[@name=\"TextArea_text\"]"));
           // String label = textFieldsElements.get(1).getDomAttribute("label");
            return textFieldsElements.get(1).getDomAttribute("label");
        }catch(Exception e){
            WebElement label = driver.findElement(By.xpath("//XCUIElementTypeTextView[@name=\"TextArea\"]"));
            return label.getText();
        }
    }

    public String getLabelFromTitleField(){
        List<WebElement> textFieldsElements = driver.findElements(By.xpath("//XCUIElementTypeOther[@name='TextArea_container' and @label]"));
        return textFieldsElements.get(0).getDomAttribute("label");
    }

    public WebElement getDescriptionField(){
        List<WebElement> textFieldsElements = driver.findElements(By.xpath("//XCUIElementTypeOther[@name='TextArea_container' and @label]"));
        return textFieldsElements.get(1);
    }


    public void assertForTest(ExtentTest test, boolean isOK, String testCaseName){
        try {
            Assert.assertTrue(isOK);
            System.out.println(ScreenshotUtils.captureScreenshot(driver, testCaseName));
        }catch(AssertionError e){
            test.fail("Test failed. Assertion error ");
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, testCaseName);

            System.out.println("Screenshot path: " + screenshotPath);

            test.fail(testCaseName,
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

        }
    }

    public static void deleteScreenshots(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public void goToPDP() throws InterruptedException {
        clickLibrary();
        clickPlayListButton();
        clickTestPlayList();
        test.info("Open PDP");
        sleep(1500);
    }

    public void clickTestPlayList() throws InterruptedException {
        List<WebElement> elements = getAllPlayLists();
        System.out.println(clickTestPlayListFromPlayListsPage(elements) ? "Open PDP" : "Test Playlist Not Found");
        sleep(2000);
    }

    public void clickBack() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeButton[contains(@name, 'Back')]")).click();//'TopAppBar_BackButton'
        test.info("Click Back");
        sleep(1000);
    }

    public void inputDescription(String testString){
        driver.findElement(By.xpath("//XCUIElementTypeTextView[@name=\"TextArea\"]")).sendKeys(testString);
        test.info("input " + testString);
    }

    public void clickLibrary() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"AMMyMusicNavigationTabIconAccessibilityIdentifier\"]")).click();
        sleep(3000);
        test.info("click Library");
    }

    public void clickPlayListButton(){
        driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"PillNavigatorTextButton\" and @label=\"Playlists\"]")).click();
        test.info("click PlayList button ");
    }

    public void clickEditButton() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"IconButton,Toolbar_EditButton\"]")).click();//Edit button
        sleep(2000);//DO NOT REDUCE
        test.info("Click Edit button");
    }

    public List<WebElement> getAllPlayLists(){
        return driver.findElements(By.xpath("//XCUIElementTypeCollectionView//XCUIElementTypeCell"));
    }

    public boolean clickTestPlayListFromPlayListsPage(List<WebElement> elements ) {
        boolean isFound = false;
        for (WebElement targetCell : elements) {
            WebElement nestedElement = targetCell.findElement(By.xpath("./XCUIElementTypeOther/XCUIElementTypeOther"));
            String playlistTitle = nestedElement.getText();
            if (playlistTitle.contains(testPlayList)) {
                isFound = true;
                nestedElement.click();
                test.info("click testPlayList ");
                break;
            }
        }
        return isFound;
    }
}
