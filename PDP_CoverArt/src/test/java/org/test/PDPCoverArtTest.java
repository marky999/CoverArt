package org.test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.*;
import org.testng.annotations.*;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.test.PDP_Helper.*;

@Listeners(org.test.ExtentTestNGReportListener.class)
public class PDPCoverArtTest extends BaseClass{
    Helper helper;
    PDP_Helper pdpHelper;
    private static boolean isFirstTest = true;
    private final boolean shouldSkip = false;
    public PDPCoverArtTest()  {

    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method,  ITestContext context, ITestResult result) throws Exception {
        context.setAttribute("WebDriver", driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        pdpHelper = new PDP_Helper();
        helper = new Helper(driver);
        sleep(4000);//DO NOT MOVE or REMOVE
        testResult = result;
        if (isFirstTest) {
            System.out.println("This is the first test in the session.");
            isFirstTest = false;
            cleanupMarketingPages();
        }
    }

    @Test(priority = 1, groups = {"descriptionCases"})
    public void testAddEditDescription() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        String testString = "New Description " + helper.getRandomNumber();
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();
        if(Android){sleep(10000);}/////////// PERFORMANCE BUG //////
        if(pdpHelper.isTextDisplayed(testString)){
            test.info("Verified \"" + testString +"\"");
        }else{
            test.info("Failed to verify \"" + testString +"\"");
            Assert.fail();
        }
    }

    @Test(priority = 2, groups = {"descriptionCases"})
    public void testCanAddEmojiAndSpecialChars() throws InterruptedException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        String testString = pdpHelper.emoji + helper.getRandomNumber();
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();

