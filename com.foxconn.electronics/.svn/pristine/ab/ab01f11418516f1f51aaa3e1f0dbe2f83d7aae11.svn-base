package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.Pair;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class DTLenovoCoverGasketCableMatrixExcelWriter implements IMatrixBOMExportWriter{

	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision, List<MatrixBOMExportBean> beanList,ArrayList<String> varList) throws Exception {
		
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");

		String innerResPath = "com/foxconn/electronics/matrixbom/export/template/DT Lenovo Cover&Gasket&Cable tie Matrix Change List.xlsx";
		URL url = this.getClass().getClassLoader().getResource(innerResPath);			
		InputStream in = url.openStream();
		
		String dir = CommonTools.getFilePath("DT Lenovo CoverGasketCable Matrix");
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
		
		ExcelWriter writer = ExcelUtil.getWriter(tempExcelFile);
		
		
		//ExcelWriter writer = ExcelUtil.getWriter(true);
		//writer.setSheet("History"); 
		writer.setSheet("Cover Matrix");	
		// 写头
		CellStyle wrapTextStyle = writer.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		wrapTextStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		wrapTextStyle.setBorderTop(BorderStyle.THIN);// 上边框
		wrapTextStyle.setBorderRight(BorderStyle.THIN);// 右边框
		wrapTextStyle.setAlignment(HorizontalAlignment.CENTER);
		wrapTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		wrapTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		CellStyle titleStyle = writer.createCellStyle();
		titleStyle.cloneStyleFrom(wrapTextStyle);
		Font titleFont = writer.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeight((short) 300);
		titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		titleStyle.setFont(titleFont);
		
		writer.merge(0, 0, 0, 9+varList.size()+1, objectName, false);
		writer.getCell(0,0).setCellStyle(titleStyle);
		
		
		CellStyle headStyle = writer.createCellStyle();
		headStyle.cloneStyleFrom(wrapTextStyle);
		Font headFont = writer.createFont();
		headFont.setBold(true);
		headFont.setFontHeight((short) 250);
		headStyle.setFont(headFont);
		headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
		int headerRowNum = 1;
		MatrixBOMExportService.setValueAndStyle(writer,0, headerRowNum, "NO.",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,1, headerRowNum, "Category",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,2, headerRowNum, "Description",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,3, headerRowNum, "Picture of screw",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,4, headerRowNum, "工廠",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,5, headerRowNum, "Lenovo P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,6, headerRowNum, "HHPN",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,7, headerRowNum, "Vendor",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,8, headerRowNum, "Recommended \r\nTorque-In (In lbs.)",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,9, headerRowNum, "Recommended \r\nTorque-Out (In lbs.)",headStyle);
		
		
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle1.setWrapText(true);
		
		
		int i = 1;
		for(;i<=varList.size();i++) {
			String key = varList.get(i-1);
			if(key.contains("/")) 
				key = key.substring(key.indexOf("/")+1, key.length());
			MatrixBOMExportService.setValueAndStyle(writer,9+i, headerRowNum, key,headStyle);
		}
		MatrixBOMExportService.setValueAndStyle(writer,9+i, headerRowNum, "Remark",headStyle);
		// 设置列宽
		writer.setColumnWidth(+0, 10);
		writer.setColumnWidth(+1, 40);
		writer.setColumnWidth(+2, 40);
		writer.setColumnWidth(+3, 15);
		writer.setColumnWidth(+4, 10);
		writer.setColumnWidth(+5, 20);
		writer.setColumnWidth(+6, 20);
		writer.setColumnWidth(+7, 20);				
		writer.setColumnWidth(+8, 20);				
		writer.setColumnWidth(+9, 20);	
		writer.setColumnWidth(+10+varList.size(), 40);
		// 写行
		i = 0;
		int lastNo = 0;
		int row = 1;
		String hhpn = "";
		
		for(;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			if(!bean.isSubstitutes) {
				lastNo++;
				row++;
				
				writer.writeCellValue(0,row,lastNo);
				writer.writeCellValue(1,row,bean.category);
				writer.writeCellValue(2,row,bean.description);
				File screwPic = bean.screwPic;
				if(screwPic == null) {
					writer.writeCellValue(3,row,"");
				}else {
					MatrixBOMExportService.insertImg(writer.getWorkbook(), writer.getSheet(), screwPic, 3,row); 
				}
				writer.writeCellValue(4,row,bean.factory);
				writer.writeCellValue(5,row,bean.lenovoPN);				
				writer.writeCellValue(6,row,bean.hhpn);
				writer.writeCellValue(7,row,bean.vendor);
				writer.writeCellValue(8,row,bean.torqueIn);
				writer.writeCellValue(9,row,bean.torqueOut);
				
				int size = bean.variablesList.size();
				int j = 1;
				for(;j<=size;j++) {
					writer.writeCellValue(9+j,row,bean.variablesList.get(j-1).getValue());
				}
				writer.writeCellValue(9+j,row,bean.remark);
				writer.setRowHeight(row, 60);
					
				hhpn = "";
				hhpn = hhpn+""+bean.hhpn;
				
			} else {
				
				if(!"".equals(hhpn)) {
					hhpn = hhpn+"\n"+bean.hhpn;
				}
				
				
				//writer.writeCellValue(6,row,hhpn);
				MatrixBOMExportService.setValueAndStyle(writer, 6,row,hhpn, blueBackgroundStyle1);
			}
			
		} 
		
		
		//writer.getWorkbook().removeSheetAt(0);
//		writer.getWorkbook().setActiveSheet(1);
//		writer.getWorkbook().removeSheetAt(3);
		
		try {
			writer.close();
		} catch (IORuntimeException e) {
			MessageBox.post(AIFUtility.getActiveDesktop(),"请关闭打开的Excel文件："+excelName, "提示", MessageBox.INFORMATION);
			
			return null;
		}
		
		String absolutePath = tempExcelFile.getAbsolutePath();
		return absolutePath;
	}
	
	@Override
	public String writerDCN(TCComponentItemRevision matrixItemRevision,String dcnNo, List<MatrixBOMExportBean> list) throws Exception {
		String innerResPath = "com/foxconn/electronics/matrixbom/export/template/DT Lenovo Cover&Gasket&Cable tie Matrix Change List.xlsx";
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
			setValueAndStyle(writer, 5, row, bean.category, textCellStyle);
			setValueAndStyle(writer, 6, row, bean.description, textCellStyle);
			setValueAndStyle(writer, 7, row, bean.quantity, textCellStyle);
			setValueAndStyle(writer, 8, row, bean.factory, textCellStyle);
			setValueAndStyle(writer, 9, row, bean.lenovoPN, textCellStyle);
			setValueAndStyle(writer, 10, row, bean.fruPN, textCellStyle);
			setValueAndStyle(writer, 11, row, bean.vendor, textCellStyle);
			setValueAndStyle(writer, 12, row, bean.vendorPN, textCellStyle);
			setValueAndStyle(writer, 13, row, bean.remark, textCellStyle);
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
		
		String dir = CommonTools.getFilePath("DT Lenovo CoverGasketCable Matrix_ChangeList");
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
			setValueAndStyle(writer, 5, row, bean.category, textCellStyle);
			setValueAndStyle(writer, 6, row, bean.description, textCellStyle);
			setValueAndStyle(writer, 7, row, bean.quantity, textCellStyle);
			setValueAndStyle(writer, 8, row, bean.factory, textCellStyle);
			setValueAndStyle(writer, 9, row, bean.lenovoPN, textCellStyle);
			setValueAndStyle(writer, 10, row, bean.fruPN, textCellStyle);
			setValueAndStyle(writer, 11, row, bean.vendor, textCellStyle);
			setValueAndStyle(writer, 12, row, bean.vendorPN, textCellStyle);
			setValueAndStyle(writer, 13, row, bean.remark, textCellStyle);
			writer.setRowHeight(row, 55);
		}
		//writer.getWorkbook().setActiveSheet(2);
		//writer.getWorkbook().removeSheetAt(3);
		writer.close();
		return dest.getAbsolutePath();
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
		e.category = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Category");
		e.description = mergeProperties(impactBOMLine, sulotionBomLine, "bl_rev_d9_EnglishDescription");
		e.quantity = mergeProperties(impactBOMLine, sulotionBomLine, "bl_quantity");
		e.factory = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Plant");
		e.lenovoPN = mergeProperties(impactBOMLine, sulotionBomLine, "bl_rev_d9_CustomerPN");
		e.fruPN = mergeProperties(impactBOMLine, sulotionBomLine, "d9_FRUPN");
		e.vendor = mergeProperties(impactBOMLine, sulotionBomLine, "d9_ManufacturerID");
		e.vendorPN = mergeProperties(impactBOMLine, sulotionBomLine, "d9_ManufacturerPN");
		e.remark = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Remark");
		return e;
	}

	@Override
	public MatrixBOMExportBean assembleMatixBOMBean(IMatixBOMBean bomBean, List<String> varList,VariableBOMBean varBean,List<PicBean> picList) throws Exception {
		if(bomBean instanceof ProductLineBOMBean) {
			ProductLineBOMBean bean = (ProductLineBOMBean) bomBean;
			MatrixBOMExportBean e = new MatrixBOMExportBean();
			e.category = bean.getCategory();
			e.description = bean.getEnglishDescription();
			e.screwPic = findPic(bean.getLineId(),picList);
			e.factory = bean.getPlant();
			e.lenovoPN = bean.getCustomerPN();
			if(bean.getItemId().startsWith("TC@")) {
				e.hhpn = "N/A";
			} else {
				e.hhpn = bean.getItemId();
			}
			e.vendor = bean.getManufacturerID();
			e.torqueIn = bean.getTorqueIn();
			e.torqueOut = bean.getTorqueOut();
			List<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
			for(String var: varList){
				int qty = findQty(var,bean.getLineId(),varBean);
				list.add(new Pair<String, Integer>(var,qty));
			}
			e.variablesList = list;
			e.remark = bean.getRemark();
			return e;
		}
		return null;
		
	}

	@Override
	public boolean equals(TCComponentBOMLine bomLine1, TCComponentBOMLine bomLine2) throws TCException {
		String bomLine1Id = "";
		String bomLine2Id = "";
		
		bomLine1Id += bomLine1.getProperty("bl_item_item_id");
		bomLine1Id += bomLine1.getProperty("bl_occ_d9_Category");
		bomLine1Id += bomLine1.getProperty("bl_occ_d9_Plant");
		
		bomLine2Id += bomLine2.getProperty("bl_item_item_id");
		bomLine2Id += bomLine2.getProperty("bl_occ_d9_Category");
		bomLine2Id += bomLine2.getProperty("bl_occ_d9_Plant");
		return StrUtil.equals(bomLine1Id, bomLine2Id);
	}

	@Override
	public void setHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) throws Exception {
		updateHistory(session,matrixItemRevision, excelPath);
	}


}
