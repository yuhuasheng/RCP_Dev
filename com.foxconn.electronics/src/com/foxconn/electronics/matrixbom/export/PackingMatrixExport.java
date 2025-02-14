package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
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
@Deprecated
public class PackingMatrixExport implements IMatrixBOMExportWriter{
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
			//ExcelWriter writer = ExcelUtil.getWriter(true);
			//writer.setSheet("History");
			writer.setSheet("PA BOM list");
			
			
			//整体表头以及变体数据
			ArrayList<PackingMatrixExportBean> exportHoder = exportHoder();
			writeTop(writer,exportHoder);
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
			
			//导出打开
			//writer.getWorkbook().setActiveSheet(1);
			//writer.getWorkbook().removeSheetAt(3);
			
			try {
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
						String revision_id = children.getProperty("bl_rev_item_revision_id");
						String bl_occ_d9_Remark = children.getProperty("bl_occ_d9_Remark");
						
						int no = i+1;
						bomInfo.setNo(""+no);
						bomInfo.setIsSub("");
						bomInfo.setLevel(""+1);
						bomInfo.setD9_Customer(bl_rev_d9_Customer);
						bomInfo.setItemid(bl_item_item_id);
						bomInfo.setDescription(description);
						bomInfo.setBl_quantity(bl_quantity);
						bomInfo.setUn(d9_Un);
						bomInfo.setD9_SupplierZF(d9_SupplierZF);
						bomInfo.setState("");
						bomInfo.setRevision(revision_id);
						bomInfo.setEcn("");
						bomInfo.setRemark(bl_occ_d9_Remark);
						
						list.add(bomInfo);	
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
	
	public void writeTop(ExcelWriter writer, ArrayList<PackingMatrixExportBean> exportHoder) {
		// 设置表头
		String [] excelTop1 = new String [] {"客戶型號","E24d HO\n(RMN:HSD-0039-F)","Foxconn型號","LP24QJ 重慶 (Commerical)"} ;
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
		
		writer.writeCellValue(0, 0, "客戶型號");
		writer.merge(0, 0, 0, 4, "客戶型號", false);
		writer.getCell(0, 0).setCellStyle(blueBackgroundStyle);
		writer.setRowHeight(0, 50);
		writer.writeCellValue(5, 0, "E24d HO \n (RMN:HSD-0039-F)");
		writer.setColumnWidth(5, 25);
		writer.getCell(5, 0).setCellStyle(blueBackgroundStyle1);
		writer.writeCellValue(6, 0, "Foxconn型號");
		writer.merge(0, 0, 6, 9, "Foxconn型號", false);
		writer.getCell(6, 0).setCellStyle(blueBackgroundStyle);
		writer.writeCellValue(10, 0, "LP24QJ 重慶 (Commerical)");
		writer.merge(0, 0, 10, 23, "LP24QJ 重慶 (Commerical)", false);
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
		
		String [] excelTop2 = new String [] {"料號編碼中心","","出貨地區物料使用"} ;
		writer.writeCellValue(0, 1, "料號編碼中心");
		writer.merge(1, 1, 0, 5, "料號編碼中心", false);
		writer.getCell(0, 1).setCellStyle(blueBackgroundStyle2);
		writer.setRowHeight(1, 25);
		writer.writeCellValue(6, 1, " ");
		writer.merge(1, 1, 6, 12, " ", false);
		writer.getCell(6, 1).setCellStyle(blueBackgroundStyle2);
		writer.writeCellValue(13, 1, "出貨地區物料使用");
		writer.merge(1, 1, 13, 23, "料號編碼中心", false);
		writer.getCell(13, 1).setCellStyle(blueBackgroundStyle2);
		
		
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
		
		String [] excelTop3 = new String [] {"序號\n(*註１)","替代料\n(*註2)","階次","客戶\nRegion/PN","料號","描  述","用量\n(每100PC)"
				,"單位","廠商","狀態","版本","ECR變更說明","備注"} ;
		
		
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
			}else {
				writer.setColumnWidth(i, 8);
			}
			
			writer.getCell(i, 2).setCellStyle(blueBackgroundStyle3);
		}
		
