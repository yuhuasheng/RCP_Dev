package com.foxconn.electronics.L10Ebom.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.electronics.managementebom.export.changelist.rf.ExportRfChangeList;
import com.foxconn.electronics.managementebom.export.changelist.rf.RfChangeListCompare;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMUpdateBean;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.ProgressBarThread;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class ExportMntL10BomAction implements Runnable{
	private TCComponentItemRevision itemRev;
	private String exportType;

	public ExportMntL10BomAction(TCComponentItemRevision itemRev, String exportType) {
		super();
		this.itemRev = itemRev;
		this.exportType = exportType;
	}



	@Override
	public void run() {
		String str = getFileName(exportType);
		String fileName = "";
		try {
			itemRev.refresh();
			fileName = itemRev.getProperty("object_name") + "_" + str + ".xlsx";
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
            ProgressBarThread barThread = new ProgressBarThread(reg.getString("wait.MSG"), str + " " + reg.getString("export.MSG"));
            try {
				barThread.start();
				BigExcelWriter writer = ExcelUtil.getBigWriter();
				if("FinishMatInfo".equals(exportType)) {
					writer.getWorkbook().setSheetName(0,"成品料號");
					// 獲取成品料號信息
					List<Map<String,Object>> rows = getFinishMatInfoRows();
					writer.write(rows, true);
				}else if("SemiFinishMatInfo".equals(exportType)) {
					// 獲取半成品料號信息
					writer.getWorkbook().setSheetName(0,"半成品料號");
					List<Map<String,Object>> list = getSemiFinishMatInfoRows();
					writer.write(list, true);
					CellStyle cellStyle = writer.getCellStyle();
			        cellStyle.setAlignment(HorizontalAlignment.CENTER);
			        cellStyle.setWrapText(true);
			        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			        cellStyle.setBorderBottom(BorderStyle.THIN);
			        cellStyle.setBorderLeft(BorderStyle.THIN);
			        cellStyle.setBorderTop(BorderStyle.THIN);
			        cellStyle.setBorderRight(BorderStyle.THIN);
			        writer.autoSizeColumnAll();
			        for (int i = 0; i < writer.getColumnCount(); i++) {
			            writer.getSheet().setColumnWidth(i, writer.getSheet().getColumnWidth(i) * 4 / 3);
			        }
					// 獲取BOM申請信息
					List<Map<String,Object>> list1 = getBOMApplyInfoRows();
					writer.setSheet("BOM申請信息");
					writer.write(list1, true);
				}else if("FinishBOMInfo".equals(exportType)) {
					writer.getWorkbook().setSheetName(0,"成品BOM");
					// 獲取成品BOM信息
					List<Map<String,Object>> rows = getFinishBOMInfoRows();
					writer.write(rows, true);
					CellStyle cellStyle = writer.getCellStyle();
			        cellStyle.setAlignment(HorizontalAlignment.CENTER);
			        cellStyle.setWrapText(true);
			        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			        cellStyle.setBorderBottom(BorderStyle.THIN);
			        cellStyle.setBorderLeft(BorderStyle.THIN);
			        cellStyle.setBorderTop(BorderStyle.THIN);
			        cellStyle.setBorderRight(BorderStyle.THIN);
			        writer.autoSizeColumnAll();
			        for (int i = 0; i < writer.getColumnCount(); i++) {
			            writer.getSheet().setColumnWidth(i, writer.getSheet().getColumnWidth(i) * 4 / 3);
			        }
			        // 獲取匯總信息
			        List<Map<String,Object>> list1 = getBOMSummaryInfoRows();
					writer.setSheet("BOM匯總信息");
					writer.write(list1, true);
				}
				CellStyle cellStyle = writer.getCellStyle();
		        cellStyle.setAlignment(HorizontalAlignment.CENTER);
		        cellStyle.setWrapText(true);
		        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		        cellStyle.setBorderBottom(BorderStyle.THIN);
		        cellStyle.setBorderLeft(BorderStyle.THIN);
		        cellStyle.setBorderTop(BorderStyle.THIN);
		        cellStyle.setBorderRight(BorderStyle.THIN);
		        writer.autoSizeColumnAll();
		        for (int i = 0; i < writer.getColumnCount(); i++) {
		            writer.getSheet().setColumnWidth(i, writer.getSheet().getColumnWidth(i) * 4 / 3);
		        }
		        FileOutputStream out = new FileOutputStream(file);
		        writer.flush(out);
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
	private List<Map<String,Object>> getBOMSummaryInfoRows() throws Exception{
		itemRev.refresh();
		TCComponent[] list = itemRev.getRelatedComponents("d9_BOMReqTable3");
		if(ObjectUtil.isNull(list) || list.length == 0) {
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("機種", "");
			map.put("panel廠商", "");
			map.put("出貨地", "");
			map.put("成品料號", "");
			map.put("Final總成料號", "");
			map.put("Panel總成料號", "");
			map.put("Packing總成料號", "");
			map.put("出貨優選方式尺寸", "");
			map.put("出貨優選方式形式", "");
			map.put("外部線材類型", "");
			return CollUtil.newArrayList(map);
		}
		TCComponent[] list1 = itemRev.getRelatedComponents("d9_BOMReqTable1");
		Map<String, String> areaMap = new HashMap<String, String>();
		for(int i = 0 ; i< list1.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list1[i];
			object.refresh();
			String finishPNDesc = object.getProperty("d9_FinishPNDesc");
			String shippingArea = object.getProperty("d9_ShippingArea");
			areaMap.put(finishPNDesc,shippingArea);
		}
		Map<String, Map<String,Object>> resMap = new HashMap<>();
		for(int i = 0 ; i< list.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list[i];
			object.refresh();
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("機種", "");
			map.put("panel廠商", "");
			map.put("出貨地", "");
			String finishPN = object.getProperty("d9_FinishPN");
			map.put("成品料號",finishPN);
			map.put("Final總成料號", "");
			map.put("Panel總成料號", "");
			map.put("Packing總成料號", "");
			String shipSize = object.getProperty("d9_ShipSize");
			map.put("出貨優選方式尺寸", shipSize);
			String shipType = object.getProperty("d9_ShipType");
			map.put("出貨優選方式形式", shipType);
			String wireType = object.getProperty("d9_WireType");
			map.put("外部線材類型", wireType);
			resMap.put(finishPN, map);
		}
		String itemIds = resMap.keySet().parallelStream().collect(Collectors.joining(";"));
		TCComponent[] executeQuery = TCUtil.executeQuery(itemRev.getSession(), "零组件版本...",  
				new String[]{"items_tag.item_id"},new String[] {itemIds});
		for(int i = 0; i< executeQuery.length;i++) {
			TCComponentItemRevision itemRevision = (TCComponentItemRevision) executeQuery[i];
			String itemId = itemRevision.getProperty("item_id");
			Map<String,Object> map = resMap.get(itemId);
			String desc = itemRevision.getProperty("d9_EnglishDescription");
			if(CollUtil.isNotEmpty(areaMap) && StrUtil.isNotBlank(areaMap.get(desc))) {
				map.put("出貨地", areaMap.get(desc));
			}
			TCComponentBOMLine bomLine = TCUtil.openBomWindow(itemRev.getSession(),itemRevision);
			AIFComponentContext[] childrens = bomLine.getChildren();
			for(int j = 0 ; j < childrens.length ; j++) {
				TCComponentBOMLine childBomLine =(TCComponentBOMLine) childrens[j].getComponent();
				String itemString = childBomLine.getProperty("bl_item_item_id");
				String type = childBomLine.getProperty("bl_item_object_type");
				if("Virtual Part".equals(type) && itemString.startsWith("71407")) {
					map.put("Final總成料號",itemString);
					AIFComponentContext[] lists = childBomLine.getChildren();
					for(AIFComponentContext items : lists) {
						TCComponentBOMLine endBomLine = (TCComponentBOMLine) items.getComponent();
						if("Virtual Part".equals(childBomLine.getProperty("bl_item_object_type")) && endBomLine.getProperty("bl_item_item_id").startsWith("71408")) {
							map.put("Panel總成料號", endBomLine.getProperty("bl_item_item_id"));
							break;
						}
					}
				}
				if("Virtual Part".equals(type) && itemString.startsWith("71300")) {
					map.put("Packing總成料號",itemString);
				}
			}
		}
		return resMap.values().parallelStream().collect(Collectors.toList());
	}
	
	private List<Map<String,Object>> getFinishBOMInfoRows() throws TCException{
		itemRev.refresh();
		TCComponent[] list = itemRev.getRelatedComponents("d9_BOMReqTable9");
		if(ObjectUtil.isNull(list) || list.length == 0) {
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("No", "");
			map.put("Shipments", "");
			map.put("Panel type", "");
			map.put("Finished product number", "");
			map.put("External wire type", "");
			map.put("Others", "");
			map.put("FINAL ASSY No", "");
			map.put("Packing ASSY No", "");
			map.put("P-BOM Time", "");
			map.put("BOM Release No.", "");
			return CollUtil.newArrayList(map);
		}
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i< list.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list[i];
			object.refresh();
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			String sequence = object.getProperty("d9_Sequence");
			map.put("No", sequence);
			String shippingArea = object.getProperty("d9_ShippingArea");
			map.put("Shipments", shippingArea);
			String panelStyle = object.getProperty("d9_PanelStyle");
			map.put("Panel type", panelStyle);
			String finishPN = object.getProperty("d9_FinishPN");
			map.put("Finished product number", finishPN);
			String wireType = object.getProperty("d9_WireType");
			map.put("External wire type", wireType);
			String other = object.getProperty("d9_Other");
			map.put("Others", other);
			String finalPN = object.getProperty("d9_FinalPN");
			map.put("FINAL ASSY No", finalPN);
			String pkgPN = object.getProperty("d9_PkgPN");
			map.put("Packing ASSY No", pkgPN);
			map.put("P-BOM Time", "");
			String BOMReleaseNo = object.getProperty("d9_BOMReleaseNo");
			map.put("BOM Release No.", BOMReleaseNo);
			resList.add(map);
		}
		return resList;
	}
	
	private List<Map<String,Object>> getBOMApplyInfoRows() throws TCException{
		itemRev.refresh();
		TCComponent[] list = itemRev.getRelatedComponents("d9_BOMReqTable3");
		if(ObjectUtil.isNull(list) || list.length == 0) {
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("項次", "");
			map.put("成品類別", "");
			map.put("成品料號", "");
			map.put("描述", "");
			map.put("Panel料號", "");
			map.put("外部線材類型", "");
			map.put("其他", "");
			map.put("出貨優選方式尺寸", "");
			map.put("出貨優選方式形式", "");
			map.put("參照BOM料號", "");
			map.put("備註", "");
			return CollUtil.newArrayList(map);
		}
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i< list.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list[i];
			object.refresh();
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			String sequence = object.getProperty("d9_Sequence");
			map.put("項次", sequence);
			String finishType = object.getProperty("d9_FinishType");
			map.put("成品類別", finishType);
			String finishPN = object.getProperty("d9_FinishPN");
			map.put("成品料號", finishPN);
			String desc = object.getProperty("d9_Desc");
			map.put("描述", desc);
			String panelPN = object.getProperty("d9_PanelPN");
			map.put("Panel料號", panelPN);
			String wireType = object.getProperty("d9_WireType");
			map.put("外部線材類型", wireType);
			String other = object.getProperty("d9_Other");
			map.put("其他", other);
			String shipSize = object.getProperty("d9_ShipSize");
			map.put("出貨優選方式尺寸", shipSize);
			String shipType = object.getProperty("d9_ShipType");
			map.put("出貨優選方式形式", shipType);
			String refMaterialPN = object.getProperty("d9_RefMaterialPN");
			map.put("參照BOM料號", refMaterialPN);
			String remark = object.getProperty("d9_Remark");
			map.put("備註", remark);
			resList.add(map);
		}
		return resList;
	}
	
	
	private List<Map<String,Object>> getSemiFinishMatInfoRows() throws TCException{
		itemRev.refresh();
		TCComponent[] list = itemRev.getRelatedComponents("d9_BOMReqTable2");
		if(ObjectUtil.isNull(list) || list.length == 0) {
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("項次", "");
			map.put("半成品類別", "");
			map.put("半成品料號", "");
			map.put("描述", "");
			map.put("備註", "");
			return CollUtil.newArrayList(map);
		}
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i< list.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list[i];
			object.refresh();
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			String sequence = object.getProperty("d9_Sequence");
			map.put("項次", sequence);
			String semiType = object.getProperty("d9_SemiType");
			map.put("半成品類別", semiType);
			String semiPN = object.getProperty("d9_SemiPN");
			map.put("半成品料號", semiPN);
			String desc = object.getProperty("d9_Desc");
			map.put("描述", desc);
			String remark = object.getProperty("d9_Remark");
			map.put("備註", remark);
			resList.add(map);
		}
		return resList;
		
	}
	
	
	private List<Map<String,Object>> getFinishMatInfoRows() throws TCException{
		itemRev.refresh();
		TCComponent[] list = itemRev.getRelatedComponents("d9_BOMReqTable1");
		
		if(ObjectUtil.isNull(list) || list.length == 0) {
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			map.put("項次", "");
			map.put("客戶 ModelName", "");
			map.put("Foxconn ModelName", "");
			map.put("成品料號描述", "");
			map.put("出貨地區", "");
			map.put("電源線類型", "");
			map.put("PCBA接口", "");
			map.put("有無喇叭", "");
			map.put("顏色", "");
			return CollUtil.newArrayList(map);
		}
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		for(int i = 0 ; i< list.length ; i++) {
			TCComponentFnd0TableRow object =(TCComponentFnd0TableRow) list[i];
			object.refresh();
			Map<String,Object> map = new LinkedHashMap<String, Object>();
			String sequence = object.getProperty("d9_Sequence");
			map.put("項次", sequence);
			String customerModelName = object.getProperty("d9_CustomerModelName");
			map.put("客戶 ModelName", customerModelName);
			String foxconnModelName = object.getProperty("d9_FoxconnModelName");
			map.put("Foxconn ModelName", foxconnModelName);
			String finishPNDesc = object.getProperty("d9_FinishPNDesc");
			map.put("成品料號描述", finishPNDesc);
			String shippingArea = object.getProperty("d9_ShippingArea");
			map.put("出貨地區", shippingArea);
			String powerLineType = object.getProperty("d9_PowerLineType");
			map.put("電源線類型", powerLineType);
			String PCBAInterface = object.getProperty("d9_PCBAInterface");
			map.put("PCBA接口", PCBAInterface);
			String isSpeaker = object.getProperty("d9_IsSpeaker");
			map.put("有無喇叭", isSpeaker);
			String color = object.getProperty("d9_Color");
			map.put("顏色", color);
			resList.add(map);
		}
		return resList;
	}

	
	private String getFileName(String exportType) {
		String res = "";
		switch (exportType) {
			case "FinishMatInfo":
				res = "成品料號";
				break;
			case "SemiFinishMatInfo":
				res = "半成品料號";
				break;
			case "FinishBOMInfo":
				res = "成品BOM";
				break;
			default:
				break;
		}
		return res;
	}
}
