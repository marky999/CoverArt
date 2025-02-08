package org.test;

import com.aventstack.extentreports.ExtentTest;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import java.net.MalformedURLException;
import static java.lang.Thread.sleep;

public class BaseClass {
    protected static AppiumDriver driver;
    public WebDriverWait wait;
    public Helper helper;
    public PDP_Helper pdpHelper;

    public final boolean skipSetup = false;  // Flag to control if BeforeMethod should run
    public static ITestResult testResult;
    public static ExtentTest test;
    public static final boolean executeDescriptionCases = true;/////////////   DEBUG PURPOSE //////////
    public static final boolean executeUpload = false;// = true;
    public static final boolean executeAll = false;
    public static  String testPlayList = "Test playlist";
    public static boolean isSimulator = false;
    public static boolean isEmulator = true;
    public String tempTestString = "";
    public static boolean Android = false;

    @BeforeSuite
    public void setOS(){
        if (System.getProperty("platformName") == null)
        {
            System.out.println("Setting property to iOS");
            System.setProperty("platformName", "iOS");
        }else{
             testPlayList = "Empty description";
            System.out.println("Setting property to Android");
           // System.setProperty("platformName", "Android");
            Android = true;
        }
    }

    @BeforeClass
    public void setup() throws MalformedURLException {
        driver = DriverManager.getDriver();
        System.out.println("System property platformName: " + System.getProperty("platformName"));

        String screenshotsPath = "./screenshots"; // Adjust path if needed
        PDP_Helper.deleteScreenshots(screenshotsPath);
        System.out.println("Deleted all files in the screenshots folder.");
    }

    @AfterClass
    public void tearDown(ITestContext context) throws InterruptedException, MalformedURLException {
        if (skipSetup) {
            return;  // Skip the setup code if the flag is true
        }

        if (driver != null) {
            DriverManager.quitDriver();
            sleep(2000);
        }
    }
}