		for (int i = 0; i < exportHoder.size(); i++) {
			PackingMatrixExportBean bomInfo = exportHoder.get(i);
			String d9_Customer = bomInfo.getD9_Customer();
			
			writer.writeCellValue(13+i, 2, d9_Customer);
			writer.setRowHeight(2, 50);
			writer.setColumnWidth(13+i, 7);
			writer.getCell(13+i, 2).setCellStyle(blueBackgroundStyle3);
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
			String no = bomInfo.getNo();
			writer.writeCellValue(0, row, no);
			writer.getCell(0, row).setCellStyle(blueBackgroundStyle1);
			String isSub = bomInfo.getIsSub();
			writer.writeCellValue(1, row, isSub);
			writer.getCell(1, row).setCellStyle(blueBackgroundStyle1);
			String level = bomInfo.getLevel();
			writer.writeCellValue(2, row, level);
			writer.getCell(2, row).setCellStyle(blueBackgroundStyle1);
			String d9_Customer = bomInfo.getD9_Customer();
			writer.writeCellValue(3, row, d9_Customer);
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
			writer.writeCellValue(9, row, state);
			writer.getCell(9, row).setCellStyle(blueBackgroundStyle1);
			String revision = bomInfo.getRevision();
			writer.writeCellValue(10, row, revision);
			writer.getCell(10, row).setCellStyle(blueBackgroundStyle1);
			String ecn = bomInfo.getEcn();
			writer.writeCellValue(11, row, ecn);
			writer.getCell(11, row).setCellStyle(blueBackgroundStyle1);
			String remark = bomInfo.getRemark();
			writer.writeCellValue(12, row, remark);
			writer.getCell(12, row).setCellStyle(blueBackgroundStyle1);
			
			for (int j = 0; j < exportHoder.size(); j++) {
				int conn = j+13;
				String d9_Customer2 = exportHoder.get(j).getD9_Customer();
				if(d9_Customer.equals(d9_Customer2)) {
					writer.writeCellValue(conn, row, "◎");
					map.put(itemid, conn);
				} else {
					writer.writeCellValue(conn, row, "");
				}
				writer.getCell(conn, row).setCellStyle(blueBackgroundStyle1);
			}
			
		}
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
						Integer integer = map.get(chilHoder_id);
						