        boolean isFound =  pdpHelper.isTextDisplayed(pdpHelper.emoji);
       Assert.assertTrue(isFound, pdpHelper.emoji + " was not saved.  Bug YFC-8944");
        test.info("Verified \"" + testString +"\"");
    }

    //////////////////////////////////////////////////////////////////////////////////////
    @Test(priority = 3, groups = {"descriptionCases"})
    public void testSavedDescriptionPersistsAfterRelaunch1() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        tempTestString = testPlayList + helper.getRandomNumber();
        pdpHelper.inputDescription(tempTestString);
        pdpHelper.clickDone();
        test.info("Will verify whether the saved description is persisted after relaunch from next test");
        ((IOSDriver) driver).terminateApp("com.amazon.mp3.CloudPlayer");
    }

    @Test(priority = 4, groups = {"descriptionCases"}, dependsOnMethods = {"testSavedDescriptionPersistsAfterRelaunch1"})
    public void testSavedDescriptionPersistsAfterRelaunch2() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        String description = pdpHelper.getLabelFromDescriptionField();
        Assert.assertEquals(description, tempTestString);
        test.info("Verified the saved description is persisted after relaunch" );
        test.info("Description : " + description);
        tempTestString = ""; //clean buffer
    }
    //////////////////////////////////////////////////////////////////////////////////////

    @Test(priority = 5, groups = {"descriptionCases"})
    public void testDeleteDescriptionsAndVerifyPrePopulatedString() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();

        String description ;
        if(Android){
            description = driver.findElement(PDP_Helper.descriptionField2).getText();
        }else{
            description = pdpHelper.getLabelFromDescriptionField();
        }
        test.info(description);
        List<String> expectedDescriptions = Arrays.asList("Add a description", "説明を追加");
        Assert.assertTrue(expectedDescriptions.contains(description), "Unexpected description: " + description);

        test.info("Verified when description field is cleared then \" " + description +  "\" auto-populated");
        pdpHelper.clickCancel();
    }

    @Test(priority = 6, groups = {"descriptionCases"})
    public void testMaximumCharsCount() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        helper.scrollUp();
        Assert.assertNotNull(helper.getStaticTextElement("0 / 300"));
        test.info("Verified 0/300 for blank description");
        pdpHelper.inputDescription(pdpHelper.maxChars);
        Assert.assertNotNull(helper.getStaticTextElement("300 / 300"));
        test.info("Verified 300/300 when 300 chars filled in description");
        test.info("add one more char to verify cannot add more");
        new Actions(driver).sendKeys("1").perform();

        Assert.assertNotNull(helper.getStaticTextElement("300 / 300"));
        test.info("Verified 300/300 after add one more char");
        System.out.println(PDP_Helper.characterLimitReached);
        Assert.assertTrue(helper.isElementFound(PDP_Helper.characterLimitReached, 2));
        test.info("Verified " + PDP_Helper.characterLimitReached );
        new Actions(driver).sendKeys("\n").perform(); //dismiss the keyboard
    }

    @Test(priority = 7, groups = {"descriptionCases"})
    public void testEditPlayListName() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        String originalPlaylistToTest = ConfigReader.getProperty("testPlaylist2");
        System.out.println(originalPlaylistToTest);
        String newPlaylistName = "newPlaylistName";
        pdpHelper.openPlaylist(originalPlaylistToTest);
        pdpHelper.clickEditButton();
        pdpHelper.clearPlaylistNameField();
        driver.findElement(PDP_Helper.descriptionField2).sendKeys(newPlaylistName);
        pdpHelper.clickDone();
        String imgPath = ScreenshotUtils.CaptureScreenshot(driver, "New Playlist Name_7");
        test.info("Playlist name edited ",
                MediaEntityBuilder.createScreenCaptureFromPath(imgPath).build());

        pdpHelper.clickBack();
        if(pdpHelper.clickTestPlayList(originalPlaylistToTest)) {//"ZEmpty description"
            Assert.fail();
        }
        test.info("Original test playlist," + originalPlaylistToTest + ", was updated");
        pdpHelper.clickTestPlayList(newPlaylistName);
        test.info("Open " + newPlaylistName);
        test.info("Change the playlist name back to original");
        pdpHelper.clickEditButton();
        pdpHelper.clearPlaylistNameField();
        driver.findElement(PDP_Helper.descriptionField2).sendKeys(originalPlaylistToTest);
        pdpHelper.clickDone();
        pdpHelper.clickBack();

        Assert.assertTrue(pdpHelper.clickTestPlayList(originalPlaylistToTest));
    }


    @Test(priority = 7, groups = {"descriptionCases"})
    public void testPlayListNameCannotBeBlank() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        String originalPlaylistToTest = ConfigReader.getProperty("testPlaylist2");
        System.out.println(originalPlaylistToTest);
        String newPlaylistName = "newPlaylistName";
        pdpHelper.openPlaylist(originalPlaylistToTest);
        pdpHelper.clickEditButton();
        pdpHelper.clearPlaylistNameField();
        Assert.assertFalse(driver.findElement(LocatorProvider.getElementLocator("saveButton")).isEnabled(),
                "Save button is enabled state");
        test.info("Save button is disabled state and does not allow to Save");

    }

    @Test(priority = 8, groups = {"descriptionCases"})
    public void testShowMoreAndShowLessButtons() throws InterruptedException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        String testString =  helper.getRandomNumber(90);
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();
        Assert.assertTrue(pdpHelper.showMore().isDisplayed());
        test.info("Show more button displayed");
        pdpHelper.clickShowMore();
        //verify testString
        boolean isFound =  pdpHelper.isTextDisplayed(testString);
        Assert.assertTrue(isFound);
        test.info("Verified testString: \"" + testString +"\"");
        test.info("Show less button displayed");
        pdpHelper.clickShowLess();
        isFound =  pdpHelper.isTextDisplayed(testString);
        Assert.assertFalse(isFound);
        test.info("Verified \"" + testString +" not visible\"");
    }

    @Test(priority = 9, groups = {"descriptionCases"})
    public void testExpandedStateNotPersistWhenUserLeavesThePage() throws InterruptedException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        String testString =  helper.getRandomNumber(200);
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();
        Assert.assertTrue(pdpHelper.showMore().isDisplayed());
        test.info("Show more button displayed");
        pdpHelper.clickShowMore();
        boolean isFound =  pdpHelper.isTextDisplayed(testString);
        Assert.assertTrue(isFound);
        pdpHelper.clickBack();
        pdpHelper.clickTestPlayList();
        Assert.assertTrue(pdpHelper.showMore().isDisplayed());
        isFound =  pdpHelper.isTextDisplayed(testString);
        Assert.assertFalse(isFound);
        test.info("Expanded State Not Persist. Expanded string not visible. Show more button is visible");
    }

    @Test(priority = 10, groups = {"descriptionCases"})
    public void testWhenSaveCollapsedStateBecomesDefault() throws InterruptedException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep();
        String testString = helper.getRandomNumber(200);
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();
        Assert.assertFalse(pdpHelper.isTextDisplayed(testString));
        Assert.assertTrue(pdpHelper.isTextDisplayed("..."));
        Assert.assertTrue(pdpHelper.showMore().isDisplayed());
        test.info("... and Show more displayed");
    }

    @Test(priority = 11, groups = {"descriptionCases"})
    public void testCreateAIGeneratedPlaylist() throws InterruptedException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        String playlistName =  "KatsEye";
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.addAIPlaylist(playlistName);
        test.info("playlist Name : " + playlistName + " was created");
        pdpHelper.clickEditButton();

        List<WebElement> staticElems = driver.findElements(By.xpath(staticTextsXpath));
        test.info(staticElems.get(0).getText());
        String newlyCreatedPlaylistName = staticElems.get(0).getText();
        String description = staticElems.get(1).getText();
        test.info("Description : " + description);
        Assert.assertFalse(description.isEmpty());
        Assert.assertNotSame(description, "Add a description");
        pdpHelper.clickCancel();
        pdpHelper.clickBack();
        driver.findElement(LocatorProvider.getElementLocator("backButton2")).click();
        driver.findElement(LocatorProvider.getElementLocator("backButton2")).click();
        pdpHelper.deletePlaylist(newlyCreatedPlaylistName);
        test.info("Delete the playlist that created");
        List<WebElement> elements = pdpHelper.getAllPlayLists();
        Assert.assertNull(pdpHelper.getTestPlayListFromPlayListsPage(elements, playlistName));
    }

    @Test(priority = 12, groups = {"uploadCases"})
    public void testPlaylistImageAppearAtFirstTimeOpen() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        String playlistName =  "Daft Punk";

        pdpHelper.createCustomPlaylist(playlistName);//Create and comes to PDP state
        String playlistImageXpath = LocatorProvider.getStringLocator("emptyPlaylistImage");
        File emptyPlaylistImage = ScreenshotUtils.captureElementScreenShot(playlistImageXpath, "empty_playlist_image");
        test.info("Empty_playlist_image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/empty_playlist_image").build());
        pdpHelper.clickBack();
        test.info("playlist Name : " + playlistName + " was created");

        String screenshotPath = ScreenshotUtils.CaptureScreenshot(driver, "customCuratedPlaylist");
        test.info(playlistName,
                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        pdpHelper.clickTestPlayList(playlistName);

        File expectedPlaylistImage = ScreenshotUtils.captureElementScreenShot(playlistImageXpath, "expected_playlist_image");
        test.info("Expected_playlist_image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/expected_playlist_image").build());
        boolean areTheySameImages = helper.isImageSame(emptyPlaylistImage, expectedPlaylistImage);
        pdpHelper.clickBack();
        pdpHelper.deletePlaylist(playlistName);

        Assert.assertFalse(areTheySameImages, "Empty playlist appeared!! Bug # YFC-8771");
        test.info("Playlist image appeared at first time Open");

        List<WebElement> elements = pdpHelper.getAllPlayLists();
        Assert.assertNull(pdpHelper.getTestPlayListFromPlayListsPage(elements, playlistName), "Failed to delete after the test ended");
        test.info("Newly created playlist, " + playlistName + " , was deleted");
    }

    @Test(priority = 14, groups = {"uploadCases"})
    public void testUploadedImageReflectedInPlaylistsPage() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        test.info("open custom CoverArt Playlist => verify those two images are different)");
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();

        capturePresentScreenShot(test, "originalPlaylistsPage");

        File originalThumbImage = pdpHelper.captureThumbNailFromPlaylists("OriginalPlayListThumbInPlaylists.png");
        test.info("originalPlaylistThumb",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/OriginalPlayListThumbInPlaylists.png").build());
        pdpHelper.clickTestPlayList();
        uploadImageFromPhotoLibrary();

        pdpHelper.clickPlayListButton();
        File uploadedThumbImage = pdpHelper.captureThumbNailFromPlaylists("afterUploadedPlaylistThumb.png");
        test.info("afterUploadedPlaylistThumb",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/afterUploadedPlaylistThumb.png").build());
        Assert.assertFalse(helper.isImageSame(originalThumbImage, uploadedThumbImage));
        test.info("Image was reflected");
    }

    @Test(priority = 15, groups = {"uploadCases"})
    public void testThumbNailReflectedInShareSheetFromContextMenu() throws InterruptedException, IOException {
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        test.info("open custom CoverArt Playlist => verify those two images are different)");
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        String imgPath2 = ScreenshotUtils.CaptureScreenshot(driver, "CurrentScreenshot");
        test.info("Before action",
                MediaEntityBuilder.createScreenCaptureFromPath(imgPath2).build());
        File originalThumbImage = pdpHelper.captureThumbNailFromPlaylists("OriginalTestplayThumbNail.png");
        test.info("Playlist Thumb",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/OriginalTestplayThumbNail.png").build());
        //click more
        int n = appearanceInPlaylists(pdpHelper.getAllPlayLists(),  testPlayList) - 1;
        driver.findElement(By.xpath("(//XCUIElementTypeButton[@name=\"AMToolbarButtonItemType_moreOptions\"])[" + n + "]")).click();
        String imgPath3 = ScreenshotUtils.CaptureScreenshot(driver, "Click ...");
        test.info("Click ...",
                MediaEntityBuilder.createScreenCaptureFromPath(imgPath3).build());

        //click Share Button
        driver.findElement(By.xpath("//XCUIElementTypeCell[@name=\"AMMContextMenuShare\"]")).click();
        sleep(2000);
        String imgPath = ScreenshotUtils.CaptureScreenshot(driver, "Click Share");
        test.info("Click Share",
                MediaEntityBuilder.createScreenCaptureFromPath(imgPath).build());

        // thumb in ShareSheet
        String xpathOfShareThumbNail = "//XCUIElementTypeWindow[1]/XCUIElementTypeOther[3]/XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeOther[1]/XCUIElementTypeImage";
        File shareSheetThumbNail = ScreenshotUtils.captureElementScreenShot(xpathOfShareThumbNail, "shareSheetThumbNail.png");
        test.info("ShareSheet ThumbNail",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/shareSheetThumbNail.png").build());

        Assert.assertTrue(helper.isImageSame(originalThumbImage, shareSheetThumbNail) , "The images do not match: original vs. shared thumbnail. Bug YFC-8863");
        test.info("Images are reflected");
    }

    @Test(priority = 16, groups = {"uploadCases"})
    public void testUploadedImageReflectedInMyProfile() throws InterruptedException, IOException {
        //==============      BUG LOGGED     ==========//
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        pdpHelper.goToMyProfile();
        //Scroll and find test playList From Library Playlist Widget
        WebElement playlistMyLikes = driver.findElement(By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell[contains(@label,'My Likes')]"));
        int maxScrollAttempts = 10;

        int startX = playlistMyLikes.getLocation().getX() + playlistMyLikes.getSize().getWidth() - 1; // Right edge
        System.out.println("startX = " + startX);
        int endX = playlistMyLikes.getLocation().getX() + 2; // Left edge
        System.out.println("endX = " + endX);
        int y = playlistMyLikes.getLocation().getY() + (playlistMyLikes.getSize().getHeight() / 2); // Center vertically

        // Define parameters for horizontal scroll
        Map<String, Object> params = new HashMap<>();
        params.put("fromX", startX);
        params.put("fromY", y);
        params.put("toX", endX);
        params.put("toY", y);
        params.put("duration", 0.2); // Swipe duration in seconds

        int attempt = scrollToFindTextFromLibrary(params, testPlayList, maxScrollAttempts);
        if (attempt >= maxScrollAttempts) {
            test.info("Playlist not found after maximum scroll attempts.");
            Assert.fail();
        }

        //Capture the testPlayList image from the playList widget and save the screenshot
        String testPlayListXpathFromPlaylistWidget = "//XCUIElementTypeCell[contains(@label, 'Test playlist')]";
        File originalScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "OriginalPlayListImageInProfile.png");
        test.info("Original PlayList image from profile",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/OriginalPlayListImageInProfile.png").build());
        pdpHelper.clickBack();
        uploadImageFromPhotoLibrary();
        pdpHelper.goToMyProfile();

        File currentScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "currentPlayListImageInProfile.png");
        test.info("Current PlayList image in My Profile page",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/CurrentPlayListImageInProfile.png").build());

        Assert.assertFalse(helper.isImageSame(originalScreenshotFile, currentScreenshotFile), "Original and current images are the same. \nImage was not reflected in the My Profile page");

        test.info("Playlist Image was replaced");
    }

    @Test(priority = 17, groups = {"uploadCases"})
    public void testUploadImageFromPhotoLibrary() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        test.info("Capture before image => upload new image => verify those two images are different)");
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "Original Uploaded Image_15");

        WebElement element = driver.findElement(By.xpath(currentCoverArt));
        File originalScreenshotFile = element.getScreenshotAs(OutputType.FILE);

        uploadImageFromPhotoLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.clickTestPlayList();

        element = driver.findElement(By.xpath(currentCoverArt));

        File uploadedScreenshotFile = element.getScreenshotAs(OutputType.FILE);
        Assert.assertFalse(helper.isImageSame(originalScreenshotFile, uploadedScreenshotFile), "New image was NOT uploaded");
        test.info("New image was uploaded");
    }

    @Test(priority = 18, enabled = false,  groups = {"uploadCases"})
    public void testUploadImageFromCamera() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "currentUploadedImage");
        WebElement element = driver.findElement(By.xpath(currentCoverArt));
        BufferedImage originalScreenshot = captureElementBufferedImage(element);
        pdpHelper.clickEditButton();
        uploadImageFromCamera();
        pdpHelper.clickBack();
        pdpHelper.clickTestPlayList();
        element = driver.findElement(By.xpath(currentCoverArt));
        BufferedImage uploadedScreenshot = captureElementBufferedImage(element);

        if (helper.isImageSame(originalScreenshot, uploadedScreenshot)){
            test.info("Images are identical");
            Assert.fail();
        }else{
            test.info("Image was replaced");
        }
    }


    @Test(priority = 19,  groups = {"uploadCases"})
    public void testScaleImageFromCamera() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "currentUploadedImageFromPhotoLibrary");
        WebElement element = driver.findElement(By.xpath(currentCoverArt));
        BufferedImage originalScreenshot = captureElementBufferedImage(element);
        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickCameraFromActionSheet();
        helper.getButtonElement("PhotoCapture").click();
        sleep(2000);
        //Scale
        String beforeZoomIn = capturePresentScreenShot(test,  "before zoom in");
        helper.pinchOut(driver.findElement(By.xpath("//XCUIElementTypeOther[4]//XCUIElementTypeOther/XCUIElementTypeOther[2]/XCUIElementTypeOther[1]")));
        String afterZoomIn = capturePresentScreenShot(test,  "after zoom in");

        Assert.assertFalse(helper.isImageSame(new File(beforeZoomIn), new File(afterZoomIn)), "Images are identical. Not scaled");
        test.info("Image was scaled.");

    }

    @Test(priority = 20, enabled = true,  groups = {"uploadCases"})
    public void testCanRetakeImageFromCamera() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "currentUploadedImage");
        WebElement element = driver.findElement(By.xpath(currentCoverArt));
        BufferedImage originalScreenshot = captureElementBufferedImage(element);
        pdpHelper.clickEditButton();
        uploadCameraCapture();
        capturePresentScreenShot(test, "currentCameraCapturedImage");

        driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"Retake\"]")).click();
        test.info("Retake button clicked");
        Assert.assertTrue(driver.findElement(pdpHelper.cancelText).isDisplayed());
    }
