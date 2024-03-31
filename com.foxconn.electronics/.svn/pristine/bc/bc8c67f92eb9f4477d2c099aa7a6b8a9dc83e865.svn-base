package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.foxconn.electronics.matrixbom.domain.BomLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.export.domain.PackingMatrixExportBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.Pair;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class PackingMatrixExport20230825 implements IMatrixBOMExportWriter{
	private TCComponentBOMLine topBOMLine;
	public int maximum = 6;
	public int rowInt = 0;
	public TCSession session;

	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision, List<MatrixBOMExportBean> list,
			ArrayList<String> varList) throws Exception {
		return null;
	}

	public String exportBOM(TCComponentItemRevision matrixItemRevision) throws Exception {
		session = matrixItemRevision.getSession();
		String absolutePath = "";
		TCComponentBOMWindow createBOMWindow = TCUtil.createBOMWindow(session);
		topBOMLine = TCUtil.getTopBomline(createBOMWindow, matrixItemRevision);
		rowInt = 0;
		
		try {
			String objectName = matrixItemRevision.getProperty("object_name");
			String revName = matrixItemRevision.getProperty("item_revision_id");
			String customerModel = matrixItemRevision.getProperty("d9_CustomerModelNumber");
			String foxconnModel = matrixItemRevision.getProperty("d9_FoxconnModelNumber");
			Map<String,String> param = new HashMap<>();
			param.put("customerModel", customerModel);
			param.put("foxconnModel", foxconnModel);
			String innerResPath = "com/foxconn/electronics/matrixbom/export/template/MNT Packing Matrix Change List.xlsx";
			URL url = this.getClass().getClassLoader().getResource(innerResPath);			
			InputStream in = url.openStream();
			
			String dir = CommonTools.getFilePath("MNT Packing Matrix");
			CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
			String excelName = objectName.replace(" ","_") + "_" + revName + ".xlsx";
			File tempExcelFile = new File(dir + File.separator + excelName);
			
			
			try {
				FileUtil.writeFromStream(in, tempExcelFile);
			} catch (IORuntimeException e) {
				MessageBox.post(AIFUtility.getActiveDesktop(),"请关闭打开的Excel文件："+excelName, "提示", MessageBox.INFORMATION);
				
				return null;
			}
			
			in.close();
			
			System.out.println(tempExcelFile);
			ExcelWriter writer = ExcelUtil.getWriter(tempExcelFile);
			writer.setSheet("PA BOM list");
		
			//整体表头以及变体数据
			ArrayList<PackingMatrixExportBean> exportHoder = exportHoder();
			writeTop(writer,exportHoder,param);
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			writeHoder(writer, exportHoder,map);
			rowInt = rowInt+2+map.size()+1;
			
			//整理物料数据及输出
			LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> exportMatrix = exportMatrix(map);
			writeMatrix(writer, exportMatrix,map.size());
			
			//整理第三层数据及输出
			rowInt = rowInt+1;
			ArrayList<PackingMatrixExportBean> exportMatrixChildren = exportMatrixChildren();
			writeMatrixChildren(writer, exportMatrixChildren,map.size());
			
			// 写入Changelog
			writeChangeLog(writer,matrixItemRevision);
			
			try {
				int sheetIndex = writer.getWorkbook().getSheetIndex("sheet1");
				writer.getWorkbook().removeSheetAt(sheetIndex);
				writer.close();
			} catch (IORuntimeException e) {
				MessageBox.post(AIFUtility.getActiveDesktop(),"请关闭打开的Excel文件："+excelName, "提示", MessageBox.INFORMATION);
				
				return null;
			}
		
			absolutePath = tempExcelFile.getAbsolutePath();
			return absolutePath;
		} catch (IOException e) {
			// TODO: handle exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(createBOMWindow)) {
				try {
					createBOMWindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
		return absolutePath;
	}

	@Override
	public String writerDCN(TCComponentItemRevision matrixItemRevision, String dcnNo, List<MatrixBOMExportBean> list)
			throws Exception {
		String innerResPath = "com/foxconn/electronics/matrixbom/export/template/MNT Packing Matrix Change List.xlsx";
		URL url = this.getClass().getClassLoader().getResource(innerResPath);			
		InputStream in = url.openStream();
		String tmpDirPath = FileUtil.getTmpDirPath();
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyyMMdd = sdf.format(new Date());
		File dest = new File(tmpDirPath+objectName.replace(" ","_")+"_"+revName+"_ChangeList"+yyyyMMdd+".xlsx");
		FileUtil.writeFromStream(in, dest);
		in.close();
		ExcelWriter writer = ExcelUtil.getWriter(dest);
		writer.setSheet("changeList");
		Cell dcnCell = writer.getCell(0,2);
		dcnCell.setCellValue("DCN/ECN NO.:"+dcnNo);
		CellStyle leftStyle = writer.createCellStyle();
		leftStyle.setAlignment(HorizontalAlignment.LEFT);
		leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		leftStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		leftStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		leftStyle.setBorderTop(BorderStyle.THIN);// 上边框
		leftStyle.setBorderRight(BorderStyle.THIN);// 右边框
		dcnCell.setCellStyle(leftStyle);
		CellStyle textCellStyle = writer.createCellStyle();
		textCellStyle.cloneStyleFrom(leftStyle);
		textCellStyle.setAlignment(HorizontalAlignment.CENTER);
		textCellStyle.setWrapText(true);
		Font textFont = writer.createFont();
		textFont.setFontHeight((short) 300);
		textCellStyle.setFont(textFont);
		for(int i=0;i<list.size();i++) {
			MatrixBOMExportBean bean = list.get(i);
			int row = i + 5;
			writer.writeCellValue(0,row,i+1);
			setValueAndStyle(writer, 1, row, bean.bomItem, textCellStyle);
			setValueAndStyle(writer, 2, row, bean.code, textCellStyle);
			setValueAndStyle(writer, 3, row, bean.parentNumber, textCellStyle);
			setValueAndStyle(writer, 4, row, bean.hhpn, textCellStyle);
			setValueAndStyle(writer, 5, row, bean.description, textCellStyle);
			setValueAndStyle(writer, 6, row, bean.quantity, textCellStyle);
			setValueAndStyle(writer, 7, row, bean.unit, textCellStyle);
			setValueAndStyle(writer, 8, row, bean.factory, textCellStyle);
			setValueAndStyle(writer, 9, row, bean.revName, textCellStyle);
			setValueAndStyle(writer, 10, row, bean.remark, textCellStyle);
			writer.setRowHeight(row, 55);
		}
		writer.close();
		return dest.getAbsolutePath();
	}

	@Override
	public String createDCNwriterDCN(TCComponentItemRevision matrixItemRevision, String dcnNo,
			List<MatrixBOMExportBean> list, String path) throws Exception {
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		
		String dir = CommonTools.getFilePath("MNT Packing Matrix_ChangeList");
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		String excelName = objectName.replace(" ","_") + "_" + revName + ".xlsx";
		File dest = new File(dir + File.separator + excelName);
		
		InputStream in = new FileInputStream(path);
		FileUtil.writeFromStream(in, dest);
		in.close();
		
		ExcelWriter writer = ExcelUtil.getWriter(dest);
		writer.setSheet("changeList");
		Cell dcnCell = writer.getCell(0,2);
		dcnCell.setCellValue("DCN/ECN NO.:"+dcnNo);
		CellStyle leftStyle = writer.createCellStyle();
		leftStyle.setAlignment(HorizontalAlignment.LEFT);
		leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		leftStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		leftStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		leftStyle.setBorderTop(BorderStyle.THIN);// 上边框
		leftStyle.setBorderRight(BorderStyle.THIN);// 右边框
		dcnCell.setCellStyle(leftStyle);
		CellStyle textCellStyle = writer.createCellStyle();
		textCellStyle.cloneStyleFrom(leftStyle);
		textCellStyle.setAlignment(HorizontalAlignment.CENTER);
		textCellStyle.setWrapText(true);
		Font textFont = writer.createFont();
		textFont.setFontHeight((short) 300);
		textCellStyle.setFont(textFont);
		for(int i=0;i<list.size();i++) {
			MatrixBOMExportBean bean = list.get(i);
			int row = i + 5;
			writer.writeCellValue(0,row,i+1);
			setValueAndStyle(writer, 1, row, bean.bomItem, textCellStyle);
			setValueAndStyle(writer, 2, row, bean.code, textCellStyle);
			setValueAndStyle(writer, 3, row, bean.parentNumber, textCellStyle);
			setValueAndStyle(writer, 4, row, bean.hhpn, textCellStyle);
			setValueAndStyle(writer, 5, row, bean.description, textCellStyle);
			setValueAndStyle(writer, 6, row, bean.quantity, textCellStyle);
			setValueAndStyle(writer, 7, row, bean.unit, textCellStyle);
			setValueAndStyle(writer, 8, row, bean.factory, textCellStyle);
			setValueAndStyle(writer, 9, row, bean.revName, textCellStyle);
			setValueAndStyle(writer, 10, row, bean.remark, textCellStyle);
			writer.setRowHeight(row, 55);
		}
		
		writer.getWorkbook().setActiveSheet(2);
		writer.getWorkbook().removeSheetAt(3);
		
		writer.close();
		return dest.getAbsolutePath();
	}


	
	public ArrayList<PackingMatrixExportBean> exportHoder() throws Exception {
		ArrayList<PackingMatrixExportBean> list = new ArrayList<PackingMatrixExportBean>();
		try {
			TCComponentItemRevision itemRevision = topBOMLine.getItemRevision();
			AIFComponentContext[] related = itemRevision.getRelated("D9_HasVariantHolder_REL");
			if(related.length > 0) {
				TCComponentItem hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
				
				TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topHoder = TCUtil.getTopBomline(bomWindow, hoderItem.getLatestItemRevision());
				AIFComponentContext[] childrensAIF = topHoder.getChildren();
				for (int i = 0; i < childrensAIF.length; i++) {
					InterfaceAIFComponent component = childrensAIF[i].getComponent();
					if(component instanceof TCComponentBOMLine) {
						PackingMatrixExportBean bomInfo = new PackingMatrixExportBean();
						
						TCComponentBOMLine children = (TCComponentBOMLine)component;
						String bl_rev_d9_Customer = children.getProperty("bl_rev_d9_Customer");
						String bl_item_item_id = children.getProperty("bl_item_item_id");
						String description = children.getProperty("bl_rev_d9_EnglishDescription");
						String bl_quantity = children.getProperty("bl_quantity");
						String d9_Un = children.getProperty("bl_Part Revision_d9_Un");
						String d9_SupplierZF = children.getProperty("bl_Part Revision_d9_SupplierZF");
						String bl_occ_d9_Remark = children.getProperty("bl_occ_d9_Remark");
						String d9_version = children.getItemRevision().getProperty("d9_AcknowledgementRev");
						String d9_IsNew = children.getProperty("bl_occ_d9_IsNew");
						String d9_ShippingArea = children.getItemRevision().getProperty("d9_ShippingArea");
						
						String d9_ChineseDescription = children.getItemRevision().getProperty("d9_ChineseDescription");
						String d9_ManufacturerID = children.getItemRevision().getProperty("d9_ManufacturerID");
						String d9_ManufacturerPN = children.getItemRevision().getProperty("d9_ManufacturerPN");
						String d9_AcknowledgementRev = children.getItemRevision().getProperty("d9_AcknowledgementRev");
						String d9_SAPRev = children.getItemRevision().getProperty("d9_SAPRev");
						String d9_MaterialType = children.getItemRevision().getProperty("d9_MaterialType");
						String d9_MaterialGroup = children.getItemRevision().getProperty("d9_MaterialGroup");
						String d9_ProcurementMethods = children.getItemRevision().getProperty("d9_ProcurementMethods");
						
						int no = i+1;
						bomInfo.setNo(""+no);
						bomInfo.setIsSub("");
						bomInfo.setLevel(""+1);
						bomInfo.setD9_Customer(bl_rev_d9_Customer);
						bomInfo.setD9_ShippingArea(d9_ShippingArea);
						bomInfo.setItemid(bl_item_item_id);
						bomInfo.setDescription(description);
						if(StrUtil.isEmpty(bl_quantity)){
							bl_quantity = "1";
						}
						bomInfo.setBl_quantity(String.format("%.3f", Float.parseFloat(bl_quantity)*100));
						bomInfo.setUn(d9_Un);
						bomInfo.setD9_SupplierZF(d9_SupplierZF);
						bomInfo.setState(d9_IsNew);
						bomInfo.setRevision(d9_version);
						bomInfo.setEcn("");
						bomInfo.setRemark(bl_occ_d9_Remark);
						
						bomInfo.setD9_ChineseDescription(d9_ChineseDescription);
						bomInfo.setD9_ManufacturerID(d9_ManufacturerID);
						bomInfo.setD9_ManufacturerPN(d9_ManufacturerPN);
						bomInfo.setD9_AcknowledgementRev(d9_AcknowledgementRev);
						bomInfo.setD9_SAPRev(d9_SAPRev);
						bomInfo.setD9_MaterialType(d9_MaterialType);
						bomInfo.setD9_MaterialGroup(d9_MaterialGroup);
						bomInfo.setD9_ProcurementMethods(d9_ProcurementMethods);
						
						list.add(bomInfo);	
					}
				}
				bomWindow.close();
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void writeTop(ExcelWriter writer, ArrayList<PackingMatrixExportBean> exportHoder,Map<String,String> param) {
		// 设置表头
		Font font = writer.createFont();
		font.setColor(IndexedColors.WHITE1.getIndex());
		font.setBold(true);
		CellStyle blueBackgroundStyle = writer.createCellStyle();
		blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle.setFont(font);
		blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blueBackgroundStyle.setWrapText(true);
		
		Font font1 = writer.createFont();
		font1.setBold(true);
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setFont(font1);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle1.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
		blueBackgroundStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blueBackgroundStyle1.setWrapText(true);
		
		writer.merge(0, 0, 0, 4, "客戶型號", false);
		writer.getCell(0, 0).setCellStyle(blueBackgroundStyle);
		writer.setRowHeight(0, 50);
		String customerModel = param.get("customerModel");
		writer.writeCellValue(5, 0, customerModel);
		writer.setColumnWidth(5, 25);
		writer.getCell(5, 0).setCellStyle(blueBackgroundStyle1);
		writer.merge(0, 0, 6, 9, "Foxconn型號", false);
		writer.getCell(6, 0).setCellStyle(blueBackgroundStyle);
		String foxconnModel = param.get("foxconnModel");
		writer.merge(0, 0, 10, 17, foxconnModel, false);
		writer.getCell(10, 0).setCellStyle(blueBackgroundStyle1);
		
		
		CellStyle blueBackgroundStyle2 = writer.createCellStyle();
		blueBackgroundStyle2.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle2.setFont(font);
		blueBackgroundStyle2.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle2.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle2.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle2.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle2.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		blueBackgroundStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		writer.merge(1, 1, 0, 17, "料號編碼中心", false);
		writer.getCell(0, 1).setCellStyle(blueBackgroundStyle2);
		writer.setRowHeight(1, 25);
//		writer.writeCellValue(6, 1, " ");
//		writer.merge(1, 1, 6, 12, " ", false);
//		writer.getCell(6, 1).setCellStyle(blueBackgroundStyle2);
		writer.merge(1, 1, 18, 18+exportHoder.size()-1, "出貨地區物料使用", false);
		writer.getCell(18, 1).setCellStyle(blueBackgroundStyle2);
		
		
		CellStyle blueBackgroundStyle3 = writer.createCellStyle();
		blueBackgroundStyle3.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle3.setFont(font);
		blueBackgroundStyle3.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle3.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle3.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle3.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle3.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		blueBackgroundStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blueBackgroundStyle3.setWrapText(true);
		
		String [] excelTop3 = new String [] {"序號","替代料","階次","中文描述","Item\n(PN)","Description","用量Qyt\n(每100PC)",
				"單位","Supplier\n(中英文)","供應商\n(Vendor)\n(英文)","供應商\n(Vendor PN)","承認書版本","SAP Rev","物料類型\nMaterial type","物料群組\nMaterail group","狀態","採購方式\nProcurement Type","備注"} ;
		
		
		for (int i = 0; i < excelTop3.length; i++) {
			writer.writeCellValue(i, 2, excelTop3[i]);
			writer.setRowHeight(2, 50);
			if(i==3) {
				writer.setColumnWidth(i, 10);
			}else if(i==4){
				writer.setColumnWidth(i, 16);
			}else if(i==5){
				writer.setColumnWidth(i, 35);
			}else if(i==12){
				writer.setColumnWidth(i, 20);
			}else if(i==13){
				writer.setColumnWidth(i, 15);
			}else if(i==14){
				writer.setColumnWidth(i, 15);
			}else if(i==16){
				writer.setColumnWidth(i, 15);
			}else if(i==17){
				writer.setColumnWidth(i, 30);
			}else {
				writer.setColumnWidth(i, 10);
			}
			
			writer.getCell(i, 2).setCellStyle(blueBackgroundStyle3);
		}
		
		for (int i = 0; i < exportHoder.size(); i++) {
			PackingMatrixExportBean bomInfo = exportHoder.get(i);
			String d9_Customer = bomInfo.getD9_ShippingArea();
			
			writer.writeCellValue(18+i, 2, d9_Customer);
			writer.setRowHeight(2, 50);
			writer.setColumnWidth(18+i, 7);
			writer.getCell(18+i, 2).setCellStyle(blueBackgroundStyle3);
		}
	
	}
	
	public void writeHoder(ExcelWriter writer, ArrayList<PackingMatrixExportBean> exportHoder, LinkedHashMap<String, Integer> map) {
		Font font1 = writer.createFont();
		font1.setBold(true);
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setFont(font1);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		
		for (int i = 0; i < exportHoder.size(); i++) {
			int row = i+3;
			PackingMatrixExportBean bomInfo = exportHoder.get(i);
			writer.writeCellValue(3, row, bomInfo.getD9_ChineseDescription());
			writer.writeCellValue(9, row, bomInfo.getD9_ManufacturerID());
			writer.writeCellValue(10, row, bomInfo.getD9_ManufacturerPN());
			writer.writeCellValue(11, row, bomInfo.getD9_AcknowledgementRev());
			writer.writeCellValue(12, row, bomInfo.getD9_SAPRev());
			writer.writeCellValue(13, row, bomInfo.getD9_MaterialType());
			writer.writeCellValue(14, row, bomInfo.getD9_MaterialGroup());
			writer.writeCellValue(16, row, bomInfo.getD9_ProcurementMethods());
			String no = bomInfo.getNo();
			writer.writeCellValue(0, row, no);
			writer.getCell(0, row).setCellStyle(blueBackgroundStyle1);
			String isSub = bomInfo.getIsSub();
			writer.writeCellValue(1, row, isSub);
			writer.getCell(1, row).setCellStyle(blueBackgroundStyle1);
			String level = bomInfo.getLevel();
			writer.writeCellValue(2, row, level);
			writer.getCell(2, row).setCellStyle(blueBackgroundStyle1);
			String d9_ShippingArea = bomInfo.getD9_ShippingArea();
			writer.getCell(3, row).setCellStyle(blueBackgroundStyle1);
			String itemid = bomInfo.getItemid();
			writer.writeCellValue(4, row, itemid);
			writer.getCell(4, row).setCellStyle(blueBackgroundStyle1);
			String description = bomInfo.getDescription();
			writer.writeCellValue(5, row, description);
			writer.getCell(5, row).setCellStyle(blueBackgroundStyle1);
			String bl_quantity = bomInfo.getBl_quantity();
			writer.writeCellValue(6, row, bl_quantity);
			writer.getCell(6, row).setCellStyle(blueBackgroundStyle1);
			String un = bomInfo.getUn();
			writer.writeCellValue(7, row, un);
			writer.getCell(7, row).setCellStyle(blueBackgroundStyle1);
			String d9_SupplierZF = bomInfo.getD9_SupplierZF();
			writer.writeCellValue(8, row, d9_SupplierZF);
			writer.getCell(8, row).setCellStyle(blueBackgroundStyle1);
			String state = bomInfo.getState();
			writer.getCell(9, row).setCellStyle(blueBackgroundStyle1);
			writer.getCell(10, row).setCellStyle(blueBackgroundStyle1);
			writer.getCell(11, row).setCellStyle(blueBackgroundStyle1);
			String remark = bomInfo.getRemark();
			writer.getCell(12, row).setCellStyle(blueBackgroundStyle1);
			writer.getCell(13, row).setCellStyle(blueBackgroundStyle1);
			writer.getCell(14, row).setCellStyle(blueBackgroundStyle1);
			writer.writeCellValue(15, row, state);
			writer.getCell(15, row).setCellStyle(blueBackgroundStyle1);
			writer.getCell(16, row).setCellStyle(blueBackgroundStyle1);
			writer.writeCellValue(17, row, remark);
			writer.getCell(17, row).setCellStyle(blueBackgroundStyle1);
			for (int j = 0; j < exportHoder.size(); j++) {
				int conn = j+18;
				String d9_ShippingArea2 = exportHoder.get(j).getD9_ShippingArea();
				if(StrUtil.isNotEmpty(d9_ShippingArea2)&&d9_ShippingArea.equals(d9_ShippingArea2)) {
					writer.writeCellValue(conn, row, "◎");
					map.put(itemid, conn);
				} else {
					writer.writeCellValue(conn, row, "");
				}
				writer.getCell(conn, row).setCellStyle(blueBackgroundStyle1);
			}
			
		}
		
		System.out.println();
	}
	
	public LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> exportMatrix(LinkedHashMap<String, Integer> map) throws Exception {
		LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> mapitem = new LinkedHashMap<String, ArrayList<PackingMatrixExportBean>>();
		int no = 0;
		TCComponentBOMWindow bomWindow = null;
		try {
			//解包
			topBOMLine.refresh();
			AIFComponentContext[] childrens_Packed = topBOMLine.getChildren();
			for (AIFComponentContext aifchildren : childrens_Packed) {
				TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				if (children.isPacked()) {
					children.unpack();
					children.refresh();
				}
			}
			topBOMLine.refresh();
			
			AIFComponentContext[] childrens = topBOMLine.getChildren();
			if (childrens.length > 0) {
				for (int i = 0; i < childrens.length; i++) {
					no = no+1;
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
					getItem(mapitem, children, null, no, "");
					
					if(children.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
						for (int k = 0; k < listSubstitutes.length; k++) {
							getItem(mapitem, listSubstitutes[k], null,no,"替代料");
						}
					}
				}
			}
			
			
			TCComponentItemRevision itemRevision = topBOMLine.getItemRevision();
			AIFComponentContext[] related = itemRevision.getRelated("D9_HasVariantHolder_REL");
			if(related.length > 0) {
				TCComponentItem hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
				
				bomWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topHoder = TCUtil.getTopBomline(bomWindow, hoderItem.getLatestItemRevision());
				AIFComponentContext[] childrensAIF = topHoder.getChildren();
				for (int i = 0; i < childrensAIF.length; i++) {
					InterfaceAIFComponent component = childrensAIF[i].getComponent();
					if(component instanceof TCComponentBOMLine) {
						TCComponentBOMLine childrenMatrix = (TCComponentBOMLine)component;
						
						String chilHoder_id = childrenMatrix.getProperty("bl_item_item_id");
						// integer 是 Holder在Excel 裡面的列
						Integer variantCol = map.get(chilHoder_id);
						
						AIFComponentContext[] matrixAIF = childrenMatrix.getChildren();
						for (int j = 0; j < matrixAIF.length; j++) {
							InterfaceAIFComponent matrix = matrixAIF[j].getComponent();
							if(matrix instanceof TCComponentBOMLine) {
								TCComponentBOMLine matrixBOMLine = (TCComponentBOMLine)matrix;
								no = no+1;
								getItem(mapitem, matrixBOMLine, variantCol,no,"");
								
								if(matrixBOMLine.hasSubstitutes()) {
									TCComponentBOMLine[] listSubstitutes = matrixBOMLine.listSubstitutes();
									for (int k = 0; k < listSubstitutes.length; k++) {
										getItem(mapitem, listSubstitutes[k], variantCol,no,"替代料");
									}
								}
							}
						}
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			if (bomWindow != null) {
				bomWindow.close();
			}
		}
		
		return mapitem;
	}

	public void writeMatrix(ExcelWriter writer, LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> exportMatrix, int k) {
		Font ft = writer.createFont();
		ft.setFontName("Microsoft YaHei Light");
		ft.setFontHeightInPoints((short) 9);
		CellStyle blueBackgroundStyle = writer.createCellStyle();
		blueBackgroundStyle.setFont(ft);
		blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setFont(ft);
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		
		int mergeRow = 0;
		ArrayList<PackingMatrixExportBean> lastList = null;
		for (Entry<String,ArrayList<PackingMatrixExportBean>> entrySet : exportMatrix.entrySet()) {
			ArrayList<PackingMatrixExportBean> bomInfoList = entrySet.getValue();
			for (PackingMatrixExportBean bomInfo : bomInfoList){
				rowInt = rowInt+1;
				writer.writeCellValue(0, rowInt, bomInfo.getNo());				
				String isSub = bomInfo.getIsSub();
				writer.writeCellValue(1, rowInt, isSub);
				writer.writeCellValue(2, rowInt, bomInfo.getLevel());
				writer.writeCellValue(3, rowInt, bomInfo.getD9_ChineseDescription());
				writer.writeCellValue(4, rowInt,  bomInfo.getItemid());
				writer.writeCellValue(5, rowInt, bomInfo.getDescription());
				writer.writeCellValue(6, rowInt, bomInfo.getBl_quantity());
				writer.writeCellValue(7, rowInt, bomInfo.getUn());
				writer.writeCellValue(8, rowInt, bomInfo.getD9_SupplierZF());				
				writer.writeCellValue(9, rowInt, bomInfo.getD9_ManufacturerID());
				writer.writeCellValue(10, rowInt, bomInfo.getD9_ManufacturerPN());
				writer.writeCellValue(11, rowInt, bomInfo.getD9_AcknowledgementRev());
				writer.writeCellValue(12, rowInt, bomInfo.getD9_SAPRev());
				writer.writeCellValue(13, rowInt, bomInfo.getD9_MaterialType());
				writer.writeCellValue(14, rowInt, bomInfo.getD9_MaterialGroup());
				writer.writeCellValue(15, rowInt, bomInfo.getState());
				writer.writeCellValue(16, rowInt, bomInfo.getD9_ProcurementMethods());
				writer.writeCellValue(17, rowInt, bomInfo.getRemark());
				
				if("替代料".equals(isSub)) {
					writer.writeCellValue(6, rowInt, lastList.get(0).getBl_quantity());
					mergeRow++;
					Sheet sheet = writer.getSheet();
					removeMergedRegion(sheet, rowInt-mergeRow, 1);
					removeMergedRegion(sheet, rowInt-mergeRow, 3);
					removeMergedRegion(sheet, rowInt-mergeRow, 5);
					removeMergedRegion(sheet, rowInt-mergeRow, 17);
					try {
						writer.merge(rowInt-mergeRow, rowInt, 1, 1, isSub, false);
						writer.merge(rowInt-mergeRow, rowInt, 3, 3, bomInfo.getD9_ChineseDescription(), false);
						writer.merge(rowInt-mergeRow, rowInt, 5, 5, bomInfo.getDescription(), false);
						writer.merge(rowInt-mergeRow, rowInt, 17, 17, bomInfo.getRemark(), false);
						writer.getCell(1, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
						writer.getCell(3, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
						writer.getCell(5, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
						writer.getCell(17, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
					}catch(Exception e){
						
					}
				} else {
					mergeRow = 0;
				}
				lastList = entrySet.getValue();
				boolean getisChildren = bomInfo.getisChildren();
				for(int i=0;i<19;i++) {
					try {
						writer.getCell(i, rowInt).setCellStyle(getisChildren?blueBackgroundStyle:blueBackgroundStyle1);
					}catch (Exception e) {
						// igonre
					}
				}				
				
				ArrayList<Integer> parentList = bomInfo.getParent();
				for (int j = 0; j < k; j++) {
					int col = j+18;
					if(CommonTools.isNotEmpty(parentList)) {
						boolean find = false;
						for (Integer variantCol:parentList) {
							if(col == variantCol) {
								find = true;
								break;
							}
						}
						if(find) {
							String preferred = bomInfo.getPreferredMap().get(col);
							writer.writeCellValue(col, rowInt, preferred==null?"○":preferred);
						}else {
							writer.writeCellValue(col, rowInt, "");
						}
					}else {
						writer.writeCellValue(col, rowInt, "");
					}
					writer.getCell(col, rowInt).setCellStyle(getisChildren?blueBackgroundStyle:blueBackgroundStyle1);
				}
			}
		}
	}
	
	public void getItem(LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> mapitem,TCComponentBOMLine matrixBOMLine,Integer variantCol, int no,String isSub) throws TCException {
		
		String bl_item_item_id = matrixBOMLine.getProperty("bl_item_item_id");
		String bl_quantity = matrixBOMLine.getProperty("bl_quantity");		
		ArrayList<PackingMatrixExportBean> arrayList = mapitem.get(bl_item_item_id);
		// 原理：variantCol 如果不為空說明當前遍歷的是變體
		if(variantCol!=null) {
			if(CommonTools.isEmpty(arrayList)) {
				return;
			}
			// 這一列需要畫圓圈
			PackingMatrixExportBean bomInfo = arrayList.get(0);
			ArrayList<Integer> parent = bomInfo.getParent();
			if(CommonTools.isEmpty(parent)){				
				parent = new ArrayList<Integer>();
				bomInfo.setParent(parent);
			}
			// 做一個安全處理，防止重複添加
			if(!parent.contains(variantCol)){
				parent.add(variantCol);
			}
			if(bomInfo.getItemid().startsWith("7130") && !"替代料".equals(isSub)) {
				   bomInfo.getPreferredMap().put(variantCol, "○優選");
			}
			return;
		}
		
		if(CommonTools.isEmpty(arrayList)) {
			String bl_rev_d9_Customer = matrixBOMLine.getProperty("bl_rev_d9_Customer");
			String description = matrixBOMLine.getProperty("bl_rev_d9_EnglishDescription");
			String d9_Un = matrixBOMLine.getProperty("bl_Part Revision_d9_Un");
			String d9_SupplierZF = matrixBOMLine.getProperty("bl_Part Revision_d9_SupplierZF");
			String revision_id = matrixBOMLine.getItemRevision().getProperty("d9_AcknowledgementRev");
			String bl_occ_d9_Remark = matrixBOMLine.getProperty("bl_occ_d9_Remark");
			String state = matrixBOMLine.getProperty("bl_occ_d9_IsNew");
			String d9_ChineseDescription = matrixBOMLine.getItemRevision().getProperty("d9_ChineseDescription");
			String d9_ManufacturerID = matrixBOMLine.getItemRevision().getProperty("d9_ManufacturerID");
			String d9_ManufacturerPN = matrixBOMLine.getItemRevision().getProperty("d9_ManufacturerPN");
			String d9_AcknowledgementRev = matrixBOMLine.getItemRevision().getProperty("d9_AcknowledgementRev");
			String d9_SAPRev = matrixBOMLine.getItemRevision().getProperty("d9_SAPRev");
			String d9_MaterialType = matrixBOMLine.getItemRevision().getProperty("d9_MaterialType");
			String d9_MaterialGroup = matrixBOMLine.getItemRevision().getProperty("d9_MaterialGroup");
			String d9_ProcurementMethods = matrixBOMLine.getItemRevision().getProperty("d9_ProcurementMethods");
			
			PackingMatrixExportBean bomInfo = new PackingMatrixExportBean();
			//int no = i+1;
			bomInfo.setNo(""+no);
			bomInfo.setIsSub(isSub);
			bomInfo.setLevel(""+2);
			bomInfo.setD9_Customer(bl_rev_d9_Customer);
			bomInfo.setItemid(bl_item_item_id);
			bomInfo.setDescription(description);
			if(StrUtil.isEmpty(bl_quantity)) {
				bl_quantity = "1";
			}
			bomInfo.setBl_quantity(String.format("%.3f", Float.parseFloat(bl_quantity)*100));
			bomInfo.setUn(d9_Un);
			bomInfo.setD9_SupplierZF(d9_SupplierZF);
			bomInfo.setState(state);
			bomInfo.setRevision(revision_id);
			bomInfo.setEcn("");
			bomInfo.setRemark(bl_occ_d9_Remark);
			
			bomInfo.setD9_ChineseDescription(d9_ChineseDescription);
			bomInfo.setD9_ManufacturerID(d9_ManufacturerID);
			bomInfo.setD9_ManufacturerPN(d9_ManufacturerPN);
			bomInfo.setD9_AcknowledgementRev(d9_AcknowledgementRev);
			bomInfo.setD9_SAPRev(d9_SAPRev);
			bomInfo.setD9_MaterialType(d9_MaterialType);
			bomInfo.setD9_MaterialGroup(d9_MaterialGroup);
			bomInfo.setD9_ProcurementMethods(d9_ProcurementMethods);
			
			AIFComponentContext[] children = matrixBOMLine.getChildren();
			if(CommonTools.isNotEmpty(children)) {
				bomInfo.setisChildren(true);
			}
			
			ArrayList<PackingMatrixExportBean> list = new ArrayList<PackingMatrixExportBean>();
			list.add(bomInfo);
			mapitem.put(bl_item_item_id, list);
		}
	}
	
	public ArrayList<PackingMatrixExportBean> exportMatrixChildren() throws Exception {
		ArrayList<PackingMatrixExportBean> list = new ArrayList<PackingMatrixExportBean>();
		int no = 0;
		try {			
			AIFComponentContext[] childrensAIF = topBOMLine.getChildren();
			for (int i = 0; i < childrensAIF.length; i++) {
				InterfaceAIFComponent component = childrensAIF[i].getComponent();
				TCComponentBOMLine childrenMatrix = (TCComponentBOMLine)component;
				AIFComponentContext[] matrixAIF = childrenMatrix.getChildren();
				if(matrixAIF.length == 0) {
					continue;
				}
				getItemChildren(childrenMatrix, list, no, "渲染颜色");
				for (AIFComponentContext aif : matrixAIF) {
					// 第三層
					TCComponentBOMLine bomLine = (TCComponentBOMLine) aif.getComponent();
					no++;
					getItemChildren(bomLine, list, no, "");
					if(bomLine.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
						for (int l = 0; l < listSubstitutes.length; l++) {
							getItemChildren(listSubstitutes[l], list, no, "替代料");
						}
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	public boolean listContains(ArrayList<PackingMatrixExportBean> lists,String item_id) {
		if(CommonTools.isNotEmpty(lists)) {
			for (PackingMatrixExportBean info : lists) {
				String itemid = info.getItemid();
				if(itemid.equals(item_id)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void getItemChildren(TCComponentBOMLine matrixBOMLine,ArrayList<PackingMatrixExportBean> list, int no,String isSub) throws TCException {
		
		String bl_rev_d9_Customer = matrixBOMLine.getProperty("bl_rev_d9_Customer");
		String bl_item_item_id = matrixBOMLine.getProperty("bl_item_item_id");
		String description = matrixBOMLine.getProperty("bl_rev_d9_EnglishDescription");
		String bl_quantity = matrixBOMLine.getProperty("bl_quantity");
		String d9_Un = matrixBOMLine.getProperty("bl_Part Revision_d9_Un");
		String d9_SupplierZF = matrixBOMLine.getProperty("bl_Part Revision_d9_SupplierZF");
		String revision_id = matrixBOMLine.getItemRevision().getProperty("d9_AcknowledgementRev");
		String state = matrixBOMLine.getProperty("bl_occ_d9_IsNew");
		String bl_occ_d9_Remark = matrixBOMLine.getProperty("bl_occ_d9_Remark");
		
		String d9_ChineseDescription = matrixBOMLine.getItemRevision().getProperty("d9_ChineseDescription");
		String d9_ManufacturerID = matrixBOMLine.getItemRevision().getProperty("d9_ManufacturerID");
		String d9_ManufacturerPN = matrixBOMLine.getItemRevision().getProperty("d9_ManufacturerPN");
		String d9_AcknowledgementRev = matrixBOMLine.getItemRevision().getProperty("d9_AcknowledgementRev");
		String d9_SAPRev = matrixBOMLine.getItemRevision().getProperty("d9_SAPRev");
		String d9_MaterialType = matrixBOMLine.getItemRevision().getProperty("d9_MaterialType");
		String d9_MaterialGroup = matrixBOMLine.getItemRevision().getProperty("d9_MaterialGroup");
		String d9_ProcurementMethods = matrixBOMLine.getItemRevision().getProperty("d9_ProcurementMethods");
		
		PackingMatrixExportBean bomInfo = new PackingMatrixExportBean();
		//int no = i+1;
		bomInfo.setNo(""+no);
		bomInfo.setIsSub(isSub);
		bomInfo.setLevel(""+3);
		bomInfo.setD9_Customer(bl_rev_d9_Customer);
		bomInfo.setItemid(bl_item_item_id);
		bomInfo.setDescription(description);	
		if(StrUtil.isEmpty(bl_quantity)) {
			bl_quantity = "1";
		}
		bomInfo.setBl_quantity(String.format("%.3f", Float.parseFloat(bl_quantity)*100));
		bomInfo.setUn(d9_Un);
		bomInfo.setD9_SupplierZF(d9_SupplierZF);
		bomInfo.setState(state);
		bomInfo.setRevision(revision_id);
		bomInfo.setEcn("");
		bomInfo.setRemark(bl_occ_d9_Remark);
		
		bomInfo.setD9_ChineseDescription(d9_ChineseDescription);
		bomInfo.setD9_ManufacturerID(d9_ManufacturerID);
		bomInfo.setD9_ManufacturerPN(d9_ManufacturerPN);
		bomInfo.setD9_AcknowledgementRev(d9_AcknowledgementRev);
		bomInfo.setD9_SAPRev(d9_SAPRev);
		bomInfo.setD9_MaterialType(d9_MaterialType);
		bomInfo.setD9_MaterialGroup(d9_MaterialGroup);
		bomInfo.setD9_ProcurementMethods(d9_ProcurementMethods);
		
		list.add(bomInfo);
	}
	public void writeMatrixChildren(ExcelWriter writer, ArrayList<PackingMatrixExportBean> bomInfoList,int k) {
		Font ft = writer.createFont();
		ft.setFontName("Microsoft YaHei Light");
		ft.setFontHeightInPoints((short) 9);
		CellStyle blueBackgroundStyle = writer.createCellStyle();
		blueBackgroundStyle.setFont(ft);
		blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setFont(ft);
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		
		int mergeRow = 0;
		for (int i=0;i<bomInfoList.size();i++) {
			PackingMatrixExportBean bomInfo = bomInfoList.get(i);
			rowInt = rowInt+1;
			String no = bomInfo.getNo();
			
			String isSub = bomInfo.getIsSub();
			
			String level = bomInfo.getLevel();
			
			String itemid = bomInfo.getItemid();
			writer.writeCellValue(4, rowInt, itemid);
			
			String description = bomInfo.getDescription();
			writer.writeCellValue(5, rowInt, description);
			String bl_quantity = bomInfo.getBl_quantity();
			writer.writeCellValue(6, rowInt, bl_quantity);
			String un = bomInfo.getUn();
			writer.writeCellValue(7, rowInt, un);
			String d9_SupplierZF = bomInfo.getD9_SupplierZF();
			writer.writeCellValue(8, rowInt, d9_SupplierZF);
			String state = bomInfo.getState();
			String remark = bomInfo.getRemark();
			
			writer.writeCellValue(3, rowInt, bomInfo.getD9_ChineseDescription());
			writer.writeCellValue(9, rowInt, bomInfo.getD9_ManufacturerID());
			writer.writeCellValue(10, rowInt, bomInfo.getD9_ManufacturerPN());
			writer.writeCellValue(11, rowInt, bomInfo.getD9_AcknowledgementRev());
			writer.writeCellValue(12, rowInt, bomInfo.getD9_SAPRev());
			writer.writeCellValue(13, rowInt, bomInfo.getD9_MaterialType());
			writer.writeCellValue(14, rowInt, bomInfo.getD9_MaterialGroup());
			writer.writeCellValue(15, rowInt, state);
			writer.writeCellValue(16, rowInt, bomInfo.getD9_ProcurementMethods());
			writer.writeCellValue(17, rowInt, remark);
			
			
			if("渲染颜色".equals(isSub)) {
				writer.writeCellValue(0, rowInt, "");
				writer.writeCellValue(1, rowInt, "");
				writer.writeCellValue(2, rowInt, "");
				writer.writeCellValue(5, rowInt, "");
				
				for(int j=0;j<19;j++) {
					try {
						writer.getCell(j, rowInt).setCellStyle(blueBackgroundStyle);
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				for (int j = 0; j < k; j++) {
					int conn = j+18;
					writer.writeCellValue(conn, rowInt, "");
					writer.getCell(conn, rowInt).setCellStyle(blueBackgroundStyle);
				}
				
			}else {
				writer.writeCellValue(0, rowInt, no);
				writer.writeCellValue(1, rowInt, isSub);
				writer.writeCellValue(2, rowInt, level);
				
				if("替代料".equals(isSub)) {
					writer.writeCellValue(6, rowInt, bomInfoList.get(i-1).getBl_quantity());
					mergeRow++;
					Sheet sheet = writer.getSheet();				
					removeMergedRegion(sheet, rowInt-mergeRow, 1);
					removeMergedRegion(sheet, rowInt-mergeRow, 3);
					removeMergedRegion(sheet, rowInt-mergeRow, 5);
					removeMergedRegion(sheet, rowInt-mergeRow, 17);
					try {
						writer.merge(rowInt-mergeRow, rowInt, 1, 1, isSub, false);
						writer.merge(rowInt-mergeRow, rowInt, 3, 3,  bomInfo.getD9_ChineseDescription(), false);
						writer.merge(rowInt-mergeRow, rowInt, 5, 5, description, false);
						writer.merge(rowInt-mergeRow, rowInt, 17, 17, remark, false);
						writer.getCell(5, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
						writer.getCell(1, rowInt-mergeRow).setCellStyle(blueBackgroundStyle1);
					}catch(Exception e){
						
					}
				} else {
					mergeRow = 0;
				}
				for(int j=0;j<19;j++) {
					try {
						writer.getCell(j, rowInt).setCellStyle(blueBackgroundStyle1);
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				for (int j = 0; j < k; j++) {
					int conn = j+18;
					writer.writeCellValue(conn, rowInt, "");
					writer.getCell(conn, rowInt).setCellStyle(blueBackgroundStyle1);
				}
			}
		}
	}
	
	
	public static int removeMergedRegion (Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			
			//System.out.println("row = "+row+",column = "+column);
			//System.out.println("firstColumn = "+firstColumn+",lastColumn = "+lastColumn+", firstRow = "+firstRow+",lastRow = "+lastRow);
			
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					sheet.removeMergedRegion(i);
					return firstRow;
				}
			}
		}
		return row;
	}
	
	
	@Override
	public MatrixBOMExportBean assembleMatixChangeBean(TCComponentBOMLine impactBOMLine,TCComponentBOMLine sulotionBomLine,String changeType) throws Exception {
		MatrixBOMExportBean e = new MatrixBOMExportBean();
		e.bomItem = mergeProperties(impactBOMLine, sulotionBomLine, "bl_sequence_no");; 
		e.code = changeType;
 		if(impactBOMLine==null) {
 			e.parentNumber = sulotionBomLine.parent().getItem().getProperty("object_name");
 			e.hhpn = sulotionBomLine.getProperty("bl_item_item_id");
 		}else {
 			e.parentNumber = impactBOMLine.parent().getItem().getProperty("object_name");
 			e.hhpn = impactBOMLine.getProperty("bl_item_item_id");
 		}
		e.description = mergeProperties(impactBOMLine, sulotionBomLine, "bl_rev_d9_EnglishDescription");
		e.quantity = mergeProperties(impactBOMLine, sulotionBomLine, "bl_quantity");
		e.unit = mergeProperties(impactBOMLine, sulotionBomLine, "d9_Un");
		e.factory = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Plant");
		e.revName = mergeProperties(impactBOMLine, sulotionBomLine, "item_revision_id");
		e.remark = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Remark");
		return e;
	}
		
	@Override
	public MatrixBOMExportBean assembleMatixBOMBean(IMatixBOMBean bomBean, List<String> varList,VariableBOMBean varBean,List<PicBean> picList) throws Exception {
		MatrixBOMExportBean e = null;
		if(bomBean instanceof ProductLineBOMBean) {
			ProductLineBOMBean bean = (ProductLineBOMBean) bomBean;
			e = new MatrixBOMExportBean();
			e.hhpn = bean.getItemId();
			e.description = bean.getEnglishDescription();
			e.unit  = bean.getUn();
			e.factory = bean.getSupplierZF();
			e.uom = bean.getUom();
			e.revName = bean.getItemRevision();
			List<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
			for(String var: varList){
				int qty = findQty(var,bean.getLineId(),varBean);
				list.add(new Pair<String, Integer>(var,qty));
			}
			e.variablesList = list;
			e.layer = 1;
			e.remark = bean.getRemark();
			return e;
		}else {
			BomLineBOMBean bean = (BomLineBOMBean) bomBean;
			TCComponentBOMLine bomLine = bean.bomLine;
			e = new MatrixBOMExportBean();
			e.bomItem = bomLine.getProperty("bl_sequence_no");
 			e.hhpn = bomLine.getProperty("bl_item_item_id");
			e.description = bomLine.getProperty("bl_rev_d9_EnglishDescription");
			e.isSubstitutes = bean.isSubstitute;
			e.unit = bomLine.getItemRevision().getProperty("d9_Un");
			e.factory = bomLine.getItemRevision().getProperty("d9_SupplierZF");
			e.uom = bomLine.getItem().getProperty("uom_tag");
			e.revName = bomLine.getItemRevision().getProperty("item_revision_id");
			e.layer = bean.layer+1;
			e.remark =bomLine.getProperty("bl_occ_d9_Remark");
		}
		return e;
		
	}

	public void getBeanList(TCComponentItemRevision matrixItemRevision,List<MatrixBOMExportBean> list) throws Exception {
		TCComponentBOMWindow bomWindow = null;
		try {
			bomWindow = TCUtil.createBOMWindow(TCUtil.getTCSession());		
			TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, matrixItemRevision);
			doRecursion(topBomline, 1, list);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bomWindow!=null) {
				bomWindow.close();
			}
		}		
	}
	
	public void doRecursion(TCComponentBOMLine bomline,int layer,List<MatrixBOMExportBean> list) throws Exception {
		AIFComponentContext[] children = bomline.getChildren();
		if(children.length > 0) {
			// 插入空行
			list.add(null);
		}
		for(AIFComponentContext context : children) {
			// 收集
			TCComponentBOMLine echoBom = (TCComponentBOMLine) context.getComponent();
			makeMatixBOMBeanByLayer(echoBom.getChildren(),layer,list);
		}
		for(AIFComponentContext context : children) {
			// 下一层
			doRecursion((TCComponentBOMLine)context.getComponent(),layer+1,list);
		}
	}
	
	public void makeMatixBOMBeanByLayer(AIFComponentContext[] children,int layer,List<MatrixBOMExportBean> list) throws Exception {
		if(children.length==0) {
			return;
		}
		// 插入父行
		MatrixBOMExportBean e = new MatrixBOMExportBean();
		TCComponentBOMLine parent = ((TCComponentBOMLine)children[0].getComponent()).parent();
		e.hhpn = parent.getProperty("bl_item_item_id");
		e.description = parent.getProperty("bl_rev_d9_EnglishDescription");
		list.add(e);
		for(AIFComponentContext context : children) {
			TCComponentBOMLine bomLine =  (TCComponentBOMLine) context.getComponent();
			list.add(assembleMatixBOMBean(new BomLineBOMBean(bomLine,layer), null, null, null));
			if(bomLine.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for(TCComponentBOMLine subBomLine : listSubstitutes) {
					list.add(assembleMatixBOMBean(new BomLineBOMBean(subBomLine,true,layer), null, null, null));
				}
			}
		}
	}

	@Override
	public boolean equals(TCComponentBOMLine bomLine1, TCComponentBOMLine bomLine2) throws TCException {
		String bomLine1Id = "";
		String bomLine2Id = "";
		
		bomLine1Id += bomLine1.getProperty("bl_item_item_id");
		
		bomLine2Id += bomLine2.getProperty("bl_item_item_id");

		return StrUtil.equals(bomLine1Id, bomLine2Id);
	}

	@Override
	public void setHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) {
	}
	
	private void writeChangeLog(ExcelWriter writer,TCComponentItemRevision itemRevision) throws TCException {
		writer.setSheet("changelog");
		TCComponent[] relatedComponents = itemRevision.getRelatedComponents("d9_MatrixChangeTable");
		for (int i = 0;i<relatedComponents.length;i++) {
			TCComponent tcComponent = relatedComponents[i];
			TCComponentFnd0TableRow row = (TCComponentFnd0TableRow)tcComponent;
			String date = row.getProperty("d9_ChangeDate");
			String ver = row.getProperty("d9_ChangeVer");
			String log = row.getProperty("d9_ChangeLog");
			String ecr = row.getProperty("d9_ChangeECR");
			String user = row.getProperty("d9_ChangeReqDateUser");
			writer.writeCellValue(0,i+2, date);
			writer.writeCellValue(1,i+2, ver);
			writer.writeCellValue(2,i+2, log);
			writer.writeCellValue(3,i+2, ecr);
			writer.writeCellValue(4,i+2, user);
		}		
	}
}
