/**************************************************
 *File Name:-ConnectionsKeywords.java
 *This class is implemented as declaring this class instantiate the driver class object and 
 *contains methods which are specific to ConnectionsKeywords modules,
 ********************************************************/
package com.gm.keywords;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static java.time.Duration.ofMillis;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gm.core.Keywords;
import com.gm.core.TestDriver;
import com.google.common.collect.ImmutableMap;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.StartsActivity;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.offset.PointOption;

import java.lang.Object;

public class ConnectionsKeywords extends Keywords {

	public static String options = "Options";
	public static String connected = "Connected";
	public static String pass = "Pass";
	public static String fail = "Fail";

	public ConnectionsKeywords() throws MalformedURLException, InterruptedException {
		super();

	}

	/**********************************************************
	 * @function disconnectPhone() Disconnect the connected Phone.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data text(Options,Disconnect Phone,Disconnect Phone)
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/

	public String disconnectPhone(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			Thread.sleep(3000);
			String datas[] = data.split(",");
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				List<MobileElement> list = driver.findElementsByXPath(TestDriver.UIMap.getProperty(object));// Connections_Phones_AvailablePhone_Xpath
				if (list.get(0).getText().equalsIgnoreCase(connected)) {

					driver.findElementByXPath("//*[@text='" + datas[0] + "']").click();
					Thread.sleep(2000);
					driver.findElementByXPath("//*[@text='" + datas[1] + "']").click();
					Thread.sleep(3000);
					driver.findElementByXPath("//*[@text='" + datas[2] + "']").click();
					Thread.sleep(5000);
					status = "Pass";
				} else {
					status = "Pass";
				}
			}

			else {
				List<MobileElement> list = driver.findElementsById(TestDriver.UIMap.getProperty(object));// Connections_Phones_AvailablePhone_Xpath
				if (list.get(0).getText().equalsIgnoreCase(connected)) {

					driver.findElementByXPath("//*[@text='" + datas[0] + "']").click();
					Thread.sleep(2000);
					driver.findElementByXPath("//*[@text='" + datas[1] + "']").click();
					Thread.sleep(3000);
					driver.findElementByXPath("//*[@text='" + datas[2] + "']").click();
					Thread.sleep(5000);

					status = "Pass";
				} else {
					status = "Pass";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}

	/**********************************************************
	 * @function disconnectWiFiNetwork() Disconnect the connected wifi network.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data text(Options,Disconnect Network)
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/

	public String disconnectWiFiNetwork(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			Thread.sleep(3000);
			String data_array[] = data.split(",");
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				List<MobileElement> list = driver.findElementsByXPath(TestDriver.UIMap.getProperty(object));// Connections_Phones_AvailablePhone_Xpath
				if (list.get(0).getText().equalsIgnoreCase(connected)) {

					driver.findElementByXPath("//*[@text='" + data_array[0] + "']").click();
					Thread.sleep(2000);
					driver.findElementByXPath("//*[@text='" + data_array[1] + "']").click();
					Thread.sleep(5000);
					// We are doing the click action in this keyword no verification point
					status = "Pass";
				} else {
					// Wi fi network is already disconnected state
					status = "Pass";
				}
			}

			else {
				List<MobileElement> list = driver.findElementsById(TestDriver.UIMap.getProperty(object));// Connections_Phones_AvailablePhone_Xpath
				if (list.get(0).getText().equalsIgnoreCase(connected)) {

					driver.findElementByXPath("//*[@text='" + data_array[0] + "']").click();
					Thread.sleep(2000);
					driver.findElementByXPath("//*[@text='" + data_array[1] + "']").click();

					Thread.sleep(5000);

					status = "Pass";
				} else {
					// Wifi network is already disconnected state
					status = "Pass";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;

	}

	/**********************************************************
	 * @function connectWifi() Connect the Wifi Network.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data text(Network Name,Password)
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/
	public String connectWiFi(String object, String data) throws InterruptedException {

		String status = "Fail";

		try {
			if (!data.isEmpty()) {
				String input[] = data.split(","); // Wifi Network name,wifi password
				String s[] = object.split(",");
				clickElement(s[0], data);
				waitForElement(s[1], data);
				clickElement(s[1], data);
				waitForElement(s[2], data);
				enterInputData(s[2], input[0]);
				waitForElement(s[3], data);
				status = enterInputData(s[3], input[1]);
			} else {
				TestDriver.logger.error("Data should not be Null or Empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

		/*****************************************************************************************
	 * @function deleteAllCards()It will Forget Phone all available devices in
	 *           Phones Screen.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data Xpath to locate the No Phones Connected on the current
	 *               screen
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *****************************************************************************************/
	public String deleteAllCards(String object, String data) throws InterruptedException {
		String status = "Fail";
		List<MobileElement> cardsSize;
		String[] obj = object.split(",");
		cardsSize = driver.findElementsByXPath(TestDriver.UIMap.getProperty(obj[0]));// options length
		System.out.println("options lenght " + cardsSize.size());
		int length = cardsSize.size();// 5
		System.out.println(length);// 5

		while (cardsSize.size() != 0) {
			System.out.println(cardsSize.size());
			for (int index = 0; index < obj.length; index++) {
				if (obj[index].equalsIgnoreCase("wait")) {
					Thread.sleep(1000);
				} else {
					Thread.sleep(1000);
					String res = clickElement(obj[index], data);
					if (res.equals("Pass")) {
						TestDriver.logger.info("Clicked on " + obj[index] + " Element");

					} else {
						status = "Fail";
						TestDriver.logger.error("Unable to click on " + obj[index] + " Element");
						break;
					}
				}
			}
			Thread.sleep(1000);
			cardsSize = driver.findElementsByXPath(TestDriver.UIMap.getProperty(obj[0]));
			System.out.println(cardsSize.size());

		}
		System.out.println("After while loop " + cardsSize.size());
		if (cardsSize.size() == 0) {// Current Cards size
			String noPhoneElement = data;
			String res = isElementExist(noPhoneElement, data);
			TestDriver.logger.info("No Phone Connected is Displayed");

			if (res.equalsIgnoreCase("Pass")) {
				status = "Pass";
			} else {
				TestDriver.logger.error("No Phone Connected screen is not Displayed : Methode Name : deleteAllCards");
			}
			TestDriver.logger.info("No Phone Connected is Displayed");
			status = "Pass";
		}

		return status;
	}

