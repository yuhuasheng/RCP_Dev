package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public interface IMatrixBOMExportWriter {

	MatrixBOMExportBean assembleMatixChangeBean(TCComponentBOMLine impactBOMLine, TCComponentBOMLine sulotionBomLine,
			String changeType) throws Exception;

	MatrixBOMExportBean assembleMatixBOMBean(IMatixBOMBean bean, List<String> varList, VariableBOMBean varBean,
			List<PicBean> picList) throws Exception;

	String writerDCN(TCComponentItemRevision matrixItemRevision, String dcnNo, List<MatrixBOMExportBean> list)
			throws Exception;

	String createDCNwriterDCN(TCComponentItemRevision matrixItemRevision, String dcnNo, List<MatrixBOMExportBean> list,
			String path ) throws Exception;

	String writerBOM(TCComponentItemRevision matrixItemRevision, List<MatrixBOMExportBean> list,
			ArrayList<String> varList) throws Exception;

	void setHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) throws Exception;

	boolean equals(TCComponentBOMLine bomLine1, TCComponentBOMLine bomLine2) throws TCException;

	default boolean isChange(TCComponentBOMLine impactBOMLine, TCComponentBOMLine sulotionBomLine) throws TCException {
		try {
			String rev1 = impactBOMLine.getItemRevision().getProperty("item_revision_id");
			String rev2 = sulotionBomLine.getItemRevision().getProperty("item_revision_id");
			boolean isChange = !rev1.equals(rev2);
			if (isChange) {
				return true;
			}
			String q1 = impactBOMLine.getProperty("bl_quantity");
			String q2 = sulotionBomLine.getProperty("bl_quantity");
			isChange = !StrUtil.equals(q1, q2);
			if (isChange) {
				return true;
			}
			String category1 = impactBOMLine.getProperty("bl_occ_d9_Category");
			String category2 = sulotionBomLine.getProperty("bl_occ_d9_Category");
			isChange = !StrUtil.equals(category1, category2);
			if (isChange) {
				return true;
			}
			String plant1 = impactBOMLine.getProperty("bl_occ_d9_Plant");
			String plant2 = sulotionBomLine.getProperty("bl_occ_d9_Plant");
			isChange = !StrUtil.equals(plant1, plant2);
			if (isChange) {
				return true;
			}
			String remark1 = impactBOMLine.getItemRevision().getProperty("d9_Remarks");
			String remark2 = sulotionBomLine.getItemRevision().getProperty("d9_Remarks");
			isChange = !StrUtil.equals(remark1, remark2);
			if (isChange) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	default boolean isValueChange(String value) throws TCException {
		return value.contains("变更前：") && value.contains("变更后：");
	};

	default File findPic(String lineId, List<PicBean> picList) {
		for (PicBean picBean : picList) {
			if (lineId.equals(picBean.lineId)) {
				String picPath = picBean.picPath;
				if (picPath == null) {
					return null;
				}
				return new File(picPath);
			}
		}
		return null;
	}

	default String mergeProperties(TCComponentBOMLine impactBOMLine, TCComponentBOMLine sulotionBomLine,
			String property) throws TCException {

		boolean isRevProperty = !property.startsWith("bl_");
		if (impactBOMLine == null) {
			return isRevProperty ? sulotionBomLine.getItemRevision().getProperty(property)
					: sulotionBomLine.getProperty(property);
		}

		if (sulotionBomLine == null) {
			return isRevProperty ? impactBOMLine.getItemRevision().getProperty(property)
					: impactBOMLine.getProperty(property);
		}

		String impactValue = isRevProperty ? impactBOMLine.getItemRevision().getProperty(property)
				: impactBOMLine.getProperty(property);
		String sulotionValue = isRevProperty ? sulotionBomLine.getItemRevision().getProperty(property)
				: sulotionBomLine.getProperty(property);
		if (impactValue.equals(sulotionValue)) {
			return impactValue;
		}
		String result = "变更前：";
		result += impactValue;
		result += "\r\n";
		result += "变更后：";
		result += sulotionValue;
		return result;
	}

	default void setValueAndStyle(ExcelWriter writer, int row, int col, String value, CellStyle style) {
		if ("/".equals(value)) {
			value = "";
		} else if (value != null) {
			String[] split = value.split("\r\n");
			if (split.length > 1) {
				List<String> list = new ArrayList<String>();
				for (String s : split) {
					if ("/".equals(s) || !list.contains(s)) {
						list.add(s);
					}
				}
				value = StrUtil.join("\r\n", list);
			}
		}

		writer.writeCellValue(row, col, value);
		writer.getCell(row, col).setCellStyle(style);
	}

	default int findQty(String varName, String lineId, VariableBOMBean varBean) {
		List<VariableBOMBean> child = varBean.getChild();
		for (VariableBOMBean varBOM : child) {
			String itemName = varBOM.getItemId() + "/" + varBOM.getItemName();
			if (!varName.equals(itemName)) {
				continue;
			}
			List<VariableBOMBean> vaiableList = varBOM.getChild();
			for (VariableBOMBean vaiable : vaiableList) {
				if (lineId.equals(vaiable.getLineId())) {
					return Integer.parseInt(StrUtil.isEmpty(vaiable.getQty()) ? "0" : vaiable.getQty());
				}
			}
		}
		return 0;
	}

	default void updateHistory(TCSession session, TCComponentItemRevision matrixItemRevision, String excelPath) throws Exception {
		com.foxconn.tcutils.util.TCUtil.setBypass(session);
		String excelName = null;
		try {
			TCComponentItemRevision documentItemRev = MatrixBOMExportService.checkDocumentExist(matrixItemRevision);
			if (documentItemRev == null) {
				return;
			}else {
				String version = documentItemRev.getProperty("item_revision_id");
				if (!version.equals("01") && !version.equals("A")) {				
					return;
				}
			}

			String actualUser = documentItemRev.getProperty("d9_ActualUserID") == null ? "" : documentItemRev.getProperty("d9_ActualUserID").trim();
			excelName = excelPath.substring(excelPath.lastIndexOf(File.separator) + 1);
			String excelVersion = excelName.substring(excelName.lastIndexOf("_") + 1).replace(".xlsx", "").replace(".xls", "");
			ExcelWriter writer = ExcelUtil.getWriter(excelPath);
			writer.setSheet("History");
			String lastModifyDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
			Cell cell = writer.getCell(1, 4);
			if (cell != null) {
				String cellValue = cell.getStringCellValue();
				documentItemRev.setProperty("d9_ChangeDescription", cellValue);
			}
//			String changeDesc = documentItemRev.getProperty("d9_ChangeDescription");
//			System.out.println("==>> changeDesc: " + changeDesc);			
			writer.writeCellValue(0, 4, excelVersion);
//			writer.writeCellValue(1, 4, changeDesc);
			writer.writeCellValue(2, 4, lastModifyDate);
			writer.writeCellValue(3, 4, actualUser);
			writer.getWorkbook().removeSheetAt(writer.getSheetCount() - 1);
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
}
