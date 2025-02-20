package org.test;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import static org.test.BaseClass.isEmulator;
import static org.test.BaseClass.isSimulator;

public class DriverManager {
    private static IOSDriver driver;
    private DriverManager() { }  // Private constructor to prevent instantiation
    public static XCUITestOptions options;
    public static UiAutomator2Options androidOptions;
    public static IOSDriver getDriver() throws MalformedURLException {

        String platform = System.getProperty("platformName", "iOS").trim();
        if (Objects.equals(platform, "iOS")) {
            options = new XCUITestOptions();
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
                        .setPlatformVersion("18.3")
                        .setDeviceName("iPhone_16 Pro")
                        .setUdid("00008140-001415481422801C") //00008110-001C45D60CC0401E
                        .setApp("com.amazon.mp3.CloudPlayer") // or the app path
                        .setAutomationName("XCUITest")
                        .setShowXcodeLog(true);
                driver = new IOSDriver(new URL("http://127.0.0.1:4723"), options);
            }
        } else if (Objects.equals(platform, "Android")) {
            if (isEmulator) {
                System.out.println("Test against Emulator");
                androidOptions = new UiAutomator2Options()
                        .setPlatformName("Android")
                        .setDeviceName("sdk_gphone64_arm64")  // Change this to your emulator name
                        .setPlatformVersion("15")  // Adjust according to your emulator version
                        .setAppPackage("com.amazon.mp3")  // Correct package
                        .setAppActivity(".activity.MusicHomeActivity")
                        .setAutomationName("UiAutomator2")
                        .setUdid("emulator-5554")
                        .noReset();
                driver = new IOSDriver(new URL("http://127.0.0.1:6000"), androidOptions);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Set implicit wait
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