	/*****************************************************************************************
	 * @function verifySecondrayText()It will Forget Phone all available devices in
	 *           Phones Screen.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data Xpath to locate the No Phones Connected on the current
	 *               screen
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *****************************************************************************************/
	public String verifySecondrayText(String object, String data) {
		String status = "Fail";
		System.out.println(saveText);
		String dynamic_data;
		String input_data[] = data.split(",");
		int lenght = input_data.length;
		System.out.println("Data lenghth =" + lenght);
		String secoundryText = "Connecting a new device will disconnect " + saveText
				+ ". Active calls may be transferred to the handset.";
		String name = "Wi-Fi Network Forgotten " + saveText;
		String second_text = saveText + " will be removed from your Wi-Fi Networks list and no longer automatically connect.";							
		String disconect = "Active calls on " + saveText + " may be moved to handset";
		String forget_Phone = saveText + " will be removed from your phones list and no longer automatically connect.";
		try {
			if (lenght == 2) {
				String pop_data = "Connecting " + TestDriver.CONFIG.getProperty(input_data[0]) + " will disconnect "
						+ TestDriver.CONFIG.getProperty(input_data[1])
						+ ". Active calls may be transferred to the handset.";
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					dynamic_data = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
					if (dynamic_data.equalsIgnoreCase(pop_data)) {
						status = "Pass";
					}
				} else {
					dynamic_data = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
					if (dynamic_data.equalsIgnoreCase(pop_data)) {
						status = "Pass";
					}
				}
			} else {

				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					dynamic_data = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
					System.out.println(dynamic_data);
					if (dynamic_data.equalsIgnoreCase(secoundryText) || dynamic_data.equalsIgnoreCase(name)
							|| dynamic_data.equalsIgnoreCase(second_text) || dynamic_data.equalsIgnoreCase(forget_Phone)
							|| dynamic_data.equalsIgnoreCase(disconect)) {
						status = "Pass";
					}
				} else {
					dynamic_data = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
					System.out.println(dynamic_data);
					System.out.println(second_text);
					if (dynamic_data.equalsIgnoreCase(secoundryText) || dynamic_data.equalsIgnoreCase(name)
							|| dynamic_data.equalsIgnoreCase(second_text) || dynamic_data.equalsIgnoreCase(forget_Phone)
							|| dynamic_data.equalsIgnoreCase(disconect)) {
						status = "Pass";
					}
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: verifyTextWithDynamicdata");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/****************************************************************************************
	 * @function verifyTitleText() verify title of the screen
	 * 
	 * @param String object get text from element using id/xpath.
	 * 
	 * @param String data verify text with @param-object text, so that @param-object
	 *               & @param-data has same text.
	 * 
	 * @return status To indicate Pass or Fail
	 * @throws InterruptedException
	 *****************************************************************************************/
	public String verifyTitleText(String object, String data) throws InterruptedException {
		String status = "Fail";
		String title = TestDriver.CONFIG.getProperty(data);
		System.out.println(title);
		Thread.sleep(2000);
		try {
			String getTextstatus = getText(object, data);

			if (getTextstatus.equalsIgnoreCase("Pass") && title != null) {
				if (title.equals(saveText)) {
					status = "Pass";
					TestDriver.logger.info(saveText + " is matching with " + title);
				} else {
					TestDriver.logger.info(saveText + " is not matching with " + title);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*************************************************************************
	 * @function enterInputData() passing text to an edit-field/text-box
	 * @param String object mobileElement it can be a edit-field/text-box
	 * @param String data input text
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************/
	public String enterInputData(String object, String data) {
		String status = "Fail";
		String input = TestDriver.CONFIG.getProperty(data);
		MobileElement element;
		if (!data.isEmpty()) {
			try {
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
					element.clear();
					element.sendKeys(input);
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
					status = "Pass";
				} else {
					element = driver.findElementById(TestDriver.UIMap.getProperty(object));
					element.clear();
					element.sendKeys(input);
					// ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.ENTER));
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
					status = "Pass";
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: inputData");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Random randomNumber = new Random();
				int number = randomNumber.nextInt(10000);
				String text = "Testing" + number;
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
					// element.clear();
					System.out.println(text);
					element.sendKeys(text);
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
					status = "Pass";
				} else {
					element = driver.findElementById(TestDriver.UIMap.getProperty(object));
					// element.clear();
					System.out.println(text);
					element.sendKeys(text);
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
					status = "Pass";
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: inputData");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	/******************************************************************************************************
	 * @function verifyNotConnectedState()It will Verify Card Status as Not Connected
	 *
	 *@param String object Null
	 *
	 * @param String data deviceName and Not Connected text
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ********************************************************************************************************/

	public String verifyNotConnectedState(String object, String data) throws InterruptedException {
		String status = "Fail";
		Thread.sleep(6000);
		String input_dataarry[] = data.split(",");
		MobileElement element = null;
		String visibilityStatus="Fail";
			try {
				element = driver.findElement(MobileBy
						.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
								+ ".scrollIntoView(new UiSelector().text(\""
								+ TestDriver.CONFIG.getProperty(input_dataarry[0]) + "\"))"));
				
			try {
				if (element.isDisplayed()) {
					visibilityStatus = "Pass";
				}
				if (visibilityStatus == "Pass") {
			
					// Waiting until connected text should be visible.
					(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataarry[0])
							+ "']/following-sibling::*[@text='" + input_dataarry[1] + "']")));

					status = "Pass";
				}
			} catch (Exception e) {
				TestDriver.logger.info("Required card is not available  in current Screens");
			}
			}
			catch (Exception ignoreit) {
						TestDriver.logger.info("Required card or Specific text is not available..");
		}

		return status;
	}

	
	/******************************************************************************************************
	 * @function verifyConnectedState()It will Verify Card Status as Connected
	 *
	 *@param String object Null
	 *
	 * @param String data deviceName and Connected text
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ********************************************************************************************************/
	
	public String verifyConnectedState(String object, String data) {
		String status = "Fail";
		String input_dataarry[] = data.split(",");
		MobileElement element = null;
		try {

			element = (MobileElement) (new WebDriverWait(driver, 40))
					.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataarry[0]) + "']")));

			if (element.isDisplayed()) {

				(new WebDriverWait(driver, 40)).until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataarry[0])
						+ "']/following-sibling::*[@text='" + input_dataarry[1] + "']")));

