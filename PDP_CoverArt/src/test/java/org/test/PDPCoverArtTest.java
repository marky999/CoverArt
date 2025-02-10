package org.test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
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

    public PDPCoverArtTest()  {
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method,  ITestContext context, ITestResult result) throws Exception {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        context.setAttribute("WebDriver", driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        pdpHelper = new PDP_Helper();
        helper = new Helper(driver);
        sleep(5000);//DO NOT MOVE or REMOVE
        testResult = result;
    }

    @Test(priority = 1, enabled = executeDescriptionCases)
    public void testAddEditDescription() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Add/Edit Description");
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

    @Test(priority = 2, enabled = executeDescriptionCases)
    public void testCanAddEmojiAndSpecialChars() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Can Add Emoji And Special Chars");
        String testString = pdpHelper.emoji + helper.getRandomNumber();
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();

        boolean isFound =  pdpHelper.isTextDisplayed(pdpHelper.emoji);
        Assert.assertTrue(isFound);
        test.info("Verified \"" + testString +"\"");
    }

    //////////////////////////////////////////////////////////////////////////////////////
    @Test(priority = 3, enabled = executeDescriptionCases)
    public void testSavedDescriptionPersistsAfterRelaunch1() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Saved Description Persists After Relaunch1");
        tempTestString = testPlayList + helper.getRandomNumber();
        pdpHelper.inputDescription(tempTestString);
        pdpHelper.clickDone();
        test.info("Will verify whether the saved description is persisted after relaunch from next test");
    }

    @Test(priority = 4, enabled = executeDescriptionCases,  dependsOnMethods = {"testSavedDescriptionPersistsAfterRelaunch1"})
    public void testSavedDescriptionPersistsAfterRelaunch2() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Starting test: Saved Description Persists A fter Relaunch2");
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        String description = pdpHelper.getLabelFromDescriptionField();
        Assert.assertEquals(description, tempTestString);
        test.info("Verified the saved description is persisted after relaunch" );
        test.info("Description : " + description);
        tempTestString = ""; //clean buffer
    }
    //////////////////////////////////////////////////////////////////////////////////////

    @Test(priority = 5, enabled = executeDescriptionCases)
    public void testDeleteDescriptionsAndVerifyPrePopulatedString() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Delete Descriptions And Verify PrePopulated String");

        String description ;
        if(Android){
            description = driver.findElement(PDP_Helper.descriptionField2).getText();
        }else{
            description = pdpHelper.getLabelFromDescriptionField();
        }

        test.info(description);
        Assert.assertEquals(description, "Add a description");
        test.info("Verified desc field was deleted and 'Adda a description' auto-populated");
        pdpHelper.clickCancel();
    }

    @Test(priority = 6, enabled = executeDescriptionCases)
    public void testMaximumCharsCount() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Maximum Char Count");

        String description ;
        if(Android){
            description = driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"TextArea\"]")).getText();
            //characterLimitReached = "//android.widget.TextView[@text=\"Character limit reached\"]";
        }else{
            description = pdpHelper.getLabelFromDescriptionField();
           // characterLimitReached = "//XCUIElementTypeStaticText[@name=\"Character limit reached\"]";
        }
        Assert.assertEquals(description, "Add a description");
        //helper.scrollUp();
        Assert.assertNotNull(helper.getStaticTextElement("0 / 300"));
        test.info("Verified 0/300 for blank description");
        pdpHelper.inputDescription(pdpHelper.maxChars);
        Assert.assertNotNull(helper.getStaticTextElement("300 / 300"));
        test.info("Verified 300/300 when 300 chars filled in description");
        test.info("add one more char to verify cannot add more");
        new Actions(driver).sendKeys("1").perform();

        Assert.assertNotNull(helper.getStaticTextElement("300 / 300"));
        test.info("Verified 300/300 after add one more char");
        Assert.assertTrue(helper.isElementFound(PDP_Helper.characterLimitReached, 2));
        test.info("Verified Character limit reached string appeared");
        new Actions(driver).sendKeys("\n").perform(); //dismiss the keyboard
    }

    @Test(priority = 7, enabled = executeDescriptionCases)
    public void testEditPlayListName() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testEditPlayListName");
        String originalPlaylistToTest = "Empty description";
        String newPlaylistName = "newPlaylistName";
        pdpHelper.openPlaylist(originalPlaylistToTest);
        pdpHelper.clickEditButton();
        pdpHelper.clearPlaylistNameField();
        driver.findElement(PDP_Helper.descriptionField2).sendKeys(newPlaylistName);
        pdpHelper.clickDone();
        pdpHelper.clickBack();
        if(pdpHelper.clickTestPlayList("Empty description")) {
            Assert.fail();
        }

        test.info("Original test playlist was updated");
        System.out.println("Original playlist name was updated");

        pdpHelper.clickTestPlayList(newPlaylistName);
        test.info("Open " + newPlaylistName);
        pdpHelper.clickEditButton();
        pdpHelper.clearPlaylistNameField();
        driver.findElement(PDP_Helper.descriptionField2).sendKeys(originalPlaylistToTest);
        pdpHelper.clickDone();
        pdpHelper.clickBack();

        Assert.assertTrue(pdpHelper.clickTestPlayList("Empty description"));
    }

    @Test(priority = 8, enabled = executeDescriptionCases)
    public void testMoreAndShowLessButtons() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: More & ShowLess Buttons");
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

    @Test(priority = 9, enabled = executeDescriptionCases)
    public void testExpandedStateNotPersistWhenUserLeavesThePage() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: Expanded State Not Persist When User Leaves The Page");
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

    @Test(priority = 10, enabled = executeDescriptionCases)
    public void testWhenSaveCollapsedStateIsDefault() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        descriptionEditTestsPrep("test: When Collapsed, Ellipsis And Show More Button appeared");
        String testString = helper.getRandomNumber(200);
        pdpHelper.inputDescription(testString);
        pdpHelper.clickDone();
        Assert.assertFalse(pdpHelper.isTextDisplayed(testString));
        Assert.assertTrue(pdpHelper.isTextDisplayed("..."));
        Assert.assertTrue(pdpHelper.showMore().isDisplayed());
        test.info("... and Show more displayed");
    }

    @Test(priority = 11, enabled = executeDescriptionCases)
    public void testCreateAIGeneratedPlaylist() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testCreateAIGeneratedPlaylist");
        String playlistName =  "KatsEye";
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.addAIPlaylist(playlistName);
        test.info("playlist Name : " + playlistName + " was created");
        pdpHelper.clickEditButton();

        List<WebElement> staticElems = driver.findElements(By.xpath("//XCUIElementTypeStaticText"));
        test.info(staticElems.get(0).getText());
        String newlyCreatedPlaylistName = staticElems.get(0).getText();
        String description = staticElems.get(1).getText();
        test.info("Description : " + description);
        Assert.assertFalse(description.isEmpty());
        Assert.assertNotSame(description, "Add a description");
        pdpHelper.clickCancel();
        pdpHelper.clickBack();

        driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"IconButton,Maestro_TopAppBar_BackButton\"]")).click();//pdpHelper.clickBack() does not work
        sleep(1000);

        driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"IconButton,Maestro_TopAppBar_BackButton\"]")).click();
        sleep(1000);

        pdpHelper.deletePlaylist(newlyCreatedPlaylistName);
        test.info("Delete the playlist that created");
        List<WebElement> elements = pdpHelper.getAllPlayLists();
        Assert.assertNull(pdpHelper.getTestPlayListFromPlayListsPage(elements, playlistName));
    }

    @Test(priority = 12, enabled = true)
    public void testUploadedImageReflectedInPlaylistsPage() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testUploadedImageReflectedInPlaylistsThumbnail");
        test.info("open custom CoverArt Playlist => verify those two images are different)");
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();

        capturePresentScreenShot(test, "originalPlaylistsPage");

        //TODO to compare actual thumb
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
        test.info("Images are reflected");
    }

    @Test(priority = 13, enabled = executeUpload)
    public void testThumbNailReflectedInShareSheetFromContextMenu() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testThumbNailReflectedInShareSheetFromContextMenu");
        test.info("open custom CoverArt Playlist => verify those two images are different)");
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        String imgPath2 = ScreenshotUtils.CaptureScreenshot(driver, "TestplayThumb");
        test.info("Before action",
                MediaEntityBuilder.createScreenCaptureFromPath(imgPath2).build());
        File originalThumbImage = pdpHelper.captureThumbNailFromPlaylists("Testplay.png");
        test.info("Playlist Thumb",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/Testplay.png").build());
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

        Assert.assertTrue(helper.isImageSame(originalThumbImage, shareSheetThumbNail));
        test.info("Images are reflected");
    }


    @Test(priority = 15, enabled = executeUpload
            , retryAnalyzer = RetryAnalyzer.class)
    public void testUploadImageFromPhotoLibrary() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testUploadImageFromPhotoLibrary");
        test.info("Capture before image => upload new image => verify those two images are different)");
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "Original Uploaded Image From PhotoLibrary");

        WebElement element = driver.findElement(By.xpath("(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]"));
        File originalScreenshotFile = element.getScreenshotAs(OutputType.FILE);

        uploadImageFromPhotoLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.clickTestPlayList();

        element = driver.findElement(By.xpath("(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]"));

        File uploadedScreenshotFile = element.getScreenshotAs(OutputType.FILE);
        Assert.assertFalse(helper.isImageSame(originalScreenshotFile, uploadedScreenshotFile));
    }

    @Test(priority = 16, enabled = executeAll, retryAnalyzer = RetryAnalyzer.class)
    public void testUploadImageFromCamera() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testUploadImageFromPhotoLibrary_ORI");
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "testUploadImageFromPhotoLibrary");
        WebElement element = driver.findElement(By.xpath("(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]"));
        BufferedImage originalScreenshot = captureElementBufferedImage(element);

        pdpHelper.clickEditButton();
        uploadImageFromCamera();
        pdpHelper.clickBack();
        pdpHelper.clickTestPlayList();

        element = driver.findElement(By.xpath("(//XCUIElementTypeOther[@name=\"Imagery_EqualizerPlaceholder\"])[1]"));
        BufferedImage uploadedScreenshot = captureElementBufferedImage(element);

        ImageDiffer imgDiff = new ImageDiffer();  // Use a library like AShot
        ImageDiff diff = imgDiff.makeDiff(originalScreenshot, uploadedScreenshot);


        if (!diff.hasDiff()) {
            test.info("Images are identical");
            Assert.fail();
        }
        test.info("Image was replaced");
    }

    @Test(priority = 17, enabled = executeUpload)
    public void testUploadedImageReflectedInLibraryPage() throws InterruptedException, IOException {
        //==============      BUG LOGGED     ==========//
        test = ExtentTestNGReportListener.getTest();
        test.info("test whether Uploaded Image is Reflected In Library Landing Page");

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
        File originalScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "OriginalPlayListImageInLibrary.png");
        test.info("Original PlayList image from Library",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/OriginalPlayListImageInLibrary.png").build());

        uploadImageFromPhotoLibrary();
        File currentScreenshotFile = ScreenshotUtils.captureElementScreenShot(testPlayListXpathFromPlaylistWidget, "currentPlayListImageInLibrary.png");

        test.info("Current PlayList image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/CurrentPlayListImageInLibrary.png").build());

        Assert.assertFalse(helper.isImageSame(originalScreenshotFile, currentScreenshotFile));

        test.info("Playlist Image was replaced");
    }

    @Test(priority = 18, enabled = executeUpload)
    public void testClicksCameraIconBringActionSheet() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testClicksCameraIconBringActionSheet");

        //Upload image so that I can see Camera icon and capture that image  to compare//
        pdpHelper.goToPDP();
        capturePresentScreenShot(test, "testUploadImageFromPhotoLibrary");
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
        List<WebElement> element = driver.findElements(By.xpath("//XCUIElementTypeStaticText[contains(@label, 'Launch Library') or contains(@label, 'Launch Camera')]"));
        Assert.assertEquals(element.size(), 2);
        test.info("Launch Library and launch Camera visible from Action sheet");
        pdpHelper.clickCancel();//To dismiss action sheet
    }

    @Test(priority = 19, enabled = executeUpload)
    public void testDefault2x2ImageReplacesAfterDeleteCustomImage() throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testDefault2x2ImageReplacesAfterDeleteCustomImage");
        pdpHelper.goToPDP();
        File coverArtBeforeAction = ScreenshotUtils.captureElementScreenShot(currentCoverArt, "currentCoverArt.png");
        test.info("Current cover art image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/currentCoverArt.png").build());
        pdpHelper.clickEditButton();

        // Define locators for the two elements
        By uploadBtn = By.xpath(uploadButton);
        By currentImage = By.xpath("//XCUIElementTypeOther[@name='PickImage_SelectedImage']");

        // Check if the upload button is visible
        if (!driver.findElements(uploadBtn).isEmpty() && driver.findElement(uploadBtn).isDisplayed()) {
            test.info("Upload button is visible.");
            helper.scrollRightToLeft(driver.findElement(uploadBtn));
        }
        else if (!driver.findElements(currentImage).isEmpty() && driver.findElement(currentImage).isDisplayed()) {
            test.info("Current image is visible.");
            // Perform actions for the selected image
            helper.scrollRightToLeft(driver.findElement(currentImage));
        }
        else {
            System.out.println("Neither element is visible.");
        }

        test.info("Scroll the upload tile to right");
        File defaultCoverArt = ScreenshotUtils.captureElementScreenShot(default2x2Image, "default2x2image.png");
        ImageDiffer imgDiff = new ImageDiffer().withColorDistortion(40);
        ImageDiff diff = imgDiff.makeDiff(ImageIO.read(coverArtBeforeAction), ImageIO.read(defaultCoverArt));
        test.info("expected 2x2 default image",
                MediaEntityBuilder.createScreenCaptureFromPath("screenshots/default2x2image.png").build());
        if (diff.hasDiff()) {
            test.info("Images are different.");
        } else {
            test.info("Images are identical.");
            throw new AssertionError("Icon validation failed.");
        }
    }

    @Test(priority = 20, enabled = executeUpload)
    public void testScaleImage () throws InterruptedException, IOException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testScaleImage");
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
        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(ImageIO.read(beforeZoomInFile), ImageIO.read(afterZoomInFile));
        if (diff.hasDiff()) {
            test.info("Images are different.");
        } else {
            test.info("Images are identical.");
            throw new AssertionError("Icon validation failed.");
        }
    }

    @Test(priority = 21, enabled = executeUpload)
    public void testCanCancelAfterSelectImageFromLibraryPage() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testCanCancelAfterSelectImageFromLibraryPage");
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        pdpHelper.clickUpload();
        pdpHelper.clickLibraryFromActionSheet();
        pdpHelper.clickRandomImageFromLibrary();
        pdpHelper.clickButton("CancelText"); //Upload page
        pdpHelper.clickButton("CancelButton");// Photo Library
        helper.getButtonElement("BauhausTextButton,TopAppBar_Cancel").click();

        sleep(1000);
        Assert.assertTrue(helper.getButtonElement("IconButton,Toolbar_EditButton").isDisplayed());
    }

    @Test(priority = 22, enabled = executeUpload)
    public void testUploadImageFromNewPlaylist() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("testUploadImageFromNewPlaylist");
        String playlistName =   "Test_" + helper.getRandomNumber();
        pdpHelper.clickLibrary();
        pdpHelper.clickPlayListButton();
        pdpHelper.addPlaylist(playlistName);
        test.info("playlist Name : " + playlistName + " was created");
        pdpHelper.clickEditButton();
       // helper.getStaticTextElement("Upload").click();
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
        Assert.assertNull(pdpHelper.getTestPlayListFromPlayListsPage(elements, playlistName));
    }

