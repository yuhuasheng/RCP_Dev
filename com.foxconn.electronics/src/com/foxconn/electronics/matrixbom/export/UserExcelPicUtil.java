package com.foxconn.electronics.matrixbom.export;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelPicUtil;

public class UserExcelPicUtil extends ExcelPicUtil {
	public UserExcelPicUtil() {
		super();
	}

	public static Map getPicMap(Workbook workbook, int sheetIndex) {
		Assert.notNull(workbook, "Workbook must be not null !", new Object[0]);
		if (sheetIndex < 0)
			sheetIndex = 0;
		// if(workbook instanceof HSSFWorkbook)
		// return getPicMapXls((HSSFWorkbook)workbook, sheetIndex);
		if (workbook instanceof XSSFWorkbook)
			return getPicMapXlsx((XSSFWorkbook) workbook, sheetIndex);
		else
			throw new IllegalArgumentException(
					StrUtil.format("Workbook type [{}] is not supported!", new Object[] { workbook.getClass() }));
	}

	private static Map getPicMapXlsx(XSSFWorkbook workbook, int sheetIndex) {
		Map sheetIndexPicMap = new HashMap();
		XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		for (Iterator iterator = sheet.getRelations().iterator(); iterator.hasNext();) {
			POIXMLDocumentPart dr = (POIXMLDocumentPart) iterator.next();
			if (dr instanceof XSSFDrawing) {
				XSSFDrawing drawing = (XSSFDrawing) dr;
				List shapes = drawing.getShapes();
				Iterator iterator1 = shapes.iterator();
				while (iterator1.hasNext()) {
					XSSFShape shape = (XSSFShape) iterator1.next();
					if (shape instanceof XSSFPicture) {
						XSSFPicture pic = (XSSFPicture) shape;
						String shapeName = pic.getShapeName();
						XSSFClientAnchor clientAnchor = pic.getClientAnchor();
						int row1 = pic.getClientAnchor().getRow1();
						short col1 = pic.getClientAnchor().getCol1();
						//System.out.println("row1 = " + row1 + ",col1 = " + col1);

						XSSFClientAnchor preferredSize = pic.getClientAnchor();
						CTMarker ctMarker = preferredSize.getFrom();
						sheetIndexPicMap.put(StrUtil.format("{}_{}", new Object[] { Integer.valueOf(ctMarker.getRow()),
								Integer.valueOf(ctMarker.getCol()) }), pic.getPictureData());
					}
				}
			}
		}

		return sheetIndexPicMap;
	}

}
