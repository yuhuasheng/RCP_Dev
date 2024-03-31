package com.foxconn.mechanism.hhpnmaterialapply.export.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.security.auth.kerberos.KerberosKey;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.foxconn.mechanism.hhpnmaterialapply.export.constants.DTDesignField;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.DataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.ExcelConfig;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.MegerCellEntity;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTBOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTMISCDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTMISCSheetConstant;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTPLDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTPLSheetConstant;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTSMDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTSMSheetConstant;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTSheetData;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.ExcelUtil;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.json.JSONUtil;

public class DTExportOperation {

	private static final String EXCEL_TEMPLATE = "com/foxconn/mechanism/hhpnmaterialapply/export/template/DTPartList.xlsx";
	private final static int IMAGECOL = 8;
	private final static int STARTROW = 4;
	private final static int ROWHEIGHT = 160;
	private static final String GROUP_0 = DTDesignField.CHANGE_HISTORY;
	private static final String GROUP_1 = DTDesignField.PL;
	private static final String GROUP_2 = DTDesignField.SM;
	private static final String GROUP_3 = DTDesignField.MISC;
	private static final String[] GROUPS = { GROUP_0, GROUP_1, GROUP_2, GROUP_3 };
	
	private TCComponentBOMLine topBOMLine;	 
	
	private List<DTBOMInfo> list;

	private ExcelConfig excelConfig;

	private List<DTDataModel> finishDataList = new ArrayList<DTDataModel>();

	public DTExportOperation(List<DTBOMInfo> list, TCComponentBOMLine topBOMLine) {
		excelConfig = new ExcelConfig();
//		excelConfig.setColLength(COLLENGTH);
		excelConfig.setExcel_template(EXCEL_TEMPLATE);
		excelConfig.setImageCol(IMAGECOL);
		excelConfig.setStartRow(STARTROW);
		excelConfig.setRowHeight(ROWHEIGHT);
		this.list = list;
		this.topBOMLine = topBOMLine;
	}

