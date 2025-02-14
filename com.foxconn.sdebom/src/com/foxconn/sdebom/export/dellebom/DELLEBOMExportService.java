package com.foxconn.sdebom.export.dellebom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;

import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCException;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.tomcat.TomcatDSFactory;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class DELLEBOMExportService {

	public static String exportExcel(TCComponentBOMLine topBOMLine) throws Exception {
		String type = topBOMLine.getItemRevision().getType();
		if(!"D9_BOMTopNodeRevision".equals(type)){
			throw new Exception("必须选择BOM虚拟顶层才能导出");
		}
		List<L6ConfigDefinition> list = new ArrayList<L6ConfigDefinition>();
		obtainL6ConfigDefinitionList(topBOMLine,list);
		Map<Pair<String, TCComponentBOMLine>,List<L6Config>> l6ConfigMap = new LinkedHashMap<Pair<String, TCComponentBOMLine>,List<L6Config>>();
		traversalL6Config(topBOMLine,0,l6ConfigMap,null,null,null);
		// 写 L6 Config Definition
		ExcelWriter writer = ExcelUtil.getWriter(true);
		Map<String, CellStyle> styleMap = createCellStyle(writer);
		CellStyle greenBackgroundXXXStyle = styleMap.get("greenBackgroundXXXStyle");
		CellStyle wrapTextStyle = styleMap.get("wrapTextStyle");
		writer.setSheet("History");
		setValueAndStyle(writer,0,0,"Revision",greenBackgroundXXXStyle);
		setValueAndStyle(writer,1,0,"Description",greenBackgroundXXXStyle);
		setValueAndStyle(writer,2,0,"Date",greenBackgroundXXXStyle);
		setValueAndStyle(writer,3,0,"Author",greenBackgroundXXXStyle);
		setValueAndStyle(writer,0,1,"",wrapTextStyle);
		setValueAndStyle(writer,1,1,"",wrapTextStyle);
		setValueAndStyle(writer,2,1,"",wrapTextStyle);
		setValueAndStyle(writer,3,1,"",wrapTextStyle);
		writer.setColumnWidth(0, 10);
		writer.setColumnWidth(1, 60);
		writer.setColumnWidth(2, 20);
		writer.setColumnWidth(3, 20);	
		writer.setSheet("L6 Config Definition");
		// 写Header
		writer.merge(1, 1, 3, 4, "L6", true);
		writer.merge(1, 1, 5, 6, "L5+", true);
		writer.merge(1, 1, 7, 8, "L5-", true);
		writer.merge(1, 1, 9, 10, "L4", true);
		setValueAndStyle(writer,3,2,"DPN",wrapTextStyle);
		setValueAndStyle(writer,4,2,"Version",wrapTextStyle);
		setValueAndStyle(writer,5,2,"DPN",wrapTextStyle);
		setValueAndStyle(writer,6,2,"Version",wrapTextStyle);
		setValueAndStyle(writer,7,2,"DPN",wrapTextStyle);
		setValueAndStyle(writer,8,2,"Version",wrapTextStyle);
		setValueAndStyle(writer,9,2,"DPN",wrapTextStyle);
		setValueAndStyle(writer,10,2,"Version",wrapTextStyle);
		// 设置列宽
		writer.setColumnWidth(1, 15);
		writer.setColumnWidth(2, 40);
		for (int i = 0; i < list.size(); i++) {
			L6ConfigDefinition l6ConfigDefinition = list.get(i);
			int rownum = 3 + i;
			setValueAndStyle(writer,1,rownum,"L6 Config " + (i+1),wrapTextStyle);
			setValueAndStyle(writer,2,rownum,l6ConfigDefinition.c,wrapTextStyle);
			setValueAndStyle(writer,3,rownum,l6ConfigDefinition.L6DPN,wrapTextStyle);
			setValueAndStyle(writer,4,rownum,l6ConfigDefinition.L6Version,wrapTextStyle);
			setValueAndStyle(writer,5,rownum,l6ConfigDefinition.L5DPNAdd,wrapTextStyle);
			setValueAndStyle(writer,6,rownum,l6ConfigDefinition.L5VersionAdd,wrapTextStyle);
			setValueAndStyle(writer,7,rownum,l6ConfigDefinition.L5DPNSub,wrapTextStyle);
			setValueAndStyle(writer,8,rownum,l6ConfigDefinition.L5VersionSub,wrapTextStyle);
			setValueAndStyle(writer,9,rownum,l6ConfigDefinition.L4DPN,wrapTextStyle);
			setValueAndStyle(writer,10,rownum,l6ConfigDefinition.L4Version,wrapTextStyle);
			if(StringUtils.isNotEmpty(l6ConfigDefinition.L6DPN)) {
				writer.getCell(3,rownum).setCellStyle(greenBackgroundXXXStyle);
			}
			if(StringUtils.isNotEmpty(l6ConfigDefinition.L5DPNAdd)) {
				writer.getCell(5,rownum).setCellStyle(greenBackgroundXXXStyle);
			}
			if(StringUtils.isNotEmpty(l6ConfigDefinition.L5DPNSub)) {
				writer.getCell(7,rownum).setCellStyle(greenBackgroundXXXStyle);
			}
			if(StringUtils.isNotEmpty(l6ConfigDefinition.L4DPN)) {
				writer.getCell(9,rownum).setCellStyle(greenBackgroundXXXStyle);
			}
		}
		// 写入L6、L5 Config
		Iterator<Pair<String, TCComponentBOMLine>> iterator = l6ConfigMap.keySet().iterator();
		while (iterator.hasNext()) {
			writeSheet(iterator.next(),l6ConfigMap,writer,styleMap);
		}
		iterator = l6ConfigMap.keySet().iterator();
		while (iterator.hasNext()) {
			writePTL(iterator.next(),l6ConfigMap,writer,styleMap);
		}
		
		// 保存到文件
		File tempExcelFile = File.createTempFile("DELLEBOM", ".xlsx");
		String absolutePath = tempExcelFile.getAbsolutePath();
		System.out.println("文件路径: " + absolutePath);
		tempExcelFile.deleteOnExit();
		writer.getWorkbook().removeSheetAt(0);
		writer.setDestFile(tempExcelFile);
		writer.close();
		return absolutePath;
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
	public static void insertImg(Workbook wb, Sheet sheet, File imgFile, int col, int row) throws IOException {
		if(imgFile == null) {
			return;
		}
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
	
	static void obtainL6ConfigDefinitionList(TCComponentBOMLine parent,List<L6ConfigDefinition> list) throws Exception {
		// 获取信息
		TCComponentItemRevision itemRevision = parent.getItemRevision();
		String property = itemRevision.getProperty("d9_ShipGrade");
		if("L6".equals(property)) {
			L6ConfigDefinition l6ConfigDefinition = new L6ConfigDefinition();
			l6ConfigDefinition.c = itemRevision.getProperty("d9_EnglishDescription");
			l6ConfigDefinition.L6DPN = itemRevision.getProperty("d9_CustomerPN");
			l6ConfigDefinition.L6Version = itemRevision.getProperty("d9_CustomerPNRev");
			list.add(l6ConfigDefinition);
		}
		if(!list.isEmpty()) {
			L6ConfigDefinition l6ConfigDefinition = list.get(list.size()-1);
			if("L5.5".equals(property)) {
				l6ConfigDefinition.L5DPNAdd = itemRevision.getProperty("d9_CustomerPN");
				l6ConfigDefinition.L5VersionAdd = itemRevision.getProperty("d9_CustomerPNRev");
			}else if("L5".equals(property)) {
				l6ConfigDefinition.L5DPNSub = itemRevision.getProperty("d9_CustomerPN");
				l6ConfigDefinition.L5VersionSub = itemRevision.getProperty("d9_CustomerPNRev");
			}else if("L4".equals(property)) {
				l6ConfigDefinition.L4DPN = itemRevision.getProperty("d9_CustomerPN");
				l6ConfigDefinition.L4Version = itemRevision.getProperty("d9_CustomerPNRev");
			}
		}
		AIFComponentContext[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			// 获取每一个子信息
			TCComponentBOMLine child = (TCComponentBOMLine) children[i].getComponent();
			// 下一层
			obtainL6ConfigDefinitionList(child,list);
		}
	}
	
	static void traversalL6Config(TCComponentBOMLine parent,int layer,Map<Pair<String, TCComponentBOMLine>,List<L6Config>> map,Pair<String, TCComponentBOMLine> l6Pair,Pair<String, TCComponentBOMLine> l5Pair,Pair<String, TCComponentBOMLine> ptlPair) throws Exception {
		
		if(parent.isSubstitute()) {
			return;
		}
		
		TCComponentItemRevision itemRevision = parent.getItemRevision();
		boolean isL6 = "L6".equals(itemRevision.getProperty("d9_ShipGrade"));
		boolean isL5 = "L5.5(VL)".equals(itemRevision.getProperty("d9_ShipGrade"));
		boolean isPTL = "PTL".equals(itemRevision.getProperty("d9_ShipGrade"));
		
		
		// 获取信息
		String sheetName = itemRevision.getProperty("d9_EnglishDescription");
		if(isL6 || isL5 || isPTL){
			if(StringUtils.isEmpty(sheetName)) {
				throw new Exception(parent.getProperty("bl_item_item_id")+" 的 d9_EnglishDescription 属性不能为空，请检查");
			}
		}
		
		if(isL6){
			sheetName = noDuplicationSheetName(map,"L6-"+sheetName);
			l6Pair = new Pair<String, TCComponentBOMLine>(sheetName, parent);
			map.put(l6Pair, new ArrayList<L6Config>());
		}
		if(isL5) {
			sheetName = noDuplicationSheetName(map,"L5-"+sheetName);
			l5Pair = new Pair<String, TCComponentBOMLine>(sheetName, parent);
			map.put(l5Pair, new ArrayList<L6Config>());
		}
		if(isPTL) {
			sheetName = noDuplicationSheetName(map,"PTL-"+sheetName);
			ptlPair = new Pair<String, TCComponentBOMLine>(sheetName, parent);
			map.put(ptlPair, new ArrayList<L6Config>());
		}
		if(l6Pair != null || l5Pair != null || (ptlPair != null)){
			
			
			L6Config e = new L6Config();
			if(l6Pair!=null) {
				map.get(l6Pair).add(e);
			}
			
			if(l5Pair!=null&&!isL5) {
				map.get(l5Pair).add(e);
			}
			
			if(ptlPair!=null && layer>1 ) {
				map.get(ptlPair).add(e);
			}
			
			//属性设置
			if(ptlPair!=null) {
				// PTL 专有属性
				if(layer == 2) {
					L6Config title = new L6Config();;
					title.title = itemRevision.getProperty("d9_EnglishDescription");
					title.isFirst = true;
					int index = map.get(ptlPair).size()-1;
					map.get(ptlPair).add(index,title);
					L6Config empty = new L6Config();
					empty.title = "empty";
					map.get(ptlPair).add(index,empty);
				}
				e.pn = getProperty(itemRevision,"d9_CustomerPN","(Foxconn P/N)");
				if(parent.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = parent.listSubstitutes();
					for (TCComponentBOMLine substitueBOMLine : listSubstitutes) {
						e.pn += "\r\n";
						e.pn += getProperty(substitueBOMLine.getItemRevision(),"d9_CustomerPN","(Foxconn P/N)");
					}
				}
			}else {
				// L6 L5 属性
				e.remark = getProperty(parent,"D9_Remark");; 
				e.level = getProperty(itemRevision,"d9_CustomerPN","(HH P/N)");
				if(parent.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = parent.listSubstitutes();
					for (TCComponentBOMLine substitueBOMLine : listSubstitutes) {
						e.remark += "\r\n";
						e.remark += getProperty(substitueBOMLine,"D9_Remark");;
						e.level += "\r\n";
						e.level += getProperty(substitueBOMLine.getItemRevision(),"d9_CustomerPN");
					}
				}
			}
			
			// 公共属性
			e.layer = layer;
			if(l5Pair != null) {
				e.layer--;
			}
			e.qty = getProperty(parent,"bl_quantity","1");
			e.descriptionInAgile = getProperty(parent.getItemRevision(),"d9_EnglishDescription");
			e.rev = getProperty(parent.getItemRevision(),"d9_CustomerPNRev");
			e.vendorName = getProperty(parent.getItemRevision(),"d9_ManufacturerID");
			e.vendorPN = getProperty(parent.getItemRevision(),"d9_ManufacturerPN");
			e.hhpn = getProperty(parent,"bl_item_item_id");
			e.ppap = getProperty(parent.getItemRevision(),"d9_PPAP");
			e.sDoCInAgile = getProperty(parent.getItemRevision(),"d9_SDoC");
			e.specInAgile = getProperty(parent.getItemRevision(),"d9_Spec");		
			File picFile = null;
			TCComponent[] relatedComponents = itemRevision.getRelatedComponents("TC_Is_Represented_By"); // 获取引用伪文件夹下面的附件
			for (TCComponent tcComponent : relatedComponents) {
				if(picFile != null) {
					break;
				}	
				if(tcComponent instanceof TCComponentItemRevision) {
					try {
						picFile = CommonTools.getModelJPEG((TCComponentItemRevision)tcComponent);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}				
			}
			if(picFile==null) {
				TCComponent[] relatedComponents2 = itemRevision.getRelatedComponents("IMAN_specification");
				for (TCComponent tcComponent : relatedComponents2) {
					if (!(tcComponent instanceof TCComponentDataset)) {
						continue;
					}
					String fileName = null;
					TCComponentDataset dataset = (TCComponentDataset) tcComponent;
					for (String name : dataset.getFileNames(null)) {
						if(name.toUpperCase().endsWith(".jpg".toUpperCase())
							|| name.toUpperCase().endsWith(".png".toUpperCase())) {
							fileName = name;
							break;
						}
					}
					if(fileName!=null) {
						picFile = dataset.getFile(null, fileName);
					}
				}
			}
			e.picture = picFile;
			
			if(parent.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = parent.listSubstitutes();
				for (TCComponentBOMLine substitueBOMLine : listSubstitutes) {
					e.descriptionInAgile += "\r\n";
					e.descriptionInAgile += getProperty(substitueBOMLine.getItemRevision(),"d9_EnglishDescription");
					
					e.rev += "\r\n";
					e.rev += getProperty(substitueBOMLine.getItemRevision(),"d9_CustomerPNRev");

					e.vendorName += "\r\n";
					e.vendorName += getProperty(substitueBOMLine.getItemRevision(),"d9_ManufacturerID");
					
					e.vendorPN += "\r\n";
					e.vendorPN += getProperty(substitueBOMLine.getItemRevision(),"d9_ManufacturerPN");
					
					e.hhpn += "\r\n";
					e.hhpn += getProperty(substitueBOMLine,"bl_item_item_id");
					
					e.ppap += "\r\n";
					e.ppap += getProperty(substitueBOMLine.getItemRevision(),"d9_PPAP");
					
					e.sDoCInAgile += "\r\n";
					e.sDoCInAgile += getProperty(substitueBOMLine.getItemRevision(),"d9_SDoC");
					
					e.specInAgile += "\r\n";
					e.specInAgile += getProperty(substitueBOMLine.getItemRevision(),"d9_Spec");
				}
			}
		}
		AIFComponentContext[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			// 下一层
			traversalL6Config((TCComponentBOMLine) children[i].getComponent(),layer+1,map,l6Pair,l5Pair,ptlPair);
		}
	}
	
	static String noDuplicationSheetName(Map<Pair<String, TCComponentBOMLine>,List<L6Config>> map,String sheetName) {
		int count = 1;
		Iterator<Pair<String, TCComponentBOMLine>> iterator = map.keySet().iterator();
		while(iterator.hasNext()) {
			Pair<String,TCComponentBOMLine> next = iterator.next();
			if(next.getKey().startsWith(sheetName)) {
				count++;
			}
		}
		if(count!=1) {
			sheetName+=count;
		}
		return sheetName;
	}
	
	static void traversalMaxLayer(TCComponentBOMLine parent,Map<String,Integer> map,int layer) throws Exception {
		// 获取信息
		Integer integer = map.get("max");
		if(integer<layer) {
			integer = layer;
		}
		map.put("max", integer);
		AIFComponentContext[] children = parent.getChildren();
		for (int i = 0; i < children.length; i++) {
			// 下一层
			traversalMaxLayer((TCComponentBOMLine) children[i].getComponent(),map,layer+1);
		}
	}
	
	static Map<String,CellStyle> createCellStyle(ExcelWriter writer) {
		Font boldFont = writer.createFont();
		boldFont.setBold(true);
		CellStyle headerStyle = writer.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setWrapText(true);
		headerStyle.setFont(boldFont);
		headerStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		headerStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		headerStyle.setBorderTop(BorderStyle.THIN);// 上边框
		headerStyle.setBorderRight(BorderStyle.THIN);// 右边框
		headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
		CellStyle greenBackgroundXXXStyle = writer.createCellStyle();
		greenBackgroundXXXStyle.setAlignment(HorizontalAlignment.CENTER);
		greenBackgroundXXXStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		greenBackgroundXXXStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		greenBackgroundXXXStyle.setFont(boldFont);
		greenBackgroundXXXStyle.setWrapText(true);
		greenBackgroundXXXStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		greenBackgroundXXXStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		greenBackgroundXXXStyle.setBorderTop(BorderStyle.THIN);// 上边框
		greenBackgroundXXXStyle.setBorderRight(BorderStyle.THIN);// 右边框
		greenBackgroundXXXStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
		CellStyle yelloBackgroundXXXStyle = writer.createCellStyle();
		yelloBackgroundXXXStyle.cloneStyleFrom(greenBackgroundXXXStyle);
		yelloBackgroundXXXStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
		Font redFont = writer.createFont();
		redFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		redFont.setBold(true);
		yelloBackgroundXXXStyle.setFont(redFont);		
		CellStyle blueFonStyle = writer.createCellStyle();
		blueFonStyle.cloneStyleFrom(yelloBackgroundXXXStyle);
		blueFonStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		Font buleFont = writer.createFont();
		buleFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		buleFont.setBold(true);
		blueFonStyle.setFont(buleFont);	
		CellStyle titleStyle = writer.createCellStyle();
		titleStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		titleStyle.setBorderLeft(BorderStyle.THICK);// 左边框
		titleStyle.setBorderTop(BorderStyle.THICK);// 上边框
		titleStyle.setBorderRight(BorderStyle.THICK);// 右边框
		titleStyle.setAlignment(HorizontalAlignment.LEFT);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		Font titleFont = writer.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeight((short) 500);
		titleStyle.setFont(titleFont);
		CellStyle wrapTextStyle = writer.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setBorderBottom(BorderStyle.THIN); // 下边框
		wrapTextStyle.setBorderLeft(BorderStyle.THIN);// 左边框
		wrapTextStyle.setBorderTop(BorderStyle.THIN);// 上边框
		wrapTextStyle.setBorderRight(BorderStyle.THIN);// 右边框
		wrapTextStyle.setAlignment(HorizontalAlignment.CENTER);
		wrapTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		CellStyle dpnStyle = writer.createCellStyle();
		dpnStyle.cloneStyleFrom(wrapTextStyle);
		dpnStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		dpnStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
		HashMap<String, CellStyle> styleMap = new HashMap<String,CellStyle>();
		styleMap.put("headerStyle", headerStyle);
		styleMap.put("greenBackgroundXXXStyle", greenBackgroundXXXStyle);
		styleMap.put("yelloBackgroundXXXStyle", yelloBackgroundXXXStyle);
		styleMap.put("blueFonStyle", blueFonStyle);
		styleMap.put("titleStyle", titleStyle);
		styleMap.put("wrapTextStyle", wrapTextStyle);
		styleMap.put("dpnStyle", dpnStyle);
		return styleMap;
	}
	
	static void writeSheet(Pair<String, TCComponentBOMLine> pair,Map<Pair<String, TCComponentBOMLine>,List<L6Config>> l6ConfigMap,ExcelWriter writer,Map<String,CellStyle> styleMap) throws Exception {
		String sheetName = pair.getKey();
		boolean isPTL = sheetName.startsWith("PTL-");
		if(isPTL) {
			return;
		}
		boolean isL6 = sheetName.startsWith("L6-");
//		sheetName = sheetName.substring(3);
		List<L6Config> l6ConfigList = l6ConfigMap.get(pair);
		Map<String,Integer> layerMap = new HashMap<String, Integer>();
		layerMap.put("max", 0);
		traversalMaxLayer(pair.getValue(),layerMap,1);
		int maxLayer = layerMap.get("max");
		if(!isL6) {
			maxLayer--;
		}
		System.out.println(sheetName);
		writer.setSheet(sheetName);
		CellStyle yelloBackgroundXXXStyle = styleMap.get("yelloBackgroundXXXStyle");
		CellStyle greenBackgroundXXXStyle = styleMap.get("greenBackgroundXXXStyle");
		CellStyle wrapTextStyle = styleMap.get("wrapTextStyle");
		CellStyle blueFonStyle = styleMap.get("blueFonStyle");
		CellStyle dpnStyle = styleMap.get("dpnStyle");
		// 写Header
		setValueAndStyle(writer,1, 0, "XXXXX",wrapTextStyle);
		setValueAndStyle(writer,1, 1, "XXXXX",wrapTextStyle);
		setValueAndStyle(writer,1, 2, "XXXXX",wrapTextStyle);
		setValueAndStyle(writer,5, 2, "FOXCONN",wrapTextStyle);
		writer.merge(0, 0, 2, 4, "Need apply new DPN.", false);
		writer.merge(1, 1, 2, 4, "Updated Item.", false);
		writer.merge(2, 2, 2, 4, "Item need to be confrimed.", false);
		writer.getCell(1, 0).setCellStyle(greenBackgroundXXXStyle);
		writer.getCell(1, 2).setCellStyle(yelloBackgroundXXXStyle);
		writer.getCell(1, 1).setCellStyle(blueFonStyle);
		int headerRowNum = 3;
		if(isL6) {
			setValueAndStyle(writer,0, headerRowNum, "Remark",wrapTextStyle);
			setValueAndStyle(writer,1, headerRowNum, "Level6",wrapTextStyle);
			setValueAndStyle(writer,2, headerRowNum, "Level5+",wrapTextStyle);
			setValueAndStyle(writer,3, headerRowNum, "Level5-",wrapTextStyle);
			setValueAndStyle(writer,4, headerRowNum, "Level4",wrapTextStyle);
			setValueAndStyle(writer,5, headerRowNum, "Level3",wrapTextStyle);
			setValueAndStyle(writer,6, headerRowNum, "Level2",wrapTextStyle);
			setValueAndStyle(writer,7, headerRowNum, "Level1",wrapTextStyle);
		}else {
			setValueAndStyle(writer,0, headerRowNum, "Remark",wrapTextStyle);
			setValueAndStyle(writer,1, headerRowNum, "Level5+",wrapTextStyle);
			setValueAndStyle(writer,2, headerRowNum, "Level5-",wrapTextStyle);
			setValueAndStyle(writer,3, headerRowNum, "Level4",wrapTextStyle);
			setValueAndStyle(writer,4, headerRowNum, "Level3",wrapTextStyle);
			setValueAndStyle(writer,5, headerRowNum, "Level2",wrapTextStyle);
			setValueAndStyle(writer,6, headerRowNum, "Level1",wrapTextStyle);
		}
		setValueAndStyle(writer,maxLayer+1, headerRowNum, "Qty",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+2, headerRowNum, "Description in Agile",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+3, headerRowNum, "Rev",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+4, headerRowNum, "Picture",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+5, headerRowNum, "Vendor Name",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+6, headerRowNum, "Vendor PN",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+7, headerRowNum, "HH P/N",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+8, headerRowNum, "PPAP",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+9, headerRowNum, "SDoC in Agile",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+10, headerRowNum, "Spec. in Agile",wrapTextStyle);
		writer.setRowHeight(headerRowNum, 30);
		CellStyle headerStyle = styleMap.get("headerStyle");
		writer.getCell(0,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(1,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(2,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(3,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(4,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(5,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(6,headerRowNum).setCellStyle(headerStyle);
		if(isL6) {
			writer.getCell(7,headerRowNum).setCellStyle(headerStyle);
		}
		writer.getCell(maxLayer+1,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+2,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+3,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+4,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+5,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+6,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+7,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+8,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+9,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+10,headerRowNum).setCellStyle(headerStyle);
		for (int i = 0; i < l6ConfigList.size(); i++) {
			L6Config l6Config = l6ConfigList.get(i);
			int row = i + headerRowNum + 1;
			setValueAndStyle(writer,0,row, l6Config.remark,wrapTextStyle);
			setValueAndStyle(writer,l6Config.layer,row, l6Config.level,l6Config.level.contains("/")?wrapTextStyle: dpnStyle);
			setValueAndStyle(writer,maxLayer+1,row,  l6Config.qty,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+2,row,  l6Config.descriptionInAgile,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+3,row,  l6Config.rev,wrapTextStyle);
			insertImg(writer.getWorkbook(), writer.getSheet(), l6Config.picture, maxLayer+4,row); 
			setValueAndStyle(writer,maxLayer+5,row,  l6Config.vendorName,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+6,row,  l6Config.vendorPN,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+7,row,  l6Config.hhpn,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+8,row,  l6Config.ppap,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+9,row,  l6Config.sDoCInAgile,wrapTextStyle);
			setValueAndStyle(writer,maxLayer+10,row, l6Config.specInAgile,wrapTextStyle);
			
			// 设置列宽
			writer.setRowHeight(row, 55);
			writer.setColumnWidth(0, 25);
			writer.setColumnWidth(maxLayer+1, 7);
			writer.setColumnWidth(maxLayer+2, 40);
			writer.setColumnWidth(maxLayer+3, 10);
			writer.setColumnWidth(maxLayer+5, 20);
			writer.setColumnWidth(maxLayer+6, 20);
			writer.setColumnWidth(maxLayer+7, 30);
			writer.setColumnWidth(maxLayer+9, 30);
			writer.setColumnWidth(maxLayer+10, 30);
		}
		
		// 冻结表头
		writer.getSheet().createFreezePane(0,headerRowNum+1);
	}
	
	static void writePTL(Pair<String, TCComponentBOMLine> pair,Map<Pair<String, TCComponentBOMLine>,List<L6Config>> l6ConfigMap,ExcelWriter writer,Map<String,CellStyle> styleMap) throws Exception {
		
		String sheetName = pair.getKey();
		boolean isPTL = sheetName.startsWith("PTL-");
		if(!isPTL) {
			return;
		}
//		sheetName = sheetName.substring(4);
		List<L6Config> l6ConfigList = l6ConfigMap.get(pair);
		if(!l6ConfigList.isEmpty()) {
			l6ConfigList.remove(0);	
		}
		Map<String,Integer> layerMap = new HashMap<String, Integer>();
		layerMap.put("max", 0);
		traversalMaxLayer(pair.getValue(),layerMap,0);
		int maxLayer = layerMap.get("max");
		writer.setSheet(sheetName);
		// 写Header
		CellStyle headerStyle = styleMap.get("headerStyle");
		CellStyle titleStyle = styleMap.get("titleStyle");
		CellStyle wrapTextStyle = styleMap.get("wrapTextStyle");
		CellStyle dpnStyle = styleMap.get("dpnStyle");
		int headerRowNum = 0;		
		for (int i = 0; i < maxLayer; i++) {
			if(i==0) {
				setValueAndStyle(writer,i, headerRowNum, "Assy",wrapTextStyle);
			}else {
				setValueAndStyle(writer,i, headerRowNum, "P/N",wrapTextStyle);
			}
			writer.getCell(i,headerRowNum).setCellStyle(headerStyle);
			writer.setColumnWidth(i, 20);
		}
		setValueAndStyle(writer,maxLayer, headerRowNum, "Description in Agile",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+1, headerRowNum, "Vendor Name",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+2, headerRowNum, "Vendor PN",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+3, headerRowNum, "HHPN",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+4, headerRowNum, "Rev",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+5, headerRowNum, "Qty",wrapTextStyle);
		setValueAndStyle(writer,maxLayer+6, headerRowNum, "Picture",wrapTextStyle);
		writer.setRowHeight(headerRowNum, 30);
		writer.getCell(maxLayer,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+1,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+2,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+3,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+4,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+5,headerRowNum).setCellStyle(headerStyle);
		writer.getCell(maxLayer+6,headerRowNum).setCellStyle(headerStyle);
		
		// Header 样式
		writer.setColumnWidth(maxLayer, 40);
		writer.setColumnWidth(maxLayer+1, 15);
		writer.setColumnWidth(maxLayer+2, 15);
		writer.setColumnWidth(maxLayer+3, 25);
		writer.setColumnWidth(maxLayer+4, 20);
		writer.setColumnWidth(maxLayer+5, 20);
		// 设置样式
		int groupBegin = 0;
		for (int i = 0; i < l6ConfigList.size(); i++) {
			L6Config l6Config = l6ConfigList.get(i);
			int row = i + headerRowNum + 1;
			if(l6Config.isFirst) {
				writer.merge(row,row,0,maxLayer+6,l6Config.title,false);
				for (int j = 0; j < maxLayer+7 ; j++) {
					writer.getCell(j,row).setCellStyle(titleStyle);
				}
				writer.setRowHeight(row, 40);
				groupBegin = row+1;
			}else if("empty".equals(l6Config.title)){
				for (int j = 0; j < maxLayer+6 ; j++) {
					writer.writeCellValue(j,row,"");
				}
				writer.merge(row,row,0,maxLayer+6,"",false);
				writer.setRowHeight(row, 15);
				writer.getSheet().groupRow(groupBegin,row-1);
				
			}else {
				setValueAndStyle(writer,l6Config.layer-2,row, l6Config.pn,l6Config.pn.contains("/")?wrapTextStyle: dpnStyle);
				setValueAndStyle(writer,maxLayer,row,  l6Config.descriptionInAgile,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+1,row,  l6Config.vendorName,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+2,row,  l6Config.vendorPN,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+3,row,  l6Config.hhpn,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+4,row,  l6Config.rev,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+5,row,  l6Config.qty,wrapTextStyle);
				setValueAndStyle(writer,maxLayer+6,row,  "",wrapTextStyle);
				insertImg(writer.getWorkbook(), writer.getSheet(), l6Config.picture, maxLayer+6,row); 
				writer.setRowHeight(row, 40);
			}
		}
		int gourpEnd = l6ConfigList.size()+headerRowNum;
		if(groupBegin < gourpEnd) {
			writer.getSheet().groupRow(groupBegin,gourpEnd);
		}
		// 冻结表头
		writer.getSheet().createFreezePane(0,1);
	}
	
	static void setValueAndStyle(ExcelWriter writer,int row,int col,String value,CellStyle style) {
		if("/".equals(value)) {
			value = "";
		}else if(value!=null){
			String[] split = value.split("\r\n");
			if(split.length > 1) {
				List<String> list = new ArrayList<String>();
				for(String s : split) {
					if("/".equals(s)||!list.contains(s)) {
						list.add(s);
					}
				}
				value = StrUtil.join("\r\n", list);
			}
		}
		
		writer.writeCellValue(row,col, value);
		writer.getCell(row,col).setCellStyle(style);
	}
	
	static String getProperty(TCComponentItemRevision itemRevision,String key) throws TCException {
		return getProperty(itemRevision,key,"/");
	}
	
	static String getProperty(TCComponentItemRevision itemRevision,String key,String defualtValue) throws TCException {
		String property = itemRevision.getProperty(key);
		if(StringUtils.isEmpty(property)) {
			return defualtValue;
		}
		return property;
	}
	
	static String getProperty(TCComponentBOMLine bomline,String key) throws TCException {
		return getProperty(bomline,key,"/");
	}
	
	static String getProperty(TCComponentBOMLine bomline,String key,String defualtValue) throws TCException {
		String property = bomline.getProperty(key);
		if(StringUtils.isEmpty(property)) {
			return defualtValue;
		}
		return property;
	}

}
