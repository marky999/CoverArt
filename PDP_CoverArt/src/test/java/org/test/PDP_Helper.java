package org.test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import static java.lang.Thread.sleep;
import static org.test.PDPCoverArtTest.test;
import static org.test.PDPCoverArtTest.testPlayList;
//import org.test.LocatorProvider;



public class PDP_Helper {
    private final AppiumDriver driver;
    private final WebDriverWait wait;
    private final Helper helper;
  //  private final LocatorProvider locatorProvider;
    //============================================================//
    static String  uploadButton = "(//XCUIElementTypeOther[@name=\"Box\"])[4]/XCUIElementTypeOther[3]";
    static String currentCoverArt = "(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]";
    static String default2x2Image = "(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]";
    final  String maxChars = "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890" +
            "123456789012345678901234567890123456789012345678901234567890";

    final String emoji = "\uD83D\uDC4C\uD83D\uDE4F\uD83D\uDEB5\u200D♂\uFE0F\uD83C\uDF97" + "@#$%^:;()&\".,?!";

//=======================================================================================================//
    public  By descriptionField = LocatorProvider.getElementLocator("descriptionField");
    public static By descriptionField2 = LocatorProvider.getElementLocator("descriptionField2");
    public  By libraryButtonLocator = LocatorProvider.getElementLocator("libraryButton");
    public  By saveButton = LocatorProvider.getElementLocator("saveButton");
    public By chooseButton = LocatorProvider.getElementLocator("chooseButton");

  //  By descriptionField = LocatorProvider.getElementLocator("descriptionField");
//===================================================================================================//
 //   public WebElement showMore;
    public PDP_Helper(AppiumDriver driver, WebDriverWait wait, Helper helper){
        this.driver = driver;
        this.wait = wait;
        this.helper = helper;
        //this.locatorProvider = locatorProvider;
    }

    public void clickUpload() throws InterruptedException {
        try{
            helper.getTypeOtherElement("PickImage_SelectedImage").click();
            //driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"PickImage_SelectedImage\"]")).click();
        }catch(Exception e)
        {
            sleep(1000);
           // driver.findElement(By.xpath(uploadButton)).click();
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"Upload\"]")).click();
        }
        sleep(1000);
    }

    public void addPlaylist(String playlistName){
        helper.getStaticTextElement("New Playlist").click();
        helper.getStaticTextElement("Curate a playlist").click();
        helper.getTextFieldElement("AMPlaylistListViewControllerTextFieldIdentifier").sendKeys(playlistName);
        //driver.findElement(By.xpath("//XCUIElementTypeTextField[@name=\"AMPlaylistListViewControllerTextFieldIdentifier\"]")).sendKeys(playlistName);
        helper.getStaticTextElement("Save").click();
    }

    public void addAIPlaylist(String playlistName) throws InterruptedException {
        helper.getStaticTextElement("New Playlist").click();
        helper.getStaticTextElement("Create with AI").click();
        sleep(1000);
        driver.findElement(By.xpath("//XCUIElementTypeSearchField[@name=\"Maestro_Box_Search\"]")).sendKeys(playlistName +"\n");

// Wait until the element is clickable
        WebElement savePlaylistButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//XCUIElementTypeButton[@name=\"Button,Maestro_Button_SavePlaylist\"]"))
        );
        savePlaylistButton.click();
        sleep(5000);

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
        switch (str){
            case "BauhausTextButton,TopAppBar_Done":
                 driver.findElement(saveButton).click();
                 break;
            case "Choose":
                driver.findElement(chooseButton).click();
                break;
            case "b":
                driver.findElement(saveButton).click();
                break;
            default:
                System.out.println("error");
        }


