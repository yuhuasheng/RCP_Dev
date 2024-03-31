package com.foxconn.mechanism.hhpnmaterialapply.export.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.MNTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.MegerCellEntity;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.HttpUtil;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.system.UserInfo;

/**
 * 
 * @author Robert
 *
 */
public class ExcelUtil {
	/**
	 * 
	 * Robert 2022年4月11日
	 * 
	 * @param fileName
	 * @return
	 */
	public Workbook getWorkbook(String fileName) {
		Workbook workbook = null;
		try (InputStream in = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(fileName),
				"文件未找到：" + fileName);) {
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

	public void setCellValue(Object bean, Row row) throws IllegalArgumentException, IllegalAccessException {
		if (bean != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcProp = fields[i].getAnnotation(TCPropertes.class);
				if (tcProp != null) {
					int col = tcProp.cell();
					if (col >= 0) {
						Cell cell = row.getCell(tcProp.cell());
						if (CommonTools.isEmpty(cell)) {
							return;
						}
						String oldVal = cell.getStringCellValue();
						String val = (String) fields[i].get(bean);
						if (oldVal != null && oldVal.length() > 0) {
							val = oldVal + "/" + val;
						}
						cell.setCellValue(val);
					}
				}
			}
		}
	}

	/**
	 * 
	 * Robert 2022年4月12日
	 * 
	 * @param wb
	 * @param sheet
	 * @param imgFile
	 * @param col
	 * @param row
	 * @throws IOException
	 */
	public void insertImg(Workbook wb, Sheet sheet, File imgFile, int col, int row) throws IOException {
		Cell c = sheet.getRow(row).getCell(col);
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		Image src = Toolkit.getDefaultToolkit().getImage(imgFile.getPath());
		BufferedImage bufferImg = toBufferedImage(src);// Image to BufferedImage
//        BufferedImage bufferImg = ImageIO.read(imgFile);
		ImageIO.write(bufferImg, "JPG", byteArrayOut);
		Drawing patriarch = sheet.createDrawingPatriarch();
		/*
		 * 
		 * @param dx1 图片的左上角在开始单元格（col1,row1）中的横坐标
		 * 
		 * @param dy1 图片的左上角在开始单元格（col1,row1）中的纵坐标
		 * 
		 * @param dx2 图片的右下角在结束单元格（col2,row2）中的横坐标
		 * 
		 * @param dy2 图片的右下角在结束单元格（col2,row2）中的纵坐标
		 * 
		 * @param col1 开始单元格所处的列号, base 0, 图片左上角在开始单元格内
		 * 
		 * @param row1 开始单元格所处的行号, base 0, 图片左上角在开始单元格内
		 * 
		 * @param col2 结束单元格所处的列号, base 0, 图片右下角在结束单元格内
		 * 
		 * @param row2 结束单元格所处的行号, base 0, 图片右下角在结束单元格内
		 */
		ClientAnchor anchor = patriarch.createAnchor(3 * 10000, 3 * 10000, 20, 70, col, row, col, row);
		Picture picture = patriarch.createPicture(anchor,
				wb.addPicture(byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_JPEG));
//        picture.resize(1, 0.96);
		picture.resize(1, 1);
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}
		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		// Copy image to buffered image
		Graphics g = bimage.createGraphics();
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	public static CellStyle getCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
		cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return cellStyle;
	}

	public List<MegerCellEntity> scanMegerCells(List resps, String fliedName, int startLine)
			throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		List<MegerCellEntity> megerList = new ArrayList<>();
		int startRow = 0;
		String lastValue = "";
		for (int i = 0; i < resps.size(); i++) {
			Object obj = resps.get(i);
			Class c = obj.getClass();
			Field field = c.getDeclaredField(fliedName);
			field.setAccessible(true);
			String value = (String) field.get(obj);
			if ("".equals(lastValue)) {
				startRow = i + 1;
				lastValue = value;
				continue;
			}
			if (i == resps.size() - 1) {
				if (lastValue.equals(value)) {
					megerList.add(new MegerCellEntity(startRow + startLine, i + 1 + startLine));
				}
			}
			if (!lastValue.equals(value)) {
				lastValue = value;
				if (startRow != i) {
					megerList.add(new MegerCellEntity(startRow + startLine, i + startLine));
				}
				startRow = i + 1;
			}
		}
		return megerList;
	}
}
