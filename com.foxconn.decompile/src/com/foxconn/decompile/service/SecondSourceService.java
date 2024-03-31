package com.foxconn.decompile.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.foxconn.decompile.pojo.BomPojo;
import com.foxconn.decompile.pojo.CustomPNPojo;
import com.foxconn.decompile.util.ExcelUtil;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class SecondSourceService {


	/**
	   * 2ndSource 比对
	 * @param session
	 * @param coms
	 * @return
	 */
	public boolean  genChangeList(TCSession session ,TCComponent[] coms){	
			try {
			     for (int j = 0; j < coms.length; j++) {		    
				     if (!(coms[j] instanceof TCComponentItemRevision)) {
					    continue;
				     }
				     handlleCompare((TCComponentItemRevision)coms[j]);
				         
			    }
		   }catch(Exception e) {
	 		   MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
	 		   return false;
	      }	
			
		 return true;
		
	}
	
	
	private void handlleCompare(TCComponentItemRevision currItemRev)throws Exception {
		TCComponentItemRevision preItemRev=null;
		String currVersion = currItemRev.getProperty("item_revision_id");
		
		TCComponent[] revions = currItemRev.getItem().getRelatedComponents("revision_list");
		if (revions != null && revions.length > 1) {
			for (int i = 0; i < revions.length; i++) {
				TCComponentItemRevision itemRev = (TCComponentItemRevision) revions[i];
				String version = itemRev.getProperty("item_revision_id");
				if(version.equalsIgnoreCase(currVersion)) {
					preItemRev=(TCComponentItemRevision) revions[i-1];
				}
			}
		}
		
		List<BomPojo> currBoms= new ArrayList<>();
		List<BomPojo> preBoms= new ArrayList<>();
		TCComponentBOMLine topLine=TCUtil.openBomWindow(currItemRev.getSession(), currItemRev);
		AIFComponentContext[] children = topLine.getChildren();
		for (AIFComponentContext aifComponentContext : children) {
			InterfaceAIFComponent component = aifComponentContext.getComponent();
			if (!(component instanceof TCComponentBOMLine)) {
				continue;
			}
			BomPojo b=  new BomPojo();
			TCComponentBOMLine childBomLine = (TCComponentBOMLine) component;
			TCComponentItemRevision itemRevision = childBomLine.getItemRevision();
			String itemId = itemRevision.getProperty("item_id");
			b.setItemNumber(itemId);
			
		    b.setParentNumber(currItemRev.getProperty("item_id"));
			String version = currItemRev.getProperty("item_revision_id");
			b.setVersion(version);
			
			String manufacturerPN = itemRevision.getProperty("d9_ManufacturerPN");
			b.setMfgPN(manufacturerPN);
			
			String descr = itemRevision.getProperty("d9_EnglishDescription");
			b.setDescr(descr);
			
			String mfg = itemRevision.getProperty("d9_ManufacturerID");
			b.setMfg(mfg);
			
			
			String findNum = childBomLine.getProperty("bl_sequence_no");
            b.setFindNum(findNum);
            currBoms.add(b);			
		}
		if(preItemRev!=null) {
			topLine=TCUtil.openBomWindow(preItemRev.getSession(), preItemRev);
			children = topLine.getChildren();
			for (AIFComponentContext aifComponentContext : children) {
				InterfaceAIFComponent component = aifComponentContext.getComponent();
				if (!(component instanceof TCComponentBOMLine)) {
					continue;
				}
				BomPojo b=  new BomPojo();
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) component;
				TCComponentItemRevision itemRevision = childBomLine.getItemRevision();
				String itemId = itemRevision.getProperty("item_id");
				b.setItemNumber(itemId);
				
			    b.setParentNumber(preItemRev.getProperty("item_id"));
			    
				String version = preItemRev.getProperty("item_revision_id");
				b.setVersion(version);
				
				String manufacturerPN = itemRevision.getProperty("d9_ManufacturerPN");
				b.setMfgPN(manufacturerPN);
				
				String descr = itemRevision.getProperty("d9_EnglishDescription");
				b.setDescr(descr);
				
				String mfg = itemRevision.getProperty("d9_ManufacturerID");
				b.setMfg(mfg);
				
				String findNum = childBomLine.getProperty("bl_sequence_no");
	            b.setFindNum(findNum);
	            preBoms.add(b);			
			}
		}
		for(BomPojo c:currBoms) {
			String itemId=c.getItemNumber();
			int f=0;
			for(BomPojo p: preBoms) {
				if(p.getItemNumber().equalsIgnoreCase(itemId)) {
					f=1;
					break;
				}
			}
			if(f==0) {
				c.setAction("A");
			}
		}

		for(BomPojo p:preBoms ) {
			String itemId=p.getItemNumber();
			int f=0;
			for(BomPojo c: currBoms) {
				if(c.getItemNumber().equalsIgnoreCase(itemId)) {
					f=1;
					break;
				}
			}
			if(f==0) {
				p.setAction("D");
			}
		}
		
		for(BomPojo p:preBoms ) {
			System.out.println(p.getVersion()+" "+p.getItemNumber()+"==============>"+p.getAction());
		}
		
		for(BomPojo p:currBoms ) {
			System.out.println(p.getVersion()+" "+p.getItemNumber()+"==============>"+p.getAction());
		}
		if(currBoms.size()<=0&&preBoms.size()<=0) {
			return;
		}
		String path=ExcportChaneList(currBoms,preBoms);
		if(path==null||"".equalsIgnoreCase(path)) {
			return;	
		}
		//path="C:\\Users\\86134\\AppData\\Local\\Temp\\121.txt";
		String dsName=path.substring(path.lastIndexOf(File.separator)+1, path.lastIndexOf("."));
		System.out.println("file name =======>"+dsName);
		TCComponentDataset dataSet= TCUtil.createDataSet(currItemRev.getSession(), path, "MSExcelX", dsName, "excel");
		//TCComponentDataset dataSet= TCUtil.createDataSet(currItemRev.getSession(), path, "Text", dsName, "Text");
		
		if(dataSet!=null) {
			TCComponent[] datesets = currItemRev.getRelatedComponents("IMAN_specification");
			for (TCComponent tcComponent : datesets) {
				    if (!(tcComponent instanceof TCComponentDataset)) {
					    continue; 
				    }
				    int f=0;
					TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
					for (String fileName : tcComponentDataset.getFileNames(null)) {
						if (fileName.contains("_changeList_")) {
							f=1;
							break;
						}
					}
				   if(f==1) {
					   currItemRev.remove("IMAN_specification", tcComponentDataset);
				   }
			}
			System.out.println("===================+++++");
			currItemRev.add("IMAN_specification", dataSet);
			
		}
		
		//
	}
	
	private String  ExcportChaneList(List<BomPojo> currBoms,List<BomPojo> preBoms)throws Exception {
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		  String path="";
		   if(currBoms.size()>0) {
		        BomPojo b=currBoms.get(0);
		        path = System.getenv("TEMP") + File.separator + b.getParentNumber()+"_REV_"+b.getVersion()+"_changeList_"+sdf.format(new Date()) + ".xlsx";
		        System.out.println(path);
		   }else if(preBoms.size()>0) {
			    BomPojo b=preBoms.get(0);
		        path = System.getenv("TEMP") + File.separator + b.getParentNumber()+"_REV_"+b.getVersion()+"_changeList_"+sdf.format(new Date()) + ".xlsx";
		        System.out.println(path);
		   }else {
			  return path; 
		   }
		   ExcelUtil util = new ExcelUtil();
		   Workbook wb = util.getWorkbook("com/foxconn/decompile/template/2ndChangeList.xlsx");
		   Sheet sheet = wb.getSheetAt(0);	
		   if(currBoms.size()>0) {
		      sheet.getRow(3).getCell(1).setCellValue(currBoms.get(0).getVersion());		  		
		      sheet.getRow(2).getCell(2).setCellValue(currBoms.get(0).getParentNumber());
		   }
		   if(preBoms.size()>0) {
		       sheet.getRow(3).getCell(6).setCellValue(preBoms.get(0).getVersion());
		       sheet.getRow(2).getCell(2).setCellValue(preBoms.get(0).getParentNumber());
		   }
		   
		   HashMap<String,Map<String,BomPojo>> mp= new HashMap<String,Map<String,BomPojo>>();
		   for(BomPojo bom:currBoms) {
			  String f= bom.getFindNum();
			  if(mp.get(f)==null) {
				  Map<String,BomPojo> m=new HashMap<String,BomPojo>();
				  mp.put(f, m);
				  m.put("curr", bom);
			  }else {
				  mp.get(f).put("curr", bom);
			  }			   
		   }
		   
		   for(BomPojo bom:preBoms) {
				String f= bom.getFindNum();
				if(mp.get(f)==null) {
					Map<String,BomPojo> m=new HashMap<String,BomPojo>();
					mp.put(f, m);
					m.put("pre", bom);
				 }else {
					mp.get(f).put("pre", bom);
				 }			   
		   }
		   List<String> findnums=new ArrayList<>();
		   Set<String> keys=mp.keySet();
		   for(String key:keys) {
			   findnums.add(key);
		   }
		   
		   Collections.sort(findnums, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
						 return o1.compareTo(o2); 
				}});
		   
		   int i=5;
		   for(String find:findnums) {
			   Row row=null;
			   try {
				 row=sheet.getRow(i);
			   }catch(Exception e) {}
			   if(row==null) {
			    row =sheet.createRow(i);
			   }
			   
			   Map<String,BomPojo> boms=mp.get(find);
			   BomPojo currBom= boms.get("curr");
			   BomPojo preBom= boms.get("pre");
			   if(currBom==null) {
				   currBom= new BomPojo();
			   }
			   if(preBom==null) {
				   preBom= new BomPojo();
			   }
			  
			   String action=currBom.getAction();
			   CellStyle cellStyle=getCellStyleFont(wb);
			   if("A".equalsIgnoreCase(action)) {
				  cellStyle=getCellStyleFontRed(wb);
			   }
			   Cell cell=row.createCell(0);
			   cell.setCellStyle(cellStyle);
			   cell.setCellValue(currBom.getItemNumber());
				  
				Cell cell1=row.createCell(1);
				cell1.setCellStyle(cellStyle);
				cell1.setCellValue(currBom.getFindNum());
				   		   
			    Cell cell2=row.createCell(2);
				cell2.setCellStyle(cellStyle);
			    cell2.setCellValue(currBom.getDescr());
				   
				Cell cell3=row.createCell(3);
				cell3.setCellStyle(cellStyle);
				cell3.setCellValue(currBom.getMfg());
				   			   
				Cell cell4=row.createCell(4);
			    cell4.setCellStyle(cellStyle);
			    cell4.setCellValue(currBom.getMfgPN());
				   
		
				action=preBom.getAction();
				cellStyle=getCellStyleFont(wb);
				if("D".equalsIgnoreCase(action)) {
					   cellStyle=getCellStyleFontDelete(wb);
				 }
				cell=row.createCell(5);
				cell.setCellStyle(cellStyle);
			    cell.setCellValue(preBom.getItemNumber());
				  
				cell1=row.createCell(6);
				cell1.setCellStyle(cellStyle);
				cell1.setCellValue(preBom.getFindNum());
				   			   
				cell2=row.createCell(7);
				cell2.setCellStyle(cellStyle);
				cell2.setCellValue(preBom.getDescr());
				   
				cell3=row.createCell(8);
				cell3.setCellStyle(cellStyle);
				cell3.setCellValue(preBom.getMfg());   
				   
				 cell4=row.createCell(9);
				cell4.setCellStyle(cellStyle);
				cell4.setCellValue(preBom.getMfgPN());				   
			   
			   i++;
		   }
		   FileOutputStream fileOutputStream= new FileOutputStream(new File(path));
           wb.write(fileOutputStream);
           wb.close();
           fileOutputStream.close();
           fileOutputStream.flush();
           return path;
	}
	
	
	
	 private   CellStyle getCellStyle(Workbook workbook)
	    {
	        CellStyle cellStyle = workbook.createCellStyle();
	        cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
	        cellStyle.setBorderLeft(BorderStyle.THIN);// 左边框
	        cellStyle.setBorderTop(BorderStyle.THIN);// 上边框
	        cellStyle.setBorderRight(BorderStyle.THIN);// 右边框
	        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        return cellStyle;
	    }

	 private CellStyle getCellStyleFont(Workbook wb)
	    {
	        CellStyle style = getCellStyle(wb);
	        Font font = wb.createFont();
	        font.setFontHeightInPoints((short) 10);
	        font.setFontName("宋体");
	        style.setFont(font);
	        style.setWrapText(true);
	        return style;
	    }

	 private CellStyle getCellStyleFontRed(Workbook wb)
	    {
	        CellStyle style = getCellStyle(wb);
	        Font font = wb.createFont();
	        font.setFontHeightInPoints((short) 10);
	        font.setFontName("宋体");
	        font.setColor(Font.COLOR_RED);	        
	        style.setFont(font);
	        style.setWrapText(true);        
	        return style;
	    }

	 
	 private CellStyle getCellStyleFontDelete(Workbook wb)
	    {
	        CellStyle style = getCellStyle(wb);
	        Font font = wb.createFont();
	        font.setFontHeightInPoints((short) 10);
	        font.setFontName("宋体");
	        font.setColor(Font.COLOR_RED);	  
	        font.setStrikeout(true);
	        style.setFont(font);
	        style.setWrapText(true);        
	        return style;
	    }
	 
	
}
