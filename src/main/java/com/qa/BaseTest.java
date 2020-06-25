package com.qa;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.screenrecording.CanRecordScreen;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
 
	protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
	protected static ThreadLocal <Properties> props = new ThreadLocal<Properties>();
	protected static ThreadLocal<HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
	protected static ThreadLocal<String> platform = new ThreadLocal<String>();
	protected static ThreadLocal<String> dateTime = new ThreadLocal<String>();
	protected static ThreadLocal <String> deviceName = new ThreadLocal<String>();
	
	TestUtils utils;
	
	public AppiumDriver getDriver() {
		  return driver.get();
	  }
	  
	  public void setDriver(AppiumDriver driver2) {
		  driver.set(driver2);
	  }
	  
	  public Properties getProps() {
		  return props.get();
	  }
	  
	  public void setProps(Properties props2) {
		  props.set(props2);
	  }
	  
	  public HashMap<String, String> getStrings() {
		  return strings.get();
	  }
	  
	  public void setStrings(HashMap<String, String> strings2) {
		  strings.set(strings2);
	  }
	  
	  public String getPlatform() {
		  return platform.get();
	  }
	  
	  public void setPlatform(String platform2) {
		  platform.set(platform2);
	  }
	  
	  public String getDateTime() {
		  return dateTime.get();
	  }
	  
	  public void setDateTime(String dateTime2) {
		  dateTime.set(dateTime2);
	  }
	  
	  public String getDeviceName() {
		  return deviceName.get();
	  }
	  
	  public void setDeviceName(String deviceName2) {
		  deviceName.set(deviceName2);
	  }
	  
	  
	public BaseTest() {
		
		PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
	}
	
	
	@BeforeMethod
	public void beforeMethod() {
		((CanRecordScreen) getDriver()).startRecordingScreen();
	}
	
	@AfterMethod
	public synchronized void afterMethod(ITestResult result) throws Exception {
		String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();
		
		Map <String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();		
		String dirPath = "videos" + File.separator + params.get("platformName") + "_" + params.get("deviceName") 
		+ File.separator + getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName();
		
		File videoDir = new File(dirPath);
		
		synchronized(videoDir){
			if(!videoDir.exists()) {
				videoDir.mkdirs();
			}	
		}
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
			stream.write(Base64.decodeBase64(media));
			stream.close();
			utils.log().info("video path: " + videoDir + File.separator + result.getName() + ".mp4");
		} catch (Exception e) {
			//utils.log().error("error during video capture" + e.toString());
		} finally {
			if(stream != null) {
				stream.close();
			}
		}		
	}
	
	 @Parameters({"emulator", "platformName", "udid", "deviceName", "systemPort", 
		  "chromeDriverPort", "wdaLocalPort", "webkitDebugProxyPort"})	
  @BeforeTest
  public void beforeTest(@Optional("androidOnly")String emulator, String platformName, String udid, String deviceName, 
		  @Optional("androidOnly")String systemPort, @Optional("androidOnly")String chromeDriverPort, 
		  @Optional("iOSOnly")String wdaLocalPort, @Optional("iOSOnly")String webkitDebugProxyPort) throws Exception {
	  
	  utils = new TestUtils();
	  setDateTime(utils.dateTime());
	  setPlatform(platformName);
	  setDeviceName(deviceName);
	  URL url;
	  InputStream InputStream = null;
	  InputStream stringsis = null;
	  Properties props = new Properties();
	  AppiumDriver driver;
	  
	  try {
		  props = new Properties();
		  String propFileName= "config.properties";
		  String xmlFileName = "strings/strings.xml";
		  
		  InputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		  props.load(InputStream);
		  setProps(props);
		  
		  stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);
		  setStrings(utils.parseStringXML(stringsis));
		  
		  DesiredCapabilities cap = new DesiredCapabilities();
		  cap.setCapability("platformName", platformName);
		  cap.setCapability("deviceName", deviceName);
		  cap.setCapability("udid", udid);
		  url = new URL(props.getProperty("appiumURL") + "4723/wd/hub");
		  
		  switch (platformName) {
		  
		  case "Android":
			  
			  cap.setCapability("automationName", props.getProperty("androidAutomationName"));
			  cap.setCapability("appPackage", props.getProperty("androidAppPackage"));
			  cap.setCapability("appActivity", props.getProperty("androidAppActivity"));
			  if(emulator.equalsIgnoreCase("true")) {
				  cap.setCapability("AVD", deviceName); 
			  } 
			  cap.setCapability("systemPort", systemPort);
			  cap.setCapability("chromeDriverPort", chromeDriverPort);
			  String androidAppUrl = getClass().getResource(props.getProperty("androidAppLocation")).getFile();
			  System.out.println("appUrl is: "+ androidAppUrl);
			  cap.setCapability("app", androidAppUrl);
			  
			  driver = new AndroidDriver(url, cap);
			  break;
			  
		  case "iOS":
			  
			  cap.setCapability("automationName", props.getProperty("iOSAutomationName"));
			  String iOSAppUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
			  cap.setCapability("bundleId", props.getProperty("iOSBundleId"));
			  System.err.println("appUrl is: "+ iOSAppUrl);
			  cap.setCapability("wdaLocalPort", wdaLocalPort);
			  cap.setCapability("webkitDebugProxyPort", webkitDebugProxyPort);
			  //cap.setCapability("app", iOSAppUrl);
			  
			  driver = new IOSDriver(url, cap);
			  break;
			  
			default:
				throw new Exception("Invalid platform!... "+ platformName);
		
		}
		setDriver(driver);
		  
	} catch (Exception e) {
		e.printStackTrace();
		throw e;
	}finally {
		if(InputStream != null) {
			InputStream.close();
		}
		if(InputStream != null) {
			stringsis.close();
		}
	}
	  
  }
  
  
	 public void waitForVisibility(MobileElement e) {
		  WebDriverWait wait = new WebDriverWait(getDriver(), TestUtils.WAIT);
		  wait.until(ExpectedConditions.visibilityOf(e));
	  }
	  
	  public void waitForVisibility(WebElement e){
		  Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
		  .withTimeout(Duration.ofSeconds(30))
		  .pollingEvery(Duration.ofSeconds(5))
		  .ignoring(NoSuchElementException.class);
		  
		  wait.until(ExpectedConditions.visibilityOf(e));
		  }
	  
	  public void clear(MobileElement e) {
		  waitForVisibility(e);
		  e.clear();
	  }
	  
	  public void click(MobileElement e) {
		  waitForVisibility(e);
		  e.click();
	  }
	  
	  public void click(MobileElement e, String msg) {
		  waitForVisibility(e);
		  utils.log().info(msg);
		  ExtentReport.getTest().log(Status.INFO, msg);
		  e.click();
	  }
	  
	  public void sendKeys(MobileElement e, String txt) {
		  waitForVisibility(e);
		  e.sendKeys(txt);
	  }
	  
	  public void sendKeys(MobileElement e, String txt, String msg) {
		  waitForVisibility(e);
		  utils.log().info(msg);
		  ExtentReport.getTest().log(Status.INFO, msg);
		  e.sendKeys(txt);
	  }
	  
	  public String getAttribute(MobileElement e, String attribute) {
		  waitForVisibility(e);
		  return e.getAttribute(attribute);
	  }
	  
	  public String getText(MobileElement e) {
		  
		  switch(getPlatform()) {
		  case "Android":
			  return getAttribute(e, "text");
		  case "iOS":
			  return getAttribute(e, "label");
		  }
		  return null;
	  }
	  
	  public void closeApp() {
		  ((InteractsWithApps) getDriver()).closeApp();
	  }
	  
	  public void launchApp() {
		  ((InteractsWithApps) getDriver()).launchApp();
	  }
	  
	  public MobileElement scrollToElement() {	  
			return (MobileElement) ((FindsByAndroidUIAutomator) getDriver()).findElementByAndroidUIAutomator(
					"new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
							+ "new UiSelector().description(\"test-Price\"));");
	  }
	  
	  public void iOSScrollToElement() {
		  RemoteWebElement element = (RemoteWebElement)getDriver().findElement(By.name("test-ADD TO CART"));
		  String elementID = element.getId();
		  HashMap<String, String> scrollObject = new HashMap<String, String>();
		  scrollObject.put("element", elementID);
//		  scrollObject.put("direction", "down");
//		  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//		  scrollObject.put("name", "test-ADD TO CART");
		  scrollObject.put("toVisible", "sdfnjksdnfkld");
		  getDriver().executeScript("mobile:scroll", scrollObject);
	  }


  @AfterTest
  public void afterTest() {
	  getDriver().quit();
  }

}
