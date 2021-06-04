/**************************************************
*File Name:-XlHelper.java
*This class is implemented as class has the methods
*to read and update the excel cell data and verifying the test suite format.
********************************************************/
package com.gm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gm.core.Constants;
import com.gm.core.TestDriver;

public class XlHelper {
	private static Logger logger = LoggerFactory.getLogger(XlHelper.class);
	public String path;
	public FileInputStream fis = null;
	public FileOutputStream fileOut = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;

	/****************************************************************
	 * @function XlHelper()--> Reads the Suite.xlsx
	 * 
	 * @param String path -->location of Suite.xlsx
	 * 
	 * ***************************************************************/
	
	public XlHelper(String path) {
		this.path = path;

		try {
			this.fis = new FileInputStream(path);
			this.workbook = new XSSFWorkbook(this.fis);
			this.sheet = this.workbook.getSheetAt(0);
			this.fis.close();
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}
	
	/****************************************************************
	 * @function verifyMainSuiteFormat()--> Verify the test suite sheet format
	 * 
	 * @return Boolean -->True/False
	 * ***************************************************************/

	public boolean verifyMainSuiteFormat() {
		if (!this.isSheetExist(Constants.TEST_SUITE_SHEET)) {
			logger.error(Constants.TEST_SUITE_SHEET + " Sheet Not Found .. Please check if there any typos");
			return false;
		} else {
			this.row = this.sheet.getRow(0);
			if (this.row.getCell(0).getStringCellValue().trim().equalsIgnoreCase(Constants.Test_Suite_ID)
					&& this.row.getCell(1).getStringCellValue().trim().equalsIgnoreCase("Description")
					&& this.row.getCell(2).getStringCellValue().trim().equalsIgnoreCase(Constants.RUNMODE)) {
				return true;
			} else {
				logger.error("Column names not defined properly .. Please check if there any typos");
				return false;
			}
		}
	}
	
	/****************************************************************
	 * @function verifyTestSuiteFormat()--> Verify the Current Module test case and step sheet format
	 * 
	 * @return Boolean -->True/False
	 * ***************************************************************/

	public boolean verifyTestSuiteFormat() {
		if (!this.isSheetExist(Constants.TEST_CASES_SHEET)) {
			logger.error(Constants.TEST_CASES_SHEET + " Sheet Not Found .. Please check if there any typos");
			return false;
		} else if (!this.isSheetExist(Constants.TEST_STEPS_SHEET)) {
			logger.error(Constants.TEST_STEPS_SHEET + " Sheet Not Found .. Please check if there any typos");
			return false;
		} else {
			this.row = this.sheet.getRow(0);
			List<String> cols = this.getColumns();
			if (cols.contains(Constants.TCID) && cols.contains("USID") && cols.contains("Description")
					&& cols.contains(Constants.RUNMODE)) {
				this.sheet = this.workbook.getSheetAt(1);
				this.row = this.sheet.getRow(0);
				cols = this.getColumns();
				if (cols.contains(Constants.TCID) && cols.contains("Description") && cols.contains(Constants.KEYWORD)
						&& cols.contains(Constants.OBJECT) && cols.contains(Constants.DATA)
						&& cols.contains("Proceed_on_Fail")) {
					this.sheet = this.workbook.getSheetAt(0);
					return true;
				} else {
					logger.error(
							"Column names not defined properly in 'Test Steps' Sheet.. Please check if there any typos");
					logger.error("Defined Columns are " + TestUtil.getListAsString(cols));
					logger.error("Expected Columns are TCID,TSID,Description,Keyword,Object,Data,Proceed_on_Fail");
					return false;
				}
			} else {
				logger.error(
						"Column names not defined properly in 'Test Cases' Sheet.. Please check if there any typos");
				logger.error("Defined Columns are " + TestUtil.getListAsString(cols));
				logger.error("Expected Columns are TCID,USID,Description,Runmode,Dependency,Status");
				return false;
			}
		}
	}

	/****************************************************************
	 * @function getColumns()--> Collect Columns Data of current sheet(Headings)
	 * 
	 * @return List<String> -->List of columns data
	 * ***************************************************************/
	public List<String> getColumns() {
		List<String> cols = new ArrayList();

		for (int i = 0; i < this.row.getLastCellNum(); ++i) {
			cols.add(this.row.getCell(i).getStringCellValue().trim());
		}

		return cols;
	}
	/****************************************************************
	 * @function getRowCount()--> Get Row count of current sheet
	 * 
	 * @param String sheetName--> 
	 * 
	 * @return int --> Rows count number
	 * ***************************************************************/

	public int getRowCount(String sheetName) {
		int index = this.workbook.getSheetIndex(sheetName);
		if (index == -1) {
			return 0;
		} else {
			this.sheet = this.workbook.getSheetAt(index);
			int number = this.sheet.getLastRowNum() + 1;
			return number;
		}
	}

	/****************************************************************
	 * @function getCellData()--> Get cell data of current Row in sheet form the  suite level
	 * 
	 * @param String sheetName--> Suite.xlsx sheet(or)Test cases sheet(or)Test steps sheet
	 * @param String colName--> Heading of Column name in current sheet(Ex:TSID)
	 * @param String rowNum--> Current row number in sheet(row number dynamically change up to last row)
	 * 
	 * @return String --> Cell data
	 * ***************************************************************/
	public String getCellData(String sheetName, String colName, int rowNum) {// in SuiteHelper
		try {
			if (rowNum <= 0) {
				return "";
			} else {
				int index = this.workbook.getSheetIndex(sheetName);
				int col_Num = -1;
				if (index == -1) {
					return "";
				} else {
					this.sheet = this.workbook.getSheetAt(index);
					this.row = this.sheet.getRow(0);

					for (int i = 0; i < this.row.getLastCellNum(); ++i) {
						if (this.row.getCell(i).getStringCellValue().trim().equals(colName.trim())) {
							col_Num = i;
						}
					}

					if (col_Num == -1) {
						return "";
					} else {
						this.sheet = this.workbook.getSheetAt(index);
						this.row = this.sheet.getRow(rowNum - 1);
						if (this.row == null) {
							return "";
						} else {
							this.cell = this.row.getCell(col_Num);
							if (this.cell == null) {
								return "";
							} else if (this.cell.getCellType() ==1) {
								return this.cell.getStringCellValue();
							} else if (this.cell.getCellType() != 0 && this.cell.getCellType() != 2) {
								if (this.cell.getCellType() == 3) {
									return "";
								} else {
									return String.valueOf(this.cell.getBooleanCellValue());
								}
							} else {
								String cellText = this.cell.getRawValue();
								/*
								 * if (HSSFDateUtil .isCellDateFormatted(this.cell)) { double d =
								 * this.cell.getNumericCellValue(); Calendar cal = Calendar.getInstance();
								 * cal.setTime(HSSFDateUtil.getJavaDate(d)); cellText =
								 * String.valueOf(cal.get(1)).substring(2); cellText = cal.get(5) + "/" +
								 * cal.get(2) + 1 + "/" + cellText; }
								 */

								return cellText;
							}
						}
					}
				}
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			return "row " + rowNum + " or column " + colName + " does not exist in xls";
		}
	}
/****************************************************************
	 * @function getCellData()--> Get cell data of current Row in sheet
	 * 
	 * @param String sheetName--> Suite.xlsx sheet(or)Test cases sheet(or)Test steps sheet
	 * @param String colName--> Heading of Column name in current sheet(Ex:TSID)
	 * @param String rowNum--> Current row number in sheet(row number dynamically change up to last row)
	 * 
	 * @return String --> Cell data
	 * ***************************************************************/


	public String getCellData(String sheetName, int colNum, int rowNum) {
		try {
			if (rowNum <= 0) {
				return "";
			} else {
				int index = this.workbook.getSheetIndex(sheetName);
				if (index == -1) {
					return "";
				} else {
					this.sheet = this.workbook.getSheetAt(index);
					this.row = this.sheet.getRow(rowNum - 1);
					if (this.row == null) {
						return "";
					} else {
						this.cell = this.row.getCell(colNum);
						if (this.cell == null) {
							return "";
						} else if (this.cell.getCellType() == 1) {
							return this.cell.getStringCellValue();
						} else if (this.cell.getCellType() != 0 && this.cell.getCellType() != 2) {
							return this.cell.getCellType() == 3 ? "" : String.valueOf(this.cell.getBooleanCellValue());
						} else {
							String cellText = this.cell.getRawValue();
							if (HSSFDateUtil.isCellDateFormatted(this.cell)) {
								double d = this.cell.getNumericCellValue();
								Calendar cal = Calendar.getInstance();
								cal.setTime(HSSFDateUtil.getJavaDate(d));
								cellText = String.valueOf(cal.get(1)).substring(2);
								cellText = cal.get(2) + 1 + "/" + cal.get(5) + "/" + cellText;
							}

							return cellText;
						}
					}
				}
			}
		} catch (Exception var9) {
			var9.printStackTrace();
			return "row " + rowNum + " or column " + colNum + " does not exist  in xls";
		}
	}

	/****************************************************************
	 * @function isSheetExist()--> Verify  the sheet availability
	 * 
	 * @return boolean --> True/False
	 * ***************************************************************/
	public boolean isSheetExist(String sheetName) {
		int index = this.workbook.getSheetIndex(sheetName);
		if (index == -1) {
			index = this.workbook.getSheetIndex(sheetName.toUpperCase());
			return index != -1;
		} else {
			return true;
		}
	}

	/****************************************************************
	 * @function getColumnCount()--> get the   Coloumn count
	 * 
	 * @return int sheetName -->
	 * ***************************************************************/
	public int getColumnCount(String sheetName) {
		if (!this.isSheetExist(sheetName)) {
			return -1;
		} else {
			this.sheet = this.workbook.getSheet(sheetName);
			this.row = this.sheet.getRow(0);
			return this.row == null ? -1 : this.row.getLastCellNum();
		}
	}
/****************************************************************
	 * @function getCellRowNum()--> Get the Row number
	 * 
	 * @Parameter String sheetName -->
	  @return int row count
	 * ***************************************************************/

	public int getCellRowNum(String sheetName, String colName, String cellValue) {
		for (int i = 2; i <= this.getRowCount(sheetName); ++i) {
			if (this.getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
				return i;
			}
		}

		return -1;
	}

/****************************************************************
	 * @function updateExcel()--> Update the Excel sheet
	 * 
	 * @Parameter String sheetName -->
	 * @Parameter String colNum -->
	 * @Parameter String rowNum -->
	 * @Parameter String newString -->
	 * ***************************************************************/

	public void updateExcel(String sheetName, int colNum, int rowNum, Object newString) {
		try {
			this.fis = new FileInputStream(new File(this.path));
			this.workbook = new XSSFWorkbook(this.fis);
			this.sheet = this.workbook.getSheet(sheetName);
			Cell cell = null;
			cell = this.sheet.getRow(rowNum).getCell(colNum);
			if (cell == null) {
				this.sheet.getRow(rowNum).createCell(colNum).setCellValue(String.valueOf(newString));
			} else {
				cell.setCellValue(String.valueOf(newString));
			}

			this.fis.close();
			FileOutputStream output_file = new FileOutputStream(new File(this.path));
			this.workbook.write(output_file);
			output_file.close();
		} catch (IOException var7) {
			logger.info(var7.getMessage());
		}

	}
	/****************************************************************
	 * @function rqm_testcase_Updation()--> Update the Excel sheet
	 * 
	 * @Parameter String testStatus -->
	 * @Parameter String TSID -->
	 * @Parameter String moduleName -->
	 * @return Null
	 * ***************************************************************/
		
	public void rqm_testcase_Updation(String testStatus,String TSID,String moduleName)
    {
        String TSID1=TSID.replaceAll(" ", "").replaceAll("/","").replaceAll("_","");//Removes _,/,Spaces of the TC
        try {
            FileInputStream fis1 = new FileInputStream(new File(System.getProperty("user.dir")+"/RQM_Sheets/"+moduleName+"_RQM.xlx"));
            XSSFWorkbook workbook1 = new XSSFWorkbook(fis1);
            XSSFSheet sheet1 = workbook1.getSheetAt(0);
            String sheetName=workbook1.getSheetName(0);
            List<String> testcasenames=new ArrayList<String>();
            if(sheet1.getLastRowNum()!=0) {
                for(int row=1;row<=sheet1.getLastRowNum();row++)
                {
                    testcasenames.add(sheet1.getRow(row).getCell(1).getStringCellValue());
                }

                for(int tcname=0;tcname<testcasenames.size();tcname++)
                {
                    String rqmTCID1=testcasenames.get(tcname).replaceAll(" ", "").replaceAll("/","").replaceAll("_","");//Removes _,/,Spaces of the RQM Formate TCID
                    if(rqmTCID1.contentEquals(TSID1))
                    {
                        sheet1.getRow(tcname+1).createCell(10).setCellValue(testStatus);
                    }
                }
                this.fis.close();
                FileOutputStream output_file1 = new FileOutputStream(new File(System.getProperty("user.dir")+"/RQM_Sheets/"+moduleName+"_RQM.xlx"));
                workbook1.write(output_file1);
                output_file1.close();
            }else {
                TestDriver.logger.error(sheetName+" have Empty rows or sheet may correpted");
            }
        }
        catch(IOException var7){logger.info(var7.getMessage());
        }
    }

}