//==================================================================================//

    @Test(priority = 21, groups = {"uploadCases"})
    public void testUploadedImageReflectedInLibraryLandingPage() throws InterruptedException, IOException {
        //==============      BUG LOGGED     ==========//
        if (shouldSkip) {
            throw new SkipException("Skipping test dynamically.");
        }
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        //Scroll and find test playList From Library Playlist Widget
        WebElement playlistMyLikes = getPlaylistMyLikes();
        int maxScrollAttempts = 30;

        int startX = playlistMyLikes.getLocation().getX() + playlistMyLikes.getSize().getWidth() - 1; // Right edge
        int endX = playlistMyLikes.getLocation().getX() + 1; // Left edge
        int y = playlistMyLikes.getLocation().getY() + (playlistMyLikes.getSize().getHeight() / 2); // Center vertically

        // Define parameters for horizontal scroll
        Map<String, Object> params = new HashMap<>();
        params.put("fromX", startX);
        params.put("fromY", y);
        params.put("toX", endX);
        params.put("toY", y);
        params.put("duration", 0.1); // Swipe duration in seconds

        int attempt = scrollToFindTextFromLibrary(params, testPlayList, maxScrollAttempts);
        if (attempt >= maxScrollAttempts) {
            test.info("Playlist not found after maximum scroll attempts.");
            Assert.fail();
        }

        //Capture the testPlayList image from the playList widget and save the screenshot
        String testPlayListXpathFromPlaylistWidget = "//XCUIElementTypeOther[.//XCUIElementTypeStaticText[contains(@label, \"" + testPlayList + "\")]]/preceding-sibling::XCUIElementTypeOther//XCUIElementTypeImage";
        File originalScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "OriginalPlayListImageInLibrary_17.png");
        test.info("Original PlayList image from Library Landing Page",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/OriginalPlayListImageInLibrary_17.png").build());

        uploadImageFromPhotoLibrary();

        File currentScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "currentPlayListImageInLibrary_17.png");

        test.info("Current PlayList image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/CurrentPlayListImageInLibrary_17.png").build());

        Assert.assertFalse(helper.isImageSame(originalScreenshotFile, currentScreenshotFile), "Original and current images are the same. " +
                                                                                   "\nImage was not reflected in the Library landing page.   Bug YFC-8783");

        test.info("Playlist Image was replaced");
    }

    @Test(priority = 22, groups = {"uploadCases"})
    public void testClicksCameraIconBringActionSheet() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());

        //Upload image so that I can see Camera icon and capture that image  to compare//
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "currentUploadedImage");
        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickLibraryFromActionSheet();
        //click the test image
        List<WebElement> WE =driver.findElements(By.xpath("//XCUIElementTypeImage"));

        try{
            driver.findElement(By.xpath("//XCUIElementTypeImage[contains(@label,\"4:14\")]")).click();//Static data. Need to be exposed in the first page of Photos Library page
        }catch(Exception e){
            helper.scrollUp();
            driver.findElement(By.xpath("//XCUIElementTypeImage[contains(@label,\"4:14\")]")).click();
        }

        capturePresentScreenShot(test, "test image uploaded");
        pdpHelper.clickChoose();

        //click Camera icon and verify the action sheet opens
        WebElement uploadedImage = helper.getTypeOtherElement("PickImage");

        File originalScreenshotFile = ScreenshotUtils.captureElementScreenShot("//XCUIElementTypeOther[@name=\"PickImage\"]", "uploadImageWithCamera.png");
        BufferedImage fullImage = ImageIO.read(originalScreenshotFile);

        // Define coordinates for cropping
        int cropX = 820;
        int cropY = 49;
        int cropWidth = 35; // Approximate width of the icon
        int cropHeight = 34; // Approximate height of the icon

        // Crop the image
        BufferedImage croppedIcon = fullImage.getSubimage(cropX, cropY, cropWidth, cropHeight);
        ImageIO.write(croppedIcon, "png", new File("screenshots/cropped.png"));
        BufferedImage referenceIcon = ImageIO.read(new File("./cropped.png"));
        ImageDiffer imgDiff = new ImageDiffer();  // Use a library like AShot

        ImageDiff diff = imgDiff.makeDiff(referenceIcon, croppedIcon);
        int ACCEPTABLE_THRESHOLD = 30;
        test.info("Camera icon",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/cropped.png").build());

        if (diff.getDiffSize() > ACCEPTABLE_THRESHOLD) {
            test.info("Camera icon is missing or incorrect.");
            throw new AssertionError("Icon validation failed.");
        } else {
            test.info("Image was found.");
        }

        uploadedImage.click();
        test.info("Clicked the Camera icon");

        String photoLibrary = LocatorProvider.getStringLocator("photoLibraryButton");
        Assert.assertNotNull(driver.findElement(By.xpath(photoLibrary)));
        String launchCamera = LocatorProvider.getStringLocator("launchCamera");
        Assert.assertNotNull(driver.findElement(By.xpath(launchCamera)));

        test.info("Launch Library and launch Camera visible from Action sheet");
        pdpHelper.clickCancel();//To dismiss action sheet
    }

    @Test(priority = 23, groups = {"uploadCases"})
    public void testDefault2x2ImageReplacesAfterDeleteCustomImage() throws InterruptedException, IOException {

        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        File coverArtBeforeAction = ScreenshotUtils.captureElementScreenShot(currentCoverArt, "currentCoverArt_19.png");
        test.info("Current cover art image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/currentCoverArt_19.png").build());
        pdpHelper.clickEditButton();

        // Define locators for the two elements
        By uploadBtn = By.xpath(uploadButton);
        By currentImage = By.xpath(currentImageXpath);

        // Check if the upload button is visible
        if (!driver.findElements(uploadBtn).isEmpty() && driver.findElement(uploadBtn).isDisplayed()) {
            test.info("Upload button is visible.");
            helper.scrollRightToLeft(driver.findElement(uploadBtn));
        }
        else if (driver.findElement(currentImage).isDisplayed()) {
            test.info("Current image is visible.");
            helper.scrollRightToLeft(driver.findElement(currentImage));
        }
        else {
            test.info("Neither element is visible.");
        }

        test.info("Scroll the upload tile to left");
        File defaultCoverArt = ScreenshotUtils.captureElementScreenShot(default2x2Image, "default2x2image.png");

        ScreenshotUtils.CaptureScreenshot(driver, "current screen shot_19");
        test.info("expected 2x2 default image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/default2x2image.png").build());

        ImageDiffer imgDiff = new ImageDiffer().withColorDistortion(40);
        Assert.assertFalse(helper.isImageSame(coverArtBeforeAction, defaultCoverArt), "Images are identical.");
        test.info("Images are different.");

    }

    @Test(priority = 24, groups = {"uploadCases"})
    public void testScaleImage () throws InterruptedException, IOException {

        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickLibraryFromActionSheet();
        pdpHelper.clickRandomImageFromLibrary();
        String beforeZoomIn = capturePresentScreenShot(test,  "before zoom in");
        helper.pinchOut(driver.findElement(By.xpath("//XCUIElementTypeApplication[@name=\"Amazon Music\"]//XCUIElementTypeOther[2]//XCUIElementTypeOther[1]//XCUIElementTypeOther[2]")));
        String afterZoomIn = capturePresentScreenShot(test,  "after zoom in");

        File beforeZoomInFile = new File(beforeZoomIn);
        File afterZoomInFile = new File(afterZoomIn);

        Assert.assertFalse(helper.isImageSame(beforeZoomInFile, afterZoomInFile), "Images are identical. Not scaled");
        test.info("Image was scaled.");
    }

    @Test(priority = 25, groups = {"uploadCases"})
    public void testCanCancelAfterSelectImageFromLibraryPage() throws InterruptedException {

        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "Before action");

        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickLibraryFromActionSheet();
        pdpHelper.clickRandomImageFromLibrary();
        capturePresentScreenShot(test, "Selected Image from picker");

        pdpHelper.clickButton("CancelText"); //Upload page
        pdpHelper.clickButton("CancelButton");// Photo Library
        helper.getButtonElement("BauhausTextButton,TopAppBar_Cancel").click();

        sleep(1000);
        Assert.assertTrue(helper.getButtonElement("IconButton,Toolbar_EditButton").isDisplayed(), "Failed to cancel image upload");
        test.info("Picked from picker image was Cancelled");
    }

    @Test(priority = 26, groups = {"uploadCases"})
    public void testUploadImageFromNewPlaylist() throws InterruptedException {

        test = ExtentTestNGReportListener.getTest();
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        String playlistName =   "Test_" + helper.getRandomNumber();
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.addPlaylist(playlistName);
        capturePresentScreenShot(test, "Before action");

        test.info("playlist Name : " + playlistName + " was created");
        pdpHelper.clickEditButton();
        capturePresentScreenShot(test, "New playlist PDP");
        driver.findElement(By.name("PickImage_UploadView")).click();
        pdpHelper.clickLibraryFromActionSheet();
        pdpHelper.clickRandomImageFromLibrary();
        pdpHelper.clickChoose();
        pdpHelper.clickDone();
        capturePresentScreenShot(test, "Image was uploaded");
        pdpHelper.clickBack();
        pdpHelper.deletePlaylist(playlistName);
        test.info("Delete the playlist that created");
        List<WebElement> elements = pdpHelper.getAllPlayLists();
        Assert.assertNull(pdpHelper.getTestPlayListFromPlayListsPage(elements, playlistName), "New playlist not found in the playlists");
    }

