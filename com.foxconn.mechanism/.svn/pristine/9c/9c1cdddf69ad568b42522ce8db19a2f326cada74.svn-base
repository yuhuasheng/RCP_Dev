/**
 * 
 */
package com.foxconn.mechanism.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;

/**
 * @author wt00110
 *
 */
public class ExcelUtils {

	public Workbook getLocalWorkbook(String fileName) {
		Workbook workbook = null;
		try {
			InputStream in = new FileInputStream(new File(fileName));

			if (Pattern.matches(".*(xls|XLS)$", fileName)) {
				workbook = new HSSFWorkbook(in);
			} else {
				workbook = new XSSFWorkbook(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

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

	/**
	 * 检查文件夹是否存在，如果不存在创建文件夹
	 * 
	 * @param filePath
	 */
	private static void checkDirs(String filePath) {
		File f = new File(filePath);
		if (f.isDirectory()) {
			if (!f.exists()) {
				f.mkdirs();
			}
			return;
		}
		f = f.getAbsoluteFile().getParentFile();
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * 输出xcel文件
	 * 
	 * @param wb
	 * @param filePath 文件路径
	 * @throws Exception
	 */
	public static boolean writeExcel(Workbook wb, String filePath) {
		checkDirs(filePath);
		OutputStream os = null;
		try {
			File file = new File(filePath);
			os = new FileOutputStream(file);
			wb.write(os);
			return true;
		} catch (Exception e) {

			return false;
		} finally {
			try {
				os.close();
			} catch (Exception e) {
			}
		}
	}

	public static boolean writeExcel(Workbook wb, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			wb.write(os);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				os.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 创建Workbook
	 * 
	 * @param workbookType HSSF(excel 97~03) XSSF(07 Excel)
	 * @return
	 * @throws Exception
	 */
	public static Workbook createWorkbook(String workbookType) throws Exception {
		Workbook wb = null;
		if (workbookType.toUpperCase().contains("HSSF")) {
			wb = new HSSFWorkbook();
		} else if (workbookType.toUpperCase().contains("XSSF")) {
			wb = new XSSFWorkbook();
		} else {
			throw new Exception("The parameter \"workbookType\" must contain either "
					+ "\"HSSF\"(for97~03) key word or \"XSSF\"(for 07) key word!");
		}
		return wb;
	}

	/**
	 * 创建sheet 页签
	 * 
	 * @param wb
	 * @param sheetName sheet名称
	 * @return
	 */
	public static Sheet createSheet(Workbook wb, String sheetName) {
		Sheet sheet = null;
		sheet = wb.createSheet(sheetName);
		return sheet;
	}

	public static Cell setCellStyleAndValue(Cell cell, Object value, XSSFCellStyle cellStyle) {
		cell.setCellStyle(cellStyle);
		if (value instanceof String) {
			cell.setCellValue(((String) value));
		} else if (value instanceof Integer) {
			cell.setCellValue(((Integer) value));
		} else if (value instanceof Double) {
			cell.setCellValue(((Double) value));
		} else if (value instanceof Date) {
			cell.setCellValue(((Date) value));
		} else if (value instanceof Calendar) {
			cell.setCellValue(((Calendar) value));
		} else if (value instanceof RichTextString) {
			cell.setCellValue(((RichTextString) value));
		} else if (value instanceof Boolean) {
			cell.setCellValue(((Boolean) value));
		}
		return cell;
	}

	public static int getNotBlankRowCount(Workbook wb, int sheetIx) {
		Sheet sheet = wb.getSheetAt(sheetIx);
		CellReference cellReference = new CellReference("A1");
		boolean flag = false;

		for (int i = cellReference.getRow(); i <= sheet.getLastRowNum();) {
			Row r = sheet.getRow(i);
			if (r == null && (sheet.getLastRowNum() >= i + 1)) {
				// 如果是空行(即没有任何数据、格式)，直接把它以下的数据往上移动
				sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
				continue;
			}

			flag = false;

			if (r != null) {
				for (Cell c : r) {
					if (c.getCellType() != CellType.BLANK) {
						flag = true;
						break;
					}
				}
			}

			if (flag) {
				i++;
				continue;
			} else {
				// 如果是空白行(即可能没有数据，但是有一定格式)
				if (i == sheet.getLastRowNum()) {// 如果到了最后一行，直接将那一行remove掉
					if (r != null)
						sheet.removeRow(r);
					else {
						i++;
						continue;
					}
				} else// 如果还没到最后一行，则数据往上移一行
					sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
			}
		}

		return sheet.getLastRowNum() > 0 ? sheet.getLastRowNum() + 1 : sheet.getLastRowNum();
	}

	/**
	 * 返回sheet 中的行数
	 * 
	 * 
	 * @param sheetIx 指定 Sheet 页，从 0 开始
	 * @return
	 */
	public static int getRowCount(Workbook wb, int sheetIx) {
		Sheet sheet = wb.getSheetAt(sheetIx);
		if (sheet.getPhysicalNumberOfRows() == 0) {
			return 0;
		}
		return sheet.getLastRowNum() + 1;
	}

	/**
	 * 
	 * 返回所在行的列数
	 * 
	 * @param sheetIx  指定 Sheet 页，从 0 开始
	 * @param rowIndex 指定行，从0开始
	 * @return 返回-1 表示所在行为空
	 */
	public static int getColumnCount(Workbook wb, int sheetIx, int rowIndex) {
		Sheet sheet = wb.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		return row == null ? -1 : row.getLastCellNum();

	}

	/**
	 * 
	 * 返回指定行的值的集合
	 * 
	 * @param sheetIx  指定 Sheet 页，从 0 开始
	 * @param rowIndex 指定行，从0开始
	 * @return
	 */
	public List<String> getRowValue(Workbook wb, int sheetIx, int rowIndex) {
		Sheet sheet = wb.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		List<String> list = new ArrayList<String>();
		if (row == null) {
			list.add(null);
		} else {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				list.add(getCellValueToString(row.getCell(i)));
			}
		}
		return list;
	}

	/**
	 * 
	 * 读取指定sheet 页指定行数据
	 * 
	 * @param sheetIx 指定 sheet 页，从 0 开始
	 * @param start   指定开始行，从 0 开始
	 * @param end     指定结束行，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> read(Workbook wb, int sheetIx, int start, int end) throws Exception {
		Sheet sheet = wb.getSheetAt(sheetIx);
		List<List<String>> list = new ArrayList<List<String>>();

//		System.out.println(getRowCount(wb, sheetIx));
		if (end > getRowCount(wb, sheetIx)) {
			end = getRowCount(wb, sheetIx);
		}

		int cols = sheet.getRow(0).getLastCellNum(); // 第一行总列数

		for (int i = start; i <= end; i++) {
			List<String> rowList = new ArrayList<String>();
			Row row = sheet.getRow(i);
			for (int j = 0; j < cols; j++) {
				if (row == null) {
					rowList.add(null);
					continue;
				}
				if (isMergedRegion(sheet, i, j)) {
					rowList.add(getMergedRegionValue(sheet, i, j));
				} else {
					rowList.add(getCellValueToString(row.getCell(j)));
				}
			}
			list.add(rowList);
		}

		return list;
	}

	/**
	 * 
	 * 返回 row 和 column 位置的单元格值
	 * 
	 * @param sheetIx  指定 Sheet 页，从 0 开始
	 * @param rowIndex 指定行，从0开始
	 * @param colIndex 指定列，从0开始
	 * @return
	 * 
	 */
	public static String getValueAt(Workbook wb, int sheetIx, int rowIndex, int colIndex) {
		Sheet sheet = wb.getSheetAt(sheetIx);
		return getCellValueToString(sheet.getRow(rowIndex).getCell(colIndex));
	}

	/**
	 * 
	 * 转换单元格的类型为String 默认的 <br>
	 * 默认的数据类型：CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4),
	 * CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
	 * CELL_TYPE_STRING(1)
	 * 
	 * @param cell
	 * @return
	 * 
	 */
	public static String getCellValueToString(Cell cell) {
		String strCell = "";
		if (cell == null) {
			return null;
		}
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case FORMULA:
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				strCell = date.toString();

				break;
			}
			// 不是日期格式，则防止当数字过长时以科学计数法显示
			cell.setCellType(CellType.STRING);
			strCell = cell.toString();
			break;
		case STRING:
			strCell = cell.getStringCellValue();
			break;
		default:
			break;
		}
		return strCell;
	}

	public static String getCellValueToString(XSSFCell cell) {
		String strCell = "";
		if (cell == null) {
			return null;
		}
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				strCell = date.toString();

				break;
			}
			// 不是日期格式，则防止当数字过长时以科学计数法显示
			cell.setCellType(CellType.STRING);
			strCell = cell.toString();
			break;
		case STRING:
			strCell = cell.getStringCellValue();
			break;
		default:
			break;
		}
		return strCell;
	}

	public static int getColumIntByString(String strColum) {
		int intColum = 0;
		int lenth = strColum.length();
		for (int i = 0; i < lenth; i++) {
			// 公式：26^指数*字母的位数
			intColum = intColum + (int) (Math.pow(26, lenth - 1 - i) * (strColum.charAt(i) - 64));
		}
		return (intColum - 1);

	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * 
	 * @param sheet
	 * @param row    行下标
	 * @param column 列下标
	 * @return
	 */
	public static boolean isMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static String getMergedRegionValue(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getCellValueToString(fCell);
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * 获取excel 中sheet 总页数
	 * 
	 * @return
	 */
	public int getSheetCount(Workbook wb) {
		return wb.getNumberOfSheets();
	}

	/**
	 * 
	 * 获取 sheet名称
	 * 
	 * @param sheetIx 指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public String getSheetName(Workbook wb, int sheetIx) throws IOException {
		Sheet sheet = wb.getSheetAt(sheetIx);
		return sheet.getSheetName();
	}

	public void setCellValue(List beans, int startRow, int collength, Sheet sheet, CellStyle cellStyle)
			throws IllegalArgumentException, IllegalAccessException {
		for (Object bean : beans) {
			Row row = sheet.createRow(startRow);
			for (int i = 0; i < collength; i++) {
				row.createCell(i);
				row.getCell(i).setCellStyle(cellStyle);
			}
			startRow++;
			if (bean != null) {
				Field[] fields = bean.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
					if (tcPropName != null) {
						int col = tcPropName.cell();
						if (col >= 0) {
							// Cell cell = row.createCell(tcProp.cell());
							Cell cell = row.getCell(tcPropName.cell());
							String val = "";
							// System.out.println(fields[i].getType());
							if (fields[i].getType() == Integer.class) {
								if (fields[i].get(bean) != null) {
									val = String.valueOf(fields[i].get(bean));
								}
							} else {
								val = (String) fields[i].get(bean);
							}
							if (val != null) {
								cell.setCellValue(val);
							}
							// if (cellStyle != null)
							// {
							// cell.setCellStyle(cellStyle);
							// }
						}
					}
				}
			}
		}
	}

	public CellStyle getCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
		cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return cellStyle;
	}
	
	
	/**
	 * 设置字体样式
	 * @param wb
	 * @return
	 */
	public CellStyle setFontStyle(Workbook wb) {
		CellStyle style = getCellStyle(wb);
		Font font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("宋体");
        style.setFont(font);
        style.setWrapText(true);
        return style;
	}
}
