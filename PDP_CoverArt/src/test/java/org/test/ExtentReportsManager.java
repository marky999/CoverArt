package org.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportsManager {

    private static ExtentReports extent;

    // Method to return a singleton instance of ExtentReports
    public static ExtentReports getExtentReports() {
        if (extent == null) {
            // Create the HTML Reporter
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter("./extent-report.html");

            // Create the ExtentReports instance
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);

            // Optionally set system information
            extent.setSystemInfo("Environment", "Test");
            extent.setSystemInfo("Tester", "Your Name");
        }
        return extent;
    }
}
