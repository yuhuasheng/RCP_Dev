package com.foxconn.electronics.managementebom.Import.bom.mnt.excel;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.MntEBOMSheetConstant;
import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.UnitEnum;
import com.foxconn.electronics.managementebom.Import.bom.mnt.dialog.MntEBOMImportDialog;
import com.foxconn.electronics.managementebom.Import.bom.mnt.domain.MntEBOMInfo;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.TCUtil;

public class ExcelEBOMAnalyseTools {

	private int startIndex = 0; // 开始索引设置为0
//	private Text logText = null;
	private StyledText styledText = null;
	private MntEBOMImportDialog dialog = null;
	
	public ExcelEBOMAnalyseTools(StyledText styledText, MntEBOMImportDialog dialog) {
		this.styledText = styledText;
		this.dialog = dialog;
	}

	public static void main(String[] args) {
		String excelFilePath = "C:\\Users\\HuashengYu\\Desktop\\EBOM.xlsx";
		List<MntEBOMInfo> list = new ExcelEBOMAnalyseTools(null, null).analyseEBOMExcel(excelFilePath);
		System.out.println(list);		
	}

	public List<MntEBOMInfo> analyseEBOMExcel(String excelFilePath) {
		List<MntEBOMInfo> sheetDataList = new ArrayList<MntEBOMInfo>();
		try {
			dialog.writeInfoLogText("************ 開始解析EBOM文件 ************");
			long timeStart = System.currentTimeMillis();
			FileInputStream fis = new FileInputStream(excelFilePath);
			Workbook workbook = WorkbookFactory.create(fis);
			int sheetNumber = workbook.getNumberOfSheets();
			System.out.println("==>> sheet页数量一共为: " + sheetNumber);
			dialog.writeInfoLogText("文件名為: " + excelFilePath.substring(excelFilePath.lastIndexOf("\\") + 1) + ", 一共有 "+ sheetNumber + " 個sheet頁");
			for (int i = 0; i < sheetNumber; i++) {
				startIndex = 0; // 开始索引设置为0
				dialog.writeInfoLogText("######## 開始解析第 " + (i + 1) + " 個sheet頁" + " ########");
				Sheet sheet = workbook.getSheetAt(i);
				int lastRowNum = sheet.getLastRowNum();
				dialog.writeInfoLogText("###### 開始校驗文件: " + excelFilePath + ", 是否符合本次導入 ######");
				if (!checkExcelTemplate(sheet, lastRowNum)) { //  校验Excel导入模板
					dialog.writeErrorLogText("###### 文件: " + excelFilePath + ", 校驗不通過，無法進行下一步操作 ######");
					break;
				}
				dialog.writeInfoLogText("###### 文件: " + excelFilePath + ", 檢驗通過, 符合本次導入 ######");
				dialog.writeInfoLogText("### 第 " + (i + 1) + "  個sheet頁總共有" + lastRowNum + " 行 ###");
				while (true) {
					startIndex = getStartIndex(sheet, startIndex, lastRowNum);
					MntEBOMInfo bean = getBomStruct(lastRowNum, sheet);
					sheetDataList.add(bean);
					if (startIndex >= lastRowNum) { 
						break;
					}
				}
				dialog.writeInfoLogText("######## 第 " + (i + 1) + " 個sheet頁解析完成" + " ########");
			}
			dialog.writeInfoLogText("************ 解析EBOM文件完成 ************");
			long timeEnd = System.currentTimeMillis();
			dialog.writeInfoLogText("总共花费: " + CommonTools.getCostTime((timeEnd - timeStart)));
		} catch (Exception e) {
			e.printStackTrace();			
			dialog.writeErrorLogText("文件為 " + excelFilePath + " 解析EBOM文件發生錯誤");
			dialog.writeErrorLogText(TCUtil.getExceptionMsg(e));
			throw new RuntimeException(e);
		}
		return sheetDataList;
	}

