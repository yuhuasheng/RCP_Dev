package com.foxconn.sdebom.export;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class HPEBOMExportService {

	public static String exportExcel(TCComponentBOMLine topBOMLine) throws Exception {
		
		List<ExportSheet> sheetList = new ArrayList<>();
		AIFComponentContext[] children = topBOMLine.getChildren();
		System.out.println("children.length = "+children.length);
		
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine subBomLine = (TCComponentBOMLine) children[i].getComponent();
			ExportSheet exportSheet = new ExportSheet();
			exportSheet.bomLine = subBomLine;
			String type = subBomLine.getItem().getType();
			if (!"D9_BOMOptNode".equals(type)) {
//				continue;
			}
			if ("D9_BOMOptNode".equals(type)) {
				exportSheet.type = ExportSheet.TYPE_OPTION;
				exportSheet.name = subBomLine.getItemRevision().getProperty("object_name");
				if(StrUtil.isEmpty(exportSheet.name)) {
					throw new Exception(subBomLine.getProperty("bl_item_item_id")+" 的 ITEM_NAME属性不能为空，请检查");
				}		
			} else if ("D9_BOMListNode".equals(type)) {
				exportSheet.type = ExportSheet.TYPE_SREW;
				exportSheet.name = "screw list";
			} else {
				exportSheet.type = ExportSheet.TYPE_BOM;
				exportSheet.name = subBomLine.getItemRevision().getProperty("d9_EnglishDescription");
				if(StrUtil.isEmpty(exportSheet.name)) {
					throw new Exception(subBomLine.getProperty("bl_item_item_id")+" 的 d9_EnglishDescription属性不能为空，请检查");
				}		
			}
			
			if(exportSheet.name.length()>30) {
				exportSheet.name = exportSheet.name.substring(0,30);
			}
			
			if(exitsSheetName(sheetList, exportSheet.name)) {
				exportSheet.name = "Sheet"+i+"_"+exportSheet.name;
				//throw new Exception("表名重复："+subBomLine.getProperty("bl_item_item_id")+"的"+exportSheet.name);
			}
			
			
			//System.out.println("exportSheet.name = "+exportSheet.name);
			
			
			sheetList.add(exportSheet);
		}	
		System.out.println("获取属性");
		for (ExportSheet sheet : sheetList) {
			TCComponentBOMLine bomLine = sheet.bomLine;
			recursionBomLine(bomLine, sheet.rowList, -1, sheet.type);
//			break;
		}
		System.out.println("获取属性完成");	
		
		System.out.println("写入excel");
		ExcelWriter writer = ExcelUtil.getWriter(true);
		for (ExportSheet sheet : sheetList) {
			// write to excel
			System.out.println("正在写表名："+sheet.name);
			
			writer.setSheet(sheet.name);
			//System.out.println("正在写表名："+sheet.name);
			writeSheet(writer, sheet);
//			break;
		}
		System.out.println("写入excel完成");
		
		File tempExcelFile = File.createTempFile("HPEBOM", ".xlsx");
		String absolutePath = tempExcelFile.getAbsolutePath();
		System.out.println("文件路径: " + absolutePath);
		tempExcelFile.deleteOnExit();
		writer.getWorkbook().removeSheetAt(0);
		writer.setDestFile(tempExcelFile);
		writer.close();
		
		return absolutePath;
	}
	
	private static boolean exitsSheetName(List<ExportSheet> list,String sheetName) {
		for(ExportSheet sheet:list) {
			if(Objects.equals(sheet.name, sheetName)) {
				return true;
			}
		}
		return false;
	}

	private static void writeSheet(ExcelWriter writer, ExportSheet sheet) throws Exception {
		List<ExportRow> rowList = sheet.rowList;
		int maxLayer = 0;
		for (ExportRow row : rowList) {
			if (maxLayer < row.layer) {
				maxLayer = row.layer;
			}
		}
		maxLayer++;
		if (sheet.type == ExportSheet.TYPE_BOM) {

			int itemIndex = 0;
			int hhPINIndex = 1;
			int hpPINIndex = 2;
			int layerIndex = 3;
			int substitueIndex = 3 + maxLayer;
			int descriptionIndex = 4 + maxLayer;
			int assemblyCodeIndex = 5 + maxLayer;
			int qtyIndex = 6 + maxLayer;
			int remarkIndex = 7 + maxLayer;

			// create head
			int headRowNum = 1;
			writer.writeCellValue(itemIndex, headRowNum, "Item");
			writer.writeCellValue(hhPINIndex, headRowNum, "HH PIN");
			writer.writeCellValue(hpPINIndex, headRowNum, "HP PIN");
			writer.writeCellValue(layerIndex, headRowNum, "layer");
			writer.writeCellValue(descriptionIndex, headRowNum, "Description");
			writer.writeCellValue(assemblyCodeIndex, headRowNum, "Assembly Code");
			writer.writeCellValue(qtyIndex, headRowNum, "Qty");
			writer.writeCellValue(remarkIndex, headRowNum, "Remark");
			if(maxLayer >= 1) {
				//writer.merge(1, 1, 3, 3 + maxLayer - 1, "layer", false);
				writer.merge(1, 1, 3, 3 + maxLayer, "layer", false);
			}			
			writer.merge(0, 0, 0, remarkIndex, sheet.name, false);
			writer.setRowHeight(0, 30);
			
			for (int i = 0; i < maxLayer; i++) {
				int width = 2;
				if (maxLayer == 1) {
					width = 5;
				}
				if (maxLayer == 2) {
					width = 3;
				}
				writer.setColumnWidth(i + layerIndex, width);
			}

			Font font = writer.createFont();
			font.setBold(true);
			CellStyle blueBackgroundStyle = writer.createCellStyle();
			blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
			blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			blueBackgroundStyle.setFont(font);
			blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
			blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
			blueBackgroundStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
			CellStyle yellowBackgroundStyle = writer.createCellStyle();
			yellowBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
			yellowBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			yellowBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			yellowBackgroundStyle.setFont(font);
			yellowBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			yellowBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			yellowBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
			yellowBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
			yellowBackgroundStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
			writer.getCell(itemIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(hhPINIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(hpPINIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(descriptionIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(assemblyCodeIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(layerIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			writer.getCell(qtyIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			writer.getCell(remarkIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			// write rows
			for (int i = 0; i < rowList.size(); i++) {
				ExportRow row = rowList.get(i);
				int rownum = i+2;
				writer.writeCellValue(itemIndex, rownum, i + 1);
				writer.writeCellValue(hhPINIndex, rownum, row.hhpin);
				writer.writeCellValue(hpPINIndex, rownum, row.hppin);
				
				if(row.substituteIndex !=0 ) {
					writer.writeCellValue(substitueIndex, rownum, row.substituteIndex);
				} else {
					writer.writeCellValue(layerIndex + row.layer, rownum, row.layer+1);
				}
				
				writer.writeCellValue(descriptionIndex, rownum, row.description);
				writer.writeCellValue(assemblyCodeIndex, rownum, row.assemblyCode);
				writer.writeCellValue(qtyIndex, rownum, row.qty);
				writer.writeCellValue(remarkIndex, rownum, row.remark);
			}
			
			// set column width
			writer.setColumnWidth(itemIndex, 6);
			writer.autoSizeColumn(hhPINIndex);
			writer.autoSizeColumn(hpPINIndex);
			writer.setColumnWidth(substitueIndex,2);
			writer.autoSizeColumn(descriptionIndex);
			writer.autoSizeColumn(assemblyCodeIndex);
			writer.autoSizeColumn(qtyIndex);
			writer.autoSizeColumn(remarkIndex);
		}

		if (sheet.type == ExportSheet.TYPE_OPTION) {
			int itemIndex = 0;
			int hhPINIndex = 1;
			int hpPINIndex = 2;
			int layerIndex = 3;
			int substitueIndex = 3 + maxLayer;
			int hpDescriptionIndex = 4 + maxLayer;
			int qtyIndex = 5 + maxLayer;
			int remarkIndex = 6 + maxLayer;
			int matlIndex = 7 + maxLayer;
			int cclIndex = 8 + maxLayer;
			
			int headRowNum = 1;			
			writer.writeCellValue(itemIndex, headRowNum, "Item");
			writer.writeCellValue(hhPINIndex, headRowNum, "HH P/N");
			writer.writeCellValue(hpPINIndex, headRowNum, "HP P/N");
			writer.writeCellValue(layerIndex, headRowNum, "layer");
			writer.writeCellValue(hpDescriptionIndex, headRowNum, "HP Description");
			writer.writeCellValue(qtyIndex, headRowNum, "QTY");
			writer.writeCellValue(remarkIndex, headRowNum, "Remark");
			writer.writeCellValue(matlIndex, headRowNum, "MATL");
			writer.writeCellValue(cclIndex, headRowNum, "CCL(Y or N)");

			if(maxLayer >= 1) {
				//writer.merge(1, 1, 3, 3 + maxLayer - 1, "layer", false);
				System.out.println(" maxLayer = "+maxLayer);
				writer.merge(1, 1, 3, 3 + maxLayer, "layer", false);
			}
			
			writer.merge(0, 0, 0, cclIndex, sheet.name, false);
			writer.setRowHeight(0, 30);
			for (int i = 0; i < maxLayer; i++) {
				int width = 2;
				if (maxLayer == 1) {
					width = 5;
				}
				if (maxLayer == 2) {
					width = 3;
				}
				writer.setColumnWidth(i + layerIndex, width);
			}

			Font font = writer.createFont();
			font.setBold(true);
			CellStyle blueBackgroundStyle = writer.createCellStyle();
			blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
			blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			blueBackgroundStyle.setFont(font);
			blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
			blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
			blueBackgroundStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
			CellStyle yellowBackgroundStyle = writer.createCellStyle();
			yellowBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
			yellowBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			yellowBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			yellowBackgroundStyle.setFont(font);
			yellowBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			yellowBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			yellowBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
			yellowBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
			yellowBackgroundStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
			writer.getCell(itemIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(hhPINIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(hpPINIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(hpDescriptionIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(layerIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			writer.getCell(qtyIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			writer.getCell(remarkIndex, headRowNum).setCellStyle(yellowBackgroundStyle);
			writer.getCell(matlIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			writer.getCell(cclIndex, headRowNum).setCellStyle(blueBackgroundStyle);
			// write rows
			for (int i = 0; i < rowList.size(); i++) {
				ExportRow row = rowList.get(i);
				int rownum = i+2;
				writer.writeCellValue(itemIndex, rownum, i + 1);
				writer.writeCellValue(hhPINIndex, rownum, row.hhpin);
				writer.writeCellValue(hpPINIndex, rownum, row.hppin);
				
				if(row.substituteIndex !=0 ) {
					writer.writeCellValue(substitueIndex, rownum, row.substituteIndex);
				} else {
					writer.writeCellValue(layerIndex + row.layer, rownum, row.layer+1);
				}	
				writer.writeCellValue(hpDescriptionIndex, rownum, row.hpDescription);
				writer.writeCellValue(qtyIndex, rownum, row.qty);
				writer.writeCellValue(remarkIndex, rownum, row.remark);
				writer.writeCellValue(matlIndex, rownum, row.matl);
				writer.writeCellValue(cclIndex, rownum, row.ccl);
			}
			writer.setColumnWidth(itemIndex, 6);
			writer.setColumnWidth(hhPINIndex, 20);
			writer.setColumnWidth(hpPINIndex, 15);
			writer.setColumnWidth(substitueIndex, 2);
			writer.autoSizeColumn(hpDescriptionIndex);
			writer.autoSizeColumn(qtyIndex);
			writer.autoSizeColumn(remarkIndex);
			writer.autoSizeColumn(matlIndex);
			writer.autoSizeColumn(cclIndex);
		}

		if (sheet.type == ExportSheet.TYPE_SREW) {
			int itemIndex = 0;
			int picIndex = 1;
			int hhPINIndex = 2;
			int hpPINIndex = 3;
			int partNameIndex = 4;
			int qtyIndex = 5;
			int purposeIndex = 6;
			int headTypeIndex = 7;
			int torqueIndex = 8;
			int assyByIndex = 9;
			int remarkIndex = 10;
			writer.writeCellValue(itemIndex, 0, "Item");
			writer.writeCellValue(picIndex, 0, "Picture");
			writer.writeCellValue(hhPINIndex, 0, "HH PIN");
			writer.writeCellValue(hpPINIndex, 0, "HP PIN");
			writer.writeCellValue(partNameIndex, 0, "Part Name");
			writer.writeCellValue(qtyIndex, 0, "Q'ty");
			writer.writeCellValue(purposeIndex, 0, "Purpose");
			writer.writeCellValue(headTypeIndex, 0, "Head type");
			writer.writeCellValue(torqueIndex, 0, "Torque\n(lb/in)");
			writer.writeCellValue(assyByIndex, 0, "Assy by");
			writer.writeCellValue(remarkIndex, 0, "Remark");

			writer.setRowHeight(0, 25);
			writer.setColumnWidth(itemIndex, 6);
			writer.setColumnWidth(picIndex, 8);
			writer.setColumnWidth(hhPINIndex, 30);
			writer.setColumnWidth(hpPINIndex, 20);
			writer.setColumnWidth(partNameIndex, 40);
			writer.setColumnWidth(qtyIndex, 10);
			writer.setColumnWidth(purposeIndex, 30);
			writer.setColumnWidth(headTypeIndex, 30);
			writer.setColumnWidth(torqueIndex, 20);
			writer.setColumnWidth(assyByIndex, 10);
			writer.setColumnWidth(remarkIndex, 30);

			Font font = writer.createFont();
			font.setBold(true);
			CellStyle blueBackgroundStyle = writer.createCellStyle();
			blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
			blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			blueBackgroundStyle.setFont(font);
			blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
			blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
			blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
			blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
			blueBackgroundStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());

			writer.getCell(itemIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(picIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(hhPINIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(hpPINIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(partNameIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(qtyIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(purposeIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(headTypeIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(torqueIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(assyByIndex, 0).setCellStyle(blueBackgroundStyle);
			writer.getCell(remarkIndex, 0).setCellStyle(blueBackgroundStyle);
			// write rows
			for (int i = 0; i < rowList.size(); i++) {
				ExportRow row = rowList.get(i);
				int rownum = i+1;
				writer.writeCellValue(itemIndex, rownum, i + 1);
				
				if(row.picture != null) {
					insertImg(writer.getWorkbook(), writer.getSheet(), row.picture, picIndex, i+1); 
				}else {
					writer.writeCellValue(picIndex, rownum, "");
				}
				
				writer.writeCellValue(hhPINIndex, rownum, row.hhpin);
				writer.writeCellValue(hpPINIndex, rownum, row.hppin);
				writer.writeCellValue(partNameIndex, rownum, row.partName);
				writer.writeCellValue(qtyIndex, rownum, row.qty);
				writer.writeCellValue(purposeIndex, rownum, row.purpose);
				writer.writeCellValue(headTypeIndex, rownum, row.headType);
				writer.writeCellValue(torqueIndex, rownum, row.torque);
				writer.writeCellValue(assyByIndex, rownum, row.assemblyCode);
				writer.writeCellValue(remarkIndex, rownum, row.remark);
				writer.setRowHeight(rownum,35);
			}

		}
	}

	private static void recursionBomLine(TCComponentBOMLine bomLine, List<ExportRow> rowList, int layer, int sheetType)
			throws Exception {

		if (!isVirtualPart(bomLine)) {
			layer++;
			rowList.add(generateRow(bomLine, layer, sheetType,0));
			if (bomLine.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for (int i = 0; i < listSubstitutes.length; i++) {
					TCComponentBOMLine substitutesBOMLine = listSubstitutes[i];
					rowList.add(generateRow(substitutesBOMLine, layer, sheetType,i+1));
				}
			}
		}

		if("N".equals(bomLine.getProperty("bl_occ_d9_ExportChild"))) {
			return;
		}
		
		AIFComponentContext[] children = bomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine subBomLine = (TCComponentBOMLine) children[i].getComponent();
			// filter substitutes
			if (subBomLine.isSubstitute()) {
				continue;
			}
			// next layer
			recursionBomLine(subBomLine, rowList, layer, sheetType);
		}
	}

	private static boolean isVirtualPart(TCComponentItem item) {
		String type = item.getType();
		if ("D9_BOMOptNode".equals(type)) {
			return true;
		} else if ("D9_BOMListNode".equals(type)) {
			return true;
		} else if ("D9_BOMTopNode".equals(type)) {
			return true;
		} else if ("D9_BOMColNode".equals(type)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isVirtualPart(TCComponentBOMLine bomLine) throws Exception {
		return isVirtualPart(bomLine.getItem());
	}

	private static ExportRow generateRow(TCComponentBOMLine bomLine, int layer, int sheetType,int substituteIndex) throws Exception {
		TCComponentItem item = bomLine.getItem();
		TCComponentItemRevision revision = bomLine.getItemRevision();
		ExportRow exportRow = new ExportRow();
		exportRow.hhpin = item.getProperty("item_id");
		exportRow.hppin = revision.getProperty("d9_CustomerPN");
		exportRow.remark = bomLine.getProperty("D9_Remark");
		exportRow.layer = layer;
		if (substituteIndex!=0) {
			exportRow.qty = "-";
			exportRow.substituteIndex=substituteIndex;
		} else {
			String property = bomLine.getProperty("bl_quantity");
			if (CommonTools.isEmpty(property)) {
				exportRow.qty = "1";
			} else {
				exportRow.qty = property;
			}	
		}
		if (sheetType == ExportSheet.TYPE_BOM) {
			exportRow.description = revision.getProperty("d9_EnglishDescription");
			exportRow.assemblyCode = revision.getProperty("d9_AssemblyCode");
		}
		if (sheetType == ExportSheet.TYPE_OPTION) {
			exportRow.hpDescription = revision.getProperty("d9_CustomerPNDescription");
			exportRow.matl = revision.getProperty("d9_MATL");
			exportRow.ccl = bomLine.getProperty("bl_occ_d9_CCL");
		}
		if (sheetType == ExportSheet.TYPE_SREW) {
			File picFile = null;
			TCComponent[] relatedComponents = revision.getRelatedComponents("TC_Is_Represented_By"); // 获取引用伪文件夹下面的附件
			for (TCComponent tcComponent : relatedComponents) {
				if(picFile != null) {
					break;
				}	
				if(tcComponent instanceof TCComponentItemRevision) {
					picFile = CommonTools.getModelJPEG_hp((TCComponentItemRevision)tcComponent);
				}				
			}
			exportRow.picture = picFile;
			exportRow.partName = revision.getProperty("d9_EnglishDescription");
			exportRow.purpose = bomLine.getProperty("bl_occ_d9_Purpose");
			exportRow.headType = revision.getProperty("d9_HeadType");
			exportRow.torque = bomLine.getProperty("bl_occ_d9_Torque");
			exportRow.assyBy = bomLine.getProperty("bl_occ_d9_AssyBy");
		}

		return exportRow;
	}
	
	/**
	 * 
	 * Robert 2022年4月12日
	 * 
	 * @param wb
	 * @param sheet
	 * @param imgFile
	 * @param col
	 * @param row
	 * @throws IOException
	 */
	public static void insertImg(Workbook wb, Sheet sheet, File imgFile, int col, int row) throws IOException {
		Cell c = sheet.getRow(row).getCell(col);
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		if(imgFile ==null) {
			return;
		}
		Image src = Toolkit.getDefaultToolkit().getImage(imgFile.getPath());
		BufferedImage bufferImg = toBufferedImage(src);// Image to BufferedImage
//        BufferedImage bufferImg = ImageIO.read(imgFile);
		ImageIO.write(bufferImg, "JPG", byteArrayOut);
		Drawing patriarch = sheet.createDrawingPatriarch();
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
		ClientAnchor anchor = patriarch.createAnchor(3 * 10000, 3 * 10000, 20, 70, col, row, col, row);
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

}
