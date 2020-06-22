package com.qa.pages;

import com.qa.BaseTest;

import io.appium.java_client.MobileElement;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class LoginPage extends BaseTest{
	
	@AndroidFindBy(accessibility = "test-Username") 
	@iOSXCUITFindBy (id = "test-Username")
	private MobileElement usernameTxtFld;
	
	@AndroidFindBy(accessibility = "test-Password") 
	@iOSXCUITFindBy (id = "test-Password")
	private MobileElement passwordTxtFld;
	
	@AndroidFindBy(accessibility = "test-LOGIN") 
	@iOSXCUITFindBy (id = "test-LOGIN")
	private MobileElement loginBtn;
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView") 
	@iOSXCUITFindBy (xpath = "//XCUIElementTypeOther[@name=\"test-Error message\"]/child::XCUIElementTypeStaticText")
	private MobileElement errTxt;
	  
	 public LoginPage enterUserName(String username) {
		 clear(usernameTxtFld);
		 System.out.println("login user with "+ username);
		 sendKeys(usernameTxtFld, username);
		 return this;
	 }
	 
	 public LoginPage enterPassword(String password) {
		 clear(passwordTxtFld);
		 System.out.println("login pass with: "+ password);
		 sendKeys(passwordTxtFld, password);
		 return this;
	 }
	 
	 public ProductsPage pressLoginBtn() {
		 System.out.println("press login button");
		 click(loginBtn);
		 return new ProductsPage();
	 }
	 
	 public ProductsPage login(String username, String password) {
		 enterUserName(username);
		 enterPassword(password);
		 return pressLoginBtn();
	 }
	 
	 public String getErrTxt() {
		 String err = getText(errTxt);
		 System.out.println("error text is - "+ err);
		 return err;
	 }
	 
}