						AIFComponentContext[] matrixAIF = childrenMatrix.getChildren();
						for (int j = 0; j < matrixAIF.length; j++) {
							InterfaceAIFComponent matrix = matrixAIF[j].getComponent();
							if(matrix instanceof TCComponentBOMLine) {
								TCComponentBOMLine matrixBOMLine = (TCComponentBOMLine)matrix;
								no = no+1;
								getItem(mapitem, matrixBOMLine, integer,no,"");
								
								if(matrixBOMLine.hasSubstitutes()) {
									TCComponentBOMLine[] listSubstitutes = matrixBOMLine.listSubstitutes();
									for (int k = 0; k < listSubstitutes.length; k++) {
										getItem(mapitem, listSubstitutes[k], integer,no,"替代料");
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
		CellStyle blueBackgroundStyle = writer.createCellStyle();
		blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		
		int mergeRow = 1;
		
		for (Entry<String,ArrayList<PackingMatrixExportBean>> entrySet : exportMatrix.entrySet()) {
			ArrayList<PackingMatrixExportBean> bomInfoList = entrySet.getValue();
			for (PackingMatrixExportBean bomInfo:bomInfoList) {
				rowInt = rowInt+1;
				
				String no = bomInfo.getNo();
				writer.writeCellValue(0, rowInt, no);
				String isSub = bomInfo.getIsSub();
				writer.writeCellValue(1, rowInt, isSub);
				String level = bomInfo.getLevel();
				writer.writeCellValue(2, rowInt, level);
				String d9_Customer = bomInfo.getD9_Customer();
				writer.writeCellValue(3, rowInt, d9_Customer);
				String itemid = bomInfo.getItemid();
				writer.writeCellValue(4, rowInt, itemid);
				String description = bomInfo.getDescription();
				
				if("替代料".equals(isSub)) {
					mergeRow++;
					Sheet sheet = writer.getSheet();
					removeMergedRegion(sheet, rowInt-mergeRow, 0);
					writer.merge(rowInt-mergeRow, rowInt, 5, 5, description, false);
				} else {
					mergeRow = 0;
					writer.writeCellValue(5, rowInt, description);
					writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle1);
				}
				
				String bl_quantity = bomInfo.getBl_quantity();
				writer.writeCellValue(6, rowInt, bl_quantity);
				String un = bomInfo.getUn();
				writer.writeCellValue(7, rowInt, un);
				String d9_SupplierZF = bomInfo.getD9_SupplierZF();
				writer.writeCellValue(8, rowInt, d9_SupplierZF);
				String state = bomInfo.getState();
				writer.writeCellValue(9, rowInt, state);
				String revision = bomInfo.getRevision();
				writer.writeCellValue(10, rowInt, revision);
				String ecn = bomInfo.getEcn();
				writer.writeCellValue(11, rowInt, ecn);
				String remark = bomInfo.getRemark();
				writer.writeCellValue(12, rowInt, remark);
				
				boolean getisChildren = bomInfo.getisChildren();
				if(getisChildren) {
					writer.getCell(0, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(1, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(2, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(3, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(4, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(6, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(7, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(8, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(9, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(10, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(11, rowInt).setCellStyle(blueBackgroundStyle);
					writer.getCell(12, rowInt).setCellStyle(blueBackgroundStyle);
				}else {
					writer.getCell(0, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(1, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(2, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(3, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(4, rowInt).setCellStyle(blueBackgroundStyle1);
					//writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(6, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(7, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(8, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(9, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(10, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(11, rowInt).setCellStyle(blueBackgroundStyle1);
					writer.getCell(12, rowInt).setCellStyle(blueBackgroundStyle1);
				}
				
				ArrayList<Integer> parentList = bomInfo.getParent();
				for (int j = 0; j < k; j++) {
					int conn = j+13;
					boolean isnot0 = true;
					for (Integer parent:parentList) {
						if(conn == parent) {
							writer.writeCellValue(conn, rowInt, "○");
							isnot0 = false;
						}
					}
					if(isnot0)
						writer.writeCellValue(conn, rowInt, "");
					writer.getCell(conn, rowInt).setCellStyle(blueBackgroundStyle1);
				}
			}
			
		}
		
	}
	
	public void getItem(LinkedHashMap<String, ArrayList<PackingMatrixExportBean>> mapitem,TCComponentBOMLine matrixBOMLine,Integer integer, int no,String isSub) throws TCException {
		
		String bl_item_item_id = matrixBOMLine.getProperty("bl_item_item_id");
		String bl_quantity = matrixBOMLine.getProperty("bl_quantity");
		
		boolean isquan = true;
		ArrayList<PackingMatrixExportBean> arrayList = mapitem.get(bl_item_item_id);
		if(CommonTools.isNotEmpty(arrayList)) {
			for (PackingMatrixExportBean array:arrayList) {
				String bl_quantity2 = array.getBl_quantity();
				ArrayList<Integer> parent = array.getParent();
				if(integer!=null) {
					if(CommonTools.isNotEmpty(parent)) {
						if(bl_quantity2.equals(bl_quantity)) {
							isquan = false;
							parent.add(integer);
						}
					} else {
						isquan = false;
						parent = new ArrayList<Integer>();
						parent.add(integer);
						array.setParent(parent);
					}
				}
			}
		}
		
		if(isquan) {
			ArrayList<PackingMatrixExportBean> arrayList1 = mapitem.get(bl_item_item_id);
			if(CommonTools.isEmpty(arrayList1)) {
				
				String bl_rev_d9_Customer = matrixBOMLine.getProperty("bl_rev_d9_Customer");
				String description = matrixBOMLine.getProperty("bl_rev_d9_EnglishDescription");
				String d9_Un = matrixBOMLine.getProperty("bl_Part Revision_d9_Un");
				String d9_SupplierZF = matrixBOMLine.getProperty("bl_Part Revision_d9_SupplierZF");
				String revision_id = matrixBOMLine.getProperty("bl_rev_item_revision_id");
				String bl_occ_d9_Remark = matrixBOMLine.getProperty("bl_occ_d9_Remark");
				
				PackingMatrixExportBean bomInfo = new PackingMatrixExportBean();
				//int no = i+1;
				bomInfo.setNo(""+no);
				bomInfo.setIsSub(isSub);
				bomInfo.setLevel(""+2);
				bomInfo.setD9_Customer(bl_rev_d9_Customer);
				bomInfo.setItemid(bl_item_item_id);
				bomInfo.setDescription(description);
				bomInfo.setBl_quantity(bl_quantity);
				bomInfo.setUn(d9_Un);
				bomInfo.setD9_SupplierZF(d9_SupplierZF);
				bomInfo.setState("");
				bomInfo.setRevision(revision_id);
				bomInfo.setEcn("");
				bomInfo.setRemark(bl_occ_d9_Remark);
				
				AIFComponentContext[] children = matrixBOMLine.getChildren();
				if(CommonTools.isNotEmpty(children)) {
					bomInfo.setisChildren(true);
				}
				
				if(integer!=null) {
					ArrayList<Integer> parent = new ArrayList<Integer>();
					parent.add(integer);
					bomInfo.setParent(parent);
				}
				

				ArrayList<PackingMatrixExportBean> list = new ArrayList<PackingMatrixExportBean>();
				list.add(bomInfo);
				mapitem.put(bl_item_item_id, list);
			}
		}
	}
	
	
	public ArrayList<PackingMatrixExportBean> exportMatrixChildren() throws Exception {
		ArrayList<PackingMatrixExportBean> list = new ArrayList<PackingMatrixExportBean>();
		int no = 0;
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
						TCComponentBOMLine childrenMatrix = (TCComponentBOMLine)component;
						
						AIFComponentContext[] matrixAIF = childrenMatrix.getChildren();
						for (int j = 0; j < matrixAIF.length; j++) {
							InterfaceAIFComponent matrix = matrixAIF[j].getComponent();
							if(matrix instanceof TCComponentBOMLine) {
								TCComponentBOMLine matrixBOMLine = (TCComponentBOMLine)matrix;
								
								AIFComponentContext[] childrens = matrixBOMLine.getChildren();
								if(childrens!=null && childrens.length > 0) {
									String item_id = matrixBOMLine.getProperty("bl_item_item_id");
									if(listContains(list, item_id)) {
										getItemChildren(matrixBOMLine, list, no, "渲染颜色");
										for (int k = 0; k < childrens.length; k++) {
											InterfaceAIFComponent children = childrens[k].getComponent();
											if(children instanceof TCComponentBOMLine) {
												TCComponentBOMLine childrenBOMLine = (TCComponentBOMLine)children;
												no = no+1;
												getItemChildren(childrenBOMLine, list, no, "");
												if(childrenBOMLine.hasSubstitutes()) {
													TCComponentBOMLine[] listSubstitutes = childrenBOMLine.listSubstitutes();
													for (int l = 0; l < listSubstitutes.length; l++) {
														getItemChildren(listSubstitutes[l], list, no, "替代料");
													}
												}
											}
										}
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
		String revision_id = matrixBOMLine.getProperty("bl_rev_item_revision_id");
		String bl_occ_d9_Remark = matrixBOMLine.getProperty("bl_occ_d9_Remark");
		
		PackingMatrixExportBean bomInfo = new PackingMatrixExportBean();
		//int no = i+1;
		bomInfo.setNo(""+no);
		bomInfo.setIsSub(isSub);
		bomInfo.setLevel(""+3);
		bomInfo.setD9_Customer(bl_rev_d9_Customer);
		bomInfo.setItemid(bl_item_item_id);
		bomInfo.setDescription(description);
		bomInfo.setBl_quantity(bl_quantity);
		bomInfo.setUn(d9_Un);
		bomInfo.setD9_SupplierZF(d9_SupplierZF);
		bomInfo.setState("");
		bomInfo.setRevision(revision_id);
		bomInfo.setEcn("");
		bomInfo.setRemark(bl_occ_d9_Remark);
		
		list.add(bomInfo);
	}
	public void writeMatrixChildren(ExcelWriter writer, ArrayList<PackingMatrixExportBean> bomInfoList,int k) {
		CellStyle blueBackgroundStyle = writer.createCellStyle();
		blueBackgroundStyle.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle.setBorderRight(BorderStyle.THIN);// 右边框
		blueBackgroundStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		blueBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle blueBackgroundStyle1 = writer.createCellStyle();
		blueBackgroundStyle1.setAlignment(HorizontalAlignment.CENTER);
		blueBackgroundStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
		blueBackgroundStyle1.setBorderBottom(BorderStyle.THIN); // 下边框
		blueBackgroundStyle1.setBorderLeft(BorderStyle.THIN);// 左边框
		blueBackgroundStyle1.setBorderTop(BorderStyle.THIN);// 上边框
		blueBackgroundStyle1.setBorderRight(BorderStyle.THIN);// 右边框
		
		int mergeRow = 0;
		for (PackingMatrixExportBean bomInfo:bomInfoList) {
			rowInt = rowInt+1;
			String no = bomInfo.getNo();
			
			String isSub = bomInfo.getIsSub();
			
			String level = bomInfo.getLevel();
			
			String d9_Customer = bomInfo.getD9_Customer();
			writer.writeCellValue(3, rowInt, d9_Customer);
			String itemid = bomInfo.getItemid();
			writer.writeCellValue(4, rowInt, itemid);
			String description = bomInfo.getDescription();
			String bl_quantity = bomInfo.getBl_quantity();
			writer.writeCellValue(6, rowInt, bl_quantity);
			String un = bomInfo.getUn();
			writer.writeCellValue(7, rowInt, un);
			String d9_SupplierZF = bomInfo.getD9_SupplierZF();
			writer.writeCellValue(8, rowInt, d9_SupplierZF);
			String state = bomInfo.getState();
			writer.writeCellValue(9, rowInt, state);
			String revision = bomInfo.getRevision();
			writer.writeCellValue(10, rowInt, revision);
			String ecn = bomInfo.getEcn();
			writer.writeCellValue(11, rowInt, ecn);
			String remark = bomInfo.getRemark();
			writer.writeCellValue(12, rowInt, remark);
			
			
			if("渲染颜色".equals(isSub)) {
				writer.writeCellValue(0, rowInt, "");
				writer.writeCellValue(1, rowInt, "");
				writer.writeCellValue(2, rowInt, "");
				writer.writeCellValue(5, rowInt, "");
				
				writer.getCell(0, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(1, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(2, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(3, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(4, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(6, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(7, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(8, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(9, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(10, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(11, rowInt).setCellStyle(blueBackgroundStyle);
				writer.getCell(12, rowInt).setCellStyle(blueBackgroundStyle);
				
				
				for (int j = 0; j < k; j++) {
					int conn = j+13;
					writer.writeCellValue(conn, rowInt, "");
					writer.getCell(conn, rowInt).setCellStyle(blueBackgroundStyle);
				}
				
			}else {
				writer.writeCellValue(0, rowInt, no);
				writer.writeCellValue(1, rowInt, isSub);
				writer.writeCellValue(2, rowInt, level);
				
				if("替代料".equals(isSub)) {
					mergeRow++;
					Sheet sheet = writer.getSheet();
					removeMergedRegion(sheet, rowInt-mergeRow, 0);
					writer.merge(rowInt-mergeRow, rowInt, 5, 5, description, false);
				} else {
					mergeRow = 0;
					writer.writeCellValue(5, rowInt, description);
					writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle1);
				}
				
				writer.getCell(0, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(1, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(2, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(3, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(4, rowInt).setCellStyle(blueBackgroundStyle1);
				//writer.getCell(5, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(6, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(7, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(8, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(9, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(10, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(11, rowInt).setCellStyle(blueBackgroundStyle1);
				writer.getCell(12, rowInt).setCellStyle(blueBackgroundStyle1);
				
				for (int j = 0; j < k; j++) {
					int conn = j+13;
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
}
