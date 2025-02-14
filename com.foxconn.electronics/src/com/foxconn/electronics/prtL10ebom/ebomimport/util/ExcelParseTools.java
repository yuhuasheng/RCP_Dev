package com.foxconn.electronics.prtL10ebom.ebomimport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.foxconn.decompile.util.ExcelUtil;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.BOMBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.CellBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.MaterialBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.PartQtyBean;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;



public class ExcelParseTools {
	
	private static BOMBean rootBomBean = null;
	private static MaterialBean rootMaterialBean = null;
	
	private static final String Level = "Level";
	private static final String SKU = "SKU";
	private static final String FamilyPartNumber = "FamilyPartNumber";
	private static final String ParentNumber = "ParentNumber";
	private static final String PartNumber = "PartNumber";
	private static final String EnglishDescription = "EnglishDescription";
	private static final String ChineseDescription = "ChineseDescription";
	private static final String UM = "UM";
	private static final String Subsystem = "Subsystem";
	private static final String CommodityType = "CommodityType";
	private static final String Material = "Material";
	private static final String SourcingType = "SourcingType";
	private static final String Draw2DRev = "2DRev";
	private static final String Draw3DRev = "3DRev";
	private static final String Remark = "Remark";
	private static final String ObjectType = "ObjectType";
	private static final String D9_SourcingType_LOV = "D9_SourcingType_LOV";
	private Object[] SOURCINGTYPELOV = null;
	
	private List<String> columnList = new ArrayList<String>() {{
		add(Level);
		add(SKU);
		add(FamilyPartNumber);
		add(ParentNumber);
		add(PartNumber);
		add(EnglishDescription);
		add(ChineseDescription);
		add(UM);
		add(Subsystem);
		add(CommodityType);
		add(Material);		
		add(Draw2DRev);
		add(Draw3DRev);
		add(SourcingType);
		add(Remark);		
		add(ObjectType);
	}};
	
	private static final int startRowIndex = 6;
	
	private static final int rootLevel = 0;
	
	private String partPrefixx = "";
	
	
	
	public ExcelParseTools() {
		super();
		SOURCINGTYPELOV = TCUtil.getLovValues(D9_SourcingType_LOV);
	}

