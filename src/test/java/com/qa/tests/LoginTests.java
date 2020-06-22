package com.qa.tests;

import org.testng.annotations.Test;


import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import java.io.*;

import java.lang.reflect.Method;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

public class LoginTests extends BaseTest{
	
	  LoginPage loginPage;
	  ProductsPage productsPage;
	  InputStream datais;
	  JSONObject loginUser;
	
	  @BeforeClass
	  public void beforeClass() throws Exception {
		  try {
			  String dataFileName = "data/loginUsers.json";
			  datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
			  JSONTokener tokener = new JSONTokener(datais);
			  loginUser = new JSONObject(tokener);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(datais != null) {
				datais.close();
			}
		}
		closeApp();
		launchApp();    
	  }

	  @AfterClass
	  public void afterClass() {
	  }	
	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  loginPage = new LoginPage();
		  System.out.println("\n"+ "******* starting test: "+ m.getName()+ " ************"+ "\n");
	  }

	  @AfterMethod
	  public void afterMethod() {
	  }
	  @Test
	  public void invalidUserName() {
			  loginPage.enterUserName(loginUser.getJSONObject("invalidUser").getString("username"));
			  loginPage.enterPassword(loginUser.getJSONObject("invalidUser").getString("password"));
			  loginPage.pressLoginBtn();
			  
			  String actualErrTxt = loginPage.getErrTxt();
			  String expectedErrTxt = strings.get("err_invalid_username_or_password");
			  System.out.println("actual error text "+ actualErrTxt+ "\n"+ "expected error txt - "+ expectedErrTxt);
			  
			  Assert.assertEquals(actualErrTxt, expectedErrTxt);  
	  }
	  
	  @Test
	  public void invalidPassword() {
		  loginPage.enterUserName(loginUser.getJSONObject("invalidPassword").getString("username"));
		  loginPage.enterPassword(loginUser.getJSONObject("invalidPassword").getString("password"));
		  loginPage.pressLoginBtn();
		  
		  String actualErrTxt = loginPage.getErrTxt();
		  String expectedErrTxt = strings.get("err_invalid_username_or_password");
		  System.out.println("actual error text "+ actualErrTxt+ "\n"+ "expected error txt - "+ expectedErrTxt);
		  
		  Assert.assertEquals(actualErrTxt, expectedErrTxt);
	  }
	  
	  @Test
	  public void succesfullLogin() {
		  loginPage.enterUserName(loginUser.getJSONObject("validUser").getString("username"));
		  loginPage.enterPassword(loginUser.getJSONObject("validUser").getString("password"));
		  productsPage = loginPage.pressLoginBtn();
		  
		  String actualProductTitle = productsPage.getTitle();
		  String expectedProductTitle = strings.get("product_title");
		  System.out.println("actual title - "+ actualProductTitle + "\n"+ "expected title - "+ expectedProductTitle);
		  
		  Assert.assertEquals(actualProductTitle, expectedProductTitle);
	 
	  }
 

  

}