//++++++++++++++++++++++++++++++++++++++++++++++ helper func ++++++++++++++++++++++++++++++++++++++++++++++++++++//
//    public int scrollToFindTextFromLibrary(WebElement element, String str, int maxScrollAttempts){
//        int attempt = 0;
//        int startX = element.getLocation().getX() + element.getSize().getWidth() - 1; // Right edge
//        int endX = element.getLocation().getX() + 1; // Left edge
//        int y = element.getLocation().getY() + (element.getSize().getHeight() / 2); // Center vertically
//
//        // Define parameters for horizontal scroll
//        Map<String, Object> params = new HashMap<>();
//        params.put("fromX", startX); // Starting X coordinate (right edge of the element)
//        params.put("fromY", y);      // Starting Y coordinate (center vertically)
//        params.put("toX", endX);     // Ending X coordinate (left edge of the element)
//        params.put("toY", y);        // Y coordinate remains the same (center vertically)
//        params.put("duration", 0.1); // Swipe duration in seconds
//
//        while(!pdpHelper.isTextDisplayed(str)  && attempt < maxScrollAttempts){
//            helper.scrollRightToLeft(params);
//            params.put("fromX", endX); // Starting X coordinate (right edge of the element)
//            params.put("fromY", y);      // Starting Y coordinate (center vertically)
//            params.put("toX", endX -= 20);     // Ending X coordinate (left edge of the element)
//            params.put("toY", y);        // Y coordinate remains the same (center vertically)
//            params.put("duration", 0.1); // Swipe duration in seconds
//
//            attempt++;
//        }
//        return attempt;
//    }

    public int scrollToFindTextFromLibrary( Map<String, Object> params, String str, int maxScrollAttempts){
        int attempt = 0;
        while(!pdpHelper.isTextDisplayed(str)  && attempt < maxScrollAttempts){
            helper.scrollRightToLeft(params);
            attempt++;
        }
        return attempt;
    }

    public BufferedImage captureElementBufferedImage(WebElement element) throws IOException {
        File originalScreenshotFile = element.getScreenshotAs(OutputType.FILE);
        return ImageIO.read(originalScreenshotFile);
    }

    public void uploadCameraCapture() throws InterruptedException {
        pdpHelper.clickUpload();
        pdpHelper.clickCameraFromActionSheet();

        // LocatorProvider.getStringLocator("PhotoCapture")
        helper.getButtonElement("PhotoCapture").click();
        sleep(2000);

    }
    public void uploadImageFromCamera() throws InterruptedException {
        uploadCameraCapture();
        helper.getStaticTextElement("Use Photo").click();
        pdpHelper.clickDone();
    }

    public void uploadImageFromPhotoLibrary() throws InterruptedException {
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickLibraryFromActionSheet();
        pdpHelper.clickRandomImageFromLibrary();
        pdpHelper.clickChoose();
        pdpHelper.clickDone();
        capturePresentScreenShot(test, "uploadedImage_" + helper.getRandomNumber());
        pdpHelper.clickBack();
        pdpHelper.clickBack();// Go to Library
    }

    public WebElement getPlaylistMyLikes() throws InterruptedException {
        pdpHelper.clickLibrary();
        return driver.findElement(By.xpath("//XCUIElementTypeImage[contains(@name, \"Mylikes\")]"));
    }

    public String capturePresentScreenShot(ExtentTest test, String testCaseName){
        String screenshotPath = ScreenshotUtils.CaptureScreenshot(driver, testCaseName);
        System.out.println(screenshotPath);
        test.info(testCaseName,
                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        return screenshotPath;
    }

    public  void descriptionEditTestsPrep() throws InterruptedException {
        test.info("Test Case: " + Reporter.getCurrentTestResult().getMethod().getMethodName());
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        String description = pdpHelper.getLabelFromDescriptionField();
        test.info("Description: " + description);
        pdpHelper.clearDescriptionTextField(description);
    }

    private void cleanupMarketingPages(){
        System.out.println("cleanupMarketingPages");
        pdpHelper.clickHome();
        if (helper.isElementExist(By.xpath("//XCUIElementTypeStaticText[@name=\"No, thanks\"]"))){
            driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"No, thanks\"]")).click();
        }
        if (helper.isElementExist(By.xpath("//XCUIElementTypeButton[@name=\"splash_dismiss\"]"))){
            driver.findElement(By.xpath("//XCUIElementTypeButton[@name=\"splash_dismiss\"]")).click();
        }
    }

    @AfterMethod
    public void tearDown(ITestContext context) throws InterruptedException, MalformedURLException {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        DriverManager.quitDriver();
        driver = DriverManager.getDriver();
    }
}
