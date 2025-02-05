package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.gson.Gson;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.testng.Assert.assertNotNull;

public class YIRTestCase {
    private WebDriverWait wait;
    private Helper helper;
    public AppiumDriver driver;
    public IOSSongLapseTester iossonglapseTester;
    public ValidateEachFrame validateEachFrame;
    Map<String, String> songs = JasonExtraction.songsMap;
    private boolean skipSetup = true;  // Flag to control if BeforeMethod should run
    private ITestResult testResult;
    public ExtentTest test;
    public final boolean execute = true; /////////////   DEBUG PURPOSE //////////

    @BeforeClass(alwaysRun = true)

    public void classSetUp() throws Exception {
        JasonExtraction.extractJson();
    }
    @BeforeSuite
    public void cleanUpScreenshots() {
        String screenshotsPath = "./screenshots"; // Adjust path if needed
        deleteScreenshots(screenshotsPath);
        System.out.println("Deleted all files in the screenshots folder.");
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method,  ITestContext context, ITestResult result) throws Exception {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        XCUITestOptions options = new XCUITestOptions()
                .setPlatformVersion("18.0.1")
                .setDeviceName("iPhone_13 Pro")
                .setUdid("00008110-001C45D60CC0401E")
                .setApp("com.amazon.mp3.CloudPlayer") // or the app path
                .setAutomationName("XCUITest")
                .setShowXcodeLog(true);
        driver = new IOSDriver(new URL("http://127.0.0.1:4723"), options);
        context.setAttribute("WebDriver", driver);
        //----------------  DeepLink ---------------------//
        driver.get("https://music.amazon.com/recap/delivered/2024");
        //------------------------------------------------//

        Thread.sleep(3000);//DO NOT MOVE or REMOVE

        //----------------  Library Ingress ---------------------//
   //     driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"AMMyMusicNavigationTabIconAccessibilityIdentifier\"]")).click();
  //      driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name=\"ExpandedInfoView_PrimaryLabel\" and @label=\"2024 Delivered\"]")).click();
        //-------------------------------------------------------//
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        helper = new Helper(driver, wait);
        validateEachFrame = new ValidateEachFrame(driver, helper, wait);
        iossonglapseTester = new IOSSongLapseTester(driver, wait, helper, validateEachFrame);
        this.testResult = result;
     //   test = ExtentTestNGReportListener.getTest();
        Thread.sleep(2000);//DO NOT MOVE
    }

    @Test(priority = 1, enabled = execute)
    public void test_S101_FirstPage() throws InterruptedException {
        ExtentTest test = ExtentTestNGReportListener.getTest();
        test.info("Starting test: S101 FirstPage");
        assertForTest(test, validateEachFrame.isFirstPageDisplayed(), "test_S101_FirstPage");
    }

    @Test(priority = 2, enabled = execute)
    public void test_S106_CountOfTracksIn2024Splash() throws InterruptedException {
        ExtentTest test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 2: S.1.06 Verify the track count appears in 2024 splash");
        int count = validateEachFrame.getCountOfDecadeTrack();
        test.info("count = " + count);
        test.info("Count Of Tracks In 2024: " + count);
        Thread.sleep(4000);
        assertForTest(test, 6 == count, "test_S106_CountOfTracksIn2024Splash");
    }