	public static void main(String[] args) {
		String floatValue = NumberUtil.mul("0.1000", "0.1000").stripTrailingZeros().toPlainString();
		System.out.println(CommonTools.getDecimalPlaces(floatValue));
		System.out.println(new BigDecimal(floatValue).stripTrailingZeros().toPlainString());
		System.out.println(NumberUtil.roundStr(floatValue, 6));
		BigDecimal bigDecimal = new BigDecimal("0.1000");
		String plainString = bigDecimal.stripTrailingZeros().toPlainString();
		System.out.println(plainString);	
		
		System.out.println(CommonTools.isNumeric("1.0"));
		System.out.println(CommonTools.isNumeric("1"));
		System.out.println(CommonTools.isNumeric("0.250"));
		System.out.println(CommonTools.isNumeric("1.2"));
		System.out.println(CommonTools.isNumeric("11"));
		System.out.println(CommonTools.isNumeric("1.2/3"));
		if (1 == 1) {
			return;
		}
		
		ExcelParseTools parseTools= new ExcelParseTools();
		String fileName = "C:\\Users\\MW00333\\Desktop\\PRT L10 EBOM 导入\\Deli Atlas FPU BOM DVT 2.1 -Rev4 (version 2) (1).xlsx";
		String logTxt = "C:\\Users\\MW00333\\Desktop\\Log.txt";
		long timeStart = System.currentTimeMillis();
		FileInputStream fis = null;
		Workbook workbook = null;
		try {			
			
			fis = new FileInputStream(fileName);
			workbook = WorkbookFactory.create(fis);
				
			Sheet sheet = workbook.getSheet("BOM");
				
			List<CellBean> colIndexList = parseTools.getColIndex(sheet, null);
			
			List<String> skuList = parseTools.getSKUList(sheet, colIndexList);
			
			List<CellBean> qtyIndexList = parseTools.getColIndex(sheet, skuList);
			
			Map<String, List<PartQtyBean>> retMap = new LinkedHashMap<String, List<PartQtyBean>>();
			List<String> errorList = new ArrayList<String>();
			
			parseTools.getPartBeanList(workbook, sheet, colIndexList, qtyIndexList, retMap, errorList);
			
			parseTools.parseExcel(workbook, sheet, colIndexList, qtyIndexList, retMap, null, null);
			
//			String txtFile = parseTools.generateTxtFile("C:\\Users\\MW00333\\Desktop", "Log");
			
			
//			System.out.println(rootBomBean);
//			String result = JSONUtil.toJsonPrettyStr(rootBomBean);
			
//			parseTools.write(txtFile, result);
			
//			System.out.println(result);
			long timeEnd = System.currentTimeMillis();
			System.out.println("总共花费：" + (timeEnd - timeStart) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (CommonTools.isNotEmpty(fis)) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
			if (workbook != null) {				
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	public static void parseExcel(List<List<Object>> list) {
		BOMBean rootBomBean = null;
		MaterialBean rootMaterialBean = null;
		for (int i = 0; i < list.size(); i++) {
			List<Object> rowList = list.get(i);
			Integer level = rowList.get(4) == null ? null : (Integer)rowList.get(4);
			if (CommonTools.isEmpty(level)) {
				continue;
			}
			
			if (level == 0) {
				rootBomBean = new BOMBean();
				rootBomBean.setLevel(level);
				
				rootMaterialBean = new MaterialBean();
				rootMaterialBean.setPartNumber(rowList.get(8) == null ? "" : (String)rowList.get(8));				
				rootMaterialBean.setEnDesc(rowList.get(9) == null ? "" : (String)rowList.get(9));
				rootMaterialBean.setChDesc(rowList.get(10) == null ? "" : (String)rowList.get(10));
				
				rootBomBean.setSelfMaterialBean(rootMaterialBean);
			}
			
			System.out.println(123);
		}
	}
	
	
	
 	public static void parseExcelNew(Sheet sheet, ExcelUtil excelUtil) {
		int count = sheet.getLastRowNum(); // 获取最后一行
		for (int i = 6; i <= count; i++) {
			Row row = sheet.getRow(i);
			if (CommonTools.isEmpty(row)) {
				continue;
			}
			if (CommonTools.isEmpty(row.getCell(3))) {
				continue;
			}
			
			short lastCellNum = row.getLastCellNum();
			for (short j = 0; j < lastCellNum; j++) {
				
			}
			Cell levelCell = row.getCell(4);
			String levelValue = excelUtil.getCellValue(levelCell);
			if (CommonTools.isEmpty(levelValue)) {
				continue;
			}
			
			BigDecimal bigDecimal = new BigDecimal(levelValue);
//			String plainString = bigDecimal.stripTrailingZeros().toPlainString();
//			System.out.println("==>> plainString: " + plainString);
			int levelIndex = Integer.valueOf(bigDecimal.stripTrailingZeros().toPlainString());
			System.out.println("==>> levelIndex: " + levelIndex);
			
			if (levelIndex == 0) {
				rootBomBean = new BOMBean();
				rootBomBean.setLevel(levelIndex);
				
				rootMaterialBean = new MaterialBean();		
//				rootMaterialBean.setParentNumber(excelUtil.getCellValue(row.getCell(7)));				
				rootMaterialBean.setPartNumber(excelUtil.getCellValue(row.getCell(8)));				
				rootMaterialBean.setEnDesc(excelUtil.getCellValue(row.getCell(9)));
				rootMaterialBean.setChDesc(excelUtil.getCellValue(row.getCell(10)));
				
				rootBomBean.setSelfMaterialBean(rootMaterialBean);
			} else {
				if (levelIndex == 1) {
					System.out.println(123);
				}
				BOMBean bomBean = new BOMBean();
				bomBean.setLevel(levelIndex);
				
				MaterialBean matBean = new MaterialBean();
				matBean.setParentNumber(excelUtil.getCellValue(row.getCell(7)));
				matBean.setPartNumber(excelUtil.getCellValue(row.getCell(8)));				
				matBean.setEnDesc(excelUtil.getCellValue(row.getCell(9)));
				matBean.setChDesc(excelUtil.getCellValue(row.getCell(10)));
				
				bomBean.setSelfMaterialBean(matBean);
				
				recurse(rootBomBean, bomBean);
			}
		}
	}
 	
 	public void parseExcel(Workbook workbook, Sheet sheet, List<CellBean> colIndexList, List<CellBean> qtyIndexList, Map<String, List<PartQtyBean>> retMap, List<BOMBean> totalList, List<String> errorList) {
 		int count = sheet.getLastRowNum(); // 获取最后一行
 		for (int index = startRowIndex; index <= count; index++) {
 			Row row = sheet.getRow(index);
			if (CommonTools.isEmpty(row)) {
				continue;
			}
			
			String levelValue = getValue(row, colIndexList, Level, null);			
			if (CommonTools.isEmpty(levelValue)) {
				continue;
			}
			
			int levelIndex = Integer.valueOf(new BigDecimal(levelValue).stripTrailingZeros().toPlainString());
			System.out.println("==>> levelIndex: " + levelIndex);			
			
			
			
			if (checkDeleteLine(workbook, row, colIndexList, SKU, null)) { // 判断是否含有删除线
				continue;
			}
			
			
			String partNum = getValue(row, colIndexList, PartNumber, null);
			if (CommonTools.isEmpty(partNum)) {
				continue;
			}
			
			String[] split = partNum.split("-");
	 		if (split[0].length() != 5 || split[1].length() != 5) {
	 			continue;
			}	 		
	 		
			MaterialBean matBean = createMatBean(row, colIndexList);
			matBean.setIndex((index + 1));
			
			String sku = matBean.getSku();
			System.out.println("==>> sku: " + sku);
			
			String totalParentNum = matBean.getParentNumber(); 
			
//			String[] skus = sku.split("/");
			String[] parentNums = totalParentNum.split("/");
//			if (skus.length != parentNums.length) {
//				continue;
//			}
			
			if (levelIndex == 2 && totalParentNum.equals("FD220-64001/64005/64006/64007")) {
				System.out.println(123);
			}
			
			if (levelIndex == rootLevel) {
				BOMBean rootBomBean = new BOMBean();
				rootBomBean.setLevel(levelIndex);
				
				
//				rootBomBean.setQty(getValue(row, qtyIndexList, null, sku));
				
				MaterialBean rootMatBean = (MaterialBean) matBean.clone();
				rootMatBean.setParentNumber("");
				rootMatBean.setSku(sku);
				
				rootBomBean.setQty(getQtyByPartNumAndSku(retMap, null, rootMatBean.getPartNumber(), sku, null, index));				
				rootBomBean.setSelfMaterialBean(rootMatBean);
				
				partPrefixx =  rootMatBean.getPartNumber().substring(0, rootMatBean.getPartNumber().lastIndexOf("-") + 1).trim();
				
				totalList.add(rootBomBean);
			} else {				
				for (int i = 0; i < parentNums.length; i++) {
					BOMBean childBomBean = new BOMBean();	
					childBomBean.setLevel(levelIndex);
					
					MaterialBean childMatBean = new MaterialBean();
					childMatBean = (MaterialBean) matBean.clone();
					String str = parentNums[i].replace(partPrefixx, "");
					StringBuilder sb = new StringBuilder(str);
					sb.insert(0, partPrefixx);
					
					if (CommonTools.isEmpty(retMap.get(sb.toString()))) {
						errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 上阶料号:" + parentNums[i] + ", 填写不正确, 料号那一列没有对应的记录"));
						continue;
					};
					
					childMatBean.setParentNumber(sb.toString());	
					childMatBean.setSku(sku);
					
					childBomBean.setQty(getQtyByPartNumAndSku(retMap, childMatBean.getParentNumber(), childMatBean.getPartNumber(), sku, totalParentNum, index));
					childBomBean.setSelfMaterialBean(childMatBean);					
					
					totalList.add(childBomBean);
				}
			}			
 		}
 	}	
 
 	
 	
 	private void recurse(List<BOMBean> rootList, BOMBean bean) {
 		rootList.stream().parallel().forEach(parentBean -> {
 			boolean recurseFlag = true;
 			List<BOMBean> childList = parentBean.getChildList();
 			if ((parentBean.getLevel().intValue() + 1) == bean.getLevel().intValue() 
					&& parentBean.getSelfMaterialBean().getPartNumber().equals(bean.getSelfMaterialBean().getParentNumber())) {				
				childList.add(bean);
				recurseFlag = false;
 			}
 			
 			if (recurseFlag) {
 				recurse(childList, bean);
			}
 			
 		});
 		
 		
// 		for (BOMBean parentBean : rootList) {
// 			List<BOMBean> childList = parentBean.getChildList();
// 			if ((parentBean.getLevel().intValue() + 1) == bean.getLevel().intValue() 
//						&& parentBean.getSelfMaterialBean().getPartNumber().equals(bean.getSelfMaterialBean().getParentNumber())) {				
//					childList.add(bean);
//					return;
//			}  
// //			else {
//				recurse(childList, bean);				
////			}
// 			
//		}
 	}
 	
  	private static void recurse(BOMBean parentBean, BOMBean bean) {
 		List<BOMBean> childList = parentBean.getChildList();
 		if (bean.getLevel().intValue() == 1) {
			childList.add(bean);
			return;
		}
 		if (CommonTools.isEmpty(childList)) {
			if ((parentBean.getLevel().intValue() + 1) == bean.getLevel().intValue() 
					&& parentBean.getSelfMaterialBean().getPartNumber().equals(bean.getSelfMaterialBean().getParentNumber())) {				
				childList.add(bean);
			}
		} else {
			BOMBean lastBean = getLastBean(childList, bean);
			if (lastBean != null) {
				lastBean.getChildList().add(bean);
				return;
			}
			
			for (BOMBean childBean : childList) {
				recurse(childBean, bean);
			}
		}
 		
 		
 	}
 	
 	/**
 	 * 通过料号，SKU获取用量
 	 * @param retMap
 	 * @param parentNum
 	 * @param partNum
 	 * @param sku
 	 * @return
 	 */
  	private String getQtyByPartNumAndSku(Map<String, List<PartQtyBean>> retMap, String parentNum, String partNum, String sku, String totalParentNum, int index) {
  		List<PartQtyBean> list = new ArrayList<PartQtyBean>();
  		if (CommonTools.isEmpty(parentNum)) {
  			list = retMap.get(partNum);
  			if (CommonTools.isEmpty(list)) {
				return null;
			}
  			Optional<PartQtyBean> findAny = list.stream().filter(bean -> bean.getSku().equalsIgnoreCase(sku)).findAny();
  			if (findAny.isPresent()) {
				return findAny.get().getQty();
			}
		} else {
			list = retMap.get(parentNum);
			if (CommonTools.isEmpty(list)) {
				return null;
			}
			String[] skus = sku.split("/");
			
			for (int i = 0; i < skus.length; i++) {
				String str = skus[i];
				try {
					boolean anyMatch = list.stream().anyMatch(bean -> bean.getSku().equalsIgnoreCase(str));
					if (anyMatch) {
						List<PartQtyBean> curList = retMap.get(partNum);
						Optional<PartQtyBean> curFindAny = curList.stream().filter(curBean -> curBean.getParentNum().equals(totalParentNum) && curBean.getSku().equalsIgnoreCase(str) && curBean.getIndex() == index).findAny();
						if (curFindAny.isPresent()) {
							return curFindAny.get().getQty();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
  		
  		return null;
  	}
  	
  	
  	private void addChildBean(List<BOMBean> list, BOMBean currentBean) {
  		for (BOMBean bean : list) {
			if ((bean.getLevel().intValue() + 1) == currentBean.getLevel().intValue() &&
					bean.getSelfMaterialBean().getPartNumber().equals(currentBean.getSelfMaterialBean().getParentNumber())) {
				bean.getChildList().add(bean);
			}
		}
  	}
  	
  	
 	private static BOMBean getLastBean(List<BOMBean> list, BOMBean currentBean) {
 		BOMBean resultBean = null;
 		Optional<BOMBean> findAny = list.stream().filter(bean -> (bean.getLevel().intValue() + 1) == currentBean.getLevel().intValue() && 
 				bean.getSelfMaterialBean().getPartNumber().equals(currentBean.getSelfMaterialBean().getParentNumber())).findAny();
 		if (findAny.isPresent()) {
 			resultBean = findAny.get();
		}
 		
 		
// 		for (int i = list.size() - 1; i >= 0; i--) {
//			BOMBean bean = list.get(i);
//			if ((bean.getLevel().intValue() + 1) == level) {
//				resultBean = bean;
//				break;
//			}
//		}
 		
 		return resultBean;
 	}
 	
 	/**
 	 * 获取值
 	 * @param row
 	 * @param list
 	 * @param columnName
 	 * @return
 	 */
 	private String getValue(Row row, List<CellBean> list, String columnName, String sku) {
 		Optional<CellBean> findAny = null;
 		if (CommonTools.isEmpty(sku)) {
 			findAny = list.stream().filter(bean -> columnName.equalsIgnoreCase(bean.getColumnName().replace(" ", ""))).findAny();
		} else {
			findAny = list.stream().filter(bean -> bean.getColumnName().startsWith(sku)).findAny();
		}
 		
 		if (!findAny.isPresent()) {
			return null;
		}
 		
 		Cell cell = row.getCell(findAny.get().getIndex()); 		
 		return removeBlank(getCellValue(cell));
 	}
 	
 	
 	/**
	 * 生成TXT文件
	 * 
	 * @param filePath 文件夹路径
	 * @param fileName 文件名
	 * @return
	 * @throws IOException
	 */

	public String generateTxtFile(String dir, String fileName) {
		try {
			String txtFilePath = dir + "\\" + fileName + ".txt";
			File file = new File(txtFilePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将内容写入到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
	public void write(String fileName, String content) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8");
			osw.write(content + "\r\n");
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 获取所有的sheet名称
	 * @param workbook
	 * @return
	 */
	public List<String> getSheetNameList(Workbook workbook) {
		List<String> list = new ArrayList<String>();
		int number = workbook.getNumberOfSheets();
		for (int i = 0; i < number; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			list.add(sheet.getSheetName());
		}
		
		return list;
	}
	
	
	/**
	 * 获取SKU清单
	 * @param sheet
	 * @param colIndexList
	 * @return
	 */
	public List<String> getSKUList(Sheet sheet, List<CellBean> colIndexList) {
		List<String> skuList = new ArrayList<String>();
		int count = sheet.getLastRowNum(); // 获取最后一行
		for (int index = startRowIndex; index <= count; index++) {
			Row row = sheet.getRow(index);
			if (CommonTools.isEmpty(row)) {
				continue;
			}
			
			String levelValue = getValue(row, colIndexList, Level, null);
			if (CommonTools.isEmpty(levelValue)) {
				continue;
			}		
			
			int levelIndex = Integer.valueOf(new BigDecimal(levelValue).stripTrailingZeros().toPlainString());
			System.out.println("==>> levelIndex: " + levelIndex);
			
			if (levelIndex != rootLevel) {
				break;
			} 
			
			String skuValue = getValue(row, colIndexList, SKU, null);			
			if (CommonTools.isNotEmpty(skuValue)) {
				skuList.add(skuValue);
			}
		}
		
		return skuList.stream().filter(CommonTools.distinctByKey(str -> str)).collect(Collectors.toList()); // 移除重复项
	}
	
	
	/**
	 * 获取顶阶料号
	 * @param workbook
	 * @param sheet
	 * @param colIndexList
	 * @return
	 */
	public String getRootPartNum(Workbook workbook, Sheet sheet, List<CellBean> colIndexList) {
		String partNum = null;
		int count = sheet.getLastRowNum(); // 获取最后一行
		for (int index = startRowIndex; index <= count; index++) {
			Row row = sheet.getRow(index);
			if (CommonTools.isEmpty(row)) {
				continue;
			}
			
			if (checkDeleteLine(workbook, row, colIndexList, SKU, null)) { // 判断是否含有删除线
				continue;
			}
			
			partNum = getValue(row, colIndexList, PartNumber, null);
			
			if (CommonTools.isEmpty(partNum)) {
				continue;
			}
			
			break;
		}
		
		return partNum;
	}
	
	public void getPartBeanList(Workbook workbook, Sheet sheet, List<CellBean> colIndexList, List<CellBean> qtyIndexList, Map<String, List<PartQtyBean>> retMap, List<String> errorList) {
		int count = sheet.getLastRowNum(); // 获取最后一行
		for (int index = startRowIndex; index <= count; index++)  {			
			Row row = sheet.getRow(index);
			if (CommonTools.isEmpty(row)) {
				continue;
			}
			
			String levelValue = getValue(row, colIndexList, Level, null);
			if (CommonTools.isEmpty(levelValue)) {
				continue;
			}
			
			int levelIndex = Integer.valueOf(new BigDecimal(levelValue).stripTrailingZeros().toPlainString());
			System.out.println(levelIndex);		
			
			String skuValue = getValue(row, colIndexList, SKU, null);
			System.out.println("==>> skuValue: " + skuValue);			
			
			
			if (CommonTools.isEmpty(skuValue)) {
				errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, SKU为空"));
				continue;
			}
			if (checkDeleteLine(workbook, row, colIndexList, SKU, null)) { // 判断是否含有删除线
				continue;
			}
			
			String partNum = getValue(row, colIndexList, PartNumber, null);
			if (CommonTools.isEmpty(partNum)) {
				errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 料号为空"));
				continue;
			}
			
			String[] split = partNum.split("-");
	 		if (split[0].length() != 5 || split[1].length() != 5) {
	 			errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 料号编码不正确"));
	 			continue;
			}
	 		
	 		
			String parentNum = getValue(row, colIndexList, ParentNumber, null);			
			System.out.println("==>> parentNum:" + parentNum);
			
			if (levelIndex != 0 && (CommonTools.isEmpty(parentNum) || CommonTools.isEmpty(partNum))) {
				errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 上阶料号/料号为空,请重新填写"));
				continue;
			}			
			
			String sourcingType = getValue(row, colIndexList, SourcingType, null);
			if (!checkSourcingType(sourcingType)) {
				errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 采购类别归属填写不正确或未填写"));
				continue;	
			}
			
			List<PartQtyBean> totalList = retMap.get(partNum);
			if (CommonTools.isEmpty(totalList)) {
				totalList = new ArrayList<PartQtyBean>();
			}
			List<PartQtyBean> list = new ArrayList<PartQtyBean>();
			String[] skus = skuValue.split("/");
			for (String sku : skus) {
				String qty = getValue(row, qtyIndexList, null, sku);
				if (CommonTools.isEmpty(qty)) {
					errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 以" + sku +"开头用量填写不正确或未填写"));
					continue;
				}
				
				if (!CommonTools.isNumeric(qty)) {
					errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + " 行, 以" + sku +"开头用量填写不全为数字,请重新填写"));
					continue;
				}
				PartQtyBean bean = new PartQtyBean();	
				bean.setIndex(index);
				bean.setParentNum(parentNum);
				bean.setPartNum(partNum);				
				bean.setSku(sku);
				
				bean.setQty(new BigDecimal(qty).stripTrailingZeros().toPlainString());
				
				
				list.add(bean);
			}
			
			if (!checkSameQty(list)) {
				errorList.add(S2TTransferUtil.toTraditionnalString("第 " + (index + 1) + ", 用量填写不一致"));
			} else {	
				totalList.addAll(list);
				retMap.put(partNum, totalList);
			}			
		}		
	}
	
	
	
	/**
	 * 判断数量是否一致
	 * @param list
	 * @return
	 */
	public boolean checkSameQty(List<PartQtyBean> list) {
		list = list.stream().filter(CommonTools.distinctByKey(bean -> bean.getQty())).collect(Collectors.toList());
		if (list.size() > 1) {
			return false;
		}
		return true;
	}
	
	/**
	 * 校验Sourcing Type
	 * @param sourcingType
	 * @return
	 */
	public boolean checkSourcingType(String sourcingType) {
		return Stream.of(SOURCINGTYPELOV).anyMatch(obj -> ((String)obj).equals(sourcingType));
	}
	
	
	/**
	 * 校验当前选中的sheet模板
	 * @param sheet
	 * @return
	 */
	public boolean checkSheetTemplate(Sheet sheet) {
		int matchColumnCount = 0;
		Row row = sheet.getRow(startRowIndex - 1);
		short lastCellNum = row.getLastCellNum(); 
		for (short i = 0; i <= lastCellNum; i++) {
			Cell cell = row.getCell(i);
			String value = removeBlank(getCellValue(cell));
			if (CommonTools.isEmpty(value)) {
				continue;
			}
			
			boolean anyMatch = columnList.stream().anyMatch(str -> str.equalsIgnoreCase(value.replace(" ", "")));
			if (anyMatch) {
				matchColumnCount++;
			}
		}
		
		return matchColumnCount == columnList.size() ? true : false;
		
	}
	
	
	/**
	 * 获取列的索引
	 * @param sheet
	 * @param skuList
	 * @return
	 */
	public List<CellBean> getColIndex(Sheet sheet, List<String> skuList) {
		List<CellBean> colIndexList = new ArrayList<CellBean>();
		Row row = null;
		if (CommonTools.isEmpty(skuList)) {
			row = sheet.getRow(startRowIndex - 1);
		} else {
			row = sheet.getRow(startRowIndex - 2);
		}
		short lastCellNum = row.getLastCellNum();
		for (short i = 0; i <= lastCellNum; i++) {
			Cell cell = row.getCell(i);
			String value = removeBlank(getCellValue(cell));
			if (CommonTools.isEmpty(value)) {
				continue;
			}
			boolean anyMatch = false;
			if (CommonTools.isEmpty(skuList)) {
				anyMatch = columnList.stream().anyMatch(str -> str.equalsIgnoreCase(value.replace(" ", "")));
			} else {
				anyMatch = skuList.stream().anyMatch(str -> value.contains(str));
			}
			if (anyMatch) {
				CellBean bean = new CellBean();
				bean.setColumnName(value.replace("\n", " "));				
				bean.setIndex(i);
				colIndexList.add(bean);
			}
		}
		
		return colIndexList.stream().filter(CommonTools.distinctByKey(bean -> bean.getColumnName())).collect(Collectors.toList()); // 移除列名重复项
	}
	
	
	private MaterialBean createMatBean(Row row, List<CellBean> colIndexList) {
		MaterialBean matBean = new MaterialBean();
		matBean.setSku(getValue(row, colIndexList, SKU, null));
		matBean.setFamilyPartNumber(getValue(row, colIndexList, FamilyPartNumber, null));			
		matBean.setParentNumber(getValue(row, colIndexList, ParentNumber, null));
		matBean.setPartNumber(getValue(row, colIndexList, PartNumber, null));
		matBean.setObjectName(getValue(row, colIndexList, EnglishDescription, null));
		matBean.setEnDesc(getValue(row, colIndexList, EnglishDescription, null));
		matBean.setChDesc(getValue(row, colIndexList, ChineseDescription, null));
		String um = getValue(row, colIndexList, UM, null);
		if (CommonTools.isNotEmpty(um)) {
			matBean.setUnit(um);
		}
		
		matBean.setSubSystem(getValue(row, colIndexList, Subsystem, null));
		matBean.setCommodityType(getValue(row, colIndexList, CommodityType, null));
		matBean.setMaterial(getValue(row, colIndexList, Material, null));			
		matBean.setDraw2DRev(getValue(row, colIndexList, Draw2DRev, null));
		matBean.setDraw3DRev(getValue(row, colIndexList, Draw3DRev, null));
		matBean.setSourcingType(getValue(row, colIndexList, SourcingType, null));
		matBean.setRemark(getValue(row, colIndexList, Remark, null));
		matBean.setObjectType(getValue(row, colIndexList, ObjectType, null));
		return matBean;
	}
	
	
	/**
	 * 获取单元格内容
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
				value = cell.getNumericCellValue() + "";
				break;
			case "BOOLEAN":
				value = String.valueOf(cell.getBooleanCellValue());
				break;
			case "BLANK":
				value = "";
				break;
			case "ERROR":
				value = cell.toString();
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
	
	/**
	 * 判断是否含有删除线
	 * @param book
	 * @param cell
	 * @param list
	 * @param columnName
	 * @param sku
	 * @return
	 */
	public boolean checkDeleteLine(Workbook workbook, Row row, List<CellBean> list, String columnName, String sku) {
		Optional<CellBean> findAny = null;
 		if (CommonTools.isEmpty(sku)) {
 			findAny = list.stream().filter(bean -> columnName.equalsIgnoreCase(bean.getColumnName().replace(" ", ""))).findAny();
		} else {
			findAny = list.stream().filter(bean -> bean.getColumnName().startsWith(sku)).findAny();
		}
 		
 		Cell cell = row.getCell(findAny.get().getIndex());
		return workbook.getFontAt(cell.getCellStyle().getFontIndex()).getStrikeout(); // 判断单元格的font是否含有删除线
	}
	
	
	/**
	* 去掉字符串中的前后空格、回车、换行符、制表符 value
	* @return
	*/

	public static String removeBlank(String value) {
	    String result = "";
	    if (value != null)
	    {
	        Pattern p = Pattern.compile("|\t|\r|\n");
	        Matcher m = p.matcher(value);
	        result = m.replaceAll("");
	        result = result.trim();
	        result = result.replace("\n", "").replace("\r", "").replace("\t", "");
	    }
	    else
	    {
	        result = value;
	    }
	    
	    return result;
	    
	}
}