				status = "Pass";
			}
		} catch (Exception e) {
			TestDriver.logger.info("Connected text is not available ");
		}

		return status;
	}

	/******************************************************************************************************
	 * @function disconnectedWifiCard()It will Verify Card Status in disconnect
	 *           state
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data devices and State to locate the Previous Phones Connected
	 *               State or Connected State
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ********************************************************************************************************/

	public String disconnectedWifiCard(String object, String data) throws InterruptedException {
		String status = "Fail";
		Thread.sleep(6000);
		MobileElement element = null;
		String input_dataarry[] = data.split(",");
		try {
			element = driver.findElement(MobileBy
					.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
							+ ".scrollIntoView(new UiSelector().text(\""
							+ TestDriver.CONFIG.getProperty(input_dataarry[0]) + "\"))"));
			try {
				if (element.isDisplayed()) {
					driver.findElementByXPath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataarry[0])
					+ "']/following-sibling::*[@text='" + input_dataarry[1] + "']").isDisplayed();
					status = "Fail";

				}
			} catch (NoSuchElementException ignore) {

				status = "Pass";

			} catch (Exception ignored) {
				status = "Pass";
			}
		} catch (Exception e) {
			TestDriver.logger.info("Required card is not available  in current Screens--");
		}

		return status;
	}

	/*****************************************************************************************
	 * @function verifyDeviceExist()It will verify the device Exist
	 *           device name on Phones Screen.
	 * 
	 * @param String object Null
	 * 
	 * @param String data device name
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *****************************************************************************************/
	public String verifyDeviceExist(String object, String data) throws InterruptedException {
		String status = "Fail";
		Thread.sleep(5000);
		MobileElement element = null;
			try {

				element = driver.findElement(MobileBy
						.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
								+ ".scrollIntoView(new UiSelector().text(\""
								+ TestDriver.CONFIG.getProperty(data) + "\"))"));
				if (element.isDisplayed()) {
					status = "Pass";
				}
			} catch (Exception e) {
				TestDriver.logger.info("Required card is not available  in current Screens");
			
		}

		return status;
	}

	/************************************************************************************************
	 * @function openConnectionsScreens()It will open the Phones/Wi-Fi
	 *           Networks/Wi-Fi Hotspot Screens device name Phones Screen.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *************************************************************************************************/

	public String openConnectionsScreens(String object, String data) {

		String status = "Fail";
		try {
			Thread.sleep(1000);
			String input[] = object.split(",");
			openApp(object, data);
			Thread.sleep(3000);
			waitForElement(input[0], data);
			waitForElement(input[1], data);
			clickElement(input[1], data);
			Thread.sleep(2000);
			waitForElement(input[1], data);
			waitForElement(input[2], data);
			clickElement(input[2], data);
			Thread.sleep(2000);
			waitForElement(input[2], data);
			Thread.sleep(2000);
			status = "Pass";

		} catch (NoSuchElementException e) {
			TestDriver.logger
			.error("AndroidDriver couldn’t locate the element, Name of current method:openConnectionsScreen()");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	/************************************************************************************************
	 * @function forgetNetwork()It will be forget the Network device name Phones
	 *           Screen.
	 * 
	 * @param String object Null
	 * 
	 * @param String data wifiNetworkName,Options,Forget Network
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *************************************************************************************************/
	public String forgetNetwork(String object, String data) throws InterruptedException {
		String status = "Fail";
		String input[] = data.split(",");
		MobileElement element = null;
		String visibilityStatus="Fail";
		try {
			element=driver.findElement(MobileBy.AndroidUIAutomator(
					"new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()" +
							".scrollIntoView(new UiSelector().text(\""+TestDriver.CONFIG.getProperty(input[0])+"\"))"));

			try {
				if (element.isDisplayed()) {
					visibilityStatus = "Pass";
				}
				if (visibilityStatus == "Pass") {

					if (data.contains(",")) {

						if ((input[1].equalsIgnoreCase("Options")))
						{
							driver.findElementByXPath("//*[@text='" + TestDriver.CONFIG.getProperty(input[0])
							+ "']/following-sibling::*[@text='" + input[1] + "']").click();
							Thread.sleep(3000);
							driver.findElementByXPath("//*[@text='" + input[2] + "']").click();
							Thread.sleep(3000);
							driver.findElementByXPath("//*[@text='" + input[2] + "']").click();
							Thread.sleep(3000);
							status = "Pass";
						}

					}
				}

			}catch(Exception e) {
				TestDriver.logger.info("Required WIFI card is not available in Screens--");
			}

		}catch(Exception e) {
			TestDriver.logger.info("WIFI Network card is not found--");
		}
		return status;
	}

	/************************************************************************************************
	 * @function isElementNotPresent() is Verify the unavailability of element in
	 *           view at Phones.
	 * 
	 * @param String object Null
	 * 
	 * @param String data device name
	 * @return [Flag] status Flag to indicate Pass or Fail
	 *************************************************************************************************/
	public String isElementNotPresent(String object, String data) throws InterruptedException {
		String status = "Fail";
		Thread.sleep(5000);
		String input_dataArry[] = data.split(",");
		MobileElement element = null;
		try {
			element = driver.findElement(MobileBy
					.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
							+ ".scrollIntoView(new UiSelector().text(\""
							+ TestDriver.CONFIG.getProperty(input_dataArry[0]) + "\"))"));

			try {

				if (element.isDisplayed()) {
					status = "Fail";
				}
			} catch (NoSuchElementException ignore) {
				status = "Pass";
			}
		}catch (Exception ignored) {
			status = "Pass";
		}	 
		return status;
	}

	/****************************************************************************************
	 * @function clearField() clear the text data in the entry field. Verifying the
	 *           Default(hint) text in entry filed
	 * @param String object Locator of the entry field to clear the text by using
	 *               id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ****************************************************************************************/

	public String clearField(String object, String data) {
		MobileElement element = null;
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = (MobileElement) driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				element.clear();
				if (!(element.getText().equals(" "))) {
					status = "Pass";
					TestDriver.logger.info("Test passed");
				}
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				element.clear();
				if (!(element.getText().equals(" "))) {
					status = "Pass";
					TestDriver.logger.info("Test passed");
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
			.error("AndroidDriver couldn’t locate the element, Name of current method: clearTextField");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/****************************************************************************************
	 * @function horizantalScroll() Verify the element Home screen
	 * @param String object Null
	 * 
	 * @param String data element Name
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ****************************************************************************************/

	public String horizantalScroll(String object, String data) throws InterruptedException {

		MobileElement element = null;
		String status = "Fail";
		String result;
		try {
			
			  driver.findElement(MobileBy.AndroidUIAutomator(
					  "new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()" +
					  ".scrollIntoView(new UiSelector().text(\""+data+"\"))"));
			  status="Pass";

			
		} catch (NoSuchElementException e) {
			TestDriver.logger
			.error("AndroidDriver couldn’t locate the element, Name of current method: horizantalScroll");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	/*************************************************************************
	 * @function inputText() passing text to an edit-field/text-box
	 * @param String object mobileElement it can be a edit-field/text-box
	 * @param String data input text
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************/

	public String inputText(String object, String data) {
		String status = "Fail";
		MobileElement element = null;
		if (!data.isEmpty()) {
			try {
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
					element.clear();
					element.sendKeys(data);
					status = "Pass";

				} else {
					element = driver.findElementById(TestDriver.UIMap.getProperty(object));
					element.clear();
					element.sendKeys(data);
					status = "Pass";
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: inputData");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return status;
	}
	/*************************************************************************
	 * @function swipingHorizontal() Horizontally scroll and click on card/options based on given data
	 * @param String object Null
	 * @param String data DeviceName/DeviceName,Options
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************/
	public String swipingHorizontal(String Object, String data) throws InterruptedException {
		String status = "Fail";
		String input_dataArry[] = data.split(",");
		MobileElement element = null;
		String visibilityState="Fail";
		try {
			element = driver.findElement(MobileBy
					.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
							+ ".scrollIntoView(new UiSelector().text(\""
							+ TestDriver.CONFIG.getProperty(input_dataArry[0]) + "\"))"));
			if (element.isDisplayed()) {
				visibilityState = "Pass";
			}

			if (visibilityState == "Pass") 
			{
				if (data.contains(",")) 
				{
					if ((input_dataArry[1].equalsIgnoreCase("Options")))
					{
						driver.findElementByXPath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataArry[0])
						+ "']/following-sibling::*[@text='" + input_dataArry[1] + "']").click();
						status = "Pass";
					} 
				}
					else {
					driver.findElementByXPath("//*[@text='" + TestDriver.CONFIG.getProperty(input_dataArry[0]) + "']").click();
					status = "Pass";
				}
			}
			}catch (Exception e) {
			TestDriver.logger.info("Required card is not available  in current Screens");
		}

		return status;

	}
	
	
	
	
	
	/*
	 * public String BTDevicePair(BluetoothDevice device,String data) { String
	 * status="Fail";
	 * 
	 * 
	 * try {
	 * 
	 * System.out.println("Start Pairing...");
	 * 
	 * 
	 * 
	 * Method m = device.getClass() .getMethod("createBond", (Class[]) null);
	 * m.invoke(device, (Object[]) null);
	 * 
	 * 
	 * System.out.println("Pairing finished."); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * return status; }
	 * 
	 * 
	 * 
	 */
	
	
}
