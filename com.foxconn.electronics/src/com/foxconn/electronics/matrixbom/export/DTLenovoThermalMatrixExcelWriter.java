package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.export.domain.CPUCoolerBean;
import com.foxconn.electronics.matrixbom.export.domain.DTThermalBean;
import com.foxconn.electronics.matrixbom.export.domain.MegerCellEntity;
import com.foxconn.electronics.matrixbom.export.domain.SSDBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.matrixbom.service.MatrixBOMService;
import com.foxconn.electronics.util.TCPropName;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.Pair;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class DTLenovoThermalMatrixExcelWriter implements IMatrixBOMExportWriter {

	private static final String EXCEL_TEMPLATE = "com/foxconn/electronics/matrixbom/export/template/DT_Lenovo_Thermal_Matrix_List.xlsx";
	
	private static final int STARTROW = 3;
	
	public static final int CPUCOLLENGTH = 10;
	
	public static final int SSDCOLLENGTH = 8;
	
	private static final int ROWHEIGHT = 160;
	
	private static final String GROUP_0 = "CPU Cooler";
	
	private static final String GROUP_1 = "SSD";
	
	
	@Override
	public String writerDCN(TCComponentItemRevision matrixItemRevision, String dcnNo, List<MatrixBOMExportBean> list)
			throws Exception {
		return null;
	}

	@Override
	public String writerBOM(TCComponentItemRevision matrixItemRevision, List<MatrixBOMExportBean> beanList, ArrayList<String> varList) throws Exception {
		URL url = this.getClass().getClassLoader().getResource(EXCEL_TEMPLATE);			
		InputStream in = url.openStream();
		
		String dir = CommonTools.getFilePath("DT Lenovo Thermal Matrix"); // 查找保存DT Lenovo Thermal Matrix文件的文件夹
		System.out.println("【INFO】 dir: " + dir);		
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		String excelName = objectName.replace(" ","_") + "_" + revName + ".xlsx";
		File templateFile = new File(dir + File.separator + excelName);
		
		try {
			FileUtil.writeFromStream(in, templateFile);
		} catch (IORuntimeException e) {
			MessageBox.post(AIFUtility.getActiveDesktop(),"请关闭打开的Excel文件："+ excelName, "提示", MessageBox.INFORMATION);			
			return null;
		}
		
		in.close();
		
		ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(templateFile);
		
		generateExcel(writer, "Thermal matrix汇总", beanList, varList, objectName);
		
		beanList.removeIf(bean -> CommonTools.isEmpty(bean.type)); // 移除type类型为空的记录，便于后续的分组
//		Map<String, List<MatrixBOMExportBean>> map = beanList.stream().collect(Collectors.groupingBy(bean -> bean.type));
		Map<String, List<MatrixBOMExportBean>> map = groupByType(beanList);
		map.forEach((key, value) -> {
//			Collections.sort(value);
			generateExcel(writer, key, value, varList, objectName);			
			
		});
		
		List<String> sheetNameList = writer.getSheetNames();
		for (int j = 0; j < sheetNameList.size(); j++) {
			if ("sheet1".equalsIgnoreCase(sheetNameList.get(j))) {
				writer.getWorkbook().removeSheetAt(j);
			}
		}		
		
		try {
			writer.close();
		} catch (IORuntimeException e) {
			MessageBox.post(AIFUtility.getActiveDesktop(),"请关闭打开的Excel文件："+excelName, "提示", MessageBox.INFORMATION);
			
			return null;
		}		
		
		return templateFile.getAbsolutePath();
	}

	@Override
	public String createDCNwriterDCN(TCComponentItemRevision matrixItemRevision, String dcnNo,
			List<MatrixBOMExportBean> list, String path) throws Exception {
		String objectName = matrixItemRevision.getProperty("object_name");
		String revName = matrixItemRevision.getProperty("item_revision_id");
		String dir = CommonTools.getFilePath("DT Lenovo Thermal Matrix_ChangeList"); // 查找保存DT Lenovo Thermal Matrix文件的文件夹
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		File dest = new File(dir + File.separator + objectName.replace(" ","_") + "_" + revName + ".xlsx");
		//File dest = new File(tmpDirPath+objectName.replace(" ","_")+"_"+revName+"_ChangeList"+yyyyMMdd+".xlsx");
		
		InputStream in = new FileInputStream(path);
		FileUtil.writeFromStream(in, dest);
		in.close();
		
		cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(dest);
		
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
			setValueAndStyle(writer, 4, row, bean.programName, textCellStyle);
			setValueAndStyle(writer, 5, row, "Cooler", textCellStyle);
			setValueAndStyle(writer, 6, row, bean.description, textCellStyle);
			setValueAndStyle(writer, 7, row, bean.quantity, textCellStyle);
			setValueAndStyle(writer, 8, row, bean.lenovoPN, textCellStyle);
			setValueAndStyle(writer, 9, row, bean.fruPN, textCellStyle);
			setValueAndStyle(writer, 10, row, bean.hhpn, textCellStyle);
			setValueAndStyle(writer, 11, row, bean.vendor, textCellStyle);
			setValueAndStyle(writer, 12, row, bean.vendorPN, textCellStyle);
			setValueAndStyle(writer, 13, row, bean.coolerFanVendor, textCellStyle);
			setValueAndStyle(writer, 14, row, bean.coolerFanModelNo, textCellStyle);
			setValueAndStyle(writer, 15, row, bean.remark, textCellStyle);
			writer.setRowHeight(row, 55);
		}
		writer.getWorkbook().setActiveSheet(0);
		writer.getWorkbook().removeSheetAt(3);
		
		writer.close();
		return dest.getAbsolutePath();
	}


	public List<DTThermalBean> getVariableList(TCSession session, TCComponentItemRevision itemRev) throws TCException {
		TCComponentBOMWindow window = null;
		TCComponentItemRevision variantsItemRev = null;
		List<DTThermalBean> list = new ArrayList<DTThermalBean>();		
		AIFComponentContext[] related = itemRev.getRelated("D9_HasVariants_REL");		
		if(related.length == 0) {
			// 没有Holder，返回
			throw new TCException("没有任何数据");
		}		
		
		for (AIFComponentContext aifComponentContext : related) {
			variantsItemRev = ((TCComponentItemRevision) aifComponentContext.getComponent());
			String objectName = variantsItemRev.getProperty("object_name");
			DTThermalBean bean = null;
			if (objectName.contains(GROUP_0)) {
				bean = new CPUCoolerBean();
			} else if (objectName.contains(GROUP_1)) {
				bean = new SSDBean();
			}
			
			if (CommonTools.isEmpty(bean)) {
				continue;
			}
			try {
				window = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topLine = TCUtil.getTopBomline(window, variantsItemRev);		
				AIFComponentContext[] children = topLine.getChildren();
				if (CommonTools.isEmpty(children)) {
					return list;
				}
				
				unpackBOMLine(children); // 解包已经打包的BOMLine
				int index = 1;
				for (AIFComponentContext context : children) {
					TCComponentBOMLine child = ((TCComponentBOMLine) context.getComponent());
					DTThermalBean childBean = null;
					if (!child.isSubstitute()) {						
						if (bean instanceof CPUCoolerBean) {
							childBean = new CPUCoolerBean(); 					
						} else if (bean instanceof SSDBean) {
							childBean = new SSDBean();
						}
						childBean.setItem(index);
						childBean.setType(objectName);
						list.add(tcPropMapping(childBean, child));
						index++;
					}
					
					if (child.hasSubstitutes()) {
						TCComponentBOMLine[] listSubstitutes = child.listSubstitutes();
						for (TCComponentBOMLine subBomLine : listSubstitutes) {
							DTThermalBean subBean = null;
							if (bean instanceof CPUCoolerBean) {
								subBean = new CPUCoolerBean(); 					
							} else if (bean instanceof SSDBean) {
								subBean = new SSDBean();
							}
							subBean.setFindNum(childBean.getFindNum());
							subBean.setItem(index);
							subBean.setType(objectName);
							subBean.setSub(true);
							list.add(tcPropMapping(subBean, subBomLine));
							index++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (window != null) {
					window.close();
				}
			}
		}	
		
		if (CommonTools.isNotEmpty(list)) {
			list.sort(Comparator.comparing(DTThermalBean::getFindNum));
		}
		return list;
	}
	
	private void unpackBOMLine(AIFComponentContext[] children) throws TCException {
		for (AIFComponentContext context : children) {
			TCComponentBOMLine child = ((TCComponentBOMLine) context.getComponent());
			if (child.isPacked()) { // 判断是否已经打包
				child.unpack();
			}
		}
	}
	
	public List<MatrixBOMExportBean> getBeanList(MatrixBOMService matrixBOMService,String uid,ArrayList<String> varList) throws Exception {
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AjaxResult ar = matrixBOMService.getMatrixBOMStruct(uid);
		JSONObject jsonObject = (JSONObject) ar.get("data");
		ProductLineBOMBean bomBean = (ProductLineBOMBean) jsonObject.get("partList");
		VariableBOMBean varBean = (VariableBOMBean) jsonObject.get("variableBOM");
//		ar = matrixBOMService.getImg(uid);
		List<ProductLineBOMBean> productList = bomBean.getChild();
		for(ProductLineBOMBean productBean : productList) {
			MatrixBOMExportBean assembleMatixBOMBean = assembleMatixBOMBean(productBean,varList,varBean,null);
			List<ProductLineBOMBean> subList = productBean.getSubList();
			if(CommonTools.isNotEmpty(subList)) {
				ArrayList<MatrixBOMExportBean> listSubMatrix = new ArrayList<MatrixBOMExportBean>();
				for(ProductLineBOMBean productSubBean : subList) {
					MatrixBOMExportBean subAssembleMatixBOMBean = assembleMatixBOMBean(productSubBean,varList,varBean,null);
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
	
	

	public String exportExcel(TCComponentItemRevision itemRev, List<DTThermalBean> list) throws Exception {
		OutputStream out = null;
		String dir = CommonTools.getFilePath("DT Lenovo Thermal Matrix"); // 查找保存DT Lenovo Thermal Matrix文件的文件夹
		System.out.println("【INFO】 dir: " + dir);		
		CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
		String objectName = itemRev.getProperty("object_name");
		String revName = itemRev.getProperty("item_revision_id");
		
		File templateFile = new File(dir + File.separator + objectName.replace(" ","_") + "_" + revName + ".xlsx");
		
		ExcelUtil excelUtil = new ExcelUtil();
		Workbook wb = excelUtil.getWorkbook(EXCEL_TEMPLATE);
		Sheet sheet = null;
		CellStyle cellStyle = excelUtil.getCellStyle2(wb);
		
		Map<String, List<DTThermalBean>> groupByMap = list.stream().collect(Collectors.groupingBy(bean -> bean.getType()));
		for (Map.Entry<String, List<DTThermalBean>> entry : groupByMap.entrySet()) {
			String key = entry.getKey();
			List<DTThermalBean> value = entry.getValue();
			if (key.contains(GROUP_0)) {
				sheet = wb.getSheet(GROUP_0);
				excelUtil.setCellValue2(value, STARTROW, CPUCOLLENGTH, sheet, cellStyle);
			} else if (key.contains(GROUP_1)) {
				sheet = wb.getSheet(GROUP_1);
				excelUtil.setCellValue2(value, STARTROW, SSDCOLLENGTH, sheet, cellStyle);
				List<MegerCellEntity> megerCellList = excelUtil.scanMegerCells(value, "findNum", 3);
				for (MegerCellEntity cellEntity : megerCellList) {
					sheet.addMergedRegion(new CellRangeAddress(cellEntity.getStartRow(), cellEntity.getEndRow(), 7, 7));
				}
			}
		}
		
		out = new FileOutputStream(templateFile);
		wb.write(out);
		out.flush();
		out.close();	
		return templateFile.getAbsolutePath();
	}
	
	@Override
	public MatrixBOMExportBean assembleMatixChangeBean(TCComponentBOMLine impactBOMLine,
			TCComponentBOMLine sulotionBomLine, String changeType) throws Exception {
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
 		
 		e.programName = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_ProgramName");
 		e.description = mergeProperties(impactBOMLine, sulotionBomLine, "bl_rev_d9_EnglishDescription");
 		e.quantity = mergeProperties(impactBOMLine, sulotionBomLine, "bl_quantity");
 		e.lenovoPN = mergeProperties(impactBOMLine, sulotionBomLine, "bl_rev_d9_CustomerPN");
 		e.fruPN = mergeProperties(impactBOMLine, sulotionBomLine, "d9_FRUPN");
 		e.vendor = mergeProperties(impactBOMLine, sulotionBomLine, "d9_ManufacturerID");
		e.vendorPN = mergeProperties(impactBOMLine, sulotionBomLine, "d9_ManufacturerPN");
		e.coolerFanVendor = mergeProperties(impactBOMLine, sulotionBomLine, "d9_CoolerFanVendor");
		e.coolerFanModelNo = mergeProperties(impactBOMLine, sulotionBomLine, "d9_CoolerFanModelNo");
		//e.category = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Category");
		//e.factory = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Plant");
		
		e.remark = mergeProperties(impactBOMLine, sulotionBomLine, "bl_occ_d9_Remark");
		return e;
	}

	@Override
	public MatrixBOMExportBean assembleMatixBOMBean(IMatixBOMBean bomBean, List<String> varList, VariableBOMBean varBean, List<PicBean> picList) throws Exception {
		if (bomBean instanceof ProductLineBOMBean) {
			ProductLineBOMBean bean = (ProductLineBOMBean) bomBean;
			MatrixBOMExportBean e = new MatrixBOMExportBean();
			e.bomItem = bean.getSequence_no() == null ? "0" : String.valueOf(bean.getSequence_no());
			e.programName = bean.getProgramName();
			e.type = bean.getThermalType();
			e.description = bean.getEnglishDescription();
			e.vendor = bean.getManufacturerID();
			e.lenovoPN = bean.getCustomerPN();
			e.fruPN = bean.getFrupn();
			e.hhpn = bean.getItemId();
			e.vendorPN = bean.getManufacturerPN();
			e.coolerFanVendor = bean.getCoolerFanVendor();
			e.coolerFanModelNo = bean.getCoolerFanModelNo();
			List<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
			for (String var : varList) {
				int qty = findQty(var,bean.getLineId(),varBean);				
				list.add(new Pair<String, Integer>(var,qty));
				checkVariableType(e, var, bean.getLineId(), varBean);
			}
			e.variablesList = list;
			e.remark = bean.getRemark();
			return e;
		}
		return null;
	}

	private void checkVariableType(MatrixBOMExportBean e, String varName,String lineId, VariableBOMBean varBean) {
		List<VariableBOMBean> child = varBean.getChild();
		for(VariableBOMBean varBOM : child) {
			String itemName = varBOM.getItemId() + "/"+varBOM.getItemName();
			if(!varName.equals(itemName)) {
				continue;
			}
			List<VariableBOMBean> vaiableList = varBOM.getChild();
			for(VariableBOMBean vaiable : vaiableList) {
				if(lineId.equals(vaiable.getLineId())) {
					e.varType = varName.split("\\/")[1];
				}
			}
		}
	}
	
	public void sortBeanList(List<MatrixBOMExportBean> beanList) {
		beanList.removeIf(bean -> bean.varType == null);
		Map<String, List<MatrixBOMExportBean>> groupByMap = beanList.stream().collect(Collectors.groupingBy(bean -> bean.varType));
		groupByMap.forEach((key, value) -> {
			List<MatrixBOMExportBean> valueList = value;
			for (MatrixBOMExportBean bean : valueList) {
				List<Pair<String,Integer>> variablesList = bean.variablesList;
				ListIterator listIterator = variablesList.listIterator();
				while (listIterator.hasNext()) {
					Pair<String,Integer> pair = (Pair<String, Integer>) listIterator.next();
					if (!pair.getKey().contains(key)) {
						listIterator.remove();
					}
				}
			}
		});
	}
	
	@Override
	public boolean equals(TCComponentBOMLine bomLine1, TCComponentBOMLine bomLine2) throws TCException {
		String bomLine1Id = "";
		String bomLine2Id = "";
		bomLine1Id = bomLine1.getProperty("bl_item_item_id");
		bomLine2Id = bomLine2.getProperty("bl_item_item_id");
		
//		bomLine1Id += bomLine1.getProperty("bl_item_item_id");
//		bomLine1Id += bomLine1.getProperty("bl_occ_d9_Category");
//		bomLine1Id += bomLine1.getProperty("bl_occ_d9_Plant");
//		
//		bomLine2Id += bomLine2.getProperty("bl_item_item_id");
//		bomLine2Id += bomLine2.getProperty("bl_occ_d9_Category");
//		bomLine2Id += bomLine2.getProperty("bl_occ_d9_Plant");
		return StrUtil.equals(bomLine1Id, bomLine2Id);
	}
	
	public <T> T tcPropMapping(T bean, TCComponentBOMLine tcbomLine) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (bean != null && tcbomLine != null) {
			TCComponentItemRevision itemRev = tcbomLine.getItemRevision();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
				if (tcPropName != null) {
					String tcAttrName = tcPropName.value();
					if (!tcAttrName.isEmpty()) {
						Object value = "";
						if (tcAttrName.startsWith("bl")) {
							value = tcbomLine.getProperty(tcAttrName);
						} else {
							value = itemRev.getProperty(tcAttrName);
						}
						
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								value = Integer.parseInt((String) value);
							}
						}
						fields[i].set(bean, value);
					}
				}
			}
		}
		return bean;
	}

	@Override
	public void setHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) throws Exception {
		com.foxconn.tcutils.util.TCUtil.setBypass(session);
		String excelName = null;
		try {
			TCComponentItemRevision documentItemRev = MatrixBOMExportService.checkDocumentExist(matrixItemRevision);
			if (documentItemRev == null) {
				return;
			} else {
				String version = documentItemRev.getProperty("item_revision_id");
				if (!version.equals("01") && !version.equals("A")) {				
					return;
				}
			}
									
			String actualUser = documentItemRev.getProperty("d9_ActualUserID") == null ? "" : documentItemRev.getProperty("d9_ActualUserID").trim();
			excelName = excelPath.substring(excelPath.lastIndexOf(File.separator) + 1);
			String excelVersion = excelName.substring(excelName.lastIndexOf("_") + 1).replace(".xlsx", "").replace(".xls", "");
			ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(excelPath);
			writer.setSheet("History");
			String lastModifyDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
			Cell cell = writer.getCell(1, 2);
			if (cell != null) {
				String cellValue = cell.getStringCellValue();
				documentItemRev.setProperty("d9_ChangeDescription", cellValue);				
			}
//			String changeDesc = documentItemRev.getProperty("d9_ChangeDescription");
//			System.out.println("==>> changeDesc: " + changeDesc);
			writer.writeCellValue(0, 2, excelVersion);
//			writer.writeCellValue(1, 2, changeDesc);
			writer.writeCellValue(2, 2, actualUser);
			writer.writeCellValue(3, 2, lastModifyDate);
			List<String> sheetNameList = writer.getSheetNames();
			for (int j = 0; j < sheetNameList.size(); j++) {
				if ("sheet1".equalsIgnoreCase(sheetNameList.get(j))) {
					writer.getWorkbook().removeSheetAt(j);
				}
			}
			writer.close();			
			
			String dsName =  excelPath.substring(excelPath.lastIndexOf(File.separator) + 1);
			TCComponentDataset dataset = MatrixBOMExportService.checkDataset(documentItemRev, dsName);
			if (dataset != null) {
				com.foxconn.tcutils.util.TCUtil.updateDataset(session, documentItemRev, "IMAN_specification", excelPath);
			}
			com.foxconn.tcutils.util.TCUtil.closeBypass(session);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(AIFUtility.getActiveDesktop(), "请关闭打开的Excel文件：" + excelName, "提示", MessageBox.INFORMATION);
			throw new Exception(e);
		}		
	}
	
	
	public void generateExcel(ExcelWriter writer, String sheetName, List<MatrixBOMExportBean> beanList, ArrayList<String> varList, String objectName) {
		writer.setSheet(sheetName);
		
		CellStyle wrapTextStyle = writer.createCellStyle(); // 写头
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
		
		writer.merge(0, 0, 0, 10+varList.size()+1, objectName, false);
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
		MatrixBOMExportService.setValueAndStyle(writer,1, headerRowNum, "Program Name",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,2, headerRowNum, "类型",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,3, headerRowNum, "Description",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,4, headerRowNum, "Lenovo P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,5, headerRowNum, "FRU PN",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,6, headerRowNum, "HH P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,7, headerRowNum, "Vendor",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,8, headerRowNum, "Vendor P/N",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,9, headerRowNum, "Cooler Fan Vendor",headStyle);
		MatrixBOMExportService.setValueAndStyle(writer,10, headerRowNum, "Cooler Fan Model No.",headStyle);
		int i = 1;
		for (; i <= varList.size(); i++) {
			String key = varList.get(i-1);
			if(key.contains("/")) {
				key = key.substring(key.indexOf("/")+1, key.length());
			}
			headStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());
			MatrixBOMExportService.setValueAndStyle(writer,10+i, headerRowNum, key,headStyle);
		}
		
		// 设置列宽
		writer.setColumnWidth(+0, 10);
		writer.setColumnWidth(+1, 40);
		writer.setColumnWidth(+2, 20);
		writer.setColumnWidth(+3, 60);
		writer.setColumnWidth(+4, 40);
		writer.setColumnWidth(+5, 40);
		writer.setColumnWidth(+6, 40);
		writer.setColumnWidth(+7, 40);				
		writer.setColumnWidth(+8, 40);				
		writer.setColumnWidth(+9, 40);
		writer.setColumnWidth(+9, 40);
		writer.setColumnWidth(+10, 40);
		for (int k = 0; k < varList.size(); k++) {
			writer.setColumnWidth(+10+ (k + 1), 20);	
		}
		
		
		// 写行
		i = 0;
		int lastNo = 0;
		int row = 1;
		for(;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			if(!bean.isSubstitutes) {
				lastNo++;
				row++;				
				writer.writeCellValue(0,row,lastNo);
				writer.writeCellValue(1,row,bean.programName);
				writer.writeCellValue(2,row,bean.type);
				writer.writeCellValue(3,row,bean.description);
				writer.writeCellValue(4,row,bean.lenovoPN);
				writer.writeCellValue(5,row,bean.fruPN);
				writer.writeCellValue(6,row,bean.hhpn);
				writer.writeCellValue(7,row,bean.vendor);
				writer.writeCellValue(8,row,bean.vendorPN);
				writer.writeCellValue(9,row,bean.coolerFanVendor);
				writer.writeCellValue(10,row,bean.coolerFanModelNo);			
				
				int size = bean.variablesList.size();
				int j = 1;
				for(;j<=size;j++) {
					Integer value = bean.variablesList.get(j-1).getValue();
					if (value == 0) {
						writer.writeCellValue(10+j,row, "");
					} else {
						writer.writeCellValue(10+j,row, value);
					}
					
				}
				
				writer.setRowHeight(row, 20);
			}
		}
		
		row = 1;
		String findNum = "";
//		String value = "";
		int len = 0;
		i = 0; 
		for(;i<beanList.size();i++) {
			MatrixBOMExportBean bean = beanList.get(i);
			if(!bean.isSubstitutes) {
//				value = bean.type;
				if(bean.bomItem.equals(findNum)) {
					len++;
				}else {	
					if (len != 0) {
						writer.merge(row, row+len, 2, 2, null, false);	
//						row+=len;
//						row++;
					}
					row++;
					// 新的轮回
					row+=len;
					len=0;
					findNum = bean.bomItem;
				}
			}
		}
		if(len!=0) {
			writer.merge(row, row+len, 2, 2, null, false);
		}
		
		
	}

	public Map<String, List<MatrixBOMExportBean>> groupByType(List<MatrixBOMExportBean> beanList) {
		Map<String, List<MatrixBOMExportBean>> resultMap = new LinkedHashMap<String, List<MatrixBOMExportBean>>();
		beanList.forEach(bean -> {
			List<MatrixBOMExportBean> list = resultMap.get(bean.type);
			if (list == null) {
				list = new ArrayList<MatrixBOMExportBean>();
				list.add(bean);
				resultMap.put(bean.type, list);
			} else {
				list.add(bean);
			}
		});
		return resultMap;
	}
	
	
}