    @Test(priority = 3, enabled = execute)
    public void test_S201_WeSpentxxxMinutes() throws FileNotFoundException, InterruptedException {
        ExtentTest test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 3: S2.01 We Spent xxx minutes");
        //----- json extraction -----//
        JasonExtraction.Data data = new Gson().fromJson(new FileReader(Config.JSON_FILE_PATH), JasonExtraction.Data.class);
        String totalMinutes = String.valueOf(data.getTotalStats().getTracks().getTotalMinutes());
        totalMinutes = helper.formatWithCommas(totalMinutes);
        test.info("Total minutes from json: " + totalMinutes);
        //----------------------------//
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(helper.weFound_xpath)));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        test.info("We spent " + totalMinutes + " minutes together this year");
        assertForTest(test, validateEachFrame.isPartOfStringFound(totalMinutes), "test_S201_WeSpentxxxMinutes");
    }

    @Test(priority = 4, enabled = execute)
    public void test_S401_TopDecade() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 4: S4.01 Top decade");
        helper.goNextFrame(2);
        String decade = validateEachFrame.getDecade();
        String decadeFromJson = JasonExtraction.getTopDecadeFromJson();
        test.info("Decade is " + decade);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertForTest(test, decade.contains(decadeFromJson.substring(2)), "test_S401_TopDecade");
    }

    @Test(priority = 6, enabled = execute)
    public void test_S501_ValidateThereWereSomeGenre() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 6: S5.01 Verify There were some genre...");
        helper.goNextFrame(3);
        assertForTest(test, validateEachFrame.genreFrameValidation(), "test_S501_ValidateThereWereSomeGenre");
    }

    @Test(priority = 7, enabled = execute)
    public void test_S502_ValidateTopGenres() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 7: S5.02 Verify Top 5 genres");

        List<String> topGenresFromJSon = JasonExtraction.getTopGenres();
        test.info("topGenresFromJSon = " + topGenresFromJSon);
        String firstGenre = topGenresFromJSon.get(0);
        helper.goNextFrame(3);
        Thread.sleep(5000);//NEEDED

        List<String>  topGenres = validateEachFrame.getTopGenres(firstGenre);
        test.info("topGenres from AM = " + topGenres);
        assertForTest(test, topGenres.equals(topGenresFromJSon), "test_S502_ValidateTopGenres");
    }

    ////////////////////////////////// Skip Setup for Next case//////////////////////////////////////////
    @Test(priority = 9, enabled = execute)
    public void test_S602_FirstHalfObsessedWith() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Case 9: S6.02 First Obsessed With");

        HashMap<String, Object> firstHalfFromJSon = JasonExtraction.getFirstHalf();
       // int count = (int)firstHalfFromJSon.get("count");
        if(firstHalfFromJSon == null || (int)firstHalfFromJSon.get("count") < 10){
            throw new SkipException("Skipping Case");
        }

        String topTrack = (String) firstHalfFromJSon.get("topTrack");
        String peakListeningMonth = "" + firstHalfFromJSon.get("peakMonth");
        peakListeningMonth = helper.getMonth(peakListeningMonth);
        Map<String, String> songs = JasonExtraction.songsMap;
        test.info("topTrack  from Json: " + topTrack + " : " + JasonExtraction.songsMap.get(topTrack));
        test.info("peakListeningMonth = " + peakListeningMonth);

        String trackNameFromJson = songs.get(topTrack);

        // Verify from the frame   //
        helper.goNextFrame("//*[contains(@name,\"First you were obsessed with\")]");
        Thread.sleep(2000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name,\"First you were obsessed with\")]")));//XCUIElementTypeStaticText[@name="First you were obsessed with"]

        Assert.assertTrue(validateEachFrame.isPartOfStringFound(trackNameFromJson.split(" ")[0]));
        test.info(trackNameFromJson);
        test.info("Peak Listening Month : " + peakListeningMonth);
        assertForTest(test, validateEachFrame.isPartOfStringFound(peakListeningMonth), "test_S602_FirstHalfObsessedWith");
       // skipSetup = true;
    }

    @Test(priority = 10, dependsOnMethods = {"test_S602_FirstHalfObsessedWith"}, enabled = execute)
    public void test_S603_SecondHalfLaterYouAre() throws InterruptedException {
        skipSetup = false;
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 10: S6.03  Later you were all about");
        HashMap<String, Object> firstHalfFromJSon = JasonExtraction.getFirstHalf();
        int count = (int)firstHalfFromJSon.get("count");
        if(count < 10){
            throw new SkipException("Skipping Case");
        }

        HashMap<String, Object> secondHalfFromJSon = JasonExtraction.getSecondHalf();
        String topTrack = (String) secondHalfFromJSon.get("topTrack");
        String peakListeningMonth = "" + secondHalfFromJSon.get("peakMonth");
        peakListeningMonth = helper.getMonth(peakListeningMonth);
        String trackNameFromJson = songs.get(topTrack);
        test.info("track From Json = " + topTrack + "  : " + trackNameFromJson);
        test.info("peakListeningMonth = " + peakListeningMonth);

        helper.goNextFrame("//*[contains(@name,\"First you were obsessed with\")]");
        //helper.goNextFrame("//*[contains(@name,\"Later, you were all about \")]");
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name,\"Later, you were all about \")]")));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(9000);//DO NOT REMOVE
        Assert.assertTrue(validateEachFrame.isPartOfStringFound(peakListeningMonth));
        test.info("\n" + trackNameFromJson + " appeared");
        test.info("Peak Listening Month : " + peakListeningMonth);
        skipSetup = false;
        assertForTest(test, validateEachFrame.isPartOfStringFound(trackNameFromJson.split(" ")[0]), "test_S603_SecondHalfLaterYouAre");
    }
