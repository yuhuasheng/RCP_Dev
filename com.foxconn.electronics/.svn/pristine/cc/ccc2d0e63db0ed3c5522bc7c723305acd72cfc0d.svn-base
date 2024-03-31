package com.foxconn.electronics.tcuserimport.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.foxconn.electronics.tcuserimport.dialog.TCUserImportDialog;
import com.foxconn.electronics.tcuserimport.domain.SpasToTCBean;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;

public class ExcelAnalyseTools {
	
	private TCUserImportDialog dialog = null;
	
	private final static int COLLENGTH = 4;
	
	public ExcelAnalyseTools(TCUserImportDialog dialog) {
		this.dialog = dialog;
	}
	
	
	public List<SpasToTCBean> analyseExcel(String excelFilePath) {
		List<SpasToTCBean> resultList = new ArrayList<SpasToTCBean>();
		Workbook workbook = null;
		FileInputStream fis = null;
		try {
			dialog.writeInfoLogText("************ 开始解析Excel文件 ************");
			long timeStart = System.currentTimeMillis();
			fis = new FileInputStream(excelFilePath);
			workbook = WorkbookFactory.create(fis);
			int sheetNumber = workbook.getNumberOfSheets();
			System.out.println("==>> sheet页数量一共为: " + sheetNumber);
			dialog.writeInfoLogText("文件名为: " + excelFilePath.substring(excelFilePath.lastIndexOf("\\") + 1) + ", 一共有 "+ sheetNumber + " 个sheet页");
			for (int i = 0; i < sheetNumber; i++) {
				dialog.writeInfoLogText("######## 开始解析第 " + (i + 1) + " 个sheet页" + " ########");
				Sheet sheet = workbook.getSheetAt(i);
				int lastRowNum = sheet.getLastRowNum();
				dialog.writeInfoLogText("######## 开始校验文件: " + (i + 1) + "个sheet页" + "########");
				if (!checkExcelTemplate(sheet, lastRowNum)) { //  校验Excel导入模板
					dialog.writeErrorLogText("###### 文件: " + excelFilePath + ", 校验不通过，无法进行下一步操作 ######");
					break;
				}
				
				dialog.writeInfoLogText("###### 文件: " + excelFilePath + ", 校验通过, 符合本次导入 ######");
				dialog.writeInfoLogText("### 第 " + (i + 1) + "  个sheet页总共有" + lastRowNum + " 行 ###");
				
				resultList.addAll(getBeanInfos(sheet, lastRowNum));
			}
			dialog.writeInfoLogText("************ 解析Excel文件完成 ************");
			long timeEnd = System.currentTimeMillis();
			dialog.writeInfoLogText("总共花费: " + CommonTools.getCostTime((timeEnd - timeStart)));
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			dialog.writeErrorLogText("文件为 " + excelFilePath + " 解析Excel文件发生错误");
			dialog.writeErrorLogText(TCUtil.getExceptionMsg(e));
			throw new RuntimeException(e);
		} finally {			
			if (CommonTools.isNotEmpty(fis)) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			if (CommonTools.isNotEmpty(workbook)) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
			
		}
	}
	
	
	/**
	 * 校验Excel导入模板
	 * @param sheet
	 * @param lastRowNum
	 * @return
	 */
	private boolean checkExcelTemplate(Sheet sheet, int lastRowNum) {
		boolean flag = false;
		for (int i = 0; i <= lastRowNum; i++) {
			if (CommonTools.isEmpty(sheet.getRow(i))) {
				continue;
			}
			
			if (CommonTools.isEmpty(sheet.getRow(i).getCell(0))) {
				continue;
			}
			
			Cell cell = sheet.getRow(i).getCell(0);
			if ("SPAS_USER_ID".equalsIgnoreCase(CommonTools.replaceBlank(getCellValue(cell)))) {
				cell = sheet.getRow(i).getCell(1);
				if ("SPAS_USER_NAME".equalsIgnoreCase(CommonTools.replaceBlank(getCellValue(cell)))) {
					cell = sheet.getRow(i).getCell(2);
					if ("TC_USER_ID".equalsIgnoreCase(CommonTools.replaceBlank(getCellValue(cell)))) {
						cell = sheet.getRow(i).getCell(3);
						if ("EMAIL".equalsIgnoreCase(CommonTools.replaceBlank(getCellValue(cell)))) {
							flag = true;
							break;
						}						
					}
				}
			}
		}
		return flag;
	}
	
	
	private List<SpasToTCBean> getBeanInfos(Sheet sheet, int lastRowNum) {
		SpasToTCBean bean = null;
		List<SpasToTCBean> list = new ArrayList<SpasToTCBean>();
		for (int startIndex = 1; startIndex <= lastRowNum; startIndex++) {
			try {
				Row row = sheet.getRow(startIndex);
				if (CommonTools.isEmpty(row)) {
					continue;
				}
				
				Cell cell_1 = row.getCell(0);
				Cell cell_2 = row.getCell(1);
				Cell cell_3 = row.getCell(2);
				Cell cell_4 = row.getCell(3);
				if (CommonTools.isEmpty(cell_1) && CommonTools.isEmpty(cell_2) && CommonTools.isEmpty(cell_3) && CommonTools.isEmpty(cell_4)) {
					continue;
				}
				
				bean = getBean(row);
				list.add(bean);
				dialog.writeInfoLogText("第 " + (startIndex + 1) + " 行 " + ", 解析完成");
			} catch (Exception e) {
				dialog.writeErrorLogText("第 " + (startIndex + 1) + " 行解析失敗");
				dialog.writeErrorLogText(TCUtil.getExceptionMsg(e));
				throw new RuntimeException(e);
			}
			
		}
		return list;
	}
	
	
	private SpasToTCBean getBean(Row row) {
		SpasToTCBean bean = new SpasToTCBean();
		for (int j = 0; j < COLLENGTH; j++) {
			Cell cell = row.getCell(j);			
			if (CommonTools.isEmpty(cell)) {
				continue;
			}
			String value = CommonTools.replaceBlank(getCellValue(cell));
			switch (j) {
			case 0:
				if (value.indexOf(".") > 0) {
					bean.setSpasUserId(value.substring(0, value.indexOf(".")));
				} else {
					bean.setSpasUserId(value);
				}				
				break;
			case 1:
				bean.setSpasUserName(value);
				break;
			case 2:
				if (value.indexOf(".") > 0) {
					bean.setTcUserId(value.substring(0, value.indexOf(".")));
				} else {
					bean.setTcUserId(value);
				}
				
				break;
			case 3:
				bean.setEmail(value);
				break;
			default:
				break;
			}
		}
		return bean;
	}
	
	
	/**
	 * 获取单元格内容
	 * 
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell) {
		String value = "";
		if (null == cell) {
			return value;
		}
		switch (cell.getCellType().name()) {
		case "STRING":
			value = cell.getRichStringCellValue().getString();
			break;
		case "NUMERIC":
			value = cell.getNumericCellValue() + "";
			break;
		case "BOOLEAN":
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case "BLANK":
			value = null;
			break;
		case "ERROR":
			value = null;
			break;
		case "FORMULA":
			value = cell.getCellFormula() + "";
			break;
		default:
			value = cell.toString();
			break;
		}
		return value;
	}	
	
}