	public String getProjectName() throws NotLoadedException, TCException {
		return IExportServices.getProjectName(topBOMLine);
	}
	
	
	/**
	 * 将料号对象按照相同的ItemID、partType、optionalType 进行合并
	 * 
	 * @return
	 */
	public List<DTDataModel> mergeBOM() {
		Map<String, List<DTBOMInfo>> groupMap1 = list.stream()
				.collect(Collectors.groupingBy(dtbominfo -> dtbominfo.getPartType()));
//		System.out.println("==>> groupMap1分组数据: " + JSONUtil.toJsonPrettyStr(groupMap1));
		List<DTDataModel> resultList = new ArrayList<DTDataModel>();
		groupMap1.forEach((key, value) -> {			
			List<DTBOMInfo> valueList = value;
			for (DTBOMInfo dtbomInfo : valueList) {
				try {
					DTDataModel bean = null;					
					if (key.equals(DTDesignField.PL)) {
						bean = new DTPLDataModel();
					} else if (key.equals(DTDesignField.SM)) {
						bean = new DTSMDataModel();
					} else if (key.equals(DTDesignField.MISC)) {
						bean = new DTMISCDataModel();
					}
					TCComponentBOMLine bomLine = dtbomInfo.getBomLine();
					IExportServices.tcPropMapping(bean, bomLine, "BOMLine");
					bean.setItemRevision(bomLine.getItemRevision());
					bean.setPartType(dtbomInfo.getPartType());
					bean.setOptionalType(dtbomInfo.getOptionalType());
					resultList.add(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		List<DTDataModel> merge = new ArrayList<DTDataModel>();
		merge.addAll(resultList.stream().collect(Collectors.toMap(k -> {
			try {
				return k.getItemRevision().getProperty("item_id") + k.getPartType() + k.getOptionalType();
			} catch (TCException e) {
				e.printStackTrace();
			}
			return null;
		}, v -> v, (n, o) -> {
			int usage = Integer.parseInt(elseEmpty(n.getUsage())) + Integer.parseInt(elseEmpty(o.getUsage()));
			n.setUsage("" + usage);
			return n;
		})).values());
//		Map<String, List<DTDataModel>> groupMap3 = merge.stream()
//				.collect(Collectors.groupingBy(dtDataModel -> dtDataModel.getPartType()));
//		System.out.println("==>> groupMap3分组数据: " + JSONUtil.toJsonPrettyStr(groupMap3));
		return merge;

	}
	

	/**
	 * 数据处理
	 * 
	 * @throws Exception
	 */
	public void dataHandle() throws Exception {
		if (CommonTools.isEmpty(list)) {
			return;
		}
		List<DTDataModel> dataList = mergeBOM();
		
		finishDataList.addAll(dataList.stream().map(e -> {
			try {
				return getRepresentations(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}).filter(e -> e.size() > 0).flatMap(e -> e.stream()).collect(Collectors.toList()));
	}

	public boolean generatePartsExcel(File targeFile) throws Exception {
		if (CommonTools.isEmpty(finishDataList)) {
			MessageBox.displayModalMessageBox(TCUtil.getDesktop().getFrame(), "不存在料號對象，無需導出", "Warning", MessageBox.WARNING);
			return false;
		}
//		if (1 ==1) {
//			return false;
//		}
		ExcelUtil excelUtil = new ExcelUtil();
		Workbook wb = excelUtil.getWorkbook(excelConfig.getExcel_template());
		CellStyle cellStyle = getCellStyle(wb);
		List<DTSheetData> dtSheetDataList = splitData();		
		if (CommonTools.isEmpty(dtSheetDataList)) {
			MessageBox.displayModalMessageBox(TCUtil.getDesktop().getFrame(), "料號分組失敗", "Warning", MessageBox.WARNING);
			return false;
		}
		for (DTSheetData dtSheetData : dtSheetDataList) {
			List<DTDataModel> dtDataModelList = dtSheetData.getDatas();
			Sheet sheet = wb.getSheetAt(dtSheetData.getIndex());
			setExcelHeader(sheet);
			// 对list集合按照某字段进行排序,然后分组
			Map<String, List<DTDataModel>> map = DTExportTools.groupByPartTypeAndOptionalType(dtDataModelList);			
			dataInsertSheet(excelUtil, wb, cellStyle, sheet, map);
		}
		OutputStream out = new FileOutputStream(targeFile);
		wb.write(out);
		out.flush();
		out.close();
		return true;
	}

	/**
	 * 
	 * @param excelUtil
	 * @param wb
	 * @param cellStyle
	 * @param sheet
	 * @param key
	 * @param list
	 * @param sheetName
	 * @param number
	 * @throws Exception
	 */
	private void dataInsertSheet(ExcelUtil excelUtil, Workbook wb, CellStyle cellStyle, Sheet sheet, Map<String, List<DTDataModel>> map) throws Exception {			
		Iterator<Map.Entry<String, List<DTDataModel>>> iterator = map.entrySet().iterator();
		String sheetName = null;
		if (CommonTools.isNotEmpty(map)) { 
			int k = 0;			
			while (iterator.hasNext()) {
				Entry<String, List<DTDataModel>> entry = iterator.next();
				String key = entry.getKey();
				sheetName = getSheetName(sheetName, key); // 获取标签名
				List<DTDataModel> list = entry.getValue();
				if (key.endsWith(DTDesignField.OPTIONALPART) && !key.endsWith(DTDesignField.NOTOPTIONALPART)) {
					int rowIndex = k + excelConfig.getStartRow();
					setOptionalPartFlag(wb, cellStyle, sheet, rowIndex);
					k++;
					k = fillingCellValue(excelUtil, wb, cellStyle, sheet, k, list, sheetName, rowIndex); // 填充单元格数值
				} else if (key.endsWith(DTDesignField.NOTOPTIONALPART)) {
					 k = fillingCellValue(excelUtil, wb, cellStyle, sheet, k, list, sheetName, 3); // 填充单元格数值
				}
				iterator.remove(); // 移除此记录
			}
			
		} 
		
//		else if (map.size() == 2) {
//			int k = 0;
//			while (iterator.hasNext()) {
//				Entry<String, List<DTDataModel>> entry = iterator.next();
//				String key = entry.getKey();
//				sheetName = getSheetName(sheetName, key); // 获取标签名
//				List<DTDataModel> list = entry.getValue();
//				k = fillingCellValue(excelUtil, wb, cellStyle, sheet, k, list, sheetName); // 填充单元格数值
//				int rowIndex = k + excelConfig.getStartRow();
//				Row newRow = sheet.createRow(rowIndex);
//				Cell cell = newRow.createCell(0);
//				cell.setCellValue(DTDesignField.OPTIONALPART); // 设置OptionalPart标识
//				k++;
//				fillingCellValue(excelUtil, wb, cellStyle, sheet, k, list, sheetName); // 填充单元格数值
//				iterator.remove();
//			}
//		}
	}

	/**
	 * 设置Optional Part标识
	 * @param cellStyle
	 * @param sheet
	 * @param rowIndex
	 */
	private void setOptionalPartFlag(Workbook wb, CellStyle cellStyle, Sheet sheet, int rowIndex) {
		CellStyle style = ExcelUtil.getCellStyle(wb);	
		style.setAlignment(HorizontalAlignment.CENTER);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 26);
        font.setFontName("宋体");
        style.setFont(font);        
		
		Row newRow = sheet.createRow(rowIndex);
		newRow.setHeightInPoints(excelConfig.getRowHeight());
		for (int c = 0; c < excelConfig.getColLength(); c++) {
			Cell cell = newRow.createCell(c);
			if (c == 0) {						
				cell.setCellValue(DTDesignField.OPTIONALPART); // 设置OptionalPart标识
			}
			cell.setCellStyle(style);				
		}
		// 合并单元格
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, 0, excelConfig.getColLength() - 1);
		sheet.addMergedRegion(region);
		
		Cell cell = sheet.getRow(rowIndex).getCell(0);
		cell.setCellStyle(style);
	}

	/**
	 * 获取标签名
	 * @param sheetName
	 * @param key
	 * @return
	 */
	private String getSheetName(String sheetName, String key) {
		if (key.startsWith(DTDesignField.PL)) {
			excelConfig.setColLength(DTPLSheetConstant.COLLENGTH);
			sheetName = DTDesignField.PL;
		} else if (key.startsWith(DTDesignField.SM)) {
			excelConfig.setColLength(DTSMSheetConstant.COLLENGTH);
			sheetName = DTDesignField.SM;
		} else if (key.startsWith(DTDesignField.MISC)) {
			excelConfig.setColLength(DTMISCSheetConstant.COLLENGTH);
			sheetName = DTDesignField.MISC;
		}
		return sheetName;
	}

	/**
	 * 填充单元格数值
	 * 
	 * @param excelUtil
	 * @param wb
	 * @param cellStyle
	 * @param sheet
	 * @param k
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private int fillingCellValue(ExcelUtil excelUtil, Workbook wb, CellStyle cellStyle, Sheet sheet, int num,
			List<DTDataModel> list, String sheetName, int index) throws Exception {
		fillingModel(list, sheetName); // 填充编号和用量参数
		for (int i = 0; i < list.size(); i++) {
			int rowIndex = num + excelConfig.getStartRow();
			Row newRow = sheet.createRow(rowIndex);
			newRow.setHeightInPoints(excelConfig.getRowHeight());
			for (int c = 0; c < excelConfig.getColLength(); c++) {
				Cell cell = newRow.createCell(c);
				cell.setCellStyle(cellStyle);				
			}
			DTDataModel bean = null;
			if (DTDesignField.PL.equals(sheetName)) {
				bean = (DTPLDataModel) list.get(i);
			} else if (DTDesignField.SM.equals(sheetName)) {
				bean = (DTSMDataModel) list.get(i);
			} else if (DTDesignField.MISC.equals(sheetName)) {
				bean = (DTMISCDataModel) list.get(i);
			}
			System.out.println(bean.getItemRevision().getProperty("item_id"));
			excelUtil.setCellValue(bean, newRow);
			File imageFile = IExportServices.getModelJPEG(bean.getItemRevision());
			if (CommonTools.isNotEmpty(imageFile)) {
				newRow.setHeightInPoints(excelConfig.getRowHeight());
				excelUtil.insertImg(wb, sheet, imageFile, excelConfig.getImageCol(), rowIndex);
			}
			num++;
		}
		
		List<MegerCellEntity> scanMegerList = excelUtil.scanMegerCells(list, "item", index);
		for (MegerCellEntity cellEntity : scanMegerList) {
			sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 1, 1));
			sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 4, 4));
			if (DTDesignField.PL.equals(sheetName)) {
				sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 11, 11));
			} else if (DTDesignField.SM.equals(sheetName)) {
				sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 10, 10));
			} else if (DTDesignField.MISC.equals(sheetName)) {
				sheet.addMergedRegion(new CellRangeAddress(cellEntity.startRow, cellEntity.endRow, 9, 9));
			}			
		}
		
		return num;
	}

	/**
	 * 拆分数据
	 * 
	 * @return
	 */
	public List<DTSheetData> splitData() {
		List<DTSheetData> sheetDataList = new ArrayList<DTSheetData>();
//		Map<String, List<DTDataModel>> groupMap = DTExportTools.groupByPartType(finishDataList);
		
		Map<String, List<DTDataModel>> groupMap2 = finishDataList.stream()
				.collect(Collectors.groupingBy(dtDataModel -> dtDataModel.getPartType()));
//		System.out.println("==>> groupMap2分组数据: " + JSONUtil.toJsonPrettyStr(groupMap2));
		for (int i = 0; i < GROUPS.length; i++) {
			if (groupMap2.containsKey(GROUPS[i])) {
				DTSheetData dtSheetData = new DTSheetData();
				dtSheetData.setIndex(i);
				List<DTDataModel> models = groupMap2.get(GROUPS[i]);
				dtSheetData.setDatas(models);
				sheetDataList.add(dtSheetData);
			}
		}
		return sheetDataList;
	}

	/**
	 * 获取设计对象下的料号对象
	 * 
	 * @param dtDataModel
	 * @return
	 * @throws Exception
	 */

	public static List<DTDataModel> getRepresentations(DTDataModel model) throws Exception {
		TCComponentItemRevision itemRev = model.getItemRevision();	
		List<DTDataModel> reps = new ArrayList<DTDataModel>();
		if (itemRev != null) {
			List<TCComponentItemRevision> repList = IExportServices.getItemRevByRepresentation(itemRev);
			if (repList != null) {
				for (TCComponentItemRevision repRev : repList) {
					if (repRev != null) {
						reps.add(IExportServices.tcPropMapping((DTDataModel) model.clone(), repRev, ""));
					}
				}
			}
		}
		return reps;
	}

	public CellStyle getCellStyle(Workbook wb) {		
		CellStyle style = ExcelUtil.getCellStyle(wb);		
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 20);
        font.setFontName("宋体");
        style.setFont(font);
        style.setWrapText(true);
        return style;		
	}

	
	/**
	 * 填充编号和用量参数
	 * 
	 * @param models
	 */
	public void fillingModel(List<DTDataModel> models, String sheetName) {
		for (int k = 0; k < models.size(); k++) {
			DTDataModel dtDataModel = models.get(k);
			if (DTDesignField.PL.equals(sheetName)) {
				DTPLDataModel dtplDataModel = (DTPLDataModel) models.get(k);
				dtplDataModel.setItem((k + 1) + "");
				dtplDataModel.setUsage(elseEmpty(dtDataModel.getUsage()));
			} else if (DTDesignField.SM.equals(sheetName)) {
				DTSMDataModel dtsmDataModel = (DTSMDataModel) models.get(k);
				dtsmDataModel.setItem((k + 1) + "");
				dtsmDataModel.setUsage(elseEmpty(dtDataModel.getUsage()));
			} else if (DTDesignField.MISC.equals(sheetName)) {
				DTMISCDataModel dtmiscDataModel = (DTMISCDataModel) models.get(k);
				dtmiscDataModel.setItem((k + 1) + "");
				dtmiscDataModel.setUsage(elseEmpty(dtDataModel.getUsage()));
			}
		}
		
		Map<String, List<DTDataModel>> groupByItemIdMap = DTExportTools.groupByItemId(models); // 按照零组件ID进行分组
		int index = 0;
		for (Map.Entry<String, List<DTDataModel>> entry : groupByItemIdMap.entrySet()) { // 零组件ID相同，设置index一致
			List<DTDataModel> dtDataModelList = entry.getValue();
			for (DTDataModel dtDataModel : dtDataModelList) {
				if (dtDataModel instanceof DTPLDataModel) {
					DTPLDataModel dtplDataModel = (DTPLDataModel) dtDataModel;	
					dtplDataModel.setItem((index + 1) + "");
				} else if (dtDataModel instanceof DTSMDataModel) {
					DTSMDataModel dtsmDataModel = (DTSMDataModel) dtDataModel;
					dtsmDataModel.setItem((index + 1) + "");					
				} else if (dtDataModel instanceof DTMISCDataModel) {
					DTMISCDataModel dtmiscDataModel = (DTMISCDataModel) dtDataModel;
					dtmiscDataModel.setItem((index + 1) + "");					
				}
			}
			index++;
		}
		
	}

	public String elseEmpty(String str) {
		if (str == null || str.length() == 0) {
			return "1";
		}
		return str;
	}

	/**
	 * 设置Excel表头
	 * @param sheet
	 * @throws TCException 
	 * @throws NotLoadedException 
	 */
	public void setExcelHeader(Sheet sheet) throws NotLoadedException, TCException {
		Row r0 = sheet.getRow(0);
		Cell c00 = r0.getCell(0);
		String sheetName = sheet.getSheetName();
		if (sheetName.equals(DTPLSheetConstant.SHEETNAME)) {
			c00.setCellValue(this.getProjectName() +  "- Partlist-Plastic parts" );
		} else if (sheetName.equals(DTSMSheetConstant.SHEETNAME)) {
			c00.setCellValue(this.getProjectName() + "- Partlist-Sheet metal parts ");
		} else if (sheetName.equals(DTMISCSheetConstant.SHEETNAME)) {
			c00.setCellValue(this.getProjectName() + " - Partlist-Outsourcing parts ");
		}		
	}

}
