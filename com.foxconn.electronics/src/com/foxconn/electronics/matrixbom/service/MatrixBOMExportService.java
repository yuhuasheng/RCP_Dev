package com.foxconn.electronics.matrixbom.service;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.domain.PicBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.domain.VariableBOMBean;
import com.foxconn.electronics.matrixbom.export.DTLenovoCableMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoCoverGasketCableMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoPSUListExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoScrewMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoThermalMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.IMatrixBOMExportWriter;
import com.foxconn.electronics.matrixbom.export.MatrixBOMExportBean;
import com.foxconn.electronics.matrixbom.export.PackingMatrixExport;
import com.foxconn.electronics.matrixbom.export.PackingMatrixExport20230825;
import com.foxconn.electronics.matrixbom.export.domain.DTLenovoPSUBean;
import com.foxconn.electronics.matrixbom.export.domain.DTThermalBean;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.constant.DatasetEnum;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamdev.jxbrowser.deps.org.checkerframework.checker.units.qual.s;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.poi.excel.ExcelWriter;
/**
 * 002349
 * 001375
 * DCN-000163
 * @author MW00343
 *
 */
public class MatrixBOMExportService {
	
	private static MatrixBOMService matrixBOMService;
	
	public static void setMatrixBOMService(MatrixBOMService service) {
		matrixBOMService = service;
	}
	
	public static void setMatrixBOMService(AbstractAIFUIApplication app) throws TCException {
		matrixBOMService = new MatrixBOMService(app);
	}

	 static String matrixType = null;
	
	 static Map<String, IMatrixBOMExportWriter> matrixTypeExcelTemplateMap = new HashMap<String, IMatrixBOMExportWriter>() {
		private static final long serialVersionUID = 1L;
		{
			put("DT Lenovo Cable Matrix", new DTLenovoCableMatrixExcelWriter());
			put("DT Lenovo Screw Matrix", new DTLenovoScrewMatrixExcelWriter());
			put("DT Lenovo Cover&Gasket&Cable tie Matrix", new DTLenovoCoverGasketCableMatrixExcelWriter());
			put("DT Lenovo Thermal Matrix", new DTLenovoThermalMatrixExcelWriter());
//			put("DT Lenovo PSU List", "DT Lenovo PSU List Change List.xlsx");
//			put("MNT Packing Matrix", new MNTPackingMatrixExcelWriter());
			
			put("DT Lenovo PSU List", new DTLenovoPSUListExcelWriter());
//			put("MNT Packing Matrix", new PackingMatrixExport());
			put("MNT Packing Matrix", new PackingMatrixExport20230825());
		}
    };
    
