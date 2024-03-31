package com.foxconn.electronics.tclicensereport.action;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.foxconn.electronics.tclicensereport.domain.DateRecordInfo;
import com.foxconn.electronics.tclicensereport.domain.WorkDayImportConstant;
import com.foxconn.electronics.util.MessageShow;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.ProgressBarThread;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

import cn.hutool.http.HttpUtil;

public class WorkDayImportAction extends AbstractAIFAction {
	
	private AbstractAIFApplication app = null;
	private Registry reg = null;
	private ProgressBarThread barThread = null;
	private static final String sdf = "yyyy/MM/dd";
	private TCSession session;
	
	public WorkDayImportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();
		reg = Registry.getRegistry("com.foxconn.electronics.tclicensereport.tclicensereport");
	}

	@Override
	public void run() {
		try {	
			barThread = new ProgressBarThread(reg.getString("Wait.MSG"), reg.getString("Import.MSG"));			
			InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
			if (!(targetComponent instanceof TCComponentDataset)) {
				MessageShow.warningMsgBox(reg.getString("SelectErr.MSG"), reg.getString("WARNING.MSG"));
				return;
			}		
			
			barThread.start();
			TCComponentDataset dataset = (TCComponentDataset) targetComponent;
			String datasetName = dataset.getProperty("object_name");
			if (!datasetName.endsWith(".xls") && !datasetName.endsWith(".xlsx")) {
				MessageShow.errorMsgBox(reg.getString("SelectDateset.MSG") + datasetName + reg.getString("ExcelErr.MSG"), reg.getString("ERROR.MSG"));
				throw new Exception();	
			}
			
			File excelFile = null;
			excelFile = TCUtil.getDataSetFile(dataset, ".xls");
			if (CommonTools.isEmpty(excelFile)) {
				excelFile = TCUtil.getDataSetFile(dataset, ".xlsx");
			}
			
			if (CommonTools.isEmpty(excelFile)) {
				MessageShow.errorMsgBox(reg.getString("SelectDateset.MSG") + datasetName + reg.getString("DownloadErr.MSG"), reg.getString("ERROR.MSG"));
				throw new Exception();				
			}			
			
			List<DateRecordInfo> list = analyseExcel(excelFile, dataset.getProperty("object_name"));
			list.removeIf(info -> CommonTools.isEmpty(info.getRecordDate())); // 去除日期为空的记录
			if (CommonTools.isEmpty(list)) {
				MessageShow.errorMsgBox(reg.getString("SelectDateset.MSG") + datasetName +reg.getString("ExcelAnalyseErr.MSG"), reg.getString("ERROR.MSG"));
				throw new Exception();				
			}			
			
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			System.out.println(gson.toJson(list));
			 String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/tc-service/tclicense/report/saveDateInfo";
//			String result = HttpUtil.post("http://127.0.0.1:8888/tclicense/report/saveDateInfo", gson.toJson(list), 60 * 1000);		
			String result = HttpUtil.post(url, gson.toJson(list), 60 * 1000);	
			System.out.println("==>> result: " + result);
			AjaxResult ajaxResult = gson.fromJson(result, AjaxResult.class);	
			if (((String)ajaxResult.get(AjaxResult.CODE_TAG)).equalsIgnoreCase(AjaxResult.STATUS_SUCCESS)) {
				MessageShow.infoMsgBox(reg.getString("ImportSuccess.MSG"), reg.getString("INFORMATION.MSG"));
			} else {
				MessageShow.errorMsgBox(reg.getString("ImportFailure.MSG"), reg.getString("ERROR.MSG"));				
			}
			barThread.stopBar();
		} catch (Exception e) {
			e.printStackTrace();
			barThread.stopBar();
			MessageShow.errorMsgBox(reg.getString("ImportFailure.MSG"), reg.getString("ERROR.MSG"));
		}
	}
	
	
	/**
	 * 解析Excel文件
	 * @param excelFile
	 * @param datasetFileName
	 * @return
	 * @throws Exception 
	 */
	private List<DateRecordInfo> analyseExcel(File excelFile, String datasetName) throws Exception {
		List<DateRecordInfo> dateRecordInfoList = new ArrayList<DateRecordInfo>();
		FileInputStream fis = new FileInputStream(excelFile);			
		Workbook workbook = WorkbookFactory.create(fis);
		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		if (!checkExcelTemplate(sheet, lastRowNum)) {
			MessageShow.warningMsgBox(reg.getString("SelectDateset.MSG") + datasetName + reg.getString("ExcelTemplateErr.MSG"), reg.getString("WARNING.MSG"));
			throw new Exception();	
		}
		
		if (!checkExcelContents(sheet, lastRowNum)) {
			MessageShow.warningMsgBox(reg.getString("SelectDateset.MSG") + datasetName + reg.getString("ExcelContentsErr.MSG"), reg.getString("WARNING.MSG"));
			throw new Exception();	
		}
		
		for (int i = 0; i <= lastRowNum; i++) {
			try {
				DateRecordInfo info = new DateRecordInfo();
				if (CommonTools.isEmpty(sheet.getRow(i))) {
					continue;
				}			
				Cell cell = sheet.getRow(i).getCell(0);	
				if (CommonTools.isEmpty(cell)) {
					continue;
				}
				if (CommonTools.isEmpty(TCUtil.removeBlank(getCellValue(cell)))) {
					continue;
				}
				
				if (WorkDayImportConstant.ITEM_1.equals(TCUtil.removeBlank(getCellValue(cell)))) {
					continue;
				}				
				
				info.setRecordDate(TCUtil.removeBlank(getCellValue(cell)));
				
				cell = sheet.getRow(i).getCell(1);
				if (CommonTools.isNotEmpty(cell)) {
					info.setWorkingDayMainland(TCUtil.removeBlank(getCellValue(cell)));
				}
				
				cell = sheet.getRow(i).getCell(2);
				if (CommonTools.isNotEmpty(cell)) {
					info.setWorkingDayTaiwan(TCUtil.removeBlank(getCellValue(cell)));
				}		
				
				dateRecordInfoList.add(info);
				System.out.println("第" + (i + 1) + "行解析完成");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("第" + (i + 1) + "行解析失败"); 				
			}			
		}
		return dateRecordInfoList;
	}
	
	
	/**
	 * 校验Excel模板
	 * @param sheet
	 * @param lastRowNum
	 * @return
	 * @throws EncryptedDocumentException
	 * @throws IOException
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
			if (WorkDayImportConstant.ITEM_1.equals(TCUtil.removeBlank(getCellValue(cell)))) {
				cell = sheet.getRow(i).getCell(1);
				if (WorkDayImportConstant.ITEM_2.equals(TCUtil.removeBlank(getCellValue(cell)))) {
					cell = sheet.getRow(i).getCell(2);
					if (WorkDayImportConstant.ITEM_3.equals(TCUtil.removeBlank(getCellValue(cell)))) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}
	

	/**
	 * 校验Excel模板内容
	 * @param sheet
	 * @param lastRowNum
	 * @return
	 */
	private boolean checkExcelContents(Sheet sheet, int lastRowNum) {
		boolean flag = true;
		for (int i = 0; i <= lastRowNum; i++) {
			if (CommonTools.isEmpty(sheet.getRow(i))) {
				continue;
			}
			
			if (CommonTools.isEmpty(sheet.getRow(i).getCell(0))) {
				continue;
			}
			
			Cell cell = sheet.getRow(i).getCell(0);
			if (WorkDayImportConstant.ITEM_1.equals(TCUtil.removeBlank(getCellValue(cell)))) {
				continue;
			}
			
			String dateTime = TCUtil.removeBlank(getCellValue(cell));
			if (CommonTools.isEmpty(dateTime)) {
				continue;
			}
			if (!TCUtil.isDate(dateTime)) { // 判断字符串是否为日期格式
				flag = false;
				break;
			}		
			
		}
		return flag;
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
			if (DateUtil.isCellDateFormatted(cell)) { // 判断是否为日期类型
				Date date = cell.getDateCellValue();
				value = new SimpleDateFormat(sdf).format(date);
			} else {
				value = cell.getNumericCellValue() + "";
			}			
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
