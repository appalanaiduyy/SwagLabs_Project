/**************************************************
*File Name:-TestDriver.java
*This class is implemented as Driver Script that invokes other components of the framework. 
*Written in Java.TestDriver.java invokes application under test.
*Reads in test scripts (which are in excel format),
*Get the testcases and test steps and executing the testcases by step wise 
*and updating the status of the testcases.
********************************************************/
package com.gm.core;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gm.keywords.AdvancedParkAssist;
import com.gm.keywords.AudioKeywords;
import com.gm.keywords.CameraKeywords;
import com.gm.keywords.ConnectionsKeywords;
import com.gm.keywords.HVAC;
import com.gm.keywords.PhoneKeywords;
import com.gm.keywords.ProjectionKeywords;
import com.gm.keywords.RadioKeywords;
import com.gm.keywords.SeatstatusKeywords;
import com.gm.keywords.SettingsKeywords;
import com.gm.keywords.TrailerKeywords;
import com.gm.utils.APIException;
import com.gm.utils.APIclient;
import com.gm.utils.ReportUtil;
import com.gm.utils.TestBase;
import com.gm.utils.TestUtil;
import com.gm.utils.XlHelper;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class TestDriver {
	public XlHelper suiteXLS;
	public int currentSuiteID;
	public String currentTestSuite;
	public static XlHelper currentTestSuiteXLS;
	public int currentTestCaseID = 1;
	public static String currentTestCaseName;
	public static int currentTestStepID;
	public static String currentKeyword;
	public static String stepDescription;
	public static String expectedResult;
	public static int currentTestDataSetID = 2;
	public static Method[] method;
	public static AndroidDriver<MobileElement> driver;
	public static Method capturescreenShot_method;
	public static Keywords keywords;
	
	public static ConnectionsKeywords connections;
	
	public static String keyword_execution_result;
	public static ArrayList<String> resultSet;
	public static String data;
	public static String object;
	public static Properties CONFIG;
	public static Properties UIMap;
	public static String module;
	public static StringBuffer modules = new StringBuffer();
	PrintWriter log_file_writer;

	String id;
	String testcycleId;
	// public static Logger logger = LoggerFactory.getLogger(TestDriver.class);

	public TestDriver(Keywords sk) throws NoSuchMethodException, SecurityException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, IOException {
		keywords = sk;
		method = sk.getClass().getMethods();
		// methodArray.add(sk.getClass().getMethods());
		// methodArray.get(0);
		capturescreenShot_method = sk.getClass().getMethod("captureScreenshot", String.class, String.class);
	}

	public TestDriver(Keywords sk, String configFile) throws NoSuchMethodException, SecurityException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		keywords = sk;
		method = sk.getClass().getMethods();
		capturescreenShot_method = sk.getClass().getMethod("captureScreenshot", String.class, String.class);
		FileInputStream fs = new FileInputStream(configFile);
		CONFIG = new Properties();
		CONFIG.load(fs);
		//Configuration for Log4j 
		System.out.println("Loading Log4j properties file-----------");
		Properties props = new Properties();
		props.load(new FileInputStream( System.getProperty("user.dir") + "/Batch/Log4j.properties"));
		PropertyConfigurator.configure(props);
	}

	static {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
		System.setProperty("currenttime", dateFormat.format(new Date()));
	}

	public static Logger logger = LoggerFactory.getLogger(TestDriver.class);

	/*****************************************************************************
	 * @function setUp() will config UIMap, Results & TestSuitesXLS paths
	 * @return type null
	 * 
	 * 
	 *****************************************************************************/
	public void setUp() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException,
			NoSuchMethodException, SecurityException {
		logger.info("Properties loaded. Starting testing");
		FileInputStream fs;
		if (CONFIG == null) {
			fs = new FileInputStream(System.getProperty("user.dir") + "/config/selenium.config.properties");
			CONFIG = new Properties();
			CONFIG.load(fs);
		}

		String UIMapPath = "";
		String ResultsPath = "";
		if (CONFIG.getProperty("UIMapPath") != null && CONFIG.getProperty("TestSuitePath") != null
				&& CONFIG.getProperty("ResultsPath") != null) {
			UIMapPath = CONFIG.getProperty("UIMapPath").trim();
			ResultsPath = CONFIG.getProperty("ResultsPath") + "/" + "Report_" + Keywords.today;
		} else {
			logger.info(
					"config properties like UIMapPath, TestSuitePath, ResultsPath are not defined .. please check your config.properties");
			ReportUtil.reportError(CONFIG.getProperty("ResultsPath"),
					"config properties like UIMapPath, TestSuitePath, ResultsPath are not defined .. please check your config.properties");
			System.exit(1);
		}

		if (!UIMapPath.endsWith("/") || !UIMapPath.endsWith("//")) {
			UIMapPath = UIMapPath + "/";
		}

		fs = new FileInputStream(UIMapPath + "UIMap.properties");
		UIMap = new Properties();
		UIMap.load(fs);
		TestUtil.checkDir(ResultsPath);
		ReportUtil.startTesting(ResultsPath, TestUtil.now("yyyy-MM-dd HH:mm:ss"), CONFIG.getProperty("ENV").trim(),
				CONFIG.getProperty("Release"), CONFIG.getProperty("deviceName"), CONFIG.getProperty("platform"),
				CONFIG.getProperty("platformVersion"), CONFIG.getProperty("apkVersion"));
		fs.close();
	}

	/****************************************************************
	 * @throws APIException 
	 * @function start() Will read Suit.xlsx & TestSuitsXLS files,
	 * starts the automation which are marked 'Y' on Suit.xls.
	 * 
	 * 
	 *********************************************************************/
	public void start() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, IOException, InterruptedException, APIException {
		String testStatus = "";
		String startTime = "";
		String automation = "";
		logger.info("CSMAppsAutomation Frame work version "+CONFIG.getProperty("CSMAppsAutomation_Version"));
		this.setUp();
		logger.info("Intialize Suite xlsx");
		SuiteHelper suiteHelper = new SuiteHelper();
		String suitePath = CONFIG.getProperty("TestSuitePath").trim();
		if (!suitePath.endsWith("/") || !suitePath.endsWith("//")) {
			suitePath = suitePath + "/";
		}

		this.suiteXLS = new XlHelper(suitePath + "Suite.xlsx");
		if (!this.suiteXLS.verifyMainSuiteFormat()) {
			logger.info("Failed to parse Suite.xlsx... please check the column headings or sheet names");
			ReportUtil.reportError(CONFIG.getProperty("ResultsPath"),
					"Failed to parse Suite.xlsx... please check the column headings or sheet names");
		} else {
			HashMap<String, String> map = suiteHelper.getSuite(this.suiteXLS);

			label119: for (Iterator var7 = map.keySet().iterator(); var7.hasNext(); ReportUtil.endSuite()) {
				String key = (String) var7.next();
				this.currentTestSuite = key;
				ReportUtil.startSuite(this.currentTestSuite, (String) map.get(key));
				if (((String) map.get(key)).equalsIgnoreCase("Y")) {
					module = key;
					modules.append(key);
					switch (key) {
					
					
						
						
					
					case "SwagLabs_TestCases":
						connections = new ConnectionsKeywords();
						keywords = connections;
						connections.launchRequiredApplication(CONFIG.getProperty("SwagLabsAppActivity"),CONFIG.getProperty("SwagLabsAppPackage"));
						break;
				
					
					}
					if(TestDriver.CONFIG.getProperty("enable_Logs").equalsIgnoreCase("Y")) {
						logCat();
						//appiumLogCat();
					}

					currentTestSuiteXLS = new XlHelper(suitePath + this.currentTestSuite + ".xlsx");
					if (!currentTestSuiteXLS.verifyTestSuiteFormat()) {
						logger.info("Failed to parse " + this.currentTestSuite
								+ ".xlsx... please check the column headings or sheet names");
						ReportUtil.reportError(CONFIG.getProperty("ResultsPath"), "Failed to parse "
								+ this.currentTestSuite + ".... please check the column headings or sheet names");
						return;
					}

					List<TestCase> tcList = suiteHelper.getTC(currentTestSuiteXLS);
					
					Iterator var21 = tcList.iterator();

					while (true) {
						while (true) {
							if (!var21.hasNext()) {
								continue label119;
							}

							TestCase tc = (TestCase) var21.next();
							currentTestCaseName = tc.getTCName();
							logger.info("THE CURRENT TESTCASE NAME IS : " + currentTestCaseName);
							if (tc.getRunMode().equalsIgnoreCase("Y")) {
								suiteHelper.readTestSteps(currentTestSuiteXLS);
								automation = "Y";

								if (!testStatus.equals(Constants.KEYWORD_PASS)
										&& tc.getDependency().equalsIgnoreCase("y")) {
									ReportUtil.addTestCase(currentTestCaseName, tc.getUSName(), tc.getTCDesc(),
											startTime, TestUtil.now("yyyy-MM-dd HH:mm:ss"),
											"Fail[Dep. Test Failed/Skipped]", automation, tc.getPriority(),
											tc.getManualExeTime());
									continue;
								}

								logger.info("Executing the test case -> " + currentTestCaseName);
								TestSteps ts = suiteHelper.getTCSteps(currentTestCaseName);
								if (ts == null) {
									ReportUtil.addTestCase(currentTestCaseName, tc.getUSName(), tc.getTCDesc(),
											startTime, TestUtil.now("yyyy-MM-dd HH:mm:ss"),
											"Fail No Test Steps Defined", automation, tc.getPriority(),
											tc.getManualExeTime());
									continue;
								}

								if (currentTestSuiteXLS.isSheetExist(currentTestCaseName)) {
									List<String> ds = suiteHelper.getDataSet(currentTestSuiteXLS, currentTestCaseName);
									currentTestDataSetID = 2;
									String tcName = "";

									for (Iterator var16 = ds.iterator(); var16.hasNext(); ++currentTestDataSetID) {
										String s = (String) var16.next();
										startTime = TestUtil.now("yyyy-MM-dd HH:mm:ss");
										resultSet = new ArrayList();
										tcName = currentTestCaseName + "_" + (currentTestDataSetID - 1);
										if (s.equals("Y")) {
											testStatus = this.executeKeywords(ts);
											logger.info("/***********************************************************");
											logger.info("**************Iteration number::{} ",
													currentTestDataSetID - 1);
											logger.info("/***********************************************************");
											ReportUtil.addTestCase(tcName, tc.getUSName(), tc.getTCDesc(), startTime,
													TestUtil.now("yyyy-MM-dd HH:mm:ss"), testStatus, automation,
													tc.getPriority(), tc.getManualExeTime());
										} else {
											ReportUtil.addTestCase(tcName, tc.getUSName(), tc.getTCDesc(), startTime,
													TestUtil.now("yyyy-MM-dd HH:mm:ss"), "Skip", automation,
													tc.getPriority(), tc.getManualExeTime());
											logger.info("Data set Runmode is set to N .. skipping");
										}
									}
								} else {
									startTime = TestUtil.now("yyyy-MM-dd HH:mm:ss");
									currentTestDataSetID = 2;
									resultSet = new ArrayList();
									testStatus = this.executeKeywords(ts);
									// add test result in test rail------
								
									if(CONFIG.getProperty("UpdateResults_testrail").equalsIgnoreCase("Y"))
									{
										logger.info("currentTestCaseName test Results update in TestRail " + currentTestCaseName);
										this.setResultInTestRail(currentTestCaseName, testStatus,ReportUtil.getDateDiff(startTime,TestUtil.now("yyyy-MM-dd HH:mm:ss")));
					
									}
							        ReportUtil.addTestCase(currentTestCaseName, tc.getUSName(), tc.getTCDesc(),
											startTime, TestUtil.now("yyyy-MM-dd HH:mm:ss"), testStatus, automation,
											tc.getPriority(), tc.getManualExeTime());
									 	//Update the result in RQM format uncomment
								//currentTestSuiteXLS.rqm_testcase_Updation(testStatus,tc.getTCName(),key); 
								}
							} else {
								logger.info("Skipping the test " + currentTestCaseName);
								testStatus = tc.getStatus();
								if (!testStatus.equals("") && !testStatus.equals((Object) null)) {
									automation = "N";
								} else {
									testStatus = "Skip";
									automation = "Y";
								}

								logger.info("***********************************" + currentTestCaseName + " --- "
										+ testStatus);
								ReportUtil.addTestCase(currentTestCaseName, tc.getUSName(), tc.getTCDesc(),
										TestUtil.now("yyyy-MM-dd HH:mm:ss"), TestUtil.now("yyyy-MM-dd HH:mm:ss"),
										testStatus, automation, tc.getPriority(), tc.getManualExeTime());
								
									//Update the result in RQM format uncomment
								//currentTestSuiteXLS.rqm_testcase_Updation(testStatus,tc.getTCName(),key); 
							}

							++this.currentTestCaseID;
						}
					}
				}
			}

			try {
				ReportUtil.updateEndTime(TestUtil.now("yyyy-MM-dd HH:mm:ss"));
			} catch (SQLException var18) {
				var18.printStackTrace();
				logger.error("Exception in executing an query : {}", var18, var18.getMessage());
			}

		}
		//stopLogCat();
	
		keywords.stopAppium();
		//stopLogCat();
		if(TestDriver.CONFIG.getProperty("enable_Logs").equalsIgnoreCase("Y")) {
			stopLogCat();
		}
		//log_file_writer.close();
	}

	/**********************************************************************
	 * @function executeKeywords() -This method will executes the everykeyword
     * 
	 * @param TestSteps ts  Test case steps
	 * 	  
	 * @return [Flag] status Flag To indicate Pass or Fail
	 * 
	 ***********************************************************************/
	private String executeKeywords(TestSteps ts) {
		boolean rc = false;
		String exeOnFailureFlag = "";
		String results = "Fail";

		for (int j = 0; j < ts.getKeywords().size(); ++j) {
			keyword_execution_result = "Fail --could be some exceptions please check logs";
			data = (String) ts.getData().get(j);
			exeOnFailureFlag = (String) ts.getExecFlag().get(j);
			if (data.startsWith(Constants.DATA_START_COL)) {
				data = currentTestSuiteXLS.getCellData(currentTestCaseName, data.split(Constants.DATA_SPLIT)[1],
						currentTestDataSetID);
			} else if (data.startsWith(Constants.CONFIG)) {
				data = CONFIG.getProperty(data.split(Constants.DATA_SPLIT)[1]).trim();
			} else if (data.startsWith(Constants.UIMAP)) {
				data = UIMap.getProperty(data.split(Constants.DATA_SPLIT)[1]).trim();
			}

			object = (String) ts.getObjects().get(j);
			currentKeyword = (String) ts.getKeywords().get(j);
			stepDescription = (String) ts.getTCDesc().get(j);
			expectedResult = (String) ts.getExpectedResult().get(j);
			logger.info(currentKeyword);
			//System.out.println(currentKeyword);
			System.out.println("Executing Step");
			System.out.println(ts.getTCName() + "--" + (String) ts.getKeywords().get(j) + "--"
					+ (String) ts.getObjects().get(j) + "--" + (String) ts.getData().get(j));
			logger.info("/******************************************");
			logger.info("/*****Keyword:{}({})*****", currentKeyword, stepDescription);
			logger.debug("/*****Keyword:{}({})*****", currentKeyword, stepDescription);
			logger.info("/***** DATA :{} Value::({})*****", ts.getData().get(j), data);
			logger.debug("/***** DATA :{} Value::({})*****", ts.getData().get(j), data);
			logger.info("/******************************************");

			try {

				keyword_execution_result = (String) keywords.getClass()
						.getMethod(currentKeyword, String.class, String.class).invoke(keywords, object, data);

			} catch (IllegalArgumentException var12) {
				var12.printStackTrace();
			} catch (IllegalAccessException var13) {
				var13.printStackTrace();
			} catch (InvocationTargetException var14) {
				keyword_execution_result = "Fail -- Failed to execute keyword";
				logger.error("Fail -- Failed to execute keyword " + var14);
				var14.printStackTrace();
			} catch (SecurityException var15) {
				var15.printStackTrace();
			} catch (NoSuchMethodException var16) {
				keyword_execution_result = "Fail -- keyword not found, please check typos";
				logger.error("Fail -- keyword not found, please check typos \n" + var16);
			}

			logger.info(keyword_execution_result);
			resultSet.add(keyword_execution_result);
			results = keyword_execution_result;
			System.out.println(results);
			String tcName = currentTestCaseName;
			if (tcName.indexOf("|") != -1) {
				tcName = currentTestCaseName.split("\\|")[1];
			}

			String fileName = this.currentTestSuite + "_" + tcName + "_TS" + j + "_" + (currentTestDataSetID - 1);
			if ((CONFIG.getProperty("screenshot_everystep").trim().equalsIgnoreCase("y")
					|| !keyword_execution_result.equals(Constants.KEYWORD_PASS))
					&& !((String) ts.getKeywords().get(j)).equals("openBrowser")
					&& !((String) ts.getKeywords().get(j)).equals("closeBrowser")
					&& !((String) ts.getKeywords().get(j)).equals("dataSetup")
					&& !((String) ts.getKeywords().get(j)).equals("runJMeter")) {
				try {
					capturescreenShot_method.invoke(keywords, fileName, keyword_execution_result);
				} catch (IllegalArgumentException var9) {
					var9.printStackTrace();
				} catch (IllegalAccessException var10) {
					var10.printStackTrace();
				} catch (InvocationTargetException var11) {
					var11.printStackTrace();
				}
			}

			ReportUtil.addKeyword(stepDescription, currentKeyword, keyword_execution_result,
					"Report_" + Keywords.today + "/" + fileName + ".jpg", data, expectedResult);
			if (!keyword_execution_result.equals(Constants.KEYWORD_PASS)) {
				results = "Fail";
				rc = true;
				if (!exeOnFailureFlag.equalsIgnoreCase("y")) {
					return results;
				}
			}
		}

		if (rc)

		{
			results = "Fail";
		}

		return results;
	}

    /**********************************************************************
	 * @function logCat() - This method is for Start for the log messages
	 * Not using presenetly
	 ***********************************************************************/
	public void logCat() {
		String path = System.getProperty("user.dir") + "\\log\\adbLogs";
		File theDir = new File(path);
		if (!theDir.exists()) {
			boolean result = theDir.mkdir();
			theDir.setWritable(true);
		}
		if (theDir.exists()) {
			String logPath = path + "\\adbLog_" + Keywords.today + ".txt";
			String runnigDevice = TestDriver.CONFIG.getProperty("deviceName");
			String[] command = { "cmd.exe", "/C", "adb -s " + runnigDevice + " logcat -c && adb logcat >" + logPath };
			// String[] command = { "cmd.exe", "/C","adb logcat >"+path };
			try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			TestDriver.logger.error("Failed to create the directory to adb logs ");
		}
	}
	/**********************************************************************
	 * @throws InterruptedException 
	 * @function stopLogCat() - This method is for Stop for the log messages
	 * Not using presenetly
	 ***********************************************************************/

	public void stopLogCat() throws InterruptedException {
		String runnigDevice = TestDriver.CONFIG.getProperty("deviceName");
		String[] command = { "cmd.exe", "/C", /* "Start/min", path */"adb -s " + runnigDevice
				+ " kill-server"/* ,"adb -s "+runnigDevice+" start-server" */ };

		String[] command1 = { "cmd.exe", "/C", /* "Start/min", path */"adb -s " + runnigDevice
				+ " start-server"/* ,"adb -s "+runnigDevice+" start-server" */ };
		try {
			Runtime.getRuntime().exec(command);
			Thread.sleep(4000);
			Runtime.getRuntime().exec(command1);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	/**********************************************************************
	 * @function setResultInTestRail() - This method is for Stop for the log messages
	 * 
	 ***********************************************************************/

	public void setResultInTestRail(String testCaseName, String testStatus, String elpsDuration)
			throws MalformedURLException, IOException, APIException {
		TestBase testBase = new TestBase();
    testBase.getTestCaseStatus(testCaseName, testStatus,elpsDuration);

}

}
