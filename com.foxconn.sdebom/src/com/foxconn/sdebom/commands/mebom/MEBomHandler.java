package com.foxconn.sdebom.commands.mebom;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;

public class MEBomHandler extends AbstractHandler {
	public String outPath;
	private TCComponentBOMLine topBOMLine;
	private BOMInfo topBomInfo;
	private ProgressMonitorDialog progressMonitorDialog;
	public int maximum = 1;
	public int rowInt = 0;
	private String[] proname = new String[] {"bl_sequence_no","bl_item_item_id","bl_rev_d9_EnglishDescription","bl_Part Revision_d9_SupplierZF",
			"bl_quantity","bl_Part Revision_d9_Un","bl_rev_item_revision_id"};

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			AbstractAIFUIApplication currentApplication = AIFUtility.getCurrentApplication();
			if (currentApplication instanceof AbstractPSEApplication) {
				String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				outPath = System.getProperty("java.io.tmpdir")+"\\导出" + dateString + ".xlsx";
				
				AbstractPSEApplication pseApp = (AbstractPSEApplication) currentApplication;
				topBOMLine = pseApp.getTopBOMLine();
				rowInt = 0;
				
				AIFComponentContext[] bomlineChildrens = topBOMLine.getChildren();
				if(bomlineChildrens !=null && bomlineChildrens.length > 0) {
					maximum = bomlineChildrens.length + 1;
				}
				
				IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("正在导出ME BOM请等待...", maximum);
						try {
							exportBOMLine(monitor);
						} catch (InterruptedException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						monitor.done();
						progressMonitorDialog.setBlockOnOpen(false);
					}
				};
				
