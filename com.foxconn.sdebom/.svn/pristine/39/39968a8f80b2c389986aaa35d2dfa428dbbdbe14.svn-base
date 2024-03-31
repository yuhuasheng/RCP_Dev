package com.foxconn.sdebom.export;

import java.io.File;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class ExcelTest {
	public static void main(String[] args) throws Exception {
		File tempExcelFile = File.createTempFile("HPEBOM", ".xlsx");
//		tempExcelFile.deleteOnExit();
		System.out.println("文件路径: " + tempExcelFile.getAbsolutePath());
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.setDestFile(tempExcelFile);
		writer.renameSheet("sht1");
		writer.setSheet("sht2");
		writer.setSheet("sht3");
		writer.close();
		
	}
}