	/**
	 * 返回遍历开始的索引
	 * 
	 * @param sheet
	 * @return
	 */
	private int getStartIndex(Sheet sheet, int j, int lastRowNum) {
		int currentRowNum = 0;
		for (int k = j; k <= lastRowNum; k++) {
			if (CommonTools.isEmpty(sheet.getRow(k))) {
				continue;
			}

			if (CommonTools.isEmpty(sheet.getRow(k).getCell(0))) {
				continue;
			}
			Cell cell = sheet.getRow(k).getCell(0);
			if (MntEBOMSheetConstant.ITEM.equals(removeBlank(getCellValue(cell)))) {
				currentRowNum = k;
				break;
			}
		}		
		return ++currentRowNum;
	}
	
	
	/**
	 * 校验Excel导入模板
	 * @param sheet
	 * @param lastRowNum
	 * @return
	 */
	private boolean checkExcelTemplate(Sheet sheet, int lastRowNum) {
		boolean flag = false;
		for (int i = 0; i <= lastRowNum; i++) {
			if (CommonTools.isEmpty(sheet.getRow(i))) {
				continue;
			}
			
			if (CommonTools.isEmpty(sheet.getRow(i).getCell(0))) {
				continue;
			}
			
			Cell cell = sheet.getRow(i).getCell(0);
			if (MntEBOMSheetConstant.ITEM.equals(removeBlank(getCellValue(cell)))) {
				cell = sheet.getRow(i).getCell(1);
				if (MntEBOMSheetConstant.PHANT_ITEM.equals(getCellValue(cell))) {
					flag = true;
					break;
				}
			}
		}		
		return flag;
	}
	
	
	
