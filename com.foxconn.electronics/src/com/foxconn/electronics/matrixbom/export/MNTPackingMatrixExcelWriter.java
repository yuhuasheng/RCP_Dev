package com.foxconn.electronics.matrixbom.export;

import java.io.File;
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

import com.foxconn.electronics.matrixbom.domain.BomLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.util.Pair;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;


/**
 * 
 * @author MW00457
 *
 */
@Deprecated
public class MNTPackingMatrixExcelWriter implements IMatrixBOMExportWriter{
	
	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision,List<MatrixBOMExportBean> beanList,ArrayList<String> varList) throws Exception {
		
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyyMMdd = sdf.format(new Date());
		String tmpDirPath = FileUtil.getTmpDirPath();
		// 产品线名称+版本号+年月日.xlsx
//		File dest = new File(tmpDirPath+"MB"+System.currentTimeMillis()+".xlsx");
//		File dest = new File(tmpDirPath+objectName+"_"+revName+"_"+yyyyMMdd+".xlsx");
		File dest = new File(tmpDirPath + File.separator + objectName.replace(" ","_") + "_" + revName + ".xlsx");
		ExcelWriter writer = ExcelUtil.getWriter(dest);
		writer.renameSheet("Cable Matrix");
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
		CellStyle headStyle = writer.createCellStyle();
		headStyle.cloneStyleFrom(wrapTextStyle);
		Font headFont = writer.createFont();
		headFont.setBold(true);
		headFont.setFontHeight((short) 250);
		headStyle.setFont(headFont);
		headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
		int headerRowNum = 1;
		int headerColNun=0;
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "NO.",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "替代料",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "階次",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "料號",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "Description",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "單位",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "廠商",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun++, headerRowNum, "度量單位",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun, headerRowNum, "版本",headStyle);
		int i = 1;
		for(;i<=varList.size();i++) {
			String key = varList.get(i-1);
			MatrixBOMExportService.setValueAndStyle(writer,headerColNun+i, headerRowNum, key,headStyle);
		}
		MatrixBOMExportService.setValueAndStyle(writer,headerColNun+i, headerRowNum, "備注",headStyle);
		// 设置列宽
		headerColNun=0;
		writer.setColumnWidth(headerColNun++, 5);
		writer.setColumnWidth(headerColNun++, 10);
		writer.setColumnWidth(headerColNun++, 10);
		writer.setColumnWidth(headerColNun++, 30);
		writer.setColumnWidth(headerColNun++, 40);
		writer.setColumnWidth(headerColNun++, 10);
		writer.setColumnWidth(headerColNun++, 10);
		writer.setColumnWidth(headerColNun++, 20);
		writer.setColumnWidth(headerColNun++, 20);
		// 写行
		i = 0;
		for(;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			if(bean==null) {
				continue;
			}
			int row = i+2;
			int col = 0;
			writer.writeCellValue(col++,row,i+1);
			if(bean.isSubstitutes) {
				writer.writeCellValue(col++,row,"替代料");
			}else {
				writer.writeCellValue(col++,row,"");
			}
			writer.writeCellValue(col++,row,bean.layer==0?"":bean.layer);
			writer.writeCellValue(col++,row,bean.hhpn);
			writer.writeCellValue(col++,row,bean.description);
			writer.writeCellValue(col++,row,bean.unit);
			writer.writeCellValue(col++,row,bean.factory);
			writer.writeCellValue(col++,row,bean.uom);
			writer.writeCellValue(col,row,bean.revName);
			if(bean.variablesList!=null) {
				int size = bean.variablesList.size();
				int j = 1;
				for(;j<=size;j++) {
					writer.writeCellValue(col+j,row,bean.variablesList.get(j-1).getValue());
				}
				writer.writeCellValue(col+j,row,bean.remark);
			}
			writer.setRowHeight(row, 30);
		}
		// 合并替代料
		int startRow = 0;
		int substituesCount = 0;
		i = 0;
		for(;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			if(bean==null) {
				continue;
			}
			boolean isPrimay = bean.layer > 0 && !bean.isSubstitutes;
			if(isPrimay) {
				if(substituesCount != 0) {
					writer.merge(startRow+2, startRow+substituesCount+2, 1, 1, "替代料", false);
				}
				startRow = i;
				substituesCount = 0;
			}
			if(bean.isSubstitutes) {
				substituesCount++;
			}
		}
//		writer.merge(0, 0, 0, col+varList.size()+1, objectName, false);
//		writer.getCell(0,0).setCellStyle(titleStyle);
		writer.close();
		return dest.getAbsolutePath();
	}
	
	@Override
	public String writerDCN(TCComponentItemRevision matrixItemRevision,String dcnNo, List<MatrixBOMExportBean> list) throws Exception {
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
		// TODO Auto-generated method stub
		return null;
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