////////////////////////////////////////////////////////////////////////////////////////////

    @Test(priority = 11, enabled = execute)
    public void test_S701_ButOnlyOneSongDominatedFrame() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 11: S7.01 But Only One Song Dominated");
        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"dominated your charts\"]");
        //helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"dominated your charts\"]");
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name=\"dominated your charts\"]")));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        //assertNotNull(elem, "But one Song Dominated frame not found on the page");
        test.info("But Only One Song Dominated frame appeared");
        assertForTest(test, elem != null, "test_S701_ButOnlyOneSongDominatedFrame");
    }

    @Test(priority = 12, enabled = execute)
    public void test_S702_YourTopSongFrame() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 12: S7.02 Your Top Song ");
        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"dominated your charts\"]");
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[contains(@name, \"You played it\") and contains(@name, \"times. That's commitment!\")]")));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        assertNotNull(elem, "'That's a commitment' not found on the page");
        test.info("Your Top Song Frame: 'That's a commitment'  appeared");

        test.info("=== Verify 'You played x times' ===");
        int count = JasonExtraction.topTracksArray.getJSONObject(0).getInt("count");
        test.info("count from json: " + count);
        Assert.assertTrue(validateEachFrame.isPartOfStringFound("" + count));
        test.info("You played " + count + " times");
        assertForTest(test, validateEachFrame.isPartOfStringFound("" + count), "test_S702_YourTopSongFrame");
    }

    @Test(priority = 13)
    public void test_S801_TopSongRareCard() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        //  TODO Need to check whether it is eligible to be included
        test.info("\nCase 13: S8.01 Your Top Song Rare card");

        //-- Extract data from json to verify --//
        String topSongID = JasonExtraction.topTracksArray.getJSONObject(0).getString("id");
        String topSongTrackTitle = JasonExtraction.songsMap.get(topSongID);
        test.info("topSongID : " + topSongID);
        test.info("topSongTrackTitle : " + topSongTrackTitle);
        //--------------------------------------//

        boolean isFound = helper.goNextFrame("//*[@name=\"BIG FAN\"] ");
        if(!isFound){
            throw new SkipException("Skipping test: Rarecard not found.");
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@name=\"Top Song\"] ")));
        test.info("TopSong Share Card Opened");

        test.info("=== Verify 'You are xPercentile listener'===");
        int countPercentile = JasonExtraction.topTracksArray.getJSONObject(0).getInt("countPercentile");
        Assert.assertTrue(validateEachFrame.isPartOfStringFound("" + countPercentile), countPercentile + " Expected " );
        test.info("You are " + countPercentile + "% listener");

        test.info("=== Verify TopSong Title appears w/icon ===");

        test.info("Verify " + topSongTrackTitle.split(" ")[0]);

        assertForTest(test, validateEachFrame.isPartOfStringFound(topSongTrackTitle.split(" ")[0]), "test_S801_TopSongRareCard");
        test.info(topSongTrackTitle + " appeared");
    }

    @Test(priority = 14, enabled = true)
    public void test_S901_AllYourFavoriteCapturedForever() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 14: S9.01  All Your Favorite Captured Forever ");
        helper.goNextFrame("//*[@name=\"All your favorites,\"]");
       // wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@name=\"All your favorites,\"]")));
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@name=\"captured forever\"]")));
        //assertNotNull(elem, "frame not appeared");
        test.info("All Your Favorite Captured Forever appeared");
        assertForTest(test, elem != null, "test_S901_AllYourFavoriteCapturedForever");

    }

    @Test(priority = 15, enabled = true)
    public void test_S903_MyTopSongs2024() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 15: S9.03 My TopSongs 2024");
        helper.goNextFrame("//*[contains(@name, \"Your playlist is waiting\")]");
        WebElement elem =  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name, \"Your playlist is waiting\")]")));
        test.info("Your playlist is waiting.... appeared");
        test.info("My Top Songs 2024 appeared");
        Thread.sleep(3000);
        assertForTest(test, elem != null, "test_S903_MyTopSongs2024");
    }


    @Test(priority = 16, enabled = true)
    public void test_S11_0_1_YouListenToXXXArtists() throws FileNotFoundException, InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("Case 16: S11.01 You listen to xxx artists this year, but someone stole the show");
        helper.goNextFrame("//*[@name=\"You listened to \"]");
        //--- Extract json data to be verified ---//
        JasonExtraction.Data data = new Gson().fromJson(new FileReader(Config.JSON_FILE_PATH), JasonExtraction.Data.class);
        String totalDistinctCountFromJson = String.valueOf(data.getTotalStats().getArtists().getDistinctCount());
        test.info("total Distinct artist Count FromJson is " + totalDistinctCountFromJson);
        WebElement elem =  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@name=\"You listened to \"]")));
        assertNotNull(elem, "S11.01 frame not appeared");

        elem =  driver.findElement(By.xpath("//*[@name=\"emphasizedText-line1-part2\"]"));
        test.info("You listen to " + elem.getAttribute("label") + " artists");

        totalDistinctCountFromJson = helper.formatWithCommas(totalDistinctCountFromJson);
      //  Assert.assertEquals(totalDistinctCountFromJson, elem.getAttribute("label"));
        test.info("You listened to " + totalDistinctCountFromJson + " artists this year, but someone stole the show");
        test.info("elem.getAttribute(\"label\") = " + elem.getAttribute("label"));

        assertForTest(test, elem.getAttribute("label").contains(totalDistinctCountFromJson), "test_S11_0_1_YouListenToXXXArtists");
    }

    @Test(priority = 17, enabled = true)
    public void test_S11_02_YourTopArtist() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 17: S11.0.2 Your Top Artist");

        //-- extract data from json to verify --//
        int minutes = JasonExtraction.topArtistsArray.getJSONObject(0).getInt("minutes");
        String artistID = JasonExtraction.topArtistsArray.getJSONObject(0).getString("id");
        String artistName = JasonExtraction.artistMap.get(artistID);
        test.info("artistID from json : " + artistID);
        test.info("artistName from json : " + artistName);
        test.info("minutes from json : " + minutes);
        //--------------------------------------//
        helper.goNextFrame("//*[@name=\"You listened to \"]");

        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name, 'minutes together and trust us, they are honored')]")));
        assertNotNull(elem, "Not found -> You spent xxxxx minutes together and trust us, they are honored");
        test.info( " Your Top Artist appeared");

        test.info("\n --- Verify 'You spent " + minutes + " minutes' --- ");
        String label = elem.getAttribute("label");

        test.info(label);

        test.info("--- Verify top artist : " + artistName + "---");
        Thread.sleep(2000);
        if(driver.findElements(By.xpath("//*[@name=\"" + artistName + "\"]")).isEmpty()){
            Assert.fail();
        }
        test.info(artistName + " found");

        assertForTest(test, label.contains(helper.formatWithCommas("" + minutes)), "test_S11_02_YourTopArtist");
    }

    @Test(priority = 18, enabled = true)
    public void test_S12_01_TopArtistRareCard() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 18: S13.0.1 Top Artist Rare Card");

        int minutes = JasonExtraction.topArtistsArray.getJSONObject(0).getInt("minutesPercentile");
        if(minutes < 5){
            throw new SkipException("Skipping Alexa. minutes : " + minutes);
        }


        //-- extract data from json to verify --//
        int minutesPercentileFromJson = JasonExtraction.topArtistsArray.getJSONObject(0).getInt("minutesPercentile");
        String artistID = JasonExtraction.topArtistsArray.getJSONObject(0).getString("id");
        int percentile = JasonExtraction.topArtistsArray.getJSONObject(0).getInt("minutesPercentile");

        String artistName = JasonExtraction.artistMap.get(artistID);
        test.info("Artist ID from json : " + artistID);
        test.info("Artist name from json : " + artistName);
        //--------------------------------------//
        if (percentile > 10){
            throw new SkipException("Skipping Case");
        }

        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Top Artist\"]");
        Thread.sleep(3000);
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name=\"BIG FAN\"]")));
        test.info( "Top Artist Rare Card appeared : BIG FAN");
        test.info( "Verify \"You're a top " + minutesPercentileFromJson + "% listener");
        boolean isFound = helper.elementFound("//*[contains(@name, \"You're a top \")]", 10);

        Assert.assertTrue(isFound,  "You're a top " + minutesPercentileFromJson + " % not found");
        test.info( " \"You're a top " + minutesPercentileFromJson + "% listener =>  Found");

        test.info("--- Verify top artist : " + artistName + "---");
        System.out.println("artist xpath = "  + "//*[@name=\"" + artistName + "\"]" );
        isFound = helper.elementFound("//*[@name=\"" + artistName + "\"]", 10);

        assertForTest(test, isFound, "test_S12_01_TopArtistRareCard");
    }

    @Test(priority = 19, enabled = true)
    public void test_S14_01_TopArtists() throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 19: S14.0.1 Top Artists");

        //----- extract data from json to verify -----//
        List<String> topArtistsNamesListFromJson = new ArrayList<>();
        // Loop through each artist JSON object in the JSONArray and extract the ID
        for (int i = 0; i < JasonExtraction.topArtistsArray.length(); i++) {
            JSONObject artist = JasonExtraction.topArtistsArray.getJSONObject(i);
            String artistId = artist.getString("id");
            topArtistsNamesListFromJson.add(JasonExtraction.artistMap.get(artistId));
        }
        // Output the list of artist IDs
        test.info("Top Artists IDs: " + topArtistsNamesListFromJson);
        helper.goNextFrame("//*[contains(@name,   '" + topArtistsNamesListFromJson.get(1) + "')]");
        //------------------------------------------//

        // Extract the artists names from the current frame and put them in array
        List<String> topArtistsArray = validateEachFrame.getTopArtists();
        test.info("Top Artists correctly displayed : " + topArtistsArray);
        assertForTest(test, topArtistsArray.containsAll(topArtistsNamesListFromJson), "test_S14_01_TopArtists");
    }

    @Test(priority = 20, enabled = true)
    public void test_S15_01_YouDoveDeep () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 20: S15.0.1 You Dove Deep");

        // Check if the JSONObject is empty
        if(JasonExtraction.topNewArtistDiscoveryArray.isEmpty() || JasonExtraction.topEarlyAlbumDiscoveryArray.isEmpty()){
            throw new SkipException("Skipping Case");
        }
        if (JasonExtraction.topNewArtistDiscoveryArray.getJSONObject(0).getInt("minutes") < 40 &&
                JasonExtraction.topEarlyAlbumDiscoveryArray.getJSONObject(0).getInt("minutes") < 40) {
            test.info("topEarlyAlbum minute is less than 35");
            throw new SkipException("Skipping Case");
        }

        helper.goNextFrame("//*[@name=\"You dove deep \"]");
        assertForTest(test, validateEachFrame.isPartOfStringFound("You dove deep"), "test_S15_01_YouDoveDeep");
        test.info("You dove deep frame appeared");
    }

    @Test(priority = 21, dependsOnMethods = {"test_S15_01_YouDoveDeep"}, enabled = true)
    public void test_S15_02_EarlyListener () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 21: S15.0.1 Early Listener");
        if (JasonExtraction.topEarlyAlbumDiscoveryArray.getJSONObject(0).getInt("minutes") < 40) {
            test.info("topEarlyAlbum minute is less than 40");
            throw new SkipException("Skipping Case");
        }
        helper.goNextFrame("//*[@name=\"You dove deep \"]");//Go to here and wait for the next. skip too fast to capture element
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name, \"You were one of the first\" )]")));
        String str = elem.getAttribute("label");
        test.info(str);
        //TODO: What to extract for album
        assertForTest(test, str.contains("You were one of the first to listen to this album by"), "test_S15_01_EarlyListener");
    }

    @Test(priority = 22, dependsOnMethods = {"test_S15_01_YouDoveDeep"}, enabled = true)
    public void test_S16_01_TrendSetterTopDiscovery () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 22: S16.0.1 Trend Setter, TOP DISCOVERY");
        if (JasonExtraction.topNewArtistDiscoveryArray.getJSONObject(0).getInt("minutes") < 40) {
            throw new SkipException("Skipping Case");
        }

        //----- Json data extraction -----//
        String artistID = JasonExtraction.topNewArtistDiscoveryArray.getJSONObject(0).getString("id");
        test.info("artist id: " + artistID);
        String artistName = JasonExtraction.artistMap.get(artistID);
        test.info("Artist: " + artistName);
        int minutesFromJson = JasonExtraction.topNewArtistDiscoveryArray.getJSONObject(0).getInt("minutes");
        test.info("Minutes from json" + minutesFromJson);
        //---------------------------------//

        boolean isFound = helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Top Discovery\"]");
        if(!isFound){
            throw new SkipException("Skipping test: Element not found.");
        }
        test.info("Found: ");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name=\"TRENDSETTER\"]")));
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[contains(@name, \"You found this artist in \")]")));
        String label = elem.getAttribute("label");
        test.info("label = " + label);
        //Verify artist name appears
        Assert.assertTrue(validateEachFrame.isPartOfStringFound(artistName));
        test.info("Verified artist name,"+ artistName + ", appeared");

        //Verify xxx minutes from : You found this artist in May and listened for xxx minutes since!
        String min = helper.formatWithCommas("" + minutesFromJson);
        test.info("Verified listened for," + min);
        assertForTest(test, label.contains(min), "test_S16_01_TrendSetterTopDiscovery");
    }

    @Test(priority = 23, enabled = true)
    public void test_S17_01_YouSpentXXXMinutesWithAlexa () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 23: S17_01 You Spent XXX Minutes With Alexa");

        //----- Json data extraction -----//
        HashMap<String, Object>  map = JasonExtraction.jsonToMap(JasonExtraction.totalStatsObject);
        HashMap<String, Object> alexaMap = (HashMap<String, Object>) map.get("alexa");
        int alexaTotalMinutesFromJson = (int) alexaMap.get("totalMinutes");

        if(alexaTotalMinutesFromJson < 61){
            System.out.println(alexaTotalMinutesFromJson);
            throw new SkipException("Skipping Alexa. minutes : " + alexaTotalMinutesFromJson);
        }

        String minutesSpentFromJson = helper.formatWithCommas("" + alexaTotalMinutesFromJson);
        test.info("Alexa Total Minutes from json: " + minutesSpentFromJson);

        //--- Frame verification ---//
        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Alexa\"]");
      //  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name=\"Alexa\"]")));
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[contains(@name, \"You spent\")]")));
        String minutesSpent = Objects.requireNonNull(elem.getAttribute("label")).split(" ")[2];
        test.info("From AM Alexa minutes " + minutesSpent);

        //Verify xxx minutes from : You found this artist in May and listened for xxx minutes since!
        test.info("Verified spent,"+ alexaTotalMinutesFromJson + ", minutes with Alexa");
     //   skipSetup = true;
        assertForTest(test, minutesSpentFromJson.equals(minutesSpent), "test_S17_01_YouSpentXXXMinutesWithAlexa");
    }

    @Test(priority = 24, dependsOnMethods = {"test_S17_01_YouSpentXXXMinutesWithAlexa"}, enabled = true)
    public void test_S17_02_AlexaPlaysSomeArtist () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 24: S17_02 Alexa Plays Some Artist");
        int minutes = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");
        if(minutes < 60){
            throw new SkipException("Skipping Alexa. minutes : " + minutes);
        }

        //----- Json data extraction -----//
        String alexaTopRequestArtist = JasonExtraction.topAlexaRequestsArray.getJSONObject(0).getString("id");
        test.info("alexa Top Request Artist id: " + alexaTopRequestArtist);
        String artistFromJson = JasonExtraction.artistMap.get(alexaTopRequestArtist);
        test.info("Alexa play : " + artistFromJson + " => From json");
        Thread.sleep(3000); //Do not remove

        //--- Frame verification ---//
        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Alexa\"]");
        Thread.sleep(5000);

        String label;
        WebElement elem;
        helper.goNextFrame("//*[@name=\"lineByLineTextContainer\"]/XCUIElementTypeOther[1]");
        elem = driver.findElement(By.xpath("//*[@name=\"lineByLineTextContainer\"]/XCUIElementTypeOther[1]"));
        label = elem.getAttribute("label");
        Assert.assertEquals(label, "\"Alexa , play");

        WebElement artistWebElement = driver.findElement(By.xpath("//XCUIElementTypeOther[@name=\"lineByLineTextContainer\"]/XCUIElementTypeOther[2]"));
        String artist = artistWebElement.getAttribute("label");
        artist = artist.endsWith("\"") ? artist.substring(0, artist.length() - 1) : artist;
        test.info(artist);
        test.info("Verified: Alexa play, " + artist);
        assertForTest(test, artistFromJson.contains(artist), "test_S17_02_AlexaPlaysSomeArtist");
    }

    @Test(priority = 25, dependsOnMethods = {"test_S17_01_YouSpentXXXMinutesWithAlexa"}, enabled = true)
    public void test_S17_04_YouAskedHerToPlayXXTimes () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 25: S17_04 S17_04 You Asked Her To Play XX Times");
        int minutes = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");
        if(minutes < 60){
            throw new SkipException("Skipping Alexa. minutes : " + minutes);
        }

        //----- Json data extraction -----//
        int countFromJson = JasonExtraction.topAlexaRequestsArray.getJSONObject(0).getInt("count");
        test.info(countFromJson + " times  => from json");

        //--- Frame verification ---//
        helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Alexa\"]");
        Thread.sleep(2000);
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("    //XCUIElementTypeStaticText[contains(@name, \"You asked her (nicely) to play\")]")));
        String label = elem.getAttribute("label");
        test.info(label);
        test.info("verifying " + countFromJson + " times");
        assertForTest(test, label.contains("" + countFromJson), "test_S17_04_YouAskedHerToPlayXXTimes");
    }

    @Test(priority = 26, enabled = true)
    public void test_S19_01_YourTopPodcast () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 26: S19_01 Your Top Podcast");
        int minutes = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");
        if(minutes < 60){
            throw new SkipException("Skipping Podcast. minutes : " + minutes);
        }
        //----- Json data extraction -----//
        String podcastIdFromJson = JasonExtraction.topPodcastsArray.getJSONObject(0).getString("id");
        test.info(podcastIdFromJson + "   => id from json");
        String topPodCastFromJson = JasonExtraction.topPodcastsMap.get(podcastIdFromJson);
        test.info("topPodcast From json: " + topPodCastFromJson);
        int minutesFromJson = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");
        test.info(minutesFromJson + "   => minutes from json");

        //--- Frame verification ---//
        helper.goNextFrame("//XCUIElementTypeImage[@name=\"podcastImage\"]");

        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name, \"You listened for\")]")));
        test.info("Verify " + minutesFromJson + " from json");
        String label = elem.getAttribute("label");
        test.info(label);
        assertForTest(test, label.contains("" + minutesFromJson), "test_S19_01_YourTopPodcast");
    }

    @Test(priority = 27, enabled = true)
    public void test_S20_01_TopPodcastRareCard () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 27: S20_01 Top Podcast rare card");
        int minutes = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");
        if(minutes < 60){
            throw new SkipException("Skipping Alexa. minutes : " + minutes);
        }

        //----- Json data extraction -----//
        String podcastIdFromJson = JasonExtraction.topPodcastsArray.getJSONObject(0).getString("id");
        test.info(podcastIdFromJson + "   => id from json");
        String topPodCastFromJson = JasonExtraction.topPodcastsMap.get(podcastIdFromJson);
        test.info("topPodcast From json: " + topPodCastFromJson);
        int minutesPercentileFromJson = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutesPercentile");
        test.info("minutesPercentile from json: " + minutesPercentileFromJson);

        boolean isFound = helper.goNextFrame("//XCUIElementTypeStaticText[@name=\"Top Podcast\"]");

        if (!isFound) {
            throw new SkipException("Skipping Case");
        }

        //--- Frame verification ---//
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("BIG FAN")));
        test.info("BIG FAN frame found");
        WebElement elem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@name, \"You're a top\")]")));
        //Assert.assertTrue(elem.getAttribute("label").contains("" + minutesPercentileFromJson));
        test.info(elem.getAttribute("label"));
        assertForTest(test, elem.getAttribute("label").contains("" + minutesPercentileFromJson), "test_S20_01_TopPodcastRareCard");
    }

    @Test(priority = 28, enabled = true)//, dependsOnMethods = {"test_S19_01_YourTopPodcast"}
    public void test_S21_01_TopPodcastList () throws InterruptedException {
        test = ExtentTestNGReportListener.getTest();
        test.info("\nCase 28: S21.01 Podcast list");
        //int minutes = JasonExtraction.topPodcastsArray.getJSONObject(0).getInt("minutes");

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        //----- Json data extraction -----//
        List<String> topPodcastsListFromJson = new ArrayList<>();
        // Loop through each podcast JSON object in the JSONArray and extract the ID
        for (int i = 0; i < JasonExtraction.topPodcastsArray.length(); i++) {
            JSONObject podcast = JasonExtraction.topPodcastsArray.getJSONObject(i);
            String artistId = podcast.getString("id");
            System.out.println(artistId);
            topPodcastsListFromJson.add(JasonExtraction.topPodcastsMap.get(artistId));
        }
        test.info("Extracted list from json: " + topPodcastsListFromJson);

        //--- Frame verification ---//
        boolean isFound = helper.goNextFrame("(//XCUIElementTypeStaticText[@name=\"Top\"])[1]");
        if(isFound){
            isFound = helper.goNextFrame("(//XCUIElementTypeStaticText[@name=\"Podcasts\"])[1]");
        }

        if (!isFound) {
            throw new SkipException("Skipping podcast image");
        }

        // Extract the artists names from the current frame and put them in array
        List<String> topArtistsArray = validateEachFrame.getTopPodcasts();
        for(int i = 0; i < topArtistsArray.size(); i++)
        {
            test.info(topArtistsArray.get(i));
        }
        //Assert.assertEquals(topArtistsArray, topPodcastsListFromJson);
        assertForTest(test, topArtistsArray.containsAll(topPodcastsListFromJson), "test_S21_01_TopPodcastList");
    }

//++++++++++++++++++++++++++++ helper func ++++++++++++++++++++++++++++++++++++++//
    public void assertForTest(ExtentTest test, boolean isOK, String testCaseName){
        try {
            Assert.assertTrue(isOK);
            capturePresentScreenShot(test, testCaseName);
        }catch(AssertionError e){
            test.fail("Test failed. Assertion error ");
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, testCaseName);
            System.out.println("Screenshot path: " + screenshotPath);
            test.fail(testCaseName,
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

        }
    }

    public void capturePresentScreenShot(ExtentTest test, String caseName) {
        String screenshotPath = ScreenshotUtils.captureScreenshot(driver, caseName);
        test.info(caseName,
                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
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

    @AfterMethod
    public void tearDown(ITestContext context) throws InterruptedException {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        if (driver != null) {
            driver.quit(); // Close the browser
            Thread.sleep(5000);
        }
    }
    @AfterClass
    public void tearDownAfterClass(ITestContext context) throws InterruptedException {
        if (driver != null) {
            driver.quit(); // Close the browser
        }
    }
}
