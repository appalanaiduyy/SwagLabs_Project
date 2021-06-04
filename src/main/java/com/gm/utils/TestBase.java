/**************************************************
 *File Name:-TestBase.java
 *This class is implemented as class to update test result status in to Test rail.
 ********************************************************/
package com.gm.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.gm.utils.APIclient;
import com.gm.core.TestDriver;
import com.gm.utils.APIException;

public class TestBase {

	public static String projectName;
	public static String projectId;
	public static String runSuiteName;
	public static String runId;
	public static String tc_id;
	public static String tc_Title;

	public static String build;

	public static String TESTRAIL_USERNAME = TestDriver.CONFIG.getProperty("TestRail_UserName");
	public static String TESTRAIL_PASSWORD =TestDriver.CONFIG.getProperty("testRail_Password");
	public static String RAILS_ENGINE_URL = TestDriver.CONFIG.getProperty("Rails_engine_Url");

	static APIclient client = new APIclient(RAILS_ENGINE_URL);

	public static JSONObject json = new JSONObject();

	public static Map data = new HashMap();

	/****************************************************************
	 * @function getTestCaseStatus()--> Get the Get testcases Status
	 * 
	 * @param String tc_Name--> Suite.xlsx sheet(or)Test cases sheet(or)Test steps sheet
	 * @param String tc_Status--> Heading of Column name in current sheet(Ex:TSID)
	 * 	@return Null
	 * ***************************************************************/
	public void getTestCaseStatus(String tc_Name, String tc_Status, String elpsDuration)
			throws MalformedURLException, IOException, APIException {

		JSONArray project,mileStones,planNames,runEntried,runs,tests,subMilestones;
		JSONObject testPlandata,runEnrty;
		build = TestDriver.CONFIG.getProperty("buildNumber");
		String executedBy=TestDriver.CONFIG.getProperty("executedBy");
		TestDriver.logger.info(" Current working BUILD : " + build);
		client.setUser(TESTRAIL_USERNAME);
		client.setPassword(TESTRAIL_PASSWORD);
		//JSONArray project = new JSONArray();
		project = (JSONArray) client.sendGet("get_projects&is_completed=0");// getting uncompleted projects
		for (int projectCount = 0; projectCount < project.size(); projectCount++) {// interating upto all open projects
			json = (JSONObject) project.get(projectCount);
			projectName = json.get("name").toString();
			projectId = json.get("id").toString();
			

			if ((projectName.trim()).equalsIgnoreCase(TestDriver.CONFIG.getProperty("testRailProject").trim())) {// checking project name.
				//TestDriver.logger.info("Current project is : "+projectName);

				//JSONArray mileStones = new JSONArray();
				mileStones = (JSONArray) client.sendGet("get_milestones/" + projectId + "&is_completed=0");// Geting all open milestones in project
				int mileStoneSize = mileStones.size();
				for (int mileStoneCount = 0; mileStoneCount < mileStoneSize; mileStoneCount++) {//iterating milestones count
					json = (JSONObject)  mileStones.get(mileStoneCount);//Getting milestone one by one

					if (json.get("name").toString().equals(TestDriver.CONFIG.getProperty("mileStone"))) {// checking with required milestone----> need dynamic
						//TestDriver.logger.info("Current MileStone is : "+json.get("name"));
						//JSONArray subMilestones=new JSONArray();
						subMilestones=(JSONArray) json.get("milestones");//getting submiles stones

						for(int subMlsCount=0;subMlsCount<subMilestones.size();subMlsCount++) {//loop for submilestones
							json = (JSONObject)  subMilestones.get(subMlsCount);//name
							if(json.get("name").equals(TestDriver.CONFIG.getProperty("subMileStone"))) {//checking the sublimestone with required submilestone
								//TestDriver.logger.info("Current SubMileStone is : "+json.get("name"));
								String mlsName=json.get("name").toString();
								String mlsId=json.get("id").toString();
								//JSONArray planNames=new JSONArray();
								planNames =  (JSONArray) client.sendGet("get_plans/" + projectId+"&is_completed=0&milestone_id="+mlsId);//getting the planes from submilestone
								//System.out.println(planNames.size());
								for (int planNameCount = 0; planNameCount < planNames.size(); planNameCount++) {// iterating loop for required plan
									json = (JSONObject) planNames.get(planNameCount);
									String testPlane = json.get("name").toString();
									String testId = json.get("id").toString();
									if (testPlane.equals(TestDriver.CONFIG.getProperty("planName"))) {//checking with required plan
										//TestDriver.logger.info("Current Plan Name is : "+testPlane);
										System.out.println(" From Plane Name");
										//	JSONObject testPlandata = new JSONObject();
										testPlandata = (JSONObject) client.sendGet("get_plan/" + testId + "&is_completed=0");//getting plan Data
										//JSONArray runEntried = new JSONArray();
										runEntried = (JSONArray) testPlandata.get("entries");//Getting Plan Entries

										for (int entriesCount = 0; entriesCount < runEntried.size(); entriesCount++) {
											runEnrty = (JSONObject) runEntried.get(entriesCount);
											//JSONArray runs = new JSONArray();
											runs = (JSONArray) runEnrty.get("runs");

											for (int runsCount = 0; runsCount < runs.size(); runsCount++) {//loop for runs 
												System.out.println(" From loop 1");
												json = (JSONObject) runs.get(runsCount);
												String runName = json.get("name").toString();
												String runId = json.get("id").toString();
												//System.out.println(runName+" "+runId);
												System.out.println(runName.length());
												System.out.println(runName);
												System.out.println(TestDriver.CONFIG.getProperty("moduleName"));
												System.out.println(TestDriver.CONFIG.getProperty("moduleName").length());
												if (runName.equals(TestDriver.CONFIG.getProperty("moduleName"))) {// checking therequired module
												//	TestDriver.logger.info("Current Module Name is : "+runName);
													//JSONArray tests = new JSONArray();
													tests = (JSONArray) client.sendGet("get_tests/" + runId);
													for (int testCount = 0; testCount < tests.size(); testCount++) 
													{
														json = (JSONObject) tests.get(testCount);
														tc_Title = json.get("title").toString();
														tc_id = json.get("case_id").toString();

														if (!(tc_Name.equalsIgnoreCase(tc_Title))) 
														{
															String input = tc_Title.replace("/", "").trim();
															String output = input.replace("_", "").trim();
															input = output.replace("-", "").trim();
															output = input.replace(" ", "").trim();
															tc_Title = output.replace(" ", "").trim();

															input = tc_Name.replace("_", "");
															output = input.replace("/", "").trim();
															input = output.replace("-", "").trim();
															output = input.replace(" ", "").trim();
															tc_Name = output.replace(" ", "").trim();
														}									
														if (tc_Name.equalsIgnoreCase(tc_Title)) 
														{

															if (tc_Status.equalsIgnoreCase("Pass"))
															{
																data.put("status_id", new Integer(1));
																data.put("comment",
																		"The test case is Passed! and the Build Number is ------ " + build);
																data.put("elapsed", elpsDuration);
																data.put("custom_executed_by", executedBy);
																

																json = (JSONObject) client
																		.sendPost("add_result_for_case/" + runId + "/" + tc_id, data);
																System.out.println("The test case is Passed!-------Updated in testrail");

															} else if (tc_Status.equalsIgnoreCase("Fail"))
															{
																data.put("status_id", new Integer(5));
																data.put("comment",
																		"The test case is Failed! and the Build Number is ------ " + build);
																data.put("elapsed", elpsDuration);
																data.put("custom_executed_by", executedBy);
																json = (JSONObject) client
																		.sendPost("add_result_for_case/" + runId + "/" + tc_id, data);
																System.out.println("The test case is Failed!-------Updated in testrail");
															}

															break;

														}

													}



												}
											}
										}

									}

								}

							}
						}

					}
				}

			}

		}
	}

}


