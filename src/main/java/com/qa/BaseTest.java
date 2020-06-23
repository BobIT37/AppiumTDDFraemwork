package com.qa;

import com.google.common.base.CaseFormat;
import com.qa.utils.TestUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;

public class BaseTest {
 
	protected static AppiumDriver driver;
	protected static Properties props;
	protected static HashMap<String, String> strings = new HashMap<String, String>();
	protected static String platform;
	protected static String dateTime;
	InputStream InputStream;
	InputStream stringsis;
	TestUtils utils;
	
  
	public BaseTest() {
		
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
  @Parameters({"emulator", "platformName", "platformVersion", "udid", "deviceName"})	
  @BeforeTest
  public void beforeTest(String emulator, String platformName, String platformVersion, String udid, String deviceName) throws Exception {
	  
	  utils = new TestUtils();
	  dateTime = utils.getDateTime();
	  platform = platformName;
	  URL url;
	  
	  try {
		  props = new Properties();
		  String propFileName= "config.properties";
		  String xmlFileName = "strings/strings.xml";
		  
		  InputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		  props.load(InputStream);
		  
		  stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);
		  strings = utils.parseStringXML(stringsis);
		  
		  DesiredCapabilities cap = new DesiredCapabilities();
		  cap.setCapability("platformName", platformName);
		  cap.setCapability("deviceName", deviceName);
		  
		  switch (platformName) {
		  
		  case "Android":
			  
			  cap.setCapability("automationName", props.getProperty("androidAutomationName"));
			  cap.setCapability("appPackage", props.getProperty("androidAppPackage"));
			  cap.setCapability("appActivity", props.getProperty("androidAppActivity"));
			  if(emulator.equalsIgnoreCase("true")) {
				  cap.setCapability("platformVersion", platformVersion);
				  cap.setCapability("AVD", deviceName); 
			  } else {
				  cap.setCapability("udid", udid); 
			  }
			  String androidAppUrl = getClass().getResource(props.getProperty("androidAppLocation")).getFile();
			  System.out.println("appUrl is: "+ androidAppUrl);
			  cap.setCapability("app", androidAppUrl);

			  url = new URL(props.getProperty("appiumURL"));
			  
			  driver = new AndroidDriver(url, cap);
			  break;
			  
		  case "iOS":
			  
			  cap.setCapability("automationName", props.getProperty("iOSAutomationName"));
			  cap.setCapability("platformVersion", platformVersion);
			  String iOSAppUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
			  cap.setCapability("bundleId", props.getProperty("iOSBundleId"));
			  System.err.println("appUrl is: "+ iOSAppUrl);
			  //cap.setCapability("app", iOSAppUrl);

			  
			  url = new URL(props.getProperty("appiumURL"));
			  
			  driver = new IOSDriver(url, cap);
			  break;
			  
			default:
				throw new Exception("Invalid platform!... "+ platformName);
		
		}
		 
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
  
  public AppiumDriver getDriver() {
	  return driver;
  }
  
  public String getDateTime() {
	  return dateTime;
  }
  
  public void waitForVisibility(MobileElement e) {
	  
	  WebDriverWait wait = new WebDriverWait(driver, TestUtils.WAIT);
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
  
  public void sendKeys(MobileElement e, String txt) {
	  waitForVisibility(e);
	  e.sendKeys(txt);
  }
  
public String getAttribute(MobileElement e, String attribute) {
	  
	  waitForVisibility(e);
	  return e.getAttribute(attribute);
  }
  
  public String getText(MobileElement e) {
	  
	  switch(platform) {
	  case "Android":
		  return getAttribute(e, "text");
	  case "iOS":
		  return getAttribute(e, "label");
	  }
	  return null;
  }
  
  public void closeApp() {
	  ((InteractsWithApps) driver).closeApp();
  }
  
  public void launchApp() {
	  ((InteractsWithApps) driver).launchApp();
  }
  
  public MobileElement scrollToElement() {	  
	  return (MobileElement) ((FindsByAndroidUIAutomator) driver).findElementByAndroidUIAutomator(
				"new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
						+ "new UiSelector().description(\"test-Price\"));");
}
  
  public void iOSScrollToElement() {
	  RemoteWebElement element = (RemoteWebElement)driver.findElement(By.name("test-ADD TO CART"));
	  String elementID = element.getId();
	  HashMap<String, String> scrollObject = new HashMap<String, String>();
	  scrollObject.put("element", elementID);
//	  scrollObject.put("direction", "down");
//	  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//	  scrollObject.put("name", "test-ADD TO CART");
	  scrollObject.put("toVisible", "sdfnjksdnfkld");
	  driver.executeScript("mobile:scroll", scrollObject);
  }
  

  @AfterTest
  public void afterTest() {
	  driver.quit();
  }

}