    @Deprecated
    public static String exportBOMExcel_Deprecated(String uid) throws Exception {
    	TCComponentBOMWindow window = null;
    	String excelPath = null;
    	try {
    		TCSession session = com.foxconn.tcutils.util.TCUtil.getTCSession();
        	com.foxconn.tcutils.util.TCUtil.setBypass(session);
        	TCComponent targetComponent = com.foxconn.tcutils.util.TCUtil.findItemRevistion(uid);
    		TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision)targetComponent;
    		String matrixVer = matrixItemRevision.getProperty("item_revision_id");
    		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
    		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {			
    			return null;
    		}
    		
    		IMatrixBOMExportWriter exportWriter = matrixTypeExcelTemplateMap.get(matrixType);
    		if (exportWriter instanceof DTLenovoPSUListExcelWriter) {
    			DTLenovoPSUListExcelWriter DTLenovoPSUExportWriter = (DTLenovoPSUListExcelWriter) exportWriter;
    			List<DTLenovoPSUBean> materialList = DTLenovoPSUExportWriter.getMaterialList(session, matrixItemRevision);
    			if (CommonTools.isEmpty(materialList)) {
    				return null;
				}
    			excelPath = DTLenovoPSUExportWriter.exportExcel(matrixItemRevision, materialList);    			
    		} else if (exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
				DTLenovoThermalMatrixExcelWriter lenovoThermalMatrixExcelWriter = (DTLenovoThermalMatrixExcelWriter)exportWriter;
				AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
				TCComponentItem hoderItem = null;
	    		if(related.length == 0) {
	    			// 没有Holder，返回
	    			throw new Exception("没有任何数据");
	    		} else {
	    			hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
	    		}
	    		
	    		window = com.foxconn.tcutils.util.TCUtil.createBOMWindow(session);
	    		TCComponentBOMLine topBomLine = com.foxconn.tcutils.util.TCUtil.getTopBomline(window, hoderItem);
	    		// 收集所有的变体
	    		ArrayList<String> varList = new ArrayList<String>();	
	    		AIFComponentContext[] children = topBomLine.getChildren();
	    		for(AIFComponentContext context : children) {
	    			TCComponentBOMLine child = ((TCComponentBOMLine)context.getComponent());
	    			String varName = child.getItem().getProperty("item_id");
	    			varName += "/" + child.getItem().getProperty("object_name");
	    			varList.add(varName);
	    		}
	    		
	    		List<MatrixBOMExportBean> beanList = lenovoThermalMatrixExcelWriter.getBeanList(matrixBOMService,uid,varList);
	    		if(beanList.isEmpty()) {
	    			throw new Exception("没有任何数据");
	    		}
	    		
//				List<DTThermalBean> variableList = dtLenovoThermalMatrixExcelWriter.getVariableList(session, matrixItemRevision);
//				if (CommonTools.isEmpty(variableList)) {
//					return null;
//				}
//				excelPath = dtLenovoThermalMatrixExcelWriter.exportExcel(matrixItemRevision, variableList);
			} else if (exportWriter instanceof PackingMatrixExport) {
				PackingMatrixExport packingMatrixExport = (PackingMatrixExport)exportWriter;
				//packingMatrixExport.writerBOM(matrixItemRevision);
				excelPath = packingMatrixExport.exportBOM(matrixItemRevision);
			} else if (exportWriter instanceof DTLenovoCableMatrixExcelWriter) {
				
				DTLenovoCableMatrixExcelWriter cableMatrixExcelWriter = (DTLenovoCableMatrixExcelWriter) exportWriter;
				AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
	    		TCComponentItem hoderItem;
	    		if(related.length == 0) {
	    			// 没有Holder，返回
	    			throw new Exception("没有任何数据");
	    		}else {
	    			hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
	    		}
	    		
	    		window = com.foxconn.tcutils.util.TCUtil.createBOMWindow(session);
	    		TCComponentBOMLine topBomLine = com.foxconn.tcutils.util.TCUtil.getTopBomline(window, hoderItem);
	    		
	    		// 收集所有的变体
	    		ArrayList<String> varList = new ArrayList<String>();	
	    		AIFComponentContext[] children = topBomLine.getChildren();
	    		for(AIFComponentContext context : children) {
	    			TCComponentBOMLine child = ((TCComponentBOMLine)context.getComponent());
	    			String varName = child.getItem().getProperty("item_id");
	    			varName += "/" + child.getItem().getProperty("object_name");
	    			varList.add(varName);
	    		}
	    		
	    		
	    		List<MatrixBOMExportBean> beanList = cableMatrixExcelWriter.getBeanList(matrixBOMService,uid,varList);
	    		if(beanList.isEmpty()) {
	    			throw new Exception("没有任何数据");
	    		}
	    		
	    		// 写Excel
	    		excelPath = cableMatrixExcelWriter.writerBOM(matrixItemRevision,beanList,varList);
				
			} else if (exportWriter instanceof DTLenovoScrewMatrixExcelWriter) {
				
				DTLenovoScrewMatrixExcelWriter cableMatrixExcelWriter = (DTLenovoScrewMatrixExcelWriter) exportWriter;
				AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
	    		TCComponentItem hoderItem;
	    		if(related.length == 0) {
	    			// 没有Holder，返回
	    			throw new Exception("没有任何数据");
	    		}else {
	    			hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
	    		}
	    		
	    		window = com.foxconn.tcutils.util.TCUtil.createBOMWindow(session);
	    		TCComponentBOMLine topBomLine = com.foxconn.tcutils.util.TCUtil.getTopBomline(window, hoderItem);
	    		
	    		// 收集所有的变体
	    		ArrayList<String> varList = new ArrayList<String>();	
	    		AIFComponentContext[] children = topBomLine.getChildren();
	    		for(AIFComponentContext context : children) {
	    			TCComponentBOMLine child = ((TCComponentBOMLine)context.getComponent());
	    			String varName = child.getItem().getProperty("item_id");
	    			varName += "/" + child.getItem().getProperty("object_name");
	    			varList.add(varName);
	    		}
	    		
	    		
	    		List<MatrixBOMExportBean> beanList = cableMatrixExcelWriter.getBeanList(matrixBOMService,uid,varList);
	    		if(beanList.isEmpty()) {
	    			throw new Exception("没有任何数据");
	    		}
	    		
	    		// 写Excel
	    		excelPath = cableMatrixExcelWriter.writerBOM(matrixItemRevision,beanList,varList);
				
			} else {
				AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
	    		TCComponentItem hoderItem;
	    		if(related.length == 0) {
	    			// 没有Holder，返回
	    			throw new Exception("没有任何数据");
	    		}else {
	    			hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
	    		}
	    		
	    		window = com.foxconn.tcutils.util.TCUtil.createBOMWindow(session);
	    		TCComponentBOMLine topBomLine = com.foxconn.tcutils.util.TCUtil.getTopBomline(window, hoderItem);
//	    		TCComponentBOMLine topBomLine = TCUtil.openBomWindow(TCUtil.getTCSession(), hoderItem.getLatestItemRevision());
	    		
	    		// 收集所有的变体
	    		ArrayList<String> varList = new ArrayList<String>();	
	    		AIFComponentContext[] children = topBomLine.getChildren();
	    		for(AIFComponentContext context : children) {
	    			TCComponentBOMLine child = ((TCComponentBOMLine)context.getComponent());
	    			String varName = child.getItem().getProperty("item_id");
	    			varName += "/" + child.getItem().getProperty("object_name");
	    			varList.add(varName);
	    		}
	    		List<MatrixBOMExportBean> beanList = getBeanList(uid,varList);
	    		if(beanList.isEmpty()) {
	    			throw new Exception("没有任何数据");
	    		}
	    		// 写Excel
//	    		exportWriter = matrixTypeExcelTemplateMap.get(matrixType);
//	    		if(exportWriter instanceof MNTPackingMatrixExcelWriter) {
//	    			((MNTPackingMatrixExcelWriter)exportWriter).getBeanList(matrixItemRevision, beanList);
//	    		} else 
	    		
	    		if (exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
					((DTLenovoThermalMatrixExcelWriter)exportWriter).sortBeanList(beanList);
				}
	    		
	    		excelPath = exportWriter.writerBOM(matrixItemRevision,beanList,varList);
			}
    		
    		System.out.println("==>> excelPath: " + excelPath);	
    		if (CommonTools.isNotEmpty(excelPath)) {
    			excelPath = generateDocument(session, matrixItemRevision, excelPath, matrixVer);			
    		}
    		
    		com.foxconn.tcutils.util.TCUtil.closeBypass(session); // 关闭旁路
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (window != null) {
				window.close();
			}
		}  
    	return excelPath;
    }

	public static String exportBOMExcel(String uid) throws Exception {
    	TCComponentBOMWindow window = null;
    	String excelPath = null;
    	TCSession session = com.foxconn.tcutils.util.TCUtil.getTCSession();
    	com.foxconn.tcutils.util.TCUtil.setBypass(session);
    	
    	try {
        	TCComponent targetComponent = com.foxconn.tcutils.util.TCUtil.findItemRevistion(uid);
    		TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision)targetComponent;
    		String matrixVer = matrixItemRevision.getProperty("item_revision_id");
    		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
    		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {			
    			return null;
    		}
    		
    		IMatrixBOMExportWriter exportWriter = matrixTypeExcelTemplateMap.get(matrixType);
    		if (exportWriter instanceof DTLenovoPSUListExcelWriter) {
    			DTLenovoPSUListExcelWriter DTLenovoPSUExportWriter = (DTLenovoPSUListExcelWriter) exportWriter;
    			List<DTLenovoPSUBean> materialList = DTLenovoPSUExportWriter.getMaterialList(session, matrixItemRevision);
    			if (CommonTools.isEmpty(materialList)) {
    				return null;
				}
    			excelPath = DTLenovoPSUExportWriter.exportExcel(matrixItemRevision, materialList);    			
    		} 
//    		else if (exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
//				DTLenovoThermalMatrixExcelWriter dtLenovoThermalMatrixExcelWriter = (DTLenovoThermalMatrixExcelWriter)exportWriter;
//				List<DTThermalBean> variableList = dtLenovoThermalMatrixExcelWriter.getVariableList(session, matrixItemRevision);
//				if (CommonTools.isEmpty(variableList)) {
//					return null;
//				}
//				excelPath = dtLenovoThermalMatrixExcelWriter.exportExcel(matrixItemRevision, variableList);
//			} 
    		else if (exportWriter instanceof PackingMatrixExport20230825) {
    			PackingMatrixExport20230825 packingMatrixExport = (PackingMatrixExport20230825)exportWriter;
				//packingMatrixExport.writerBOM(matrixItemRevision);
				excelPath = packingMatrixExport.exportBOM(matrixItemRevision);
			} else {
				AIFComponentContext[] related = matrixItemRevision.getRelated("D9_HasVariantHolder_REL");
	    		TCComponentItem hoderItem;
	    		if(related.length == 0) {
	    			// 没有Holder，返回
	    			throw new Exception("没有任何数据");
	    		}else {
	    			hoderItem = ((TCComponentItemRevision) related[0].getComponent()).getItem();
	    		}
	    		
	    		window = com.foxconn.tcutils.util.TCUtil.createBOMWindow(session);
	    		TCComponentBOMLine topBomLine = com.foxconn.tcutils.util.TCUtil.getTopBomline(window, hoderItem);
	    		
	    		// 收集所有的变体
	    		ArrayList<String> varList = new ArrayList<String>();	
	    		AIFComponentContext[] children = topBomLine.getChildren();
	    		for(AIFComponentContext context : children) {
	    			TCComponentBOMLine child = ((TCComponentBOMLine)context.getComponent());
	    			String varName = child.getItem().getProperty("item_id");
	    			varName += "/" + child.getItem().getProperty("object_name");
	    			varList.add(varName);
	    		}
	    		
				if (exportWriter instanceof DTLenovoCableMatrixExcelWriter) {
					DTLenovoCableMatrixExcelWriter cableMatrixExcelWriter = (DTLenovoCableMatrixExcelWriter) exportWriter;
		    		List<MatrixBOMExportBean> beanList = cableMatrixExcelWriter.getBeanList(matrixBOMService,uid,varList);
		    		if(beanList.isEmpty()) {
		    			throw new Exception("没有任何数据");
		    		}
		    		// 写Excel
		    		excelPath = cableMatrixExcelWriter.writerBOM(matrixItemRevision,beanList,varList);
					
				} else if (exportWriter instanceof DTLenovoScrewMatrixExcelWriter) {
					
					DTLenovoScrewMatrixExcelWriter cableMatrixExcelWriter = (DTLenovoScrewMatrixExcelWriter) exportWriter;
		    		List<MatrixBOMExportBean> beanList = cableMatrixExcelWriter.getBeanList(matrixBOMService,uid,varList);
		    		if(beanList.isEmpty()) {
		    			throw new Exception("没有任何数据");
		    		}
		    		// 写Excel
		    		excelPath = cableMatrixExcelWriter.writerBOM(matrixItemRevision,beanList,varList);
					
				} else if (exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
					DTLenovoThermalMatrixExcelWriter lenovoThermalMatrixExcelWriter = (DTLenovoThermalMatrixExcelWriter)exportWriter;
					List<MatrixBOMExportBean> beanList = lenovoThermalMatrixExcelWriter.getBeanList(matrixBOMService, uid, varList);
					if(beanList.isEmpty()) {
		    			throw new Exception("没有任何数据");
		    		}					
					excelPath = lenovoThermalMatrixExcelWriter.writerBOM(matrixItemRevision, beanList, varList);
				} else {
		    		List<MatrixBOMExportBean> beanList = getBeanList(uid,varList);
		    		if(beanList.isEmpty()) {
		    			throw new Exception("没有任何数据");
		    		}
		    		if (exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
						((DTLenovoThermalMatrixExcelWriter)exportWriter).sortBeanList(beanList);
					}
		    		// 写Excel
		    		excelPath = exportWriter.writerBOM(matrixItemRevision,beanList,varList);
				}
			}
    		
    		System.out.println("=>> excelPath: " + excelPath);
    		if (CommonTools.isNotEmpty(excelPath)) {
    			excelPath = generateDocument(session, matrixItemRevision, excelPath, matrixVer);
    		}   		
    		
    		if (exportWriter instanceof DTLenovoCableMatrixExcelWriter || exportWriter instanceof DTLenovoScrewMatrixExcelWriter 
    				|| exportWriter instanceof DTLenovoCoverGasketCableMatrixExcelWriter || exportWriter instanceof DTLenovoThermalMatrixExcelWriter) {
				exportWriter.setHistory(session, matrixItemRevision, excelPath);
    		}
    		
    		com.foxconn.tcutils.util.TCUtil.closeBypass(session); // 关闭旁路
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			com.foxconn.tcutils.util.TCUtil.closeBypass(session);
			if (window != null) {
				window.close();
			}
		}  
    	return excelPath;
    }
 
	public static void exportDCNExcel(TCComponent targetComponent) throws Exception {
		/**
		 * 注意：假定DCN中只有变体
		 */
		String dcnNo = targetComponent.getProperty("item_id");
		AIFComponentContext[] solutionItems = targetComponent.getRelated("CMHasSolutionItem");
		if(solutionItems.length == 0) {
			throw new Exception("没有任何更改");
		}
		TCComponentItemRevision matrixItemRevision = null;
		for(AIFComponentContext context:solutionItems) {
			AIFComponentContext[] related = ((TCComponentItemRevision)context.getComponent()).whereReferencedByTypeRelation(new String[] {"D9_ProductLineRevision"},new String[] {"D9_HasVariants_REL"});;
			if(related.length != 0) {
				matrixItemRevision = (TCComponentItemRevision) related[0].getComponent();
			}
		}
		if(matrixItemRevision == null) {
			throw new Exception("没有找到Matrix，请检查");
		}		
		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {
			throw new Exception("不支持的Matrix类型");
		}
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AIFComponentContext[] impactedItems = targetComponent.getRelated("CMHasImpactedItem");
		for (int i = 0; i < impactedItems.length; i++) {
			TCComponentItemRevision impactedItem = (TCComponentItemRevision) impactedItems[i].getComponent();
			String itemId = impactedItem.getProperty("item_id");
			InterfaceAIFComponent solutionItem = findNextRevision(solutionItems, itemId);
			if(solutionItem==null) {
				continue;
			}
			String property = impactedItem.getProperty("item_id");
			System.out.println(property);
			if(!"TC@000000001114".equals(property)) {
//				continue;
			}
			comparison(impactedItem, (TCComponentItemRevision)solutionItem, list);
		}
		if(list.isEmpty()) {
			throw new Exception("没有任何更改");
		}
		list.sort(new Comparator<MatrixBOMExportBean>() {
			@Override
			public int compare(MatrixBOMExportBean o1, MatrixBOMExportBean o2) {
				return o1.bomItem.compareTo(o2.bomItem);
			}
		});
		//写Excel
		String excelPath = matrixTypeExcelTemplateMap.get(matrixType).writerDCN(matrixItemRevision,dcnNo, list);
		try {
			TCUtil.setBypass(TCUtil.getTCSession());
			TCComponentDataset createDataSet = TCUtil.createDataSet(TCUtil.getTCSession(), excelPath, DatasetEnum.MSExcelX.type(), new File(excelPath).getName(), DatasetEnum.MSExcelX.refName());
			targetComponent.add("IMAN_specification", createDataSet);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(TCUtil.getTCSession());
		}
		
		RuntimeUtil.exec("cmd /c start " + excelPath);
		
	}
	
	public static String createDCNexportDCNExcel(TCComponent targetComponent,String path) throws Exception {
		/**
		 * 注意：假定DCN中只有变体
		 */
		String dcnNo = targetComponent.getProperty("item_id");
		AIFComponentContext[] solutionItems = targetComponent.getRelated("CMHasSolutionItem");
		if(solutionItems.length == 0) {
			throw new Exception("没有任何更改！");
		}
		TCComponentItemRevision matrixItemRevision = null;
		for(AIFComponentContext context:solutionItems) {
			AIFComponentContext[] related = ((TCComponentItemRevision)context.getComponent()).whereReferencedByTypeRelation(new String[] {"D9_ProductLineRevision"},new String[] {"D9_HasVariants_REL"});;
			if(related.length != 0) {
				matrixItemRevision = (TCComponentItemRevision) related[0].getComponent();
			}
		}
		if(matrixItemRevision == null) {
			throw new Exception("没有找到Matrix，请检查");
		}		
		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {
			throw new Exception("不支持的Matrix类型");
		}
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AIFComponentContext[] impactedItems = targetComponent.getRelated("CMHasImpactedItem");
		for (int i = 0; i < impactedItems.length; i++) {
			TCComponentItemRevision impactedItem = (TCComponentItemRevision) impactedItems[i].getComponent();
			String itemId = impactedItem.getProperty("item_id");
			InterfaceAIFComponent solutionItem = findNextRevision(solutionItems, itemId);
			if(solutionItem==null) {
				continue;
			}
			System.out.println("item_id = "+itemId);
			comparison(impactedItem, (TCComponentItemRevision)solutionItem, list);
		}
		if(list.isEmpty()) {
			throw new Exception("没有任何更改");
		}
		list.sort(new Comparator<MatrixBOMExportBean>() {
			@Override
			public int compare(MatrixBOMExportBean o1, MatrixBOMExportBean o2) {
				return o1.bomItem.compareTo(o2.bomItem);
			}
		});
		
		//写Excel
		File file = new File(path);
		String excelPath = matrixTypeExcelTemplateMap.get(matrixType).createDCNwriterDCN(matrixItemRevision,dcnNo, list,path);
		return excelPath;
	}
	
	
    public static String generateDocument(TCSession session, TCComponentItemRevision matrixItemRevision, String filePath, String matrixVer) throws Exception {
    	com.foxconn.tcutils.util.TCUtil.setBypass(session);
    	String dsName =  filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    	TCComponentItemRevision documentItemRev = checkDocumentExist(matrixItemRevision);
    	if (CommonTools.isEmpty(documentItemRev)) {
			documentItemRev = createDocument(session, matrixItemRevision); //创建document文档
		}
    	
    	if (com.foxconn.tcutils.util.TCUtil.isReleased(documentItemRev)) { // 判断是否已经发行
    		String versionRule = com.foxconn.tcutils.util.TCUtil.getVersionRule(documentItemRev);
    		String newRevId = com.foxconn.tcutils.util.TCUtil.reviseVersion(session, versionRule, documentItemRev.getTypeObject().getName(), documentItemRev.getUid()); // 返回指定版本规则的版本号
    		TCComponentItemRevision newDocumentItemRev = com.foxconn.tcutils.util.TCUtil.doRevise(documentItemRev, newRevId); // 进行升版
    		TCComponentDataset dataset = checkDataset(newDocumentItemRev, dsName);
    		if (dataset != null) {
				newDocumentItemRev.remove("IMAN_specification", dataset);
			}
    		
    		
    		TCComponent[] relatedComponents = matrixItemRevision.getRelatedComponents("IMAN_specification");
    		if (CommonTools.isEmpty(relatedComponents)) {
    			matrixItemRevision.add("IMAN_specification", newDocumentItemRev);
			} else {
				matrixItemRevision.add("IMAN_specification", newDocumentItemRev, relatedComponents.length + 1);
			}
    		
    		documentItemRev = newDocumentItemRev;
			dsName =  documentItemRev.getProperty("object_name").replace(" ","_") + "_" + newRevId + ".xlsx";
    	}
    	
    	dsName = dsName.replace(matrixVer, documentItemRev.getProperty("item_revision_id"));
    	String tempFilePath = filePath.replace(filePath.substring(filePath.lastIndexOf(File.separator) + 1), dsName);
    	if(!filePath.equals(tempFilePath)) 
    		CommonTools.reNameFile(filePath, tempFilePath);
    	
    	if (checkDataset(documentItemRev, dsName) == null) {
    		TCComponentDataset createDataSet = com.foxconn.tcutils.util.TCUtil.createDataSet(session, tempFilePath, DatasetEnum.MSExcelX.type(), dsName, DatasetEnum.MSExcelX.refName());
    		documentItemRev.add("IMAN_specification", createDataSet);    		
    	} else {
			com.foxconn.tcutils.util.TCUtil.updateDataset(session, documentItemRev, "IMAN_specification", tempFilePath);
		}   
    	com.foxconn.tcutils.util.TCUtil.closeBypass(session);
		
    	return tempFilePath;
    }
    
    /**
	 * 校验文档是否存在
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	public static TCComponentItemRevision checkDocumentExist(TCComponentItemRevision itemRev) throws TCException {
		//List<TCComponentItemRevision> documentList = new ArrayList<TCComponentItemRevision>();
		itemRev.refresh();
		TCComponent[] imanSpecifications = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isEmpty(imanSpecifications)) {
			return null;
		}
		
		TCComponentItem document = null;
		for (int i = 0; i < imanSpecifications.length; i++) {
			if(imanSpecifications[i] instanceof TCComponentItemRevision) {
				TCComponentItemRevision documentRev = (TCComponentItemRevision)imanSpecifications[i];
				String objectName = documentRev.getProperty("object_name");
				String objectType = documentRev.getTypeObject().getName();
				if(objectName.equals(itemRev.getProperty("object_name")) && "DocumentRevision".equals(objectType)) {
					document = documentRev.getItem();
					break;
				}
			}
		}
		if(document!=null) {
			return document.getLatestItemRevision();
		}
		
//		Stream.of(imanSpecifications).forEach(component -> {
//			try {
//				String objectType = component.getTypeObject().getName();
//				String objectName = component.getProperty("object_name");
//				if ("DocumentRevision".equals(objectType) && objectName.equals(itemRev.getProperty("object_name"))) {
//					documentList.add((TCComponentItemRevision) component);
//				}				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		if (CommonTools.isNotEmpty(documentList)) {
//			return documentList.get(documentList.size() - 1);
//		}
		return null;
	}
	
	public static TCComponentDataset checkDataset(TCComponentItemRevision itemRev, String dsName) throws TCException {
		itemRev.refresh();
		TCComponent[] imanSpecifications = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isEmpty(imanSpecifications)) {
			return null;
		}
		
		Optional<TCComponent> findAny = Stream.of(imanSpecifications).filter(component -> {
			try {
				String objectType = component.getTypeObject().getName();
				String objectName = component.getProperty("object_name");
				if ("MSExcelX".equals(objectType) && objectName.startsWith(dsName.substring(0, dsName.lastIndexOf("_")))) {
					return true;
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}).findAny();
		
		if (findAny.isPresent()) {
			return (TCComponentDataset) findAny.get();
		}
		return null;	
	}
	
	/**
	 * 创建文档对象
	 * @param itemRev
	 * @throws Exception 
	 */
	public static TCComponentItemRevision createDocument(TCSession session, TCComponentItemRevision itemRev) throws Exception {
		Map<String, Object> itemMap = new HashMap<String, Object>();
		String systemType = "S";
		String systemName = "D";
		String systemLevel = "3";
		String productCode = "DT";
		String productLine = "CM";
		String customerName = "L";
		String id = com.foxconn.tcutils.util.TCUtil.generateId(session, "Document");
        id = id.replace(systemType + systemName + systemLevel, "");
		String docId = systemType + systemName + systemLevel + productCode + productLine + customerName + id;
		System.out.println("==>> docId: " + docId);
		itemMap.put("object_name", itemRev.getProperty("object_name") == null ? "" : itemRev.getProperty("object_name"));
		itemMap.put("d9_DocumentType", "Other Design Document");
		
		Map<String, Object> revisionMap = new HashMap<String, Object>();
		revisionMap.put("d9_ActualUserID", itemRev.getProperty("d9_ActualUserID") == null ? "" : itemRev.getProperty("d9_ActualUserID"));				
		TCComponentItem document = (TCComponentItem) com.foxconn.tcutils.util.TCUtil.createObject(com.foxconn.tcutils.util.TCUtil.getTCSession(), "Document", docId, itemMap, revisionMap);
		TCComponentItemRevision documentItemRev = document.getLatestItemRevision();
		itemRev.add("IMAN_specification", documentItemRev);
		return documentItemRev;
	}
	
	private static List<MatrixBOMExportBean> getBeanList(String uid,ArrayList<String> varList) throws Exception {
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AjaxResult ar = matrixBOMService.getMatrixBOMStruct(uid);
		JSONObject jsonObject = (JSONObject) ar.get("data");
		ProductLineBOMBean bomBean = (ProductLineBOMBean) jsonObject.get("partList");
		VariableBOMBean varBean = (VariableBOMBean) jsonObject.get("variableBOM");
		ar = matrixBOMService.getImg(uid);
		ArrayList<PicBean>  imgList = (ArrayList<PicBean>) ar.get("data");
		List<ProductLineBOMBean> productList = bomBean.getChild();
		IMatrixBOMExportWriter iMatrixBOMExportWriter = matrixTypeExcelTemplateMap.get(matrixType);
		for(ProductLineBOMBean productBean : productList) {
			MatrixBOMExportBean assembleMatixBOMBean = iMatrixBOMExportWriter.assembleMatixBOMBean(productBean,varList,varBean,imgList);
			list.add(assembleMatixBOMBean);
			List<ProductLineBOMBean> subList = productBean.getSubList();
			if(subList==null) {
				continue;
			}
			
			for(ProductLineBOMBean productSubBean : subList) {
				MatrixBOMExportBean subAssembleMatixBOMBean = iMatrixBOMExportWriter.assembleMatixBOMBean(productSubBean,varList,varBean,imgList);
				subAssembleMatixBOMBean.isSubstitutes = true;
				subAssembleMatixBOMBean.varType = assembleMatixBOMBean.varType;
				list.add(subAssembleMatixBOMBean);
			}
		}
		return list;
	}

	static InterfaceAIFComponent findNextRevision(AIFComponentContext[] solutionItems,String impactItemId) throws Exception {
		for (int i = 0; i < solutionItems.length; i++) {
			InterfaceAIFComponent component = solutionItems[i].getComponent();
			String solutionItemId = component.getProperty("item_id");
			if(solutionItemId.equals(impactItemId)) {
				return component;
			}
		}
		return null;
	}
	
	static void comparison(TCComponentItemRevision impactRevision,TCComponentItemRevision solutionRevision,List<MatrixBOMExportBean> list) throws Exception {
		TCComponentBOMWindow createBomWindowBySnapshot = PartBOMUtils.createBomWindowBySnapshot(impactRevision);
		if(createBomWindowBySnapshot==null) {
			return;
		}
		TCComponentBOMLine impactBOMLine = createBomWindowBySnapshot.getTopBOMLine();
		TCComponentBOMLine solutionBOMLine = TCUtil.openBomWindow(TCUtil.getTCSession(), solutionRevision);
		allBOMLineUnpackage(impactBOMLine);
		allBOMLineUnpackage(solutionBOMLine);
		// 比对
		doComparison(getChildren(impactBOMLine),getChildren(solutionBOMLine),impactBOMLine,solutionBOMLine,list);
	}

	static void doComparison(TCComponentBOMLine[] impactList,TCComponentBOMLine[] solutionList,TCComponentBOMLine impactPrimayBOMLine,TCComponentBOMLine solutionPrimayBOMLine,List<MatrixBOMExportBean> list) throws Exception {
		
		if(impactList==null) {
			impactList = new TCComponentBOMLine[0];
		}
		
		if(solutionList==null) {
			solutionList = new TCComponentBOMLine[0];
		}
		
		IMatrixBOMExportWriter iMatrixBOMExportWriter = matrixTypeExcelTemplateMap.get(matrixType);
		
		// 找到所有新增的
		for (TCComponentBOMLine solutionBomLine : solutionList) {
			TCComponentBOMLine  findComponent = findComponent(impactList, solutionBomLine,iMatrixBOMExportWriter);
			if(findComponent == null) {
				// 新增
				MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(null,solutionBomLine, "A");
				if(solutionBomLine.isSubstitute()) {
					primaryBean.bomItem = solutionPrimayBOMLine.getProperty("bl_sequence_no");
					primaryBean.quantity = solutionPrimayBOMLine.getProperty("bl_quantity");
				}
				list.add(primaryBean);
				TCComponentBOMLine[] listSubstitutes = listSubstitutes(solutionBomLine);
				for (TCComponentBOMLine substitutes : listSubstitutes) {
					MatrixBOMExportBean beanSubstitutes = iMatrixBOMExportWriter.assembleMatixChangeBean(null,substitutes, "A");
					beanSubstitutes.bomItem = primaryBean.bomItem;
					beanSubstitutes.quantity = primaryBean.quantity;
					list.add(beanSubstitutes);
				}
			}
		}
		// 找到所有删除和修改的
		for (TCComponentBOMLine  impactBomLine : impactList) {
			TCComponentBOMLine solutionBomLine = findComponent(solutionList, impactBomLine,iMatrixBOMExportWriter);
			if(solutionBomLine == null) {
				// 删除
				MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(impactBomLine, null, "D");
				if(impactBomLine.isSubstitute()) {
					String bl_sequence_no = impactPrimayBOMLine.getProperty("bl_sequence_no");
					String bl_quantity = impactPrimayBOMLine.getProperty("bl_quantity");
					
					primaryBean.bomItem = bl_sequence_no;
					primaryBean.quantity = bl_quantity;
				}
				list.add(primaryBean);
				TCComponentBOMLine[] listSubstitutes = listSubstitutes(impactBomLine);
				for (TCComponentBOMLine substitutes : listSubstitutes) {
					MatrixBOMExportBean beanSubstitutes = iMatrixBOMExportWriter.assembleMatixChangeBean(substitutes, null, "D");
					beanSubstitutes.bomItem = primaryBean.bomItem;
					beanSubstitutes.quantity = primaryBean.quantity;
					list.add(beanSubstitutes);
				}
			}else {
				if(iMatrixBOMExportWriter.isChange(impactBomLine,solutionBomLine)) {
					// 修改
					MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(impactBomLine,solutionBomLine, "C");
					if(primaryBean!=null) {
						list.add(primaryBean);
					}
				}
			    // 比较替代料
				doComparison(listSubstitutes(impactBomLine),listSubstitutes(solutionBomLine),impactBomLine,solutionBomLine,list);
			}
		}	
	}
	
	/**
	 * 解包全部BOMLine
	 * 
	 * @param designBOMTopLine
	 * @throws Exception
	 */
	private static void allBOMLineUnpackage(TCComponentBOMLine designBOMTopLine) throws Exception {
		AIFComponentContext[] children = designBOMTopLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			if (bomLine.isPacked()) {
				bomLine.unpack();
			}
			allBOMLineUnpackage(bomLine);
		}
	}
	
	static TCComponentBOMLine[] getChildren(TCComponentBOMLine bomline) throws TCException {
		AIFComponentContext[] children = bomline.getChildren();
		if(ArrayUtil.isEmpty(children)) {
			return new TCComponentBOMLine[0];
		}
		TCComponentBOMLine[] list = new TCComponentBOMLine[children.length];
		for(int i=0;i<list.length;i++) {
			list[i] = (TCComponentBOMLine) children[i].getComponent();
		}
		return list;
	}
	
	static TCComponentBOMLine[] listSubstitutes(TCComponentBOMLine bomline) throws TCException {
		if(bomline.isSubstitute()||!bomline.hasSubstitutes()) {
			return new TCComponentBOMLine[0];
		}
		return bomline.listSubstitutes();
	}

	static TCComponentBOMLine findComponent(TCComponentBOMLine[] children,TCComponentBOMLine bomline,IMatrixBOMExportWriter iMatrixBOMExportWriter) throws Exception {
		for (TCComponentBOMLine childBomLine : children) {		
			if(iMatrixBOMExportWriter.equals(childBomLine,bomline)) {
				return childBomLine;
			}
		}
		return null;
	}
	
	public static File extractPic(TCComponentItemRevision itemRevision) throws Exception {
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
		return picFile;
	}
	
	public static void setValueAndStyle(ExcelWriter writer,int row,int col,String value,CellStyle style) {
		writer.writeCellValue(row,col, value);
		writer.getCell(row,col).setCellStyle(style);
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


}
