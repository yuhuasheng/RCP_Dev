package com.hh.fx.rewrite.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelUtil {

	public static HSSFRow getRow(HSSFSheet sheet,int index) {
		HSSFRow row = sheet.getRow(index);
		if(row == null) {
			row = sheet.createRow(index);
		}
		return row;
	}
	
	public static HSSFCell getCell(HSSFRow row,int index) {
		HSSFCell cell = row.getCell(index);
		if(cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}
	
	public static XSSFRow getRow(XSSFSheet sheet,int index) {
		XSSFRow row = sheet.getRow(index);
		if(row == null) {
			row = sheet.createRow(index);
		}
		return row;
	}
	
	public static XSSFCell getCell(XSSFRow row,int index) {
		XSSFCell cell = row.getCell(index);
		if(cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}
	
	public static String getValue(HSSFCell cell) {
		try {
			if(cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
				return  "";
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				return String.valueOf(cell.getBooleanCellValue());
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				return String.valueOf(cell.getNumericCellValue());
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
				return cell.getCellFormula();
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
				return cell.getStringCellValue();
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				return cell.getStringCellValue();
			} else {
				return cell.getStringCellValue();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return "";
		
	}
	
	public static String getValue(XSSFCell cell) {
		try {
			if(cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
				return  "";
			} else if(cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				return String.valueOf(cell.getBooleanCellValue());
			} else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				return String.valueOf(cell.getNumericCellValue());
			} else if(cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
				return cell.getStringCellValue();
			} else if(cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
				return cell.getErrorCellString();
			} else if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return cell.getStringCellValue();
			} else {
				return cell.getStringCellValue();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return "";
		
	}
	
	public static void copyRow(XSSFSheet sheet,int srcStartRow, int dstStartRow, int rowCount) {
		try {
			for(int i=0;i<rowCount;i++) {
				XSSFRow srcRow =sheet.getRow(srcStartRow+i);
				XSSFRow dstRow = ExcelUtil.getRow(sheet, dstStartRow+i);
				//HSSFRow dstRow =  sheet.getRow(dstStartRow+i);
				if(srcRow == null) {
					continue;
				}
				
				
				//dstRow.setRowStyle(srcRow.getRowStyle());
				dstRow.setHeight(srcRow.getHeight());
				for(int j=0;j<srcRow.getLastCellNum();j++) {
					XSSFCell srcCell =srcRow.getCell(j);
					XSSFCell dstCell = dstRow.createCell(j);
					if(srcCell == null) {
						continue;
					}
					int srcCellType = srcCell.getCellType();
					dstCell.setCellStyle(srcCell.getCellStyle());
					if(srcCellType == XSSFCell.CELL_TYPE_FORMULA){
						dstCell.setCellFormula(srcCell.getCellFormula());
					} 
				}
			}
		}catch(Exception  e) {
			e.printStackTrace();
		}
	}
	
	public static void copyRow(HSSFSheet sheet,int srcStartRow, int dstStartRow, int rowCount) {
		try {
			for(int i=0;i<rowCount;i++) {
				HSSFRow srcRow =sheet.getRow(srcStartRow+i);
				HSSFRow dstRow = ExcelUtil.getRow(sheet, dstStartRow+i);
				//HSSFRow dstRow =  sheet.getRow(dstStartRow+i);
				if(srcRow == null) {
					continue;
				}
				
				
				//dstRow.setRowStyle(srcRow.getRowStyle());
				dstRow.setHeight(srcRow.getHeight());
				for(int j=0;j<srcRow.getLastCellNum();j++) {
					HSSFCell srcCell =srcRow.getCell(j);
					HSSFCell dstCell = dstRow.createCell(j);
					if(srcCell == null) {
						continue;
					}
					int srcCellType = srcCell.getCellType();
					dstCell.setCellStyle(srcCell.getCellStyle());
					if(srcCellType == HSSFCell.CELL_TYPE_FORMULA){
						dstCell.setCellFormula(srcCell.getCellFormula());
					} 
				}
			}
		}catch(Exception  e) {
			e.printStackTrace();
		}
	}
	
	public static void shiftRow(HSSFSheet sheet,int index,int rowCount){
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
	
	public static void addMergedRegion(HSSFSheet sheet,int startRow,int endRow,int firstCol,int endCol){
		sheet.addMergedRegion(new CellRangeAddress(startRow,endRow,firstCol,endCol));
	}
	

	public static void jointPic(List<File> files, String path) {   
		try {
			Integer allWidth = 0;	// 图片总宽度
			Integer allHeight = 0;	// 图片总高度
			List<BufferedImage> imgs = new ArrayList<>(); 
			for(int i=0; i<files.size(); i++){
				imgs.add(ImageIO.read(files.get(i)));
				//竖向
//				if (i==0) {
//					allWidth = imgs.get(0).getWidth();
//				}
//				allHeight += imgs.get(i).getHeight();
				// 横向
				if (i==0) {
					allHeight = imgs.get(0).getHeight();
				}
				allWidth += imgs.get(i).getWidth();

			}
	        BufferedImage combined = new BufferedImage(allWidth, allHeight, BufferedImage.TYPE_INT_RGB);  
	        // paint both images, preserving the alpha channels  
	        Graphics g = combined.getGraphics();
	         // 竖向合成
//	        Integer height = 0;
//	        for(int i=0; i< imgs.size(); i++){
//        		g.drawImage(imgs.get(i), 0, height, null);  
//        		height +=  imgs.get(i).getHeight();
//	        }
	     // 横向合成
	        Integer width = 0;
	        for(int i=0; i< imgs.size(); i++){
	        	g.drawImage(imgs.get(i), width, 0, null);
	        	width +=  imgs.get(i).getWidth();
	        }

	        ImageIO.write(combined, "jpg", new File(path));  
	        System.out.println("===合成成功====");
		} catch (Exception e) {
			System.out.println("===合成失败====");
			e.printStackTrace();
		}
	}
	
	  
	    public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet,  
	            boolean copyStyle) {  
	        int maxColumnNum = 0;  
	        Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>()  
	                : null;  
	        
	        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {  
	            HSSFRow srcRow = sheet.getRow(i);  
	            HSSFRow destRow = newSheet.createRow(i);  
	            if (srcRow != null) {  
	                copyRow(sheet, newSheet, srcRow, destRow,  
	                        styleMap);  
	                if (srcRow.getLastCellNum() > maxColumnNum) {  
	                    maxColumnNum = srcRow.getLastCellNum();  
	                }  
	            }  
	        }  
	        for (int i = 0; i <= maxColumnNum; i++) {    //设置列宽  
	            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));  
	        }  
	    }  
	  
	    /** 
	     * 复制并合并单元格 
	     * @param newSheet 
	     * @param sheet 
	     * @param copyStyle 
	     */  
	    public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet,  
	            HSSFRow srcRow, HSSFRow destRow,  
	            Map<Integer, HSSFCellStyle> styleMap) {  
	        Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();  
	        destRow.setHeight(srcRow.getHeight());  
	        int deltaRows = destRow.getRowNum() - srcRow.getRowNum(); //如果copy到另一个sheet的起始行数不同  
	        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {  
	            HSSFCell oldCell = srcRow.getCell(j); // old cell  
	            HSSFCell newCell = destRow.getCell(j); // new cell  
	            if (oldCell != null) {  
	                if (newCell == null) {  
	                    newCell = destRow.createCell(j);  
	                }  
	                copyCell(oldCell, newCell, styleMap);  
	                CellRangeAddress mergedRegion = getMergedRegion(srcSheet,  
	                        srcRow.getRowNum(), (short) oldCell.getColumnIndex());  
	                if (mergedRegion != null) {  
	                    CellRangeAddress newMergedRegion = new CellRangeAddress(  
	                            mergedRegion.getFirstRow() + deltaRows,  
	                            mergedRegion.getLastRow() + deltaRows, mergedRegion  
	                                    .getFirstColumn(), mergedRegion  
	                                    .getLastColumn());  
	                    ExcelUtil.CellRangeAddressWrapper wrapper = new ExcelUtil.CellRangeAddressWrapper(  
	                            newMergedRegion);  
	                    if (isNewMergedRegion(wrapper, mergedRegions)) {  
	                        mergedRegions.add(wrapper);  
	                        try {
	                        	destSheet.addMergedRegion(wrapper.range);  
	                        } catch(Exception e) {
	                        	e.printStackTrace();
	                        }
	                        
	                    }  
	                }  
	            }  
	        }  
	    }  
	  
	    /** 
	     * 把原来的Sheet中cell（列）的样式和数据类型复制到新的sheet的cell（列）中 
	     *  
	     * @param oldCell 
	     * @param newCell 
	     * @param styleMap 
	     */  
	    public static void copyCell(HSSFCell oldCell, HSSFCell newCell,  
	            Map<Integer, HSSFCellStyle> styleMap) {  
	        if (styleMap != null) {  
	            if (oldCell.getSheet().getWorkbook() == newCell.getSheet()  
	                    .getWorkbook()) {  
	                newCell.setCellStyle(oldCell.getCellStyle());  
	            } else {  
	                int stHashCode = oldCell.getCellStyle().hashCode();  
	                HSSFCellStyle newCellStyle = styleMap.get(stHashCode);  
	                if (newCellStyle == null) {  
	                    newCellStyle = newCell.getSheet().getWorkbook()  
	                            .createCellStyle();  
	                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());  
	                    styleMap.put(stHashCode, newCellStyle);  
	                }  
	                newCell.setCellStyle(newCellStyle);  
	            }  
	        }  
	        switch (oldCell.getCellType()) {  
	        case HSSFCell.CELL_TYPE_STRING:  
	            newCell.setCellValue(oldCell.getStringCellValue());  
	            break;  
	        case HSSFCell.CELL_TYPE_NUMERIC:  
	            newCell.setCellValue(oldCell.getNumericCellValue());  
	            break;  
	        case HSSFCell.CELL_TYPE_BLANK:  
	            newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);  
	            break;  
	        case HSSFCell.CELL_TYPE_BOOLEAN:  
	            newCell.setCellValue(oldCell.getBooleanCellValue());  
	            break;  
	        case HSSFCell.CELL_TYPE_ERROR:  
	            newCell.setCellErrorValue(oldCell.getErrorCellValue());  
	            break;  
	        case HSSFCell.CELL_TYPE_FORMULA:  
	        	try{
	        		newCell.setCellFormula(oldCell.getCellFormula());  
	        	} catch (Exception e) {
					// TODO: handle exception
				}
	            
	            break;  
	        default:  
	            break;  
	        }  
	  
	    }  
	  
	    // 获取merge对象  
	    public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum,  
	            short cellNum) {  
	        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {  
	            CellRangeAddress merged = sheet.getMergedRegion(i);  
	            if (merged.isInRange(rowNum, cellNum)) {  
	                return merged;  
	            }  
	        }  
	        return null;  
	    }  
	  
	    private static boolean isNewMergedRegion(  
	            CellRangeAddressWrapper newMergedRegion,  
	            Set<CellRangeAddressWrapper> mergedRegions) {  
	        boolean bool = mergedRegions.contains(newMergedRegion);  
	        return !bool;  
	    }  
	  
	    public static class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {    
	         
	        public CellRangeAddress range;    
	            
	        public CellRangeAddressWrapper(CellRangeAddress theRange) {    
	              this.range = theRange;    
	        }    
	            
	        public int compareTo(CellRangeAddressWrapper craw) {    
	            if (range.getFirstColumn() < craw.range.getFirstColumn()    
	                        && range.getFirstRow() < craw.range.getFirstRow()) {    
	                  return -1;    
	            } else if (range.getFirstColumn() == craw.range.getFirstColumn()    
	                        && range.getFirstRow() == craw.range.getFirstRow()) {    
	                  return 0;    
	            } else {    
	                  return 1;    
	            }    
	        }    
	            
	    }  

