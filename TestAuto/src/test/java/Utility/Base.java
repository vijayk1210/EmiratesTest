package Utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class Base {
    public static ExtentReports extent;
    public static ExtentTest test;
    public static WebDriver driver;
   
   
    private void setDriver(String browserType, String appURL) {
        switch(browserType){
     	  case "chrome":
     		 ExtentTestManager.getTest().log(LogStatus.INFO, "Launching Chrome browser"); 
     	     System.setProperty("webdriver.chrome.driver","chromedriver.exe");
     	     driver = new ChromeDriver();
     	     break;
     	  
     	  case "firefox":
     		ExtentTestManager.getTest().log(LogStatus.INFO, "Launching Firefox browser"); 
      	System.setProperty("webdriver.gecko.driver","geckodriver.exe");
  		driver = new FirefoxDriver();
  		
         	  
          }
        driver.manage().window().maximize();		   
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(appURL);

				
		
		
	}

    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }
    
    @BeforeMethod
	public void initializeTestBaseSetup(Method method) {
		try {
			 ExtentTestManager.startTest(method.getName());
			setDriver(util.getConfigValue("browserVal"), util.getConfigValue("appUrl"));

		} catch (Exception e) {
			System.out.println("Error....." + e.getStackTrace());
		}
	}
    
   
   

    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {
    	  ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));  // new
          ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));  // new

          for (String group : result.getMethod().getGroups()) {
              ExtentTestManager.getTest().assignCategory(group);  // new
          }

          if (result.getStatus() == 1) {
              ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");  // new
          } else if (result.getStatus() == 2) {
                String path = util.getscreenshot(driver, result.getName());
              	
                String image = ExtentTestManager.getTest().addScreenCapture(path);
               	ExtentTestManager.getTest().log(LogStatus.FAIL,getStackTrace(result.getThrowable()));
               	ExtentTestManager.getTest().log(LogStatus.FAIL,image);
               	driver.get(path);
              
          	
          } else if (result.getStatus() == 3) {
              ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");  // new
          }

          ExtentTestManager.endTest();  // new
     
          extent.flush();
        driver.quit();
    }

    @AfterSuite
    public void generateReport() {
        extent.close();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
