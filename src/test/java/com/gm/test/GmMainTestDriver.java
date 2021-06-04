/**************************************************
*File Name:-GmMainTestDriver.java
*This class is to initialize the objects of Keywords and 
*Test Driver classes and invokes the Test Driver.
********************************************************/
package com.gm.test;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gm.core.Keywords;
import com.gm.core.TestDriver;
import com.gm.utils.APIException;

public class GmMainTestDriver {
	public static void main(String[] args) throws SecurityException, IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, IOException, InterruptedException, APIException {
		
		Keywords kw = new Keywords();
		System.out.println("Initilaization.........");
		TestDriver td = new TestDriver(kw, System.getProperty("user.dir") + "/Config/config.properties");
		System.out.println("CSMAppsAutomation Frame work version "+td.CONFIG.getProperty("CSMAppsAutomation_Version"));
		System.out.println("Start .........");
		td.start();
	}

}