//        try {
//            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"" + str + "\"]")).click();
//        }catch(Exception e){
//            try {
//                driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"" + str  + "\"]")).click();
//            }catch(Exception ex){
//                test.info("Failed to locate and click the button with name: " + str);
//                System.out.println("Executed #3");
//                throw ex; // Re-throw exception if necessary
//            }
//        }finally{
//            test.info("Click " + str);
//        }

        sleep(1000);
    }

    public void clickLibraryFromActionSheet() throws InterruptedException {
        if(helper.isElementExist(By.xpath("//XCUIElementTypeButton[@name=\"Allow Full Access\"]"))){
            driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"Allow Full Access\"]")).click();
        }
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
        if(PDPCoverArtTest.Android){
            List<WebElement> elems = driver.findElements(By.xpath("//android.widget.TextView"));
          //  System.out.println(elems.get(1).getText());
          //  System.out.println(str);
            return elems.get(1).getText().equals(str);
        }else {
            try {
                return driver.findElement(By.xpath("//XCUIElementTypeStaticText[contains(@label, \"" + str + "\")]")).isDisplayed();
            } catch (Exception e) {
                // return driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"ExpandedInfoView_PrimaryLabel\" and @label=\"Empty desc\"]")).isDisplayed();
                return false;
            }
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
        test.info("click Save");
        sleep(1000);
        if(PDPCoverArtTest.Android){sleep(10000);}/////////// PERFORMANCE BUG //////

    }

    public void clickCancel() throws InterruptedException {
        driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"BauhausTextButton,TopAppBar_Cancel\"]")).click();
        sleep(2000);//DO NOT REDUCE
        test.info("click Cancel");
    }

    public void clearDescriptionTextField(String description) throws InterruptedException {
        String currentDescription = "";
        if (PDPCoverArtTest.Android){
            currentDescription = getLabelFromDescriptionField();
            List<WebElement> elements = driver.findElements(By.xpath("//android.view.View[@content-desc]"));
            String str = elements.get(1).getAttribute("content-desc");
            assert str != null;
            test.info(str.split(",")[0]);
            elements.get(1).click();
            if(currentDescription.contains("Add a description")){
                System.out.println("Already desc field is blank");
            }else{
                //elements.get(1).clear();
                driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"TextArea\"]")).clear();
            }
        }
        else{
            currentDescription = getLabelFromDescriptionField();
            WebElement textFieldsElement;
            try {
                textFieldsElement = driver.findElements(descriptionField).get(1);//By.xpath("//XCUIElementTypeStaticText[@name=\"TextArea_text\"]")).get(1);
            }catch(Exception e){
                System.out.println("CAME TO CATCH clearDescriptionTextField");
                textFieldsElement = driver.findElement(By.id("AMMPageHeaderSecondaryTextAccessibilityIdentifier"));
            }
            textFieldsElement.click();
            if(currentDescription.contains("Add a description")){
                //Do nothing
                System.out.println("Already desc field is blank");
            }else{
                driver.findElement(descriptionField2).clear();
                test.info("Description text now cleared");
            }

        }
    }

    public String getLabelFromDescriptionField() throws InterruptedException {
        if(PDPCoverArtTest.Android){
            sleep(1000);
            List<WebElement> elements = driver.findElements(By.xpath("//android.view.View[@content-desc]"));
            String str = elements.get(1).getAttribute("content-desc");
            assert str != null;
            test.info(str.split(",")[0]);
           // elements.get(1).click();
            return str.split(",")[0];
        }
        else{
            try{
                List<WebElement> textFieldsElements = driver.findElements(descriptionField);
               // System.out.println(textFieldsElements.size());
              //  dump();
                // String label = textFieldsElements.get(1).getDomAttribute("label");
                return textFieldsElements.get(1).getDomAttribute("label");
            }catch(Exception e){
                try{
                    WebElement label = driver.findElement(descriptionField2);
                    return label.getText();
                }catch(Exception e1){
                    WebElement label = driver.findElement(By.id("AMMPageHeaderSecondaryTextAccessibilityIdentifier"));
                    return label.getText();
                }
            }
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


    public void clearPlaylistNameField(){
        List<WebElement> textFields = driver.findElements(descriptionField);
        String playlistName = textFields.get(0).getText();
        test.info("Current testPlayList: " +  playlistName);
        textFields.get(0).click();
        driver.findElement(descriptionField2).clear();
    }

    public void openPlaylist(String playlist) throws InterruptedException {
        clickLibrary();
        clickPlayListButton();
        clickTestPlayList(playlist);
        test.info("Open PDP");
        sleep(2000);
    }

    public void goToPDP() throws InterruptedException {
        clickLibrary();
        clickPlayListButton();
        clickTestPlayList();
        test.info("Open PDP");
        sleep(4000);
    }

    public void clickTestPlayList() throws InterruptedException {
        //open default playlist, Test playlist
        List<WebElement> elements = getAllPlayLists();
        test.info(clickTestPlayListFromPlayListsPage(elements, testPlayList) ? "Open PDP" : "Test Playlist Not Found");
        sleep(2000);
    }

    public boolean clickTestPlayList(String playlist) throws InterruptedException {
        //open playlist that was provided in param
        List<WebElement> elements = getAllPlayLists();
        boolean isTapped = clickTestPlayListFromPlayListsPage(elements, playlist);
        test.info(isTapped ? "Open PDP" : "Test Playlist Not Found");

        return isTapped;
    }


    public void deletePlaylist(String playlistName) throws InterruptedException {
        List<WebElement> elements = getAllPlayLists();
        driver.findElement(By.xpath("(//XCUIElementTypeButton[@name=\"AMToolbarButtonItemType_moreOptions\"])[1]")).click();
        helper.getStaticTextElementWithLabel("Delete Playlist").click();
        helper.getStaticTextElement("Delete").click();
    }

    public void clickBack() throws InterruptedException {
        try {
            driver.findElement(By.xpath("//XCUIElementTypeButton[contains(@name, 'Back')]")).click();
         //   driver.findElement(By.xpath("//XCUIElementTypeButton[contains(@name, 'Back')]")).getLocation();
        } catch (Exception e) {
            driver.findElement(By.xpath("//XCUIElementTypeOther[contains(@name, 'Back')]")).click();
       //     driver.findElement(By.xpath("//XCUIElementTypeOther[contains(@name, 'Back')]")).getLocation();
        }
        sleep(1000);
    }

    public void inputDescription(String testString) throws InterruptedException {
        driver.findElement(descriptionField2).sendKeys(testString);
        test.info("input " + testString);
        sleep(1000);
    }

    public void clickLibrary() throws InterruptedException {
        driver.findElement(libraryButtonLocator).click();
        sleep(5000);
        test.info("click Library");
    }

    public void clickPlayListButton() throws InterruptedException {
        By playListPillocator = LocatorProvider.getElementLocator("playListPill");
        if(!driver.findElements(playListPillocator).isEmpty()){
            driver.findElement(playListPillocator).click();
            test.info("click PlayList button ");
        }
        sleep(1000);
    }

    public void clickEditButton() throws InterruptedException {
        By editButton = LocatorProvider.getElementLocator("editButton");
        sleep(1000);//DO NOT REDUCE
        driver.findElement(editButton).click();//Edit button
        sleep(2000);//DO NOT REDUCE
        test.info("Click Edit button");
    }

    public List<WebElement> getAllPlayLists(){
        By allPlayLists = LocatorProvider.getElementLocator("allPlayLists");
        return driver.findElements(allPlayLists);
    }

    public boolean openPlaylist(List<WebElement> elements, String platform, String testPlayList){
        String playlistTitle = "";
        boolean isFound = false;

        for (WebElement targetCell : elements) {
            //Android
            if(platform.contains("android")){
                playlistTitle = targetCell.getText();
                if (playlistTitle.contains(testPlayList)) {
                    isFound = true;
                    targetCell.click();
                    test.info("click testPlayList ");
                    break;
                }
            }else{
                //iOS
                WebElement nestedElement = targetCell.findElement(By.xpath("./XCUIElementTypeOther/XCUIElementTypeOther"));
                playlistTitle = nestedElement.getText();
                if (playlistTitle.contains(testPlayList)) {
                    isFound = true;
                    nestedElement.click();
                    test.info("click testPlayList ");
                    break;
                }
            }
        }
        return isFound;
    }

    public boolean clickTestPlayListFromPlayListsPage(List<WebElement> elements, String  testPlayList) {
    //    boolean isFound = false;
     //   String playlistTitle = "";
        System.out.println(System.getProperty("platformName"));
        String platform = System.getProperty("platformName").trim().toLowerCase();
       // openPlaylist(elements, platform, testPlayList);

//        for (WebElement targetCell : elements) {
//            //Android
//            if(platform.contains("android")){
//                playlistTitle = targetCell.getText();
//                if (playlistTitle.contains(testPlayList)) {
//                    isFound = true;
//                    targetCell.click();
//                    test.info("click testPlayList ");
//                    break;
//                }
//            }else{
//                //iOS
//                WebElement nestedElement = targetCell.findElement(By.xpath("./XCUIElementTypeOther/XCUIElementTypeOther"));
//                playlistTitle = nestedElement.getText();
//                if (playlistTitle.contains(testPlayList)) {
//                    isFound = true;
//                    nestedElement.click();
//                    test.info("click testPlayList ");
//                    break;
//                }
//            }
//        }
        return openPlaylist(elements, platform, testPlayList);
    }

    public void dump(){
        String pageSource = driver.getPageSource();
        System.out.println("Android UI Hierarchy: ");
        System.out.println(pageSource);
    }
    public WebElement getTestPlayListFromPlayListsPage(List<WebElement> elements, String playlistName ) {
        boolean isFound = false;
        WebElement nestedElement = null;
        for (WebElement targetCell : elements) {
            nestedElement = targetCell.findElement(By.xpath("./XCUIElementTypeOther/XCUIElementTypeOther"));
            String playlistTitle = nestedElement.getText();
            if (playlistTitle.contains(playlistName)) {
                isFound = true;
                test.info("testPlayList found");
                break;
            }
        }

        return isFound ? nestedElement : null;
    }
}