//++++++++++++++++++++++++++++++++++++++++++++++ helper func ++++++++++++++++++++++++++++++++++++++++++++++++++++//
    public int scrollToFindTextFromLibrary(WebElement element, String str, int maxScrollAttempts){
        int attempt = 0;
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

        while(!pdpHelper.isTextDisplayed(str)  && attempt < maxScrollAttempts){
            helper.scrollRightToLeft(params);
            params.put("fromX", endX); // Starting X coordinate (right edge of the element)
            params.put("fromY", y);      // Starting Y coordinate (center vertically)
            params.put("toX", endX -= 20);     // Ending X coordinate (left edge of the element)
            params.put("toY", y);        // Y coordinate remains the same (center vertically)
            params.put("duration", 0.1); // Swipe duration in seconds

            attempt++;
        }
        return attempt;
    }

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

    public void uploadImageFromCamera() throws InterruptedException {
        pdpHelper.clickUpload();
        pdpHelper.clickCameraFromActionSheet();
        helper.getButtonElement("PhotoCapture").click();
        sleep(2000);
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

    public  void descriptionEditTestsPrep(String testCase) throws InterruptedException {
        test.info(testCase);
        pdpHelper.goToPDP();
        pdpHelper.clickEditButton();
        String description = pdpHelper.getLabelFromDescriptionField();
        test.info("Description: " + description);
        pdpHelper.clearDescriptionTextField(description);
    }

    @AfterMethod
    public void tearDown(ITestContext context) throws InterruptedException, MalformedURLException {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        DriverManager.quitDriver();
        driver = DriverManager.getDriver();
    }

//        if (driver != null) {
//            try {
////                if (driver instanceof AndroidDriver) {
////                    ((AndroidDriver) driver).hideKeyboard(); // Cast driver to AndroidDriver
////                    System.out.println("Soft keyboard hidden successfully.");
////                }
//            } catch (Exception e) {
//                System.out.println("Keyboard was not open, skipping hideKeyboard()");
//            }
//            if (driver != null) {
//                driver.quit(); // Close the browser
//                sleep(2000);
//            }
 //       }
   // }
}
