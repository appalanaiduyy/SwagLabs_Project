/**************************************************
*File Name:-ReportUtil.java
*This class is implemented as class generates the HTML custom reports
*for the executed tests along with screen shots for failed tests.
********************************************************/
package com.gm.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gm.core.TestDriver;
import com.gm.core.TestResults;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ReportUtil {
	public static Logger logger = LoggerFactory.getLogger(ReportUtil.class);
	public static int scriptNumber = 1;
	public static String indexResultFilename;
	public static String currentDir;
	public static String currentSuiteName;
	public static int tcid;
	public static double passNumber = 0.0D;
	public static double failNumber = 0.0D;
	public static double skipNumber = 0.0D;
	public static int passCount = 0;
	public static int failCount = 0;
	public static int skipCount = 0;
	public static int totalCount = 0;
	public static boolean newTest = true;
	public static ArrayList<String> description = new ArrayList<String>();
	public static ArrayList<String> keyword = new ArrayList<String>();
	public static ArrayList<String> teststatus = new ArrayList<String>();
	public static ArrayList<String> screenShotPath = new ArrayList<String>();
	public static ArrayList<String> dataSets = new ArrayList<String>();
	public static ArrayList<String> errors = new ArrayList<String>();
	public static List<TestResults> tcList = new ArrayList<TestResults>();
	public static ArrayList<String> usCoverage = new ArrayList<String>();
	public static ArrayList<String> expectedResult = new ArrayList<String>();
	public static String project;
	public static String browser;
	public static String testtype;
	private static String env;
	private static String tStartTime;
	private static String etime;
	private static int testrunID;
	private static String testreportDate;
	private static DecimalFormat df = new DecimalFormat("0");

	
	/****************************************************************
	 * @function reportError()--> display the Error message in index.html
	 * 
	 * @param String filePath -->location of index.html
	 * 
	 * @param String errMsg --> Error message that need to display in index.html
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/
	public static void reportError(String filePath, String errMsg) {
		String filename = filePath + "/index.html";
		indexResultFilename = filename;
		currentDir = filePath;
		FileWriter fstream = null;
		BufferedWriter out = null;

		try {
			fstream = new FileWriter(filename);
			out = new BufferedWriter(fstream);
			String runDate = TestUtil.now("dd.MMMMM.yyyy").toString();
			out.newLine();
			out.write("<html>\n");
			out.write("<HEAD>\n");
			out.write(" <TITLE>Automation Test Results For " + project + "</TITLE>\n");
			out.write("</HEAD>\n");
			out.write("<body>\n");
			out.write(
					"<table cellspacing=0 cellpadding=0 width=100% ><tr width=100% bgcolor=COLORCODE ><td align=center><FONT COLOR=white FACE=Arial SIZE=2.5><h1 align='center'>"
							+ errMsg + "</h1></td></tr></table>\n");
			out.write("<FONT COLOR=white FACE=Arial SIZE=2.5><h1 align='center'>" + errMsg + "</h1>");
			out.close();
		} catch (Exception var9) {
			System.err.println("Error: " + var9.getMessage());
		} finally {
			fstream = null;
			out = null;
		}

	}
	
	
	/****************************************************************
	 * @function startTesting()--> To display Test Environment Details
	 * 
	 * @param String String filePath -->location of index.html
	 * @param String testStartTime -->Starting time of Execution
	 *  String environment --> Environment information (Ex:CSMApps)
	 * String release --> Sprint Number (Ex:151.1 Q Regression)
	 * String deviceName -->Device name
	 * String platform --> Platform (Ex:Android)
	 * String platformVersion -->platform Version(Ex:9)
	 * String apkVersion -->Current Build Number(Ex:QIH22B-22)
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/
//Creating of Test 1) Environment Details : 2) Test Summary :
	public static void startTesting(String filePath, String testStartTime, String environment, String release,String deviceName,String platform,String platformVersion,String apkVersion) {
		FileWriter fstream = null;
		BufferedWriter out = null;
		env = environment;
		tStartTime = testStartTime;
		String filename = filePath + "/index.html";
		indexResultFilename = filename;
		currentDir = filePath;

		try {
			fstream = new FileWriter(filename);
			out = new BufferedWriter(fstream);
			String runDate = TestUtil.now("dd.MMMMM.yyyy").toString();
			out.newLine();
			out.write("<html>\n");
			out.write("<HEAD>\n");
			out.write(" <TITLE>Automation Test Results For " + project + "</TITLE>\n");
			out.write("</HEAD>\n");
			out.write("<body>\n");
			out.write(
					"<table cellspacing=0 cellpadding=0 width=100% ><tr width=100% bgcolor=COLORCODE ><td align=center><FONT COLOR=white FACE=Arial SIZE=2.5><h1 align='center'>Automation Test Results For "
							+ project + "</h1></td></tr></table>\n");
			out.write("<table  border=1 cellspacing=1 cellpadding=1 >\n");
			out.write("<tr>\n");
			out.write("<br></br>");
			out.write("<br></br>");
			out.write("<p></p>");
			out.write("<h4> <FONT COLOR=660000 FACE=Arial SIZE=4.5> <u>Test Environment Details :</u></h4>\n");
			out.write(
					"<td width=250 align=left bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2.75><b>Run Date</b></td>\n");
			out.write(
					"<td width=250 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + runDate + "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			out.write(
					"<td width=250 align=left bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2.75><b>Run StartTime</b></td>\n");
			out.write("<td width=250 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + testStartTime
					+ "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Run EndTime</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>END_TIME</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Environment</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + environment
					+ "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Release</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + release
					+ "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Device Name</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + deviceName
					+ "</b></td>\n");
			
			out.write("</tr>\n");
			out.write("<tr>\n");
			
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Platform</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + platform
					+ "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Platfrom Version</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + platformVersion
					+ "</b></td>\n");
			out.write("</tr>\n");
			out.write("<tr>\n");
			
			out.write(
					"<td width=250 align= left  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>APK Release Version</b></td>\n");
			out.write("<td width=250 align= left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2.75><b>" + apkVersion
					+ "</b></td>\n");
			
			
			
			out.write("</tr>\n");
			out.write("</table>\n");
			out.write("<p></p>");
			out.write("<table cellspacing=1 cellpadding=1 >");
			out.write("<tr>");
			out.write("<td width=50%>");
			out.write(
					"<table cellspacing=1 cellpadding=1 ><tr><h4><FONT COLOR=660000 FACE=Arial SIZE=4.5> <u>Test Summary :</u></h4></tr><tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Tests</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>TOTALTESTS</b></td><td width='100' align='center'><div><span style='width:450px;float:left;background-color: white; width: 100%'></span></div></td><td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b></b></span></td></tr><tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Pass</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>PASSCOUNT</b></td><td width='100'><div ><span style='width:450px;float:left;background-color: green; width: PASSNUMBER%'></span></div></td><td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>PASSNUMBER%</b></span></td></tr><tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Skipped</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>SKIPCOUNT</b></td><td width='100'><div><span style='width:450px;float:left;background-color: orange;width: SKIPNUMBER%'></span></div></td><td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>SKIPNUMBER%</b></span></td></tr><tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Fail</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>FAILCOUNT</b></td><td width='100'><div><span style='width:450px;float:left;background-color: red;width: FAILNUMBER%'></span></div></td><td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>FAILNUMBER%</b></span></td></tr></table>");
			
			out.close();
			fstream.close();
		} catch (Exception var11) {
			System.err.println("Error: " + var11.getMessage());
		} finally {
			fstream = null;
			out = null;
		}

	}

	/****************************************************************
	 * @function startSuite()--> To display Row Headings in table of index.html
	 * 
	 * @param String suiteName -->Current executing module name
	 * 
	 * @param String runMode -->Run mode(Y/N)
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/
	public static void startSuite(String suiteName, String runMode) {
		FileWriter fstream = null;
		BufferedWriter out = null;
		currentSuiteName = suiteName.trim().replaceAll(" ", "_");
		tcid = 1;
		scriptNumber = 1;

		try {
			fstream = new FileWriter(indexResultFilename, true);
			out = new BufferedWriter(fstream);
			if (runMode.equals("Y")) {
				out.write("<h4> <FONT COLOR=660000 FACE= Arial  SIZE=4.5> <u>" + suiteName + " Report :</u></h4>\n");
				out.write("<table  border=1 cellspacing=1 cellpadding=1 width=100%>\n");
				out.write("<tr>\n");
				out.write(
						"<td width=5%  align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Test Script#</b></td>\n");
				out.write(
						"<td width=20% align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Test Case Name</b></td>\n");
				out.write(
						"<td width=20% align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Test Case Desc</b></td>\n");
				out.write(
						"<td width=15% align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Status</b></td>\n");
				out.write(
						"<td width=20% align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Run Start Time</b></td>\n");
				out.write(
						"<td width=20% align= center  bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Run End Time</b></td>\n");
				out.write("</tr>\n");
			} else {
				out.write("<h4> <FONT COLOR=660000 FACE= Arial  SIZE=4.5> <u>" + suiteName
						+ " Report :</u>         ------ SKIPPED ----- </h4>\n");
			}

			out.close();
			fstream.close();
			
		} catch (Exception var8) {
			System.err.println("Error: " + var8.getMessage());
		} finally {
			fstream = null;
			out = null;
		}

	}
	/****************************************************************
	 * @function endSuite()--> Ends suite reading
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/

	public static void endSuite() {
		FileWriter fstream = null;
		BufferedWriter out = null;

		try {
			fstream = new FileWriter(indexResultFilename, true);
			out = new BufferedWriter(fstream);
			out.write("</table>\n");
			out.close();
			fstream.close();
		} catch (Exception var6) {
			System.err.println("Error: " + var6.getMessage());
		} finally {
			fstream = null;
			out = null;
		}
		
	}
	
	/****************************************************************
	 * @function addTestCase()--> To display every test case details in table of index.html
	 * 
	 * @param String testCaseName--> Name of test case
	 * @param String testDesc --> Test description
	 * @param String testCaseStartTime --> Starting time
	 * @param String testCaseEndTime -->Ending time
	 * @param String status --> Status(Y/N)
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/
	public static void addTestCase(String testCaseName, String us, String testDesc, String testCaseStartTime,
			String testCaseEndTime, String status, String isAuto, String priority, int manualExeTime) {
		newTest = true;
		String tcName = testCaseName;
		FileWriter fstream = null;
		BufferedWriter out = null;
		String userStory = "";

		label248 : {
			try {
				newTest = true;
				if (!status.equalsIgnoreCase("Skipped") && !status.equalsIgnoreCase("Skip")) {
					File f = new File(currentDir + "//" + currentSuiteName + "_TC" + tcid + "_"
							+ tcName.replaceAll(" ", "_") + ".html");
					f.createNewFile();
					fstream = new FileWriter(currentDir + "//" + currentSuiteName + "_TC" + tcid + "_"
							+ tcName.replaceAll(" ", "_") + ".html");
					out = new BufferedWriter(fstream);
					out.write("<html>");
					out.write("<head>");
					out.write("<title>");
					out.write(tcName + " Detailed Reports");
					out.write("</title>");
					out.write("</head>");
					out.write("<body>");
					out.write("<h4> <FONT COLOR=660000 FACE=Arial SIZE=4.5> Detailed Report for Test Case " + tcName
							+ " :</h4>");
					out.write("<table  border=1 cellspacing=1    cellpadding=1 width=100%>");
					out.write("<tr> ");
					out.write(
							"<td align=center width=10%  align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Step/Row#</b></td>");
					out.write(
							"<td align=center width=30% align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Test Step Description</b></td>");
					out.write(
							"<td align=center width=10% align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Keyword</b></td>");
					out.write(
							"<td align=center width=5% align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Result</b></td>");
					out.write(
							"<td align=center width=5% align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Screen Shot</b></td>");
					out.write(
							"<td align=center width=30% align=center bgcolor=#153E7E><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2><b>Expected Result</b></td>");
					out.write("</tr>");
					if (keyword != null) {
						for (int i = 0; i < keyword.size(); ++i) {
							out.write("<tr> ");
							out.write("<td align=center width=5%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>TS" + (i + 1)
									+ "</b></td>");
							out.write("<td align=left width=20%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>"
									+ (String) description.get(i) + "</b></td>");
							out.write("<td align=center width=10%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>"
									+ (String) keyword.get(i) + "</b></td>");
							if (((String) teststatus.get(i)).startsWith("Pass")) {
								out.write(
										"<td width=5% align= center  bgcolor=#BCE954><FONT COLOR=#153E7E FACE=Arial SIZE=2><b>"
												+ (String) teststatus.get(i) + "</b></td>\n");
							} else if (((String) teststatus.get(i)).startsWith("Fail")) {
								out.write(
										"<td width=5% align= center  bgcolor=#F78181><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>"
												+ (String) teststatus.get(i) + "</b></td>\n");
							}

							if (((String) teststatus.get(i)).equalsIgnoreCase("pass")
									&& !TestDriver.CONFIG.getProperty("screenshot_everystep").equalsIgnoreCase("y")) {
								out.write(
										"<td align=center width=5%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>&nbsp;</b></td>");
							} else if (!((String) keyword.get(i)).equals("openBrowser")
									&& !((String) keyword.get(i)).equals("closeBrowser")
									&& !((String) keyword.get(i)).equals("dataSetup")
									&& !((String) keyword.get(i)).equals("testWebServices")
									&& !((String) keyword.get(i)).equals("runJMeter")) {
								if (TestDriver.CONFIG.getProperty("ResultsPath").trim().contains(":")) {
									out.write(
											"<td align=center width=10%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b><a href='"
													+ (String) screenShotPath.get(i)
													+ "' target=_blank>Screen Shot</a></b></td>");
								} else {
									out.write(
											"<td align=center width=10%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b><a href='../"
													+ (String) screenShotPath.get(i)
													+ "' target=_blank>Screen Shot</a></b></td>");
								}
							} else {
								out.write(
										"<td align=center width=20%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>&nbsp;</b></td>");
							}
							out.write("<td align=left width=35%><FONT COLOR=#153E7E FACE=Arial SIZE=1><b>"
									+ (String) expectedResult.get(i) + "</b></td>");
							out.write("</tr>");
						}
					}

					out.close();
				}

				fstream = new FileWriter(indexResultFilename, true);
				new BufferedWriter(fstream);
				fstream = new FileWriter(indexResultFilename, true);
				out = new BufferedWriter(fstream);
				out.write("<tr>\n");
				out.write("<td width=5% align= center ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>" + scriptNumber
						+ "</b></td>\n");
				if (!status.equalsIgnoreCase("Skipped") && !status.equalsIgnoreCase("Skip")) {
					out.write("<td width=20% align= Left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b><a href="
							+ currentSuiteName + "_TC" + tcid + "_" + tcName.replaceAll(" ", "_") + ".html>" + tcName
							+ "</a></b></td>\n");
				} else {
					out.write("<td width=20% align= Left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>" + tcName
							+ "</b></td>\n");
				}

				out.write("<td width=20% align= Left ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>&nbsp" + testDesc
						+ "</b></td>\n");
				++tcid;
				TestResults ts;
				if (status.equalsIgnoreCase("pass")) {
					out.write("<td width=15% align= center  bgcolor=#BCE954><FONT COLOR=#153E7E FACE=Arial SIZE=2><b>"
							+ status + "</b></td>\n");
					if (!testCaseName.equalsIgnoreCase("setup") && !testCaseName.equalsIgnoreCase("teardown")) {
						++passCount;
						ts = new TestResults(currentSuiteName, tcName, testDesc, userStory, testCaseStartTime,
								testCaseEndTime, status, " ", isAuto, priority, manualExeTime);
						tcList.add(ts);
					}
				} else if (status.startsWith("Fail")) {
					out.write("<td width=15% align= center  bgcolor=#F78181><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>"
							+ status + "</b></td>\n");
					if (!testCaseName.equalsIgnoreCase("setup") && !testCaseName.equalsIgnoreCase("teardown")) {
						++failCount;
						ts = new TestResults(currentSuiteName, tcName, testDesc, userStory, testCaseStartTime,
								testCaseEndTime, status, (String) teststatus.get(teststatus.size() - 1), isAuto,
								priority, manualExeTime);
						tcList.add(ts);
					}
				} else {
					out.write("<td width=15% align= center  bgcolor=yellow><FONT COLOR=153E7E FACE=Arial SIZE=2><b>"
							+ status + "</b></td>\n");
					if (!testCaseName.equalsIgnoreCase("setup") && !testCaseName.equalsIgnoreCase("teardown")) {
						++skipCount;
						ts = new TestResults(currentSuiteName, tcName, testDesc, userStory, testCaseStartTime,
								testCaseEndTime, status, " ", isAuto, priority, manualExeTime);
						tcList.add(ts);
					}
				}

				out.write("<td width=20% align= center ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>" + testCaseStartTime
						+ "</b></td>\n");
				out.write("<td width=20% align= center ><FONT COLOR=#153E7E FACE= Arial  SIZE=2><b>" + testCaseEndTime
						+ "</b></td>\n");
				out.write("</tr>\n");
				++scriptNumber;
				break label248;
			} catch (IOException var23) {
				var23.printStackTrace();
			} finally {
				try {
					out.close();
					fstream.close();
					out = null;
					fstream = null;
				} catch (IOException var22) {
					var22.printStackTrace();
				}

			}

			return;
		}

		description = new ArrayList();
		keyword = new ArrayList();
		teststatus = new ArrayList();
		screenShotPath = new ArrayList();
		dataSets = new ArrayList();
		expectedResult = new ArrayList();
		newTest = false;
		
	}

	/****************************************************************
	 * @function addKeyword()--> To display every test step details in table of index.html
	 * 
	 * @param String desc--> Currentt test step Description
	 * @param String key --> Current keyword name
	 * @param String stat --> Current keyword result
	 * @param String path --> Location of index.html
	 * @param String expResult --> Current test step expected result
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/
	public static void addKeyword(String desc, String key, String stat, String path, String data, String expResult) {
		description.add(desc);
		keyword.add(key);
		teststatus.add(stat);
		screenShotPath.add(path);
		dataSets.add(data);
		expectedResult.add(expResult);
	}
	
	/****************************************************************
	 * @function updateEndTime()--> To display test cases Pass,Fail,Skip percentages in index.html
	 * 
	 * @param String endTime --> Script ending time
	 * 
	 * @return [Flag] status Flag -->Null
	 * ***************************************************************/

	public static void updateEndTime(String endTime) throws SQLException {
		etime = endTime;
		StringBuffer buf = new StringBuffer();
		StringBuffer content = new StringBuffer();
		String tableRow = "";
		String tableRow1 = "";
		String tmp = "";
		String tmp1 = "";
		String color = "";
		double passCov = 0.0D;
		double failCov = 0.0D;
		double tbdCov = 0.0D;
		//int tbdCount = false;
		totalCount = passCount + failCount + skipCount;
		
		if (totalCount != 0) {
			passNumber = (double) (100 * passCount)/ totalCount;
			failNumber = (double) (100 * failCount)/ totalCount;
			skipNumber = (double) (100 * skipCount)/ totalCount;
			passNumber = Double.parseDouble(df.format(passNumber));
			failNumber = Double.parseDouble(df.format(failNumber));
			skipNumber = Double.parseDouble(df.format(skipNumber));
			try {
			//Email.send(TestDriver.CONFIG.getProperty("username"),TestDriver.CONFIG.getProperty("password"),TestDriver.CONFIG.getProperty("to_email_two"),TestDriver.CONFIG.getProperty("to_email_one"),"Automation Results ",""+" "+TestDriver.module+" " +"Automation Results " +"  PASS = "+String.valueOf(passNumber)+"  FAIL = "+String.valueOf(failNumber)+"  SKIP = "+String.valueOf(skipNumber));
			}
			catch(NullPointerException e) {
				System.out.println("username, password, toEmail not set in config.properties");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		String row = "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>USNAME</b></td><td width=30 align=center><span><FONT COLOR=#4B8A08 FACE=Arial SIZE=2.75><b>COVERAGE%</b></span></td><td width='500' align='center'><div ><span style='width:450px;float:left;background-color: green; width: COVERAGE%'></span><span style='width:450px;float:left;background-color: red; width: FAIL%'></span><span style='width:450px;float:left;background-color: orange; width: TBD%'></span></div></td></tr>";
		String row1 = "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>USNAME</b></td><td width=30 align=center><span><FONT COLOR=#4B8A08 FACE=Arial SIZE=2.75><b>COVERAGE%</b></span></td></tr>";
		if (failCount > 0) {
			color = "red";
		} else {
			color = "green";
		}

		String tc = "<table width=100% ><tr bgcolor=" + color
				+ " align='center'><FONT COLOR=white FACE=Arial SIZE=2.5><h3>Automation Test Results For " + project
				+ "</h3></tr></table><table cellspacing=1 cellpadding=1  border=1 width=300><tr><tr><h4><FONT COLOR=660000 FACE=Arial SIZE=4.5> <u>Test Summary :</u></h4></tr><tr><td width=100 align=left>"
				+ "<FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Tests</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ totalCount + "</b></td>"
				+ "<td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b></b></span></td></tr><tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75>"
				+ "<b>Total Pass</b></td><td  width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ passCount + "</b></td>" + "<td bgcolor=green><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"+ passNumber + "%</b></span></td></tr>"
				+ "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Skipped</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ skipCount + "</b></td>" + "<td bgcolor=orange><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ skipNumber + "%</b></span></td></tr>"
				+ "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Fail</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ failCount + "</b></td>" + "<td bgcolor=red><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ failNumber + "%</b></span></td></tr></table>";

		for (int i = 0; i < usCoverage.size(); i += 4) {
			//int totalCov = false;
			int pc = Integer.parseInt((String) usCoverage.get(i + 1));
			int fc = Integer.parseInt((String) usCoverage.get(i + 2));
			int sc = Integer.parseInt((String) usCoverage.get(i + 3));
			int totalCov = pc + fc + sc;
			int tbdCount;
			if (totalCov < 2) {
				if (pc + fc != 0) {
					tbdCount = 2 - totalCov;
					totalCov = 2;
				} else {
					tbdCount = 2;
					totalCov = 2;
				}
			} else {
				tbdCount = sc;
			}

			passCov = (double) (100 * Integer.parseInt((String) usCoverage.get(i + 1)) / totalCov);
			failCov = (double) (100 * Integer.parseInt((String) usCoverage.get(i + 2)) / totalCov);
			tbdCov = (double) (100 * tbdCount / totalCov);
			
			tmp = row.replace("USNAME", (CharSequence) usCoverage.get(i))
					.replaceAll("COVERAGE", String.valueOf(passCov)).replace("FAIL", String.valueOf(failCov))
					.replace("TBD", String.valueOf(tbdCov));
			tmp1 = row1.replace("USNAME", (CharSequence) usCoverage.get(i)).replaceAll("COVERAGE",
					String.valueOf(passCov));
			tableRow = tableRow + tmp;
			tableRow1 = tableRow1 + tmp1;
		}

		String envDetails = "<table cellspacing=1 cellpadding=1  border=1 width=300><tr><td width =50%><h4><FONT COLOR=660000 FACE=Arial SIZE=4.5> <u>Environment:</u></h4></td><td  width=50% align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ env + "</b></td></tr></table>\n";

		try {
			FileInputStream fstream = new FileInputStream(indexResultFilename);
			DataInputStream in = new DataInputStream(fstream);

			String strLine;
			BufferedReader br;
			for (br = new BufferedReader(new InputStreamReader(in)); (strLine = br.readLine()) != null; buf
					.append(strLine)) {
				if (strLine.indexOf("END_TIME") != -1) {
					strLine = strLine.replace("END_TIME", endTime);
				}

				if (strLine.indexOf("COLORCODE") != -1) {
					strLine = strLine.replace("COLORCODE", color);
				}

				if (strLine.indexOf("TOTALTESTS") != -1) {
					strLine = strLine.replace("TOTALTESTS", String.valueOf(totalCount));
					strLine = strLine.replace("PASSNUMBER", String.valueOf(passNumber));
					strLine = strLine.replace("PASSCOUNT", String.valueOf(passCount));
					strLine = strLine.replace("FAILNUMBER", String.valueOf(failNumber));
					strLine = strLine.replace("FAILCOUNT", String.valueOf(failCount));
					strLine = strLine.replace("SKIPNUMBER", String.valueOf(skipNumber));
					strLine = strLine.replace("SKIPCOUNT", String.valueOf(skipCount));
				}

				if (strLine.indexOf("COVERAGE") != -1) {
					strLine = strLine.replace("COVERAGE", tableRow);
					content.append(strLine);
				}
			}

			in.close();
			br.close();
			FileOutputStream fos = new FileOutputStream(indexResultFilename);
			DataOutputStream output = new DataOutputStream(fos);
			output.writeBytes(buf.toString());
			output.close();
			fos.close();
			fstream.close();
			
		} catch (Exception var26) {
			System.err.println("Error: " + var26.getMessage());
		}

	}
	 /****************************************************************
	 * @function getDateDiff()--> Get test start date and end date diffrence 
	 * 
	 * @param String dateStart --> Script dateStart
	 * @param String dateStart --> Script dateStop
	 * @return String date diffrence
	 * ***************************************************************/
	 

	public static String getDateDiff(String dateStart, String dateStop) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;

		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
		} catch (ParseException var14) {
			var14.printStackTrace();
		}

		long diff = d2.getTime() - d1.getTime();
		System.out.println(diff);
		long diffSeconds = diff / 1000L % 60L;
		long diffMinutes = diff / 60000L % 60L;
		long diffHours = diff / 3600000L;
		String duration = diffHours + "h " + diffMinutes + "m " + diffSeconds + "s";
		//return String.valueOf(diff);
		return duration;
	}
 /****************************************************************
	 * @function getreportDate()--> Get test report date current date
	 * @return String testreportDate
	 * ***************************************************************/

	protected static String getreportDate() {
		testreportDate = TestUtil.now("yyyy-MM-dd HH:mm:ss");
		
		return testreportDate;
	}

	static {
		project = TestDriver.CONFIG.getProperty("project");
		browser = TestDriver.CONFIG.getProperty("testBrowser");
		testtype = TestDriver.CONFIG.getProperty("testtype");
	}
}
