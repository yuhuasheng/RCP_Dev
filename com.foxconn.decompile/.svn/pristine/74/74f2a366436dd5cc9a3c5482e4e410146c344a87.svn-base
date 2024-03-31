package com.foxconn.decompile.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 * 
 * @author Robert
 *
 */
public class ExcelUtil
{
    /**
     * 
     * Robert 2022年4月11日
     * 
     * @param fileName
     * @return
     */
    public Workbook getWorkbook(String fileName)
    {
        Workbook workbook = null;
        try (InputStream in = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(fileName), "文件未找到：" + fileName);)
        {
            if (Pattern.matches(".*(xls|XLS)$", fileName))
            {
                workbook = new HSSFWorkbook(in);
            }
            else
            {
                workbook = new XSSFWorkbook(in);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return workbook;
    }

  

    public static  Workbook getLocalWorkbook(String fileName) {
        Workbook workbook = null;
        InputStream in = null;
        try {
            in = new FileInputStream(new File(fileName));
            if (Pattern.matches(".*(xls|XLS)$", fileName)) {
                workbook = new HSSFWorkbook(in);
            } else {
                workbook = new XSSFWorkbook(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return workbook;
    }


    /**
     * 更新某一列单元格的内容
     * 
     * @param sheet
     * @param startRow
     * @param cellNum
     * @param value
     */
    public void updateCellValue(Sheet sheet, int startRow, int cellNum, String value)
    {
        int count = sheet.getLastRowNum(); // 获取最后一行
        for (int i = startRow; i <= count; i++)
        {
            Row row = sheet.getRow(i);
            if (row == null)
            {
                break;
            }
            Cell cell = row.getCell(cellNum);
            if (cell == null)
            {
                break;
            }
            if (CommonTools.isEmpty(removeBlank(getCellValue(cell))))
            {
                continue;
            }
            if (cell.getCellStyle() != null)
            {
                cell.setCellValue(value);
            }
        }
    }

    public static CellStyle getCellStyle(Workbook workbook)
    {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
        cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
        cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public CellStyle getCellStyleFont(Workbook wb)
    {
        CellStyle style = getCellStyle(wb);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    public CellStyle getCellStyle2(Workbook wb)
    {
        CellStyle style = getCellStyle(wb);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    /**
     * 获取单元格内容
     * 
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell)
    {
        String value = "";
        if (null == cell)
        {
            return value;
        }
        switch (cell.getCellType().name())
        {
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
    public static String removeBlank(String value)
    {
        String result = "";
        if (value != null)
        {
            Pattern p = Pattern.compile("|\t|\r|\n");
            Matcher m = p.matcher(value);
            result = m.replaceAll("");
            result = result.trim();
        }
        else
        {
            result = value;
        }
        return result;
    }

    public static void main(String[] args)
    {
    }
}
