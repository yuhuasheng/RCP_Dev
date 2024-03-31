package com.foxconn.electronics.issuemanagement.handler;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.electronics.util.ExcelUtils;
import com.foxconn.tcutils.util.IndeterminateLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentIssueReport;
import com.teamcenter.rac.kernel.TCComponentIssueReportRevision;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class ExportIssueHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		File file = TCUtil.openSaveFileDialog(shell,System.currentTimeMillis()+".xlsx");
		if(file == null) {
			return null;
		}
		InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
	
		new IndeterminateLoadingDialog(shell, "正在导出IssueList...", new IndeterminateLoadingDialog.Action() {
			
			@Override
			public void run(){
				XSSFWorkbook workbook = null;
				FileOutputStream fos = null;
				try {
					TCComponentIssueReportRevision tcItemRevision = (TCComponentIssueReportRevision) targetComponents[0];
					String type = tcItemRevision.getType();
					String sheetName = "";
					String tcPreferenceName = "";
					if(type.startsWith("D9_IR_HPRevision")){
						sheetName = "HP IssueList";
						tcPreferenceName = "D9_EXPORT_ISSUE_HP_PROP_MAPPING";
					}else if(type.startsWith("D9_IR_DELLRevision")) {
						sheetName = "DELL IssueList";
						tcPreferenceName = "D9_EXPORT_ISSUE_DELL_PROP_MAPPING";
					}else if(type.startsWith("D9_IR_LENOVORevision")) {
						sheetName = "Lenovo IssueList";
						tcPreferenceName = "D9_EXPORT_ISSUE_LENOVO_PROP_MAPPING";
					}
					// 读取首选项
					List<String> actualProps = TCUtil.getArrayPreference((TCSession)app.getSession(),TCPreferenceService.TC_preference_site,tcPreferenceName);

					workbook = new XSSFWorkbook();
					Font font = workbook.createFont();
			        font.setFontHeightInPoints((short) 10);
			        font.setFontName("Microsoft JhengHei UI");
			        font.setBold(true);
					CellStyle baseStyle = workbook.createCellStyle();
					baseStyle.setVerticalAlignment(VerticalAlignment.CENTER);
					baseStyle.setAlignment(HorizontalAlignment.CENTER);
					baseStyle.setWrapText(true);
					baseStyle.setBorderBottom(BorderStyle.THIN); //下边框
					baseStyle.setBorderLeft(BorderStyle.THIN);//左边框
					baseStyle.setBorderTop(BorderStyle.THIN);//上边框
					baseStyle.setBorderRight(BorderStyle.THIN);//右边框
					baseStyle.setFont(font);
					CellStyle titleStyle = workbook.createCellStyle();
					titleStyle.cloneStyleFrom(baseStyle);
					titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
					XSSFSheet sheet = workbook.createSheet(sheetName);
					// 寫標題
					XSSFRow titlRow = sheet.createRow(0);
					for(int i=0;i<actualProps.size();i++) {
						String columnMapping = actualProps.get(i);
						String[] split = columnMapping.split("=");
						String showProp = split[0];
						XSSFCell columnCell = titlRow.createCell(i);
						ExcelUtils.setCellStyleAndValue(columnCell, showProp, titleStyle);
					}
					for(int i=0;i<targetComponents.length;i++) {
						tcItemRevision = (TCComponentIssueReportRevision) targetComponents[i];
						XSSFRow row = sheet.createRow(i+1);
						for(int j=0;j<actualProps.size();j++) {
							String columnMapping = actualProps.get(j);
							String[] split = columnMapping.split("=");
							String actualProp = split[1];
							XSSFCell columnCell = row.createCell(j);
							String value = tcItemRevision.getProperty(actualProp);
							ExcelUtils.setCellStyleAndValue(columnCell, value, baseStyle);
						}
					}
					if(!file.exists()) {
						file.createNewFile();
					}
					fos = new FileOutputStream(file);
					workbook.write(fos);					
					Desktop.getDesktop().open(file);
				}catch(Exception e) {
					e.printStackTrace();
					MessageBox.post(shell, e.toString(), "ERR", MessageBox.ERROR);
				}finally {
					try {
						if(workbook!=null) {
							workbook.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if(fos!=null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		return null;
	}

}