	/**
	 * 获取含有BOM层级的对象
	 * 
	 * @param startIndex
	 * @param lastRowNum
	 * @param sheet
	 * @return
	 */
	private MntEBOMInfo getBomStruct(int lastRowNum, Sheet sheet) {
		MntEBOMInfo rootBean = null;
		MntEBOMInfo childBean = null;
		List<MntEBOMInfo> childList = new ArrayList<MntEBOMInfo>();
		for (; startIndex <= lastRowNum; startIndex++) {
			try {
				Row row = sheet.getRow(startIndex);
				if (CommonTools.isEmpty(row)) {
					break;
				}
				if (CommonTools.isEmpty(sheet.getRow(startIndex).getCell(0))
						&& CommonTools.isEmpty(sheet.getRow(startIndex).getCell(1))
						&& CommonTools.isEmpty(sheet.getRow(startIndex).getCell(3))) {
					break;
				}
				if (CommonTools.isEmpty(removeBlank(getCellValue(sheet.getRow(startIndex).getCell(0))))
						&& CommonTools.isEmpty(removeBlank(getCellValue(sheet.getRow(startIndex).getCell(1))))
						&& CommonTools.isEmpty(removeBlank(getCellValue(sheet.getRow(startIndex).getCell(2))))) { // 假如第一个单元格，第二个单元格，第三个单元格都为空，则代表单个BOM已经遍历到最后
					break;
				}

				if (CommonTools.isEmpty(removeBlank(getCellValue(sheet.getRow(startIndex).getCell(0))))) {
					rootBean = getBeanInfo(row);
					dialog.writeInfoLogText("第 " + (startIndex + 1) + " 行 " + "P/N為: " + rootBean.getItemId() + ", Description為: "
							+ rootBean.getDescription() + ", 解析完成");
				} else {
					childBean = getBeanInfo(row);
					dialog.writeInfoLogText("第 " + (startIndex + 1) + " 行 " + "P/N為: " + childBean.getItemId() + ", Description為: "
							+ childBean.getDescription() + ", 解析完成");
				}
				if (CommonTools.isNotEmpty(childBean)) {
					childList.add(childBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
				dialog.writeErrorLogText("第 " + (startIndex + 1) + " 行解析失敗");
				dialog.writeErrorLogText(TCUtil.getExceptionMsg(e));
				throw new RuntimeException(e);
//				continue;
			}
		}
		if (CommonTools.isNotEmpty(childList)) {
			Map<Integer, List<MntEBOMInfo>> map = groupByFindNum(childList);
			List<MntEBOMInfo> childs = addSub(map);
			rootBean.setChilds(childs);
		}
		return rootBean;
	}

	/**
	 * 添加替代料
	 * 
	 * @param map
	 * @return
	 */
	private List<MntEBOMInfo> addSub(Map<Integer, List<MntEBOMInfo>> map) {
		List<MntEBOMInfo> childs = new ArrayList<MntEBOMInfo>();
		map.forEach((key, value) -> {
			List<MntEBOMInfo> valueList = value;
			MntEBOMInfo child = null;
			List<MntEBOMInfo> subBeanList = new ArrayList<MntEBOMInfo>();
			for (int i = 0; i < valueList.size(); i++) {
				if (i == 0) {
					child = valueList.get(i);
				} else {
					MntEBOMInfo subInfo = value.get(i);
					subBeanList.add(subInfo);
				}
			}
			child.setSubstitutesList(subBeanList);
			childs.add(child);
		});
		return childs;
	}

	/**
	 * 返回分组后的子BOMLine信息集合
	 * 
	 * @param rootBean
	 * @return
	 */
	private Map<Integer, List<MntEBOMInfo>> groupByFindNum(List<MntEBOMInfo> list) {
		Map<Integer, List<MntEBOMInfo>> resultMap = new LinkedHashMap<Integer, List<MntEBOMInfo>>();
		List<MntEBOMInfo> collect = list.stream()
				.sorted(Comparator.comparing(MntEBOMInfo::getFindNum, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList()); // 按照findNum进行升序排序
		collect.forEach(mntEBOMInfo -> {
			List<MntEBOMInfo> mntEBOMInfoList = resultMap.get(mntEBOMInfo.getFindNum());
			if (mntEBOMInfoList == null) {
				mntEBOMInfoList = new ArrayList<MntEBOMInfo>();
				mntEBOMInfoList.add(mntEBOMInfo);
				resultMap.put(mntEBOMInfo.getFindNum(), mntEBOMInfoList);
			} else {
				mntEBOMInfoList.add(mntEBOMInfo);
			}
		});
		return resultMap;
	}

	/**
	 * 获取创建BOM行的信息
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private MntEBOMInfo getBeanInfo(Row row) throws Exception {
		MntEBOMInfo bean = new MntEBOMInfo();
		try {
			for (int j = 0; j < MntEBOMSheetConstant.COLLENGTH; j++) {
				Cell cell = row.getCell(j);
				String value = removeBlank(getCellValue(cell));
				switch (j) {
				case 0:
					if (CommonTools.isNotEmpty(value)) {
						float f = Float.parseFloat(value);
						bean.setFindNum((int) f);
					}
					break;
				case 2:
					bean.setItemId(value);
					break;
				case 3:
					bean.setDescription(value);
					break;
				case 4:
					bean.setSupplier(value);
					break;
				case 5:
					bean.setSupplierPN(value);
					break;
				case 6:
					bean.setSupplierZF(value);
					break;
				case 8:
					bean.setUsage(value);
					break;
				case 9:
					bean.setGrpQty(value);
					break;
				case 10:
					bean.setUnit(value);
					break;
				case 11:
					bean.setLocation(value);
					break;
				default:
					break;
				}
			}
			if (UnitEnum.KEA.unit().equals(bean.getUnit())) {
				int n1 = (int) (Float.parseFloat(bean.getUsage()) * UnitEnum.KEA.multiple());
				int n2 = (int) (Float.parseFloat(bean.getGrpQty()) * UnitEnum.KEA.multiple());
				bean.setUsage(String.valueOf(n1));
				bean.setGrpQty(String.valueOf(n2));
			}
			
			if (CommonTools.isNotEmpty(bean.getUsage())) {				
				float float1 = Float.valueOf(bean.getUsage());
				int us = (int) float1;
				bean.setUsage(String.valueOf(us));
			}
			
			if (CommonTools.isNotEmpty(bean.getGrpQty())) {
				float float2 = Float.valueOf(bean.getGrpQty());
				int qty = (int) float2;
				bean.setGrpQty(String.valueOf(qty));
			}
			
		} catch (Exception e) {
			throw new Exception(e);		
		}
		return bean;
	}

	/**
	 * 获取单元格内容
	 * 
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
			value = null;
			break;
		case "ERROR":
			value = null;
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
	 * 去掉字符串中的前后空格、回车、换行符、制表符 value
	 *
	 * @return
	 */
	public String removeBlank(String value) {
		String result = "";
		if (value != null) {
			Pattern p = Pattern.compile("|\t|\r|\n");
			Matcher m = p.matcher(value);
			result = m.replaceAll("");
			result = result.trim();
		} else {
			result = value;
		}
		return result;
	}

//	private void writeInfoLogText(final String message) {
//		
//		Display.getDefault().syncExec(new Runnable() {
//
//			@Override
//			public void run() {
//				if (!styledText.isDisposed()) {
//					String msg = "【INFO】 " + message;					
//					styledText.append(msg + "\n");					
//					StyleRangeConstant.INFOSTYLE.start = styledText.getCharCount() - msg.length() - 1;
//					StyleRangeConstant.INFOSTYLE.length = msg.length();
//					styledText.setStyleRange(StyleRangeConstant.INFOSTYLE);
//				}
//			}
//		});
//	}
//
//	private void writeErrorLogText(final String message) {
//		
//		Display.getDefault().syncExec(new Runnable() {
//
//			@Override
//			public void run() {
//				if (!styledText.isDisposed()) {
//					String msg = "【ERROR】 " + message;
//					styledText.append(msg + "\n");
//					
//					StyleRangeConstant.ERRORSTYLE.start = styledText.getCharCount() - msg.length() - 1;
//					StyleRangeConstant.ERRORSTYLE.length = msg.length();
//					styledText.setStyleRange(StyleRangeConstant.ERRORSTYLE);
//				}
//			}
//		});
//	}

}
