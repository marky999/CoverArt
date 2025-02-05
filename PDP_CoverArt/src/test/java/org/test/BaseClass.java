package org.test;

import com.aventstack.extentreports.ExtentTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import static java.lang.Thread.sleep;

public class BaseClass {
    public static AppiumDriver driver;
    public WebDriverWait wait;
    public Helper helper;
    public PDP_Helper pdpHelper;

    // private LocatorProvider locatorProvider;
    public final boolean skipSetup = false;  // Flag to control if BeforeMethod should run
    public ITestResult testResult;
    public static ExtentTest test;
    public final boolean executeDescriptionCases = false;/////////////   DEBUG PURPOSE //////////
    public final boolean executeUpload = true;
    public boolean isSimulator = false;
    public boolean isEmulator = true;
    public final boolean executeAll = false;
    public static final String testPlayList = "Test playlist";
    public String tempTestString = "";
    public static boolean Android = false;
    public  XCUITestOptions options;

    @BeforeSuite
    public void setOS(){
        System.setProperty("platformName", "iOS");
    }

    @BeforeClass
    public void cleanUpScreenshots() throws MalformedURLException {
        String screenshotsPath = "./screenshots"; // Adjust path if needed
        PDP_Helper.deleteScreenshots(screenshotsPath);
        System.out.println("Deleted all files in the screenshots folder.");

        options = new XCUITestOptions();
        String platform = System.getProperty("platformName", "Android");
        platform = platform.trim();
        if (Objects.equals(platform, "iOS")) {
            if (isSimulator) {
                System.out.println("Test against iPhone Simulator");
                options
                        .setPlatformVersion("18.2")
                        .setDeviceName("iPhone 16 Pro")
                        .setUdid("BBD30097-6EBA-4326-ADCD-96BF36DDED76")
                        .setApp("com.amazon.mp3.CloudPlayer") // or the app path
                        .setAutomationName("XCUITest").noReset().setForceAppLaunch(true)
                        .setShowXcodeLog(true);
                driver = new IOSDriver(new URL("http://127.0.0.1:4723"), options);
            } else {
                System.out.println("Test against iPhone Physical");
                options
                        .setPlatformVersion("18.0.1")
                        .setDeviceName("iPhone_13 Pro")
                        .setUdid("00008110-001C45D60CC0401E")
                        .setApp("com.amazon.mp3.CloudPlayer") // or the app path
                        .setAutomationName("XCUITest")
                        .setShowXcodeLog(true);
                driver = new IOSDriver(new URL("http://127.0.0.1:4723"), options);
            }
        } else if (Objects.equals(platform, "Android")) {
            if (isEmulator) {
                System.out.println("Test against Emulator");
                UiAutomator2Options androidOptions = new UiAutomator2Options()
                        .setPlatformName("Android")
                        .setDeviceName("sdk_gphone64_arm64")  // Change this to your emulator name
                        .setPlatformVersion("15")  // Adjust according to your emulator version
                        .setAppPackage("com.amazon.mp3")  // Correct package
                        .setAppActivity(".activity.MusicHomeActivity")
                        .setAutomationName("UiAutomator2")
                        .setUdid("emulator-5554")
                        .noReset();
                System.setProperty("platformName", "Android");
                driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), androidOptions);
                Android = true;
            }
        }
    }

    @AfterClass
    public void tearDown(ITestContext context) throws InterruptedException {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        if (driver != null) {
            driver.quit(); // Close the browser
            sleep(2000);
        }
    }
}
