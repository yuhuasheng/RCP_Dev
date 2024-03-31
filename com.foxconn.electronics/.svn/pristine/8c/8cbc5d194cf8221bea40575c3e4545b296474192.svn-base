package com.foxconn.electronics.managementebom.export.changelist.rf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.electronics.managementebom.export.changelist.mnt.MntDCNChange;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMUpdateBean;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.ProgressBarThread;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soaictstubs.preferenceContext_s;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;


public class ExportRfChangeList implements Runnable{
	private TCComponentItemRevision itemRev;
	
	public ExportRfChangeList(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
	}

	@Override
	public void run() {
		TCComponent[] relatedComponents = null;
		TCComponent[] impackedItemComponents = null;
		String fileName = "";
		try {
			fileName = "BOM-ChangeList-" + itemRev.getProperty("object_name") + "-" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + ".xlsx";
			relatedComponents = itemRev.getRelatedComponents("CMHasSolutionItem");
			impackedItemComponents = itemRev.getRelatedComponents("CMHasImpactedItem");
		}catch (Exception e) {
			return;
		}
		Registry reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
		
		JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
        jFileChooser.setFileFilter(filter);
        
        jFileChooser.setSelectedFile(new File(fileName));
        int openDialog = jFileChooser.showSaveDialog(null);
        if (openDialog == JFileChooser.APPROVE_OPTION){
            File file = jFileChooser.getSelectedFile();
            String fname = jFileChooser.getName(file);
            if (fname.indexOf(".xlsx") == -1)
            {
                file = new File(jFileChooser.getCurrentDirectory(), fname);
            }
            if (CommonTools.isEmpty(file))
            {
                MessageBox.displayModalMessageBox(AIFDesktop.getActiveDesktop()
                                                            .getFrame(), "You must select a directory", "Warn", MessageBox.WARNING);
            }
            ProgressBarThread barThread = new ProgressBarThread(reg.getString("wait.MSG"), "RF ChangeList " + reg.getString("export.MSG"));
            try {
            	barThread.start();
            	List<EBOMLineBean> addList = new ArrayList<>();
        		List<EBOMLineBean> delList = new ArrayList<>();
        		List<EBOMUpdateBean> changeList = new ArrayList<>();
            	for (TCComponent solution : relatedComponents) {
    				TCComponentItemRevision solutionItemRev = (TCComponentItemRevision) solution;
    				String type = solutionItemRev.getTypeObject().getName();
    				if("DocumentRevision".equals(type)) {
    					continue;
    				}
    				String objName = solutionItemRev.getStringProperty("object_name");
    				TCComponentItemRevision previousRevision = null;
    				// 查詢受影響項是否有同名的
    				if(impackedItemComponents != null && impackedItemComponents.length > 0) {
    					List<TCComponent> list = Stream.of(impackedItemComponents).filter(item -> {
    						try {
    							TCComponentItemRevision itemRev = (TCComponentItemRevision) item;
    							String name = itemRev.getStringProperty("object_name");
    							return name.equals(objName);
    						}catch (Exception e) {
    							return false;
							}
    					}).collect(Collectors.toList());
    					if(CollUtil.isNotEmpty(list)) {
    						previousRevision = (TCComponentItemRevision) list.get(0);
    					}else {
    						previousRevision = TCUtil.getPreviousRevision(solutionItemRev);
    					}
    				}else {
    					previousRevision = TCUtil.getPreviousRevision(solutionItemRev);
    				}
    				if (previousRevision == null){
    	                continue;
    	            }
    				RfChangeListCompare change = new RfChangeListCompare(previousRevision, solutionItemRev);
    				change.compareBOM(new String[] {"description","qty" });
    				if(CollUtil.isNotEmpty(change.getAdd())) {
    					addList.addAll(change.getAdd());
    				}
    				if(CollUtil.isNotEmpty(change.getDels())) {
    					delList.addAll(change.getDels());
    				}
    				if(CollUtil.isNotEmpty(change.getChanges())) {
    					changeList.addAll(change.getChanges());
    				}
    			}
				InputStream inputStream = ExportRfChangeList.class.getResourceAsStream("/com/foxconn/electronics/managementebom/export/changelist/rf/template.xlsx");
				XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
				XSSFSheet sheet = workbook.getSheetAt(0);
				XSSFCellStyle cellStyle = initStyle(workbook,true);
				// 實際作者，創建時間，
				sheet.getRow(2).getCell(5).setCellValue(itemRev.getStringProperty("d9_ActualUserID"));
				sheet.getRow(2).getCell(7).setCellValue(DateUtil.formatDateTime(itemRev.getDateProperty("creation_date")));
				sheet.getRow(2).getCell(12).setCellValue(itemRev.getStringProperty("item_id"));
				int index = 6;
				if(CollUtil.isNotEmpty(delList)) {
					for(int i = 0; i< delList.size() ; i++){
						addRow(null,delList.get(i),sheet,index,cellStyle);
						index++;
					} 
				}
				if(CollUtil.isNotEmpty(addList)) {
					for(int i = 0; i< addList.size() ; i++){
						addRow(addList.get(i),null,sheet,index,cellStyle);
						index++;
					}
				}
				if(CollUtil.isNotEmpty(changeList)) {
					for(int i = 0; i< changeList.size() ; i++) {
						EBOMUpdateBean updateBean = changeList.get(i);
						addRow(updateBean.getNewEBomBean(),updateBean.getOldEBomBean(),sheet,index,cellStyle);
						index++;
					}
				}
				XSSFRow row = sheet.createRow(index);
				for(int i = 0;i< 16;i++) {
					setCellValue(row,i,cellStyle,"");
				}
				row.getCell(0).setCellValue("以下空白");
				FileOutputStream out = new FileOutputStream(file);
				workbook.write(out);
				out.flush();
				IoUtil.close(out);
                barThread.stopBar();
                TCUtil.infoMsgBox(reg.getString("exportSuccess.MSG"), reg.getString("INFORMATION.MSG"));
    		} catch (Exception e) {
    			e.printStackTrace();
    			barThread.stopBar();
    			TCUtil.infoMsgBox(reg.getString("exportFailure.MSG"), reg.getString("INFORMATION.MSG"));
    			return;
    		}
        }
	}
	
	
	private void addRow(EBOMLineBean newEBomBean,EBOMLineBean oldEBomBean,XSSFSheet sheet,int index,XSSFCellStyle cellStyle) {
		if(ObjectUtil.isNotNull(newEBomBean) && ObjectUtil.isNull(oldEBomBean)) {
			XSSFRow row = sheet.createRow(index);
			setCellValue(row,0,cellStyle,String.valueOf(index - 5));
			// Add
			for(int i = 1;i< 10;i++) {
				setCellValue(row,i,cellStyle,"");
			}
			setCellValue(row,10,cellStyle,"A");
			setCellValue(row,11,cellStyle,newEBomBean.getItem());
			setCellValue(row,12,cellStyle,newEBomBean.getDescription());
			setCellValue(row,13,cellStyle,newEBomBean.getUnit());
			setCellValue(row,14,cellStyle,newEBomBean.getQty());
			setCellValue(row,15,cellStyle,"");
		}else if(ObjectUtil.isNull(newEBomBean) && ObjectUtil.isNotNull(oldEBomBean)) {
			// Delete
			XSSFRow row = sheet.createRow(index);
			setCellValue(row,0,cellStyle,String.valueOf(index - 5));
			setCellValue(row,1,cellStyle,"");
			setCellValue(row,2,cellStyle,"D");
			setCellValue(row,3,cellStyle,oldEBomBean.getParentItem());
			setCellValue(row,4,cellStyle,oldEBomBean.getItem());
			setCellValue(row,5,cellStyle,oldEBomBean.getDescription());
			setCellValue(row,6,cellStyle,oldEBomBean.getUnit());
			setCellValue(row,7,cellStyle,oldEBomBean.getQty());
			for(int i = 8;i< 16;i++) {
				setCellValue(row,i,cellStyle,"");
			}
		}else {
			// Change
			XSSFRow row = sheet.createRow(index);
			setCellValue(row,0,cellStyle,String.valueOf(index - 5));
			setCellValue(row,1,cellStyle,"");
			setCellValue(row,2,cellStyle,"C");
			setCellValue(row,3,cellStyle,oldEBomBean.getParentItem());
			setCellValue(row,4,cellStyle,oldEBomBean.getItem());
			setCellValue(row,5,cellStyle,oldEBomBean.getDescription());
			setCellValue(row,6,cellStyle,oldEBomBean.getUnit());
			setCellValue(row,7,cellStyle,oldEBomBean.getQty());
			setCellValue(row,8,cellStyle,"");
			setCellValue(row,9,cellStyle,"");
			setCellValue(row,10,cellStyle,"C");
			setCellValue(row,11,cellStyle,newEBomBean.getItem());
			setCellValue(row,12,cellStyle,newEBomBean.getDescription());
			setCellValue(row,13,cellStyle,newEBomBean.getUnit());
			setCellValue(row,14,cellStyle,newEBomBean.getQty());
			setCellValue(row,15,cellStyle,"");
		}
	}

	private void setCellValue(XSSFRow row,int cellIndex,XSSFCellStyle cellStyle,String value) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(value);
	}
	
	
	private static XSSFCellStyle initStyle(XSSFWorkbook workbook, Boolean border) {
        XSSFCellStyle cellStyle = workbook.getCellStyleAt(0);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        if (border) {
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
        }
        return cellStyle;
    }
}