//	    public static void addPic(HSSFSheet sheet,HSSFWorkbook workbook,String imgPath, int startRow, int startCol, int endRow,
//				int endCol) {
//			try {
//				ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
//				File imgFile = new File(imgPath);
//				BufferedImage bufferImg = ImageIO.read(imgFile);
//				FileStreamUtil fileStreamUtil = new FileStreamUtil();
//				String suffix = fileStreamUtil.getSuffix(imgPath);
//				ImageIO.write(bufferImg, suffix, byteArrayOut);
//				HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
//				HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,
//						(short) startCol, startRow, (short) endCol, endRow);
//				int imgType = getImgType(suffix);
//				patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), imgType));
//				byteArrayOut.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	    
		public static int getImgType(String suffix) {
			if ("jpeg".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)) {
				return HSSFWorkbook.PICTURE_TYPE_JPEG;
			} else if ("png".equalsIgnoreCase(suffix)) {
				return HSSFWorkbook.PICTURE_TYPE_PNG;
			} else if ("gif".equalsIgnoreCase(suffix)) {
				return HSSFWorkbook.PICTURE_TYPE_JPEG;
			}
			return HSSFWorkbook.PICTURE_TYPE_JPEG;
		}
		
		public static void refreshFormula(HSSFCell cell ) {
			if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
				cell.setCellFormula(cell.getCellFormula());
			}
		}
		
}
