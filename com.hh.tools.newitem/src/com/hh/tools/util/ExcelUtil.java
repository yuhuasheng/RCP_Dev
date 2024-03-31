package com.hh.tools.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    public static HSSFRow getRow(HSSFSheet sheet, int index) {
        HSSFRow row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;
    }

    public static HSSFCell getCell(HSSFRow row, int index) {
        HSSFCell cell = row.getCell(index);
        if (cell == null) {
            cell = row.createCell(index);
        }
        return cell;
    }

    public static XSSFRow getRow(XSSFSheet sheet, int index) {
        XSSFRow row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;
    }

    public static XSSFCell getCell(XSSFRow row, int index) {
        XSSFCell cell = row.getCell(index);
        if (cell == null) {
            cell = row.createCell(index);
        }
        return cell;
    }

    //map key:��ע�����ݣ�value:����ֵ
    public static void updateComment(HSSFSheet sheet, HashMap<String, String> map) {
        Map<CellAddress, HSSFComment> commentMap = sheet.getCellComments();
        for (Map.Entry<CellAddress, HSSFComment> entry : commentMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            CellAddress address = entry.getKey();
            HSSFComment comment = entry.getValue();
            int col = address.getColumn();
            int row = address.getRow();
            System.out.println("row == " + row);
            System.out.println("col == " + col);

            HSSFRichTextString str = comment.getString();

            System.out.println("str == " + str);
            if (map.containsKey(str.toString())) {
                String value = map.get(str.toString());
                System.out.println("value == " + value);
                sheet.getRow(row).getCell(col).setCellValue(value);
            } else {
                System.out.println("not contains ==  " + str);
            }

        }
    }

    public static void copyRow(XSSFSheet sheet, int srcStartRow, int dstStartRow, int rowCount) {
        try {
            for (int i = 0; i < rowCount; i++) {
                XSSFRow srcRow = sheet.getRow(srcStartRow + i);
                XSSFRow dstRow = ExcelUtil.getRow(sheet, dstStartRow + i);
                //HSSFRow dstRow =  sheet.getRow(dstStartRow+i);
                if (srcRow == null) {
                    continue;
                }


                //dstRow.setRowStyle(srcRow.getRowStyle());
                dstRow.setHeight(srcRow.getHeight());
                for (int j = 0; j < srcRow.getLastCellNum(); j++) {
                    XSSFCell srcCell = srcRow.getCell(j);
                    XSSFCell dstCell = dstRow.createCell(j);
                    if (srcCell == null) {
                        continue;
                    }
                    int srcCellType = srcCell.getCellType();
                    dstCell.setCellStyle(srcCell.getCellStyle());
                    if (srcCellType == XSSFCell.CELL_TYPE_FORMULA) {
                        dstCell.setCellFormula(srcCell.getCellFormula());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyRow(HSSFSheet sheet, int srcStartRow, int dstStartRow, int rowCount) {
        try {
            for (int i = 0; i < rowCount; i++) {
                HSSFRow srcRow = sheet.getRow(srcStartRow + i);
                HSSFRow dstRow = ExcelUtil.getRow(sheet, dstStartRow + i);
                //HSSFRow dstRow =  sheet.getRow(dstStartRow+i);
                if (srcRow == null) {
                    continue;
                }


                //dstRow.setRowStyle(srcRow.getRowStyle());
                dstRow.setHeight(srcRow.getHeight());
                for (int j = 0; j < srcRow.getLastCellNum(); j++) {
                    HSSFCell srcCell = srcRow.getCell(j);
                    HSSFCell dstCell = dstRow.createCell(j);
                    if (srcCell == null) {
                        continue;
                    }
                    int srcCellType = srcCell.getCellType();
                    dstCell.setCellStyle(srcCell.getCellStyle());
                    if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
                        dstCell.setCellFormula(srcCell.getCellFormula());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shiftRow(HSSFSheet sheet, int index, int rowCount) {
        sheet.shiftRows(index, sheet.getLastRowNum(), rowCount);
    }

    public static void HiddenCell(JTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(
                column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMinWidth(0);
    }

    public static void addMergedRegion(HSSFSheet sheet, int startRow, int endRow, int firstCol, int endCol) {
        sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, firstCol, endCol));
    }

    public static void addMergedRegion(XSSFSheet sheet, int startRow, int endRow, int firstCol, int endCol) {
        sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, firstCol, endCol));
    }

    public static void jointPic(List<File> files, String path) {
        try {
        	Integer allWidth = 0;	// ͼƬ�ܿ��
			Integer allHeight = 0;	// ͼƬ�ܸ߶�
            List<BufferedImage> imgs = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                imgs.add(ImageIO.read(files.get(i)));
                //����
//				if (i==0) {
//					allWidth = imgs.get(0).getWidth();
//				}
//				allHeight += imgs.get(i).getHeight();
				// ����
                if (i == 0) {
                    allHeight = imgs.get(0).getHeight();
                }
                allWidth += imgs.get(i).getWidth();

            }
            BufferedImage combined = new BufferedImage(allWidth, allHeight, BufferedImage.TYPE_INT_RGB);
            // paint both images, preserving the alpha channels
            Graphics g = combined.getGraphics();
            // ����ϳ�
//	        Integer height = 0;
//	        for(int i=0; i< imgs.size(); i++){
//        		g.drawImage(imgs.get(i), 0, height, null);  
//        		height +=  imgs.get(i).getHeight();
//	        }
            // ����ϳ�
            Integer width = 0;
            for (int i = 0; i < imgs.size(); i++) {
                g.drawImage(imgs.get(i), width, 0, null);
                width += imgs.get(i).getWidth();
            }

            ImageIO.write(combined, "jpg", new File(path));
            System.out.println("===�ϳɳɹ�====");
        } catch (Exception e) {
        	System.out.println("===�ϳ�ʧ��====");
            e.printStackTrace();
        }
    }

    /**
	 * 
	 * ת����Ԫ�������ΪString Ĭ�ϵ� <br>
	 * Ĭ�ϵ��������ͣ�CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4),
	 * CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
	 * CELL_TYPE_STRING(1)
	 * 
	 * @param cell
	 * @return
	 * 
	 */
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (null != cell) {
        	// �������ж����ݵ�����
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC: // ����
                    if (0 == cell.getCellType()) {// �жϵ�Ԫ��������Ƿ���NUMERIC����
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {// �ж��Ƿ�Ϊ��������
                            Date date = cell.getDateCellValue();
//                      DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
                            cellValue = formater.format(date);
                        } else {
                        	// ��Щ���ֹ���ֱ�����ʹ�õ��ǿ�ѧ�������� 2.67458622E8 Ҫ���д���
                            DecimalFormat df = new DecimalFormat("####.####");
                            cellValue = df.format(cell.getNumericCellValue());
                            // cellValue = cell.getNumericCellValue() + "";
                        }
                    }
                    break;
                case HSSFCell.CELL_TYPE_STRING: // �ַ���
                    cellValue = cell.getStringCellValue();
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                case HSSFCell.CELL_TYPE_FORMULA: // ��ʽ
                    try {
                        // �����ʽ���Ϊ�ַ���
                        cellValue = String.valueOf(cell.getStringCellValue());
                    } catch (IllegalStateException e) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {// �ж��Ƿ�Ϊ��������
                            Date date = cell.getDateCellValue();
//                      	DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            DateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
                            cellValue = formater.format(date);
                        } else {
                            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper()
                                    .createFormulaEvaluator();
                            evaluator.evaluateFormulaCell(cell);
                         // ��Щ���ֹ���ֱ�����ʹ�õ��ǿ�ѧ�������� 2.67458622E8 Ҫ���д���
                            DecimalFormat df = new DecimalFormat("####.####");
                            cellValue = df.format(cell.getNumericCellValue());
//                          cellValue = cell.getNumericCellValue() + "";
                        }
                    }
//              //ֱ�ӻ�ȡ��ʽ
//              cellValue = cell.getCellFormula() + "";
                    break;
                case HSSFCell.CELL_TYPE_BLANK: // ��ֵ
                    cellValue = "";
                    break;
                case HSSFCell.CELL_TYPE_ERROR: // ����
                	cellValue = "�Ƿ��ַ�";
                    break;
                default:
                	cellValue = "δ֪����";
                    break;
            }
        }
        return cellValue;
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    public static Workbook getWorkbook(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(".xls")) {
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(".xlsx")) {
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }
}
