package com.qa.tests;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;

public class ProductTests extends BaseTest{
	
	  LoginPage loginPage;
	  ProductsPage productsPage;
	  SettingsPage settingsPage;
	  ProductDetailsPage productDetailsPage;
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
		  
		  productsPage = loginPage.login(loginUser.getJSONObject("validUser").getString("username"), 
				  loginUser.getJSONObject("validUser").getString("password"));
	  }

	  @AfterMethod
	  public void afterMethod() {
		  
		  settingsPage = productsPage.pressSettingsBtn();
		  loginPage = settingsPage.pressLogoutBtn();
	  }
	  
	  @Test
	  public void validateProductOnProductPage() {
		  
		  SoftAssert sa = new SoftAssert();
		  
		  String SLBTitle = productsPage.getSLBTitle();
		  sa.assertEquals(SLBTitle, strings.get("products_page_slb_title"));
		  
		  String SLBPrice = productsPage.getSLBPrice();
		  sa.assertEquals(SLBPrice, strings.get("products_page_slb_price"));
		  
		  sa.assertAll(); 
	  }
	  
	  @Test
	  public void validateProductOnProductDetailsPage() {
		  
		  SoftAssert sa = new SoftAssert();
		  
		  productDetailsPage = productsPage.pressSLBTitle();
		  
		  String SLBTitle = productDetailsPage.getSLBTitle();
		  sa.assertEquals(SLBTitle, strings.get("product_details_page_slb_title"));
		  
		  String SLBTxt = productDetailsPage.getSLBTxt();
		  sa.assertEquals(SLBTxt, strings.get("product_details_page_slb_txt"));
		  
//		  String SLBPrice = productDetailsPage.scrollToSLBPriceAndGetSLBPrice();
//		  sa.assertEquals(SLBPrice, strings.get("product_details_page_slb_price"));
		  
		  productDetailsPage.scrollPage();
		  sa.assertTrue(productDetailsPage.isAddToCartBtnDisplayed());
		  
		  productsPage = productDetailsPage.pressBackToProductBtn();
		  
		  sa.assertAll(); 
	  }
	

}
