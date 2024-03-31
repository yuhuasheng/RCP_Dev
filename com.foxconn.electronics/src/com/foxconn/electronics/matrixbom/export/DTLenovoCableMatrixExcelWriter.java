package com.foxconn.electronics.matrixbom.export;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.matrixbom.service.MatrixBOMService;
import com.foxconn.tcutils.util.AjaxResult;
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

public class DTLenovoCableMatrixExcelWriter implements IMatrixBOMExportWriter{
	
	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision,List<MatrixBOMExportBean> beanList,ArrayList<String> varList) throws Exception  {
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		String innerResPath = "com/foxconn/electronics/matrixbom/export/template/DT Lenovo Cable Matrix Change List.xlsx";
		URL url = this.getClass().getClassLoader().getResource(innerResPath);			
		InputStream in = url.openStream();
		
		String dir = CommonTools.getFilePath("DT Lenovo Cable Matrix");
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
		writer.setSheet("Cable Matrix");	
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
		titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		Font titleFont = writer.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeight((short) 300);
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
		MatrixBOMExportService.setValueAndStyle(writer,3, headerRowNum, "Spec/Pic",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,4, headerRowNum, "工廠",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,5, headerRowNum, "Lenovo P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,6, headerRowNum, "FRU P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,7, headerRowNum, "Vendor",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,8, headerRowNum, "Vendor P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,9, headerRowNum, "HHPN",headStyle);
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
		
		CellStyle headStyle1 = writer.createCellStyle();
		headStyle1.setWrapText(true);
		headStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		headStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		headStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		headStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		headStyle1.setAlignment(HorizontalAlignment.LEFT);
		headStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		headFont.setBold(true);
		Sheet sheet = writer.getSheet();
		
		// 写行
		i = 0;
		int lastNo = 0;
		int row = 1;
		for(i = 0;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			lastNo++;
			row++;
			
			List<MatrixBOMExportBean> substitutes = bean.substitutes;
			if(CommonTools.isNotEmpty(substitutes)) {
				int subSize = substitutes.size();
				writer.merge(row, row+subSize, 0, 0, lastNo, false);
				writer.merge(row, row+subSize, 1, 1, bean.category, false);
				writer.merge(row, row+subSize, 2, 2, bean.description, false);
				writer.merge(row, row+subSize, 3, 3, "", false);
				insertImg(writer.getWorkbook(), writer.getSheet(), bean.specPic, 3,row,3,row+subSize+1); 
				writer.merge(row, row+subSize, 4, 4, bean.factory, false);
				writer.merge(row, row+subSize, 6, 6, bean.fruPN, false);
				
				int size = bean.variablesList.size();
				int j = 1;
				for(;j<=size;j++) {
					writer.merge(row, row+subSize, 9+j, 9+j, bean.variablesList.get(j-1).getValue(), false);
				}
				writer.merge(row, row+subSize, 9+j, 9+j, bean.remark, false);
				writer.getCell(9+j,row).setCellStyle(headStyle1);
				
				writer.writeCellValue(5,row,bean.lenovoPN);
				writer.writeCellValue(7,row,bean.vendor);
				writer.writeCellValue(8,row,bean.vendorPN);
				String hhpn = bean.hhpn;
				if(hhpn.startsWith("TC@")) 
					hhpn = "N/A";
				writer.writeCellValue(9,row,hhpn);
				
				writer.setRowHeight(row, 60/(subSize+1));
				for (int k = 0; k < substitutes.size(); k++) {
					row++;
					
					MatrixBOMExportBean matrixBOMExportBean = substitutes.get(k);
					writer.writeCellValue(5,row,matrixBOMExportBean.lenovoPN);
					writer.writeCellValue(7,row,matrixBOMExportBean.vendor);
					writer.writeCellValue(8,row,matrixBOMExportBean.vendorPN);
					//writer.writeCellValue(9,row,matrixBOMExportBean.hhpn);
					String subhhpn = matrixBOMExportBean.hhpn;
					if(subhhpn.startsWith("TC@")) 
						subhhpn = "N/A";
					writer.writeCellValue(9,row,subhhpn);
					
//					if(k == 0) {
//						String fruPN = bean.fruPN;
//						String subFruPN = matrixBOMExportBean.fruPN;
//						if(!fruPN.equals(subFruPN)) {
//							removeMergedRegion(sheet, row, 6);
//							writer.writeCellValue(6,row,matrixBOMExportBean.fruPN);
//						}
//					}
					
					
					writer.setRowHeight(row, 60/(subSize+1));
				}
			} else {
				writer.writeCellValue(0,row,lastNo);
				writer.writeCellValue(1,row,bean.category);
				writer.writeCellValue(2,row,bean.description);
				writer.writeCellValue(3,row,"");
				insertImg(writer.getWorkbook(), writer.getSheet(), bean.specPic, 3,row,3,row); 
				writer.writeCellValue(4,row,bean.factory);
				writer.writeCellValue(5,row,bean.lenovoPN);
				writer.writeCellValue(6,row,bean.fruPN);
				writer.writeCellValue(7,row,bean.vendor);
				writer.writeCellValue(8,row,bean.vendorPN);
				String hhpn = bean.hhpn;
				if(hhpn.startsWith("TC@")) 
					hhpn = "N/A";
				writer.writeCellValue(9,row,hhpn);
				//writer.writeCellValue(9,row,bean.hhpn);
				int size = bean.variablesList.size();
				int j = 1;
				for(;j<=size;j++) {
					writer.writeCellValue(9+j,row,bean.variablesList.get(j-1).getValue());
				}
				writer.writeCellValue(9+j,row,bean.remark);
				writer.getCell(9+j,row).setCellStyle(headStyle1);
				writer.setRowHeight(row, 60);
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
		String innerResPath = "com/foxconn/electronics/matrixbom/export/template/DT Lenovo Cable Matrix Change List.xlsx";
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
	public String createDCNwriterDCN(TCComponentItemRevision matrixItemRevision,String dcnNo, List<MatrixBOMExportBean> list,String path) throws Exception {
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		String dir = CommonTools.getFilePath("DT Lenovo Cable Matrix_ChangeList");
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		String excelName = objectName.replace(" ","_") + "_" + revName + ".xlsx";
		File dest = new File(dir + File.separator + excelName);
		
//		String excelName = path.getName();
//		File dest = new File(dir + File.separator + excelName);
		
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



	
	public void insertImg(Workbook wb, Sheet sheet, File imgFile, int col, int row, int col1, int row1) throws IOException {
		if(imgFile == null) {
			return;
		}
		Cell c = sheet.getRow(row).getCell(col);
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		Image src = Toolkit.getDefaultToolkit().getImage(imgFile.getPath());
		BufferedImage bufferImg = toBufferedImage(src);
		ImageIO.write(bufferImg, "JPG", byteArrayOut);
		Drawing<?> patriarch = sheet.createDrawingPatriarch();
		/*
		 * 
		 * @param dx1 图片的左上角在开始单元格（col1,row1）中的横坐标
		 * 
		 * @param dy1 图片的左上角在开始单元格（col1,row1）中的纵坐标
		 * 
		 * @param dx2 图片的右下角在结束单元格（col2,row2）中的横坐标
		 * 
		 * @param dy2 图片的右下角在结束单元格（col2,row2）中的纵坐标
		 * 
		 * @param col1 开始单元格所处的列号, base 0, 图片左上角在开始单元格内
		 * 
		 * @param row1 开始单元格所处的行号, base 0, 图片左上角在开始单元格内
		 * 
		 * @param col2 结束单元格所处的列号, base 0, 图片右下角在结束单元格内
		 * 
		 * @param row2 结束单元格所处的行号, base 0, 图片右下角在结束单元格内
		 */
		ClientAnchor anchor = patriarch.createAnchor(3 * 10000, 3 * 10000, 20, 70, col, row, col1, row1);
		Picture picture = patriarch.createPicture(anchor,
				wb.addPicture(byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_JPEG));
//        picture.resize(1, 0.96);
		picture.resize(1, 1);
	}
	
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}
		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}
	
	public List<MatrixBOMExportBean> getBeanList(MatrixBOMService matrixBOMService,String uid,ArrayList<String> varList) throws Exception {
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AjaxResult ar = matrixBOMService.getMatrixBOMStruct(uid);
		JSONObject jsonObject = (JSONObject) ar.get("data");
		ProductLineBOMBean bomBean = (ProductLineBOMBean) jsonObject.get("partList");
		VariableBOMBean varBean = (VariableBOMBean) jsonObject.get("variableBOM");
		ar = matrixBOMService.getImg(uid);
		ArrayList<PicBean>  imgList = (ArrayList<PicBean>) ar.get("data");
		List<ProductLineBOMBean> productList = bomBean.getChild();
		for(ProductLineBOMBean productBean : productList) {
			MatrixBOMExportBean assembleMatixBOMBean = assembleMatixBOMBean(productBean,varList,varBean,imgList);
			
			List<ProductLineBOMBean> subList = productBean.getSubList();
			if(CommonTools.isNotEmpty(subList)) {
				ArrayList<MatrixBOMExportBean> listSubMatrix = new ArrayList<MatrixBOMExportBean>();
				for(ProductLineBOMBean productSubBean : subList) {
					MatrixBOMExportBean subAssembleMatixBOMBean = assembleMatixBOMBean(productSubBean,varList,varBean,imgList);
					subAssembleMatixBOMBean.isSubstitutes = true;
					subAssembleMatixBOMBean.varType = assembleMatixBOMBean.varType;
					listSubMatrix.add(subAssembleMatixBOMBean);
				}
				assembleMatixBOMBean.substitutes = listSubMatrix;
			}
			
			list.add(assembleMatixBOMBean);
		}
		return list;
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
			e.specPic = findPic(bean.getLineId(),picList);
			e.factory = bean.getPlant();
			e.lenovoPN = bean.getCustomerPN();
			e.fruPN = bean.getFrupn();
			e.vendor = bean.getManufacturerID();
			e.vendorPN = bean.getManufacturerPN();
			e.hhpn = bean.getItemId();
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
		updateHistory(session, matrixItemRevision, excelPath);
	}

}