				try {
					progressMonitorDialog = new ProgressMonitorDialog(AIFUtility.getActiveDesktop().getShell());
					progressMonitorDialog.run(true, false, iRunnableWithProgress);
				} catch (InvocationTargetException | InterruptedException e) {
					progressMonitorDialog.setBlockOnOpen(false);
					e.printStackTrace();
				}

			} else {
				MessageBox.post("结构管理器中行此操作！", "错误", MessageBox.ERROR);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public void exportBOMLine(IProgressMonitor monitor) throws Exception {
		try {
			AIFComponentContext[] bomlineChildrens = topBOMLine.getChildren();
			String[] topProperties = topBOMLine.getProperties(proname);
			topBomInfo = new BOMInfo(topProperties);
			
			int level = 0;
			
			if (bomlineChildrens != null && bomlineChildrens.length > 0) {
				getBOMLine(topBOMLine, topBomInfo,monitor,level+1);
			}
			//输出Excel
			exportExcel(topBomInfo, outPath);
			monitor.worked(1);
			
			//执行完打开
			Runtime.getRuntime().exec("cmd /c start  " + "\"\" " + "\"" + outPath + "\"");
		} catch (TCException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	
	}
	
	public void getBOMLine(TCComponentBOMLine bomline, BOMInfo bomInfo, IProgressMonitor monitor, int level) throws Exception{
		ArrayList<BOMInfo> infoList = new ArrayList<BOMInfo>();
		
		AIFComponentContext[] bomlineChildrens = bomline.getChildren();
		if(bomlineChildrens!=null && bomlineChildrens.length > 0) {
			
			for (int i = 0; i < bomlineChildrens.length; i++) {
				InterfaceAIFComponent component = bomlineChildrens[i].getComponent();
				if(component instanceof TCComponentBOMLine) {
					if(level == 1) 
						monitor.worked(1);
					
					TCComponentBOMLine children = (TCComponentBOMLine)component;
					String[] properties = children.getProperties(proname);
					boolean substitute = children.isSubstitute();
					if(substitute) {
						InterfaceAIFComponent component_s = bomlineChildrens[i-1].getComponent();
						String property = component_s.getProperty("bl_sequence_no");
						
						properties[0] = property;
					}
					BOMInfo bomInfo1 = new BOMInfo(properties);
					getBOMLine(children, bomInfo1, monitor,level+1);
					
					infoList.add(bomInfo1);
				}
			}
			bomInfo.setChildrens(infoList);
		}
	}
	
	public void exportExcel(BOMInfo topBomInfo, String fileName) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);// 文件已打开，请关闭
		try {
			XSSFWorkbook xwb = new XSSFWorkbook();
			XSSFSheet xSheet = xwb.createSheet("ME BOM");
			xSheet.setColumnWidth((short) 0, (short) 2500);
			xSheet.setColumnWidth((short) 1, (short) 2500);
			xSheet.setColumnWidth((short) 2, (short) 5500);
			xSheet.setColumnWidth((short) 3, (short) 10500);
			xSheet.setColumnWidth((short) 4, (short) 3500);
			xSheet.setColumnWidth((short) 5, (short) 3500);
			xSheet.setColumnWidth((short) 6, (short) 3500);
			xSheet.setColumnWidth((short) 7, (short) 3500);
			xSheet.setColumnWidth((short) 8, (short) 3500);
			
//			XSSFRow rowx = xSheet.createRow(0);
//			// 设置表头
//			tableTop(rowx,xwb);
//			//输出topBOMLine属性
//			phantBOMLine(xwb,xSheet, topBomInfo);
			//循环写入子健属性
			writerExcel(xwb, xSheet, topBomInfo);
			
			//保存关闭
			xwb.write(out);
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}  finally {
			out.close();
		}
		
		
	}
	
	public void writerExcel(XSSFWorkbook xwb, XSSFSheet xSheet,BOMInfo topBomInfo) {
		// 设置表头
		rowInt = rowInt + 1;
		XSSFRow rowx = xSheet.createRow(rowInt);
		rowInt = rowInt + 1;
		tableTop(rowx,xwb);
		phantBOMLine(xwb,xSheet, topBomInfo);
		
		ArrayList<BOMInfo> childrens = topBomInfo.getChildrens();
		for (BOMInfo bomInfo : childrens) {
			String bl_sequence = bomInfo.getBl_sequence();
			String itemid = bomInfo.getItemid();
			String description = bomInfo.getDescription();
			String supplier = bomInfo.getSupplier();
			String usage = bomInfo.getUsage();
			String un = bomInfo.getUn();
			String location = bomInfo.getLocation();
			String revision = bomInfo.getRevision();
			
			XSSFRow createRow = xSheet.createRow(rowInt);
			rowInt = rowInt + 1;
			XSSFCell createCell0 = createRow.createCell(0);
			createCell0.setCellValue(bl_sequence);
			XSSFCell createCell1 = createRow.createCell(1);
			ArrayList<BOMInfo> childrens2 = bomInfo.getChildrens();
			if(childrens2!=null && childrens2.size() > 0) 
				createCell1.setCellValue("*");
			
			XSSFCell createCell2 = createRow.createCell(2);
			createCell2.setCellValue(itemid);
			XSSFCell createCell3 = createRow.createCell(3);
			createCell3.setCellValue(description);
			XSSFCell createCell4 = createRow.createCell(4);
			createCell4.setCellValue(supplier);
			XSSFCell createCell5 = createRow.createCell(5);
			createCell5.setCellValue(usage);
			XSSFCell createCell6 = createRow.createCell(6);
			createCell6.setCellValue(un);
			XSSFCell createCell7 = createRow.createCell(7);
			createCell7.setCellValue(location);
			XSSFCell createCell8 = createRow.createCell(8);
			createCell8.setCellValue(revision);
		}
		
		
		for (BOMInfo bomInfo : childrens) {
			ArrayList<BOMInfo> childrens2 = bomInfo.getChildrens();
			if(childrens2!=null && childrens2.size() > 0) 
				writerExcel(xwb,xSheet, bomInfo);
		}
		
	}
	
	public void tableTop(XSSFRow rowx,XSSFWorkbook xwb) {
		// 设置表头
		String [] excelTop = new String [] {
				"ITEM","Phant.item","P/N","Description","Supplier","Usage","Un","Location","Revision"} ;
		
		XSSFCellStyle style = xwb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		for (int i = 0; i < excelTop.length; i++) {
			XSSFCell cell = rowx.createCell(i);
			setCellValue_style(cell, excelTop[i], style);
		}
	}
	
	public void phantBOMLine(XSSFWorkbook xwb,XSSFSheet xSheet,BOMInfo bomInfo) {
		String itemid = bomInfo.getItemid();
		String description = bomInfo.getDescription();
		String revision = bomInfo.getRevision();
		
		XSSFRow createRow = xSheet.createRow(rowInt);
		rowInt = rowInt + 1;
		
		XSSFCellStyle style = xwb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		XSSFCell createCell0 = createRow.createCell(0);
		setCellValue_style(createCell0, "", style);
		XSSFCell createCell1 = createRow.createCell(1);
		setCellValue_style(createCell1, "", style);
		XSSFCell createCell2 = createRow.createCell(2);
		setCellValue_style(createCell2, itemid, style);
		XSSFCell createCell3 = createRow.createCell(3);
		setCellValue_style(createCell3, description, style);
		XSSFCell createCell4 = createRow.createCell(4);
		setCellValue_style(createCell4, "", style);
		XSSFCell createCell5 = createRow.createCell(5);
		setCellValue_style(createCell5, "", style);
		XSSFCell createCell6 = createRow.createCell(6);
		setCellValue_style(createCell6, "", style);
		XSSFCell createCell7 = createRow.createCell(7);
		setCellValue_style(createCell7, "", style);
		XSSFCell createCell8 = createRow.createCell(8);
		setCellValue_style(createCell8, revision, style);
	}
	
	public void setCellValue_style(XSSFCell cell,String value, XSSFCellStyle style) {
		if(style !=null) {
			cell.setCellValue(value);
			cell.setCellStyle(style);
		} else {
			cell.setCellValue(value);
		}
	}
	
}
