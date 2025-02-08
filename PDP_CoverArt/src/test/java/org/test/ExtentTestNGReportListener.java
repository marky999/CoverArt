package org.test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.test.ExtentReportsManager; // Adjust package name as needed

import  org.test.PDPCoverArtTest;

import java.net.MalformedURLException;

import static org.test.PDPCoverArtTest.test;

public class ExtentTestNGReportListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
    PDPCoverArtTest pdp_cover_art_test = new PDPCoverArtTest();

    public ExtentTestNGReportListener() throws MalformedURLException {
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentReportsManager.getExtentReports()
                .createTest(result.getMethod().getMethodName());
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        try {
            PDP_Helper.CapturePresentScreenShot(test, result.getTestName());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        testThread.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            PDP_Helper.CapturePresentScreenShot(test, result.getTestName());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        testThread.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testThread.get().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportsManager.getExtentReports().flush();
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }


}
