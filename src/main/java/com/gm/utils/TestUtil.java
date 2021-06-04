/**************************************************
*File Name:-TestUtil.java
*This class is implemented as class has the methods
*to update the test results currtent date and time.
********************************************************/
package com.gm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gm.core.Constants;

public class TestUtil {
	public static Logger logger = LoggerFactory.getLogger(TestUtil.class);
	static final String encoding = "UTF-8";

/****************************************************************
	 * @function now()--> Returns the currtent date and time for results updations
	 * 
	 * @param String dateFormat--> Current date and time
	 * 
	 * @return [String] date
	 * ***************************************************************/

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}
	/****************************************************************
	 * @function zip()--> Zip the require folder
	 * @param String filepath--> 
	 * 
	 * @return Null
	 * ***************************************************************/
	public static void zip(String filepath) {
		String ts = now("dd.MMM.yyyy_hh_mm_ss");
		ZipOutputStream out = null;
		File inFolder = null;
		File outFolder = null;
		String[] files = null;
		BufferedInputStream in = null;

		try {
			inFolder = new File(filepath);
			outFolder = new File("Reports" + ts + ".zip");
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
			byte[] data = new byte[1000];
			files = inFolder.list();
			System.out.println("zip size " + files.length);

			for (int i = 0; i < files.length; ++i) {
				in = new BufferedInputStream(new FileInputStream(inFolder.getPath() + "/" + files[i]), 1000);
				out.putNextEntry(new ZipEntry(files[i]));

				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}

				in.close();
			}

			out.closeEntry();
			out.flush();
			out.close();
		} catch (Exception var18) {
			var18.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
				inFolder = null;
				in.close();
				in = null;
			} catch (IOException var17) {
				var17.printStackTrace();
				logger.error("Exception in file : {}", var17, var17.getMessage());
			}

		}

	}
    /****************************************************************
	 * @function deleteFiles()--> Delete the require folder
	 * @param String filepath--> 
	 * 
	 * @return Null
	 * ***************************************************************/

	public static void deleteFiles(String filepath) {
		String[] files = null;
		File inFolder = null;

		try {
			inFolder = new File(filepath);
			files = inFolder.list();

			for (int i = 0; i < files.length; ++i) {
				File myFile = new File(inFolder, files[i]);
				myFile.delete();
				System.out.println("zip size ");
			}
		} catch (Exception var5) {
			var5.printStackTrace();
			logger.error("Exception in deleting the files : {}", var5, var5.getMessage());
		}

	}
	/****************************************************************
	 * @function checkDir()--> Check the dir
	 * @param String filepath--> 
	 * 
	 * @return Null
	 * ***************************************************************/

	public static void checkDir(String filepath) throws IOException {
		File theDir = new File(filepath);
		if (!theDir.exists()) {
			try {
				boolean result = theDir.mkdir();
				theDir.setWritable(true);
				if (!result) {
					System.out.println("Faile to Create DIR");
				}
			} catch (Exception var3) {
				logger.error("Exception in creating the directory : {}", var3, var3.getMessage());
			}
		}

	}
	/****************************************************************
	 * @function getArray()--> getArray
	 * @param String data,sparator
	 * 
	 * @return array
	 * ***************************************************************/

	public static String[] getArray(String data, String sparator) {
		return data.equals("") ? null : data.split(sparator);
	}

	public static String[] getArray(String data) {
		return getArray(data, ",");
	}

	public static List<String> getList(String data, String sparator) {
		if (data.equals("")) {
			return new ArrayList();
		} else {
			String[] str = data.split(sparator);
			List<String> lst = new ArrayList();

			for (int i = 0; i < str.length; ++i) {
				lst.add(str[i].trim());
			}

			return lst;
		}
	}

	public static List<String> getList(String data) {
		return getList(data, ",");
	}

	public static List<String> getList(String[] data) {
		List<String> lst = new ArrayList();

		for (int i = 0; i < 3; ++i) {
			lst.add(data[i].trim());
		}

		return lst;
	}

	public static String getListAsString(List<String> list) {
		String str = "";

		String s;
		for (Iterator var2 = list.iterator(); var2.hasNext(); str = str + "," + s) {
			s = (String) var2.next();
		}

		if (str.length() > 0) {
			str = str.substring(1, str.length());
		}

		return str;
	}

	/****************************************************************
	 * @function verifyResults()--> verify the test results
	 * @param String filepath--> 
	 * 
	 * @return String Status pass /fail
	 * ***************************************************************/

	public static String verifyResults(HashMap<String, ArrayList<String>> exp, HashMap<String, ArrayList<String>> act) {
		Iterator var2 = exp.keySet().iterator();

		ArrayList expData;
		ArrayList actData;
		do {
			if (!var2.hasNext()) {
				return Constants.KEYWORD_PASS;
			}

			String k = (String) var2.next();
			expData = (ArrayList) exp.get(k);
			actData = (ArrayList) act.get(k);
		} while ((new HashSet(expData)).equals(new HashSet(actData)));

		String tmp = "";
		String tmp1 = "";

		Iterator var8;
		String s;
		for (var8 = expData.iterator(); var8.hasNext(); tmp = tmp + s + ",") {
			s = (String) var8.next();
		}

		for (var8 = actData.iterator(); var8.hasNext(); tmp1 = tmp1 + s + ",") {
			s = (String) var8.next();
		}

		return Constants.KEYWORD_FAIL + " Exp Data " + tmp + " Did not match with Act Data " + tmp1;
	}
	/****************************************************************
	 * @function writeToFile()--> Write in the file
	 * @param String filename
	 * @param String data
	 * @return null
	 * ***************************************************************/
	public static void writeToFile(String fileName, String data) {
		try {
			File responseFile = new File(fileName);
			responseFile.createNewFile();
			OutputStreamWriter oSW = new OutputStreamWriter(new FileOutputStream(responseFile), "UTF-8");
			oSW.write(data);
			oSW.flush();
			oSW.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}

	}
}
