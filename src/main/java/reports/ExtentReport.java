package reports;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


public class ExtentReport {
    public static WebDriver driver;
    public static ExtentHtmlReporter htmlReporter;
    public static ExtentReports extent;
    public static ExtentTest test;

    @BeforeTest
    public void extentReportSetup() {
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/ExtentReport.html");
        extent = new ExtentReports();  //create object of ExtentReports
        extent.attachReporter(htmlReporter);

        htmlReporter.config().setDocumentTitle("Automation Report"); // Tittle of Report
        htmlReporter.config().setReportName("Extent Report"); // Name of the report
        htmlReporter.config().setTheme(Theme.DARK);//Default Theme of Report

        // General information releated to application
        extent.setSystemInfo("Application Name", "Xfinity");
        extent.setSystemInfo("User Name", "Ahmed Foysol Hasan");
        extent.setSystemInfo("Envirnoment", "Testing");
    }

    @AfterTest
    public void endReport() {
        extent.flush();
    }

    @BeforeMethod
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/Users/ahmedfhasan/Downloads/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.xfinity.com/");
    }

    @Test
    public void xfinityTitleValidation() {
        test = extent.createTest("Xfinity Title Validation");
        Assert.assertEquals(driver.getTitle(), "Access My Account | Email | Online News | My Xfinity®");
    }

    @Test
    public void xfinityLogoValidation() {
        test = extent.createTest("Xfinity Logo Validation");
        boolean img = driver.findElement(By.className("xc-polaris-logo")).isDisplayed();
        test.createNode("ImagePresentYes");
        Assert.assertTrue(img);
        test.createNode("ImagePresentNo");
        Assert.assertFalse(img);
    }

    @AfterMethod
    public void getResult(ITestResult result) throws Exception {
        if (result.getStatus() == ITestResult.FAILURE) {
            //MarkupHelper is used to display the output in different colors
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));

            //To capture screenshot path and store the path of the screenshot in the string "screenshotPath"
            //We do pass the path captured by this method in to the extent reports using "logger.addScreenCapture" method.

            //	String Scrnshot=TakeScreenshot.captuerScreenshot(driver,"TestCaseFailed");
            String screenshotPath = TakeScreenshot(driver, result.getName());
            //To add it in the extent report

            test.fail("Test Case Failed Snapshot is below " + test.addScreenCaptureFromPath(screenshotPath));


        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped", ExtentColor.ORANGE));
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " Test Case PASSED", ExtentColor.GREEN));
        }
        driver.quit();
    }

    public static String TakeScreenshot(WebDriver driver, String screenshotName) throws IOException {
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        // after execution, you could see a folder "FailedTestsScreenshots" under src folder
        String destination = System.getProperty("user.dir") + "/Screenshots/" + screenshotName + dateName + ".png";
        File finalDestination = new File(destination);
        FileUtils.copyFile(source, finalDestination);
        return destination;
    }
}
