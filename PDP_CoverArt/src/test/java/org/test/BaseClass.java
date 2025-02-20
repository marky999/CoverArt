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
import java.util.Objects;

import static java.lang.Thread.sleep;

public class BaseClass {
    protected static AppiumDriver driver;
    public WebDriverWait wait;
    public Helper helper;
    public PDP_Helper pdpHelper;

    public final boolean skipSetup = false;  // Flag to control if BeforeMethod should run
    public static ITestResult testResult;
    public static ExtentTest test;
    public static  String testPlayList = ConfigReader.getProperty("testPlayList");
    public static boolean isSimulator = Boolean.parseBoolean(ConfigReader.getProperty("isSimulator"));
    public static boolean isEmulator = Boolean.parseBoolean(ConfigReader.getProperty("isEmulator"));
    public static boolean Android = Boolean.parseBoolean(ConfigReader.getProperty("Android"));
    public String tempTestString = "";

    @BeforeSuite
    public void setOS(){
        String locale = ConfigReader.getProperty("locale");
        String platformName = ConfigReader.getProperty("platformName");
        if (platformName.equals("iOS"))
        {
            System.out.println("Setting property to iOS");
            System.setProperty("platformName", "iOS");
           // System.setProperty("locale","en");
        }else{
             testPlayList = "Empty description";
            System.out.println("Setting property to Android");
            Android = true;
        }
        if(locale.equals("ja")){
            System.setProperty("locale","ja");
        }
    }

    @BeforeSuite
    public void setup() throws MalformedURLException, InterruptedException {
        driver = DriverManager.getDriver();
        sleep(2000);
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
